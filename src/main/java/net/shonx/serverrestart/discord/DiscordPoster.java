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

package net.shonx.serverrestart.discord;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.shonx.serverrestart.ServerRestart;

public class DiscordPoster {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(EmbedObject.class, new EmbedSerializer()).create();
    private static String stringUrl = null;

    public static void postEmbed(EmbedObject embed) {
        postEmbed(GSON.toJson(embed));
    }

    private static void postEmbed(String json) {
        OutputStream output;
        InputStream input;
        try {
            if ("null".equals(stringUrl))
                return;

            URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("User-Agent", "Java-ServerRestart-Webhook");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            output = connection.getOutputStream();
            output.write(json.getBytes());
            output.flush();
            output.close();

            input = connection.getInputStream();
            input.close();

            connection.disconnect();

        } catch (Exception e) {
            // Doesn't affect us really...
            StringBuilder builder = new StringBuilder();
            builder.append("Error in Discord I/O" + System.lineSeparator());
            builder.append("JSON: " + json + System.lineSeparator());
            builder.append("Webhook URL = " + stringUrl + System.lineSeparator());
            ServerRestart.LOGGER.error(builder.toString(), e);
        }
    }

    public static void setWebhookURL(String webhookURL) {
        if (webhookURL == null)
            throw new NullPointerException("webhookURL cannot be null here.");
        stringUrl = webhookURL;
    }
}
