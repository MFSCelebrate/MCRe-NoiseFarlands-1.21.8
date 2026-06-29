/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.ColorLerper;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class NowPlayingToast
implements Toast {
    final static private Identifier TEXTURE = Identifier.ofVanilla("toast/now_playing");
    final static private Identifier MUSIC_NOTES_ICON = Identifier.of("icon/music_notes");
    final static private int MARGIN = 7;
    final static private int MUSIC_NOTES_ICON_SIZE = 16;
    final static private int field_60727 = 30;
    final static private int field_60728 = 30;
    final static private int VISIBILITY_DURATION = 5000;
    final static private int TEXT_COLOR = DyeColor.LIGHT_GRAY.getSignColor();
    final static private long MUSIC_NOTE_COLOR_CHANGE_INTERVAL = 25L;
    static private int musicNoteColorChanges;
    static private long lastMusicNoteColorChangeTime;
    static private int musicNotesIconColor;
    private boolean showing;
    private double displayTimeMultiplier;
    @Nullable
    static private String musicTranslationKey;
    final private MinecraftClient client;
    private Toast.Visibility visibility = Toast.Visibility.HIDE;

    public NowPlayingToast() {
        this.client = MinecraftClient.getInstance();
    }

    public static void draw(DrawContext context, TextRenderer textRenderer) {
        if (musicTranslationKey != null) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, NowPlayingToast.getMusicTextWidth(musicTranslationKey, textRenderer), 30);
            int i = 7;
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MUSIC_NOTES_ICON, 7, 7, 16, 16, musicNotesIconColor);
            context.drawTextWithShadow(textRenderer, NowPlayingToast.getMusicText(musicTranslationKey), 30, 15 - textRenderer.fontHeight / 2, TEXT_COLOR);
        }
    }

    public static void tick() {
        long l;
        musicTranslationKey = MinecraftClient.getInstance().getMusicTracker().getCurrentMusicTranslationKey();
        if (musicTranslationKey != null && (l = System.currentTimeMillis()) > lastMusicNoteColorChangeTime + 25L) {
            lastMusicNoteColorChangeTime = l;
            musicNotesIconColor = ColorLerper.lerpColor(ColorLerper.Type.MUSIC_NOTE, ++musicNoteColorChanges);
        }
    }

    private static Text getMusicText(@Nullable String translationKey) {
        if (translationKey == null) {
            return Text.empty();
        }
        return Text.translatable(translationKey.replace("/", "."));
    }

    public void show(GameOptions options) {
        this.showing = true;
        this.displayTimeMultiplier = options.getNotificationDisplayTime().getValue();
        this.setVisibility(Toast.Visibility.SHOW);
    }

    @Override
    public void update(ToastManager manager, long time) {
        if (this.showing) {
            this.visibility = (double)time < 5000.0 * this.displayTimeMultiplier ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
            NowPlayingToast.tick();
        }
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        NowPlayingToast.draw(context, textRenderer);
    }

    @Override
    public void onFinishedRendering() {
        this.showing = false;
    }

    @Override
    public int getWidth() {
        return NowPlayingToast.getMusicTextWidth(musicTranslationKey, this.client.textRenderer);
    }

    private static int getMusicTextWidth(@Nullable String translationKey, TextRenderer textRenderer) {
        return 30 + textRenderer.getWidth(NowPlayingToast.getMusicText(translationKey)) + 7;
    }

    @Override
    public int getHeight() {
        return 30;
    }

    @Override
    public float getXPos(int scaledWindowWidth, float visibleWidthPortion) {
        return (float)this.getWidth() * visibleWidthPortion - (float)this.getWidth();
    }

    @Override
    public float getYPos(int topIndex) {
        return 0.0f;
    }

    @Override
    public Toast.Visibility getVisibility() {
        return this.visibility;
    }

    public void setVisibility(Toast.Visibility visibility) {
        this.visibility = visibility;
    }

    static {
        musicNotesIconColor = -1;
    }
}

