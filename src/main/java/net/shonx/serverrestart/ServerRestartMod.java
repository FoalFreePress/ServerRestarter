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
import java.util.Timer;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.shonx.serverrestart.discord.DiscordPoster;
import net.shonx.serverrestart.discord.EmbedObject;
import net.shonx.serverrestart.messages.Message;
import net.shonx.serverrestart.messages.MessageLoader;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(ServerRestartMod.MOD_ID)
public class ServerRestartMod {
    public static final String MOD_ID = "serverrestart";
    public static final Logger LOGGER = LogManager.getLogger();
    private Timer timer;
    private ArrayList<Message> messages;

    public ServerRestartMod() {
        Config.load();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of((Supplier<String>) () -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        timer = new Timer();
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        final long shutdownIn = Config.SERVER.s_shutdownLength.get() * 1000;

        timer.schedule(new KillServerTask(), shutdownIn);

        printLog(shutdownIn);
        messages = MessageLoader.loadMessages();
        messages.forEach(message -> timer.schedule(new AnnounceTask(message), shutdownIn - message.time * 1000L));
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        EmbedObject embed = new EmbedObject("The server has shut down!", null);
        DiscordPoster.postEmbed(embed);
        timer.cancel();
    }

    private void printLog(long shutdownIn) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        format.setTimeZone(TimeZone.getDefault());
        LOGGER.warn(String.format("Server will restart at %s.", format.format(new Date(System.currentTimeMillis() + shutdownIn))));

        String startupMessage = Config.SERVER.d_startupMessage.get();
        EmbedObject embed = new EmbedObject(String.format("Hey everyone! The server is up. It will restart at <t:%d:T>", (System.currentTimeMillis() + shutdownIn) / 1000), "null".equals(startupMessage) ? null : startupMessage);
        DiscordPoster.postEmbed(embed);
    }
}
