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

import java.util.ArrayList;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class Config {
    public static final ServerConfig SERVER;
    private static final ForgeConfigSpec SERVER_SPEC;
    private static final ForgeConfigSpec WEBHOOK_SPEC;
    public static final WebhookConfig WEBHOOK;

    static {
        Pair<ServerConfig, ForgeConfigSpec> serverPair = (new Builder()).configure(Config.ServerConfig::new);
        SERVER_SPEC = serverPair.getRight();
        SERVER = serverPair.getLeft();
        Pair<WebhookConfig, ForgeConfigSpec> webhookPair = (new Builder()).configure(Config.WebhookConfig::new);
        WEBHOOK_SPEC = webhookPair.getRight();
        WEBHOOK = webhookPair.getLeft();
    }

    public static void load() {
        ModLoadingContext.get().registerConfig(Type.SERVER, Config.SERVER_SPEC);
        ModLoadingContext.get().registerConfig(Type.SERVER, Config.WEBHOOK_SPEC, String.format("%s-%s.toml", ServerRestartMod.MOD_ID, "discord-webhook"));
    }

    public static class ServerConfig {

        public ConfigValue<ArrayList<String>> s_shutdownMessages;
        public ConfigValue<Long> s_shutdownLength;

        public ConfigValue<String> d_startupMessage;
        public ConfigValue<String> d_serverUserName;
        public ConfigValue<String> d_avatarUrl;
        public ConfigValue<Integer> d_embed_color;
        public ConfigValue<String> d_embed_footer_text;
        public ConfigValue<String> d_embed_footer_url;

        ServerConfig(Builder builder) {
            // Server Restart Values
            builder.push("restart");
            this.s_shutdownMessages = builder.comment("List of Strings and delay before announcement.").define("shutdownMessages", getDefaultShutdownMessages());
            this.s_shutdownLength = builder.comment("Time in seconds before the server will restart.").defineInRange("shutdownLength", 60L * 60 * 6, 60L, Long.MAX_VALUE / 1000);
            builder.pop();

            // Discord embed Values
            builder.push("discord");

            this.d_startupMessage = builder.comment("The string to send when the server first starts. Mentions are allowed here.").define("startupMessage", "null");
            this.d_serverUserName = builder.comment("The name of the webhook to send to Discord. Use \"null\" to disable.").define("serverName", "Server");
            this.d_avatarUrl = builder.comment("The URL to use for the webhook avatar. Use \"null\" to disable.").define("avatarUrl", "null");
            this.d_embed_color = builder.comment("The color to use for the Discord embed.").defineInRange("embedColor", 15258703, 0, 16777215);
            this.d_embed_footer_text = builder.comment("The comment to show in the Discord embed Footer. Use \"null\" to disable.").define("embedFooterText", "Server Restart Notification");
            this.d_embed_footer_url = builder.comment("The image URL to show in the Discord embed Footer. Use \"null\" to disable.").define("embedFooterUrl", "null");
            builder.pop();

        }

        private ArrayList<String> getDefaultShutdownMessages() {
            ArrayList<String> map = new ArrayList<String>();
            map.add("3600|The server will restart in 60 minutes.");
            map.add("900|The server will restart in 15 minutes.");
            map.add("300|The server will restart in 5 minutes.");
            map.add("15|Restarting in 15 seconds...");
            map.add("5|Restarting in 5 seconds...");
            map.add("4|Restarting in 4 seconds...");
            map.add("3|Restarting in 3 seconds...");
            map.add("2|Restarting in 2 seconds...");
            map.add("1|Restarting in 1 second...");
            map.add("0|Server restarting now!");
            return map;
        }
    }

    public static class WebhookConfig {
        public ConfigValue<String> d_webhook_url;

        WebhookConfig(Builder builder) {
            builder.push("discord");
            this.d_webhook_url = builder.comment("The Discord webhook URL. Use \"null\" to disable.").define("webhook", "null");
            builder.pop();
        }
    }

}
