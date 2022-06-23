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

import java.util.Objects;
import java.util.TimerTask;

import javax.annotation.Nonnull;

import net.shonx.serverrestart.discord.DiscordPoster;
import net.shonx.serverrestart.discord.EmbedObject;
import net.shonx.serverrestart.messages.Message;

import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class AnnounceTask extends TimerTask {

    private Message message;
    private static final Color COLOR = Color.fromRgb(16711935); // #FF00FF
    private static final Style STYLE = Style.EMPTY.withColor(COLOR).withFont(Style.DEFAULT_FONT);

    public AnnounceTask(@Nonnull Message message) {
        this.message = Objects.requireNonNull(message);
    }

    @Override
    public void run() {
        ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(new StringTextComponent(message.message).withStyle(STYLE), ChatType.SYSTEM, Util.NIL_UUID);
        if (message.announceToDiscord)
            DiscordPoster.postEmbed(new EmbedObject(message.message, null));
    }

}
