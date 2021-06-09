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

import java.util.HashMap;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class Config {
    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;
    public static long shutdownLength;
    public static HashMap<Long, String> shutdownMessages;
    
    static {
        Pair<ServerConfig, ForgeConfigSpec> specPair = (new Builder()).configure(Config.ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }
    
    public Config() {
        shutdownLength = SERVER.shutdownLength.get();
        shutdownMessages = SERVER.shutdownMessages.get();
    }
    
    
    public static class ServerConfig {

        public ConfigValue<HashMap<Long, String>> shutdownMessages;
        public ConfigValue<Long> shutdownLength;

        ServerConfig(Builder builder) {
           builder.push("settings");
           this.shutdownMessages = builder.comment("List of Strings and delay before announcement.").define("shutdownMessages", getDefaultShutdownMessages());
           this.shutdownLength = builder.comment("Time in seconds before the server will restart.").define("shutdownLength", 60L * 60 * 6);
           builder.pop();
        }
        
        private static HashMap<Long, String> getDefaultShutdownMessages() {
            HashMap<Long, String> map = new HashMap<Long, String>();
            map.put(86400L, "The server will restart in 60 minutes.");
            map.put(3600L, "The server will restart in 15 minutes.");
            map.put(300L,  "The server will restart in 5 minutes.");
            map.put(15L, "Restarting in 15 seconds...");
            map.put(5L, "Restarting in 5 seconds...");
            map.put(4L, "Restarting in 4 seconds...");
            map.put(3L, "Restarting in 3 seconds...");
            map.put(2L, "Restarting in 2 seconds...");
            map.put(1L, "Restarting in 1 second...");
            return map;
        }
     }
}
