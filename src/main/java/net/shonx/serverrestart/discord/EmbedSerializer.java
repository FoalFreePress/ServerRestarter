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

import java.lang.reflect.Type;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EmbedSerializer implements JsonSerializer<EmbedObject> {

    @Override
    public JsonElement serialize(EmbedObject src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        if (src.username != null)
            result.add("username", new JsonPrimitive(src.username));
        if (src.avatarUrl != null)
            result.add("avatar_url", new JsonPrimitive(src.avatarUrl));
        if (src.content != null)
            result.add("content", new JsonPrimitive(src.content));
        //
        // EMBED JSON
        //
        JsonObject embed = new JsonObject();
        if (src.embed_description != null)
            embed.add("description", new JsonPrimitive(src.embed_description));
        embed.add("color", new JsonPrimitive(src.color));
        embed.add("timestamp", new JsonPrimitive(getCurrentTimestamp()));
        //
        // FOOTER JSON
        //
        JsonObject footer = new JsonObject();
        if (src.footerText != null)
            footer.add("text", new JsonPrimitive(src.footerText));
        if (src.footerUrl != null)
            footer.add("icon_url", new JsonPrimitive(src.footerUrl));

        embed.add("footer", footer);

        JsonArray embeds = new JsonArray();
        embeds.add(embed);

        result.add("embeds", embeds);

        return result;
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(System.currentTimeMillis()));
    }

}
