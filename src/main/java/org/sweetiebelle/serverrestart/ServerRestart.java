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

package org.sweetiebelle.serverrestart;

import java.util.ArrayList;
import java.util.Timer;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(ServerRestart.MOD_ID)
public class ServerRestart {
    public static final String MOD_ID = "serverrestart";
    public static final Logger LOGGER = LogManager.getLogger();
    private Timer timer;

    public ServerRestart() {
        Config.load();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of((Supplier<String>) () -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        timer = new Timer();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerFinished(FMLServerStartedEvent event) {
        final long shutdownIn = Config.SERVER.shutdownLength.get() * 1000;
        timer.schedule(new KillServerTask(), shutdownIn);
        LOGGER.info("Restarting server in " + (shutdownIn / 60 / 1000) + " minutes.");
        ArrayList<ShutdownMessage> shutdownMessages = Config.SERVER.getMessages();
        if (isEmpty(shutdownMessages))
            throw new NullPointerException("ServerRestartConfig.shutdownMessages");
        shutdownMessages.forEach((message) -> {
            timer.schedule(new AnnounceTask(message.message), shutdownIn - (message.time * 1000L));
        });
    }

    public static boolean isEmpty(ArrayList<?> list) {
        if (list == null)
            return true;
        if (list.size() < 1)
            return true;
        return false;
    }
}
