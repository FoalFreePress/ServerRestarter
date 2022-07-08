/*
 * MIT License
 *
 * Copyright (c) 2021 shroomdog27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.shonx.serverrestart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.shonx.serverrestart.discord.DiscordPoster;
import net.shonx.serverrestart.discord.EmbedObject;
import net.shonx.serverrestart.messages.Message;
import net.shonx.serverrestart.messages.MessageLoader;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Mod(ServerRestart.MOD_ID)
public class ServerRestart {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "serverrestart";

    public static final void onServerCrash() {
        try {
            EmbedObject embed = new EmbedObject(":boom: Oh no! The sever has crashed! :boom:", null);
            embed.color = 16711680;
            DiscordPoster.postEmbed(embed);
        } catch (Throwable ignored) {
            // Server is already crashing... don't make it worse
        }
    }

    private ArrayList<Message> messages;

    private ScheduledThreadPoolExecutor timer;

    public ServerRestart() {
        Config.load();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of((Supplier<String>) () -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        timer = new ScheduledThreadPoolExecutor(1);
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        final long shutdownIn = Config.SERVER.s_shutdownLength.get();

        timer.schedule(() -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            StringTextComponent message = new StringTextComponent("Server is restarting!");
            for (ServerPlayerEntity player : new ArrayList<ServerPlayerEntity>(server.getPlayerList().getPlayers()))
                player.connection.disconnect(message);
            server.halt(false);
        }, shutdownIn, TimeUnit.SECONDS);

        printLog(shutdownIn);
        messages = MessageLoader.loadMessages();
        messages.forEach(message -> timer.schedule(() -> {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(new StringTextComponent(message.message).withStyle(Style.EMPTY.withColor(Color.fromRgb(16711935)).withFont(Style.DEFAULT_FONT)), ChatType.SYSTEM, Util.NIL_UUID);
            if (message.announceToDiscord)
                DiscordPoster.postEmbed(new EmbedObject(message.message, null));
        }, shutdownIn - message.time, TimeUnit.SECONDS));
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        EmbedObject embed = new EmbedObject("The server has shut down!", null);
        DiscordPoster.postEmbed(embed);
        timer.shutdownNow();
    }

    private void printLog(long shutdownIn) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        format.setTimeZone(TimeZone.getDefault());
        long shutdownAtInMillis = System.currentTimeMillis() + shutdownIn * 1000L;
        LOGGER.warn(String.format("Server will restart at %s.", format.format(new Date(shutdownAtInMillis))));

        String startupMessage = Config.SERVER.d_startupMessage.get();
        EmbedObject embed = new EmbedObject(String.format("Hey everyone! The server is up. It will restart at <t:%d:T>", shutdownAtInMillis / 1000L), "null".equals(startupMessage) ? null : startupMessage);
        embed.color = 65280;
        DiscordPoster.postEmbed(embed);
    }
}
