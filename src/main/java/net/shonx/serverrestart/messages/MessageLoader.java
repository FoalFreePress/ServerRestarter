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

package net.shonx.serverrestart.messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import net.shonx.serverrestart.ServerRestartMod;

import net.minecraftforge.fml.loading.FMLPaths;

public class MessageLoader {

    private static final Gson gson = new Gson();

    public static ArrayList<Message> loadMessages() {
        try {
            Message[] messages = gson.fromJson(new InputStreamReader(new FileInputStream(getFile())), Message[].class);
            Arrays.sort(messages);
            return Lists.newArrayList(messages);
        } catch (IOException e) {
            ServerRestartMod.LOGGER.error("Error in Message JSON I/O", e);
            return new ArrayList<Message>();
        }
    }

    private static File getFile() throws IOException {
        // File jsonFile =
        // FMLPaths.CONFIGDIR.get().resolve(Paths.get(ServerRestartMod.MOD_ID,
        // "messages.json")).toFile();

        File jsonFile = new File(FMLPaths.CONFIGDIR.get().toAbsolutePath().toFile(), String.format("%s/%s", ServerRestartMod.MOD_ID, "messages.json"));
        if (jsonFile.exists())
            return jsonFile;
        URL defaultFile = MessageLoader.class.getResource("/default-messages.json");
        Files.copy(defaultFile.openStream(), jsonFile.toPath());
        return jsonFile;
    }
}
