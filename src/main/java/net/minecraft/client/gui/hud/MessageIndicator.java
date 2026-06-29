/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.hud;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record MessageIndicator(int indicatorColor, @Nullable Icon icon, @Nullable Text text, @Nullable String loggedName) {
    final static private Text SYSTEM_TEXT = Text.translatable("chat.tag.system");
    final static private Text SINGLE_PLAYER_TEXT = Text.translatable("chat.tag.system_single_player");
    final static private Text NOT_SECURE_TEXT = Text.translatable("chat.tag.not_secure");
    final static private Text MODIFIED_TEXT = Text.translatable("chat.tag.modified");
    final static private Text ERROR_TEXT = Text.translatable("chat.tag.error");
    final static private int NOT_SECURE_COLOR = 0xD0D0D0;
    final static private int MODIFIED_COLOR = 0x606060;
    final static private MessageIndicator SYSTEM = new MessageIndicator(0xD0D0D0, null, SYSTEM_TEXT, "System");
    final static private MessageIndicator SINGLE_PLAYER = new MessageIndicator(0xD0D0D0, null, SINGLE_PLAYER_TEXT, "System");
    final static private MessageIndicator NOT_SECURE = new MessageIndicator(0xD0D0D0, null, NOT_SECURE_TEXT, "Not Secure");
    final static private MessageIndicator CHAT_ERROR = new MessageIndicator(0xFF5555, null, ERROR_TEXT, "Chat Error");

    public static MessageIndicator system() {
        return SYSTEM;
    }

    public static MessageIndicator singlePlayer() {
        return SINGLE_PLAYER;
    }

    public static MessageIndicator notSecure() {
        return NOT_SECURE;
    }

    public static MessageIndicator modified(String originalText) {
        MutableText text = Text.literal(originalText).formatted(Formatting.GRAY);
        MutableText text2 = Text.empty().append(MODIFIED_TEXT).append(ScreenTexts.LINE_BREAK).append(text);
        return new MessageIndicator(0x606060, Icon.CHAT_MODIFIED, text2, "Modified");
    }

    public static MessageIndicator chatError() {
        return CHAT_ERROR;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MessageIndicator.class, "indicatorColor;icon;text;logTag", "indicatorColor", "icon", "text", "loggedName"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MessageIndicator.class, "indicatorColor;icon;text;logTag", "indicatorColor", "icon", "text", "loggedName"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MessageIndicator.class, "indicatorColor;icon;text;logTag", "indicatorColor", "icon", "text", "loggedName"}, this, object);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Icon
    extends Enum<Icon> {
        final static public Icon CHAT_MODIFIED = new Icon(Identifier.ofVanilla("icon/chat_modified"), 9, 9);
        final public Identifier texture;
        final public int width;
        final public int height;
        final static private Icon[] field_39768;

        public static Icon[] values() {
            return (Icon[])field_39768.clone();
        }

        public static Icon valueOf(String string) {
            return Enum.valueOf(Icon.class, string);
        }

        private Icon(Identifier texture, int width, int height) {
            this.texture = texture;
            this.width = width;
            this.height = height;
        }

        public void draw(DrawContext context, int x, int y) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, x, y, this.width, this.height);
        }

        private static Icon[] method_44711() {
            return new Icon[]{CHAT_MODIFIED};
        }

        static {
            field_39768 = Icon.method_44711();
        }
    }
}

