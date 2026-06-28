/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ReloadableTexture;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SplashOverlay
extends Overlay {
    final static public Identifier LOGO = Identifier.ofVanilla("textures/gui/title/mojangstudios.png");
    final static private int MOJANG_RED = ColorHelper.getArgb(255, 239, 50, 61);
    final static private int MONOCHROME_BLACK = ColorHelper.getArgb(255, 0, 0, 0);
    final static private IntSupplier BRAND_ARGB = () -> MinecraftClient.getInstance().options.getMonochromeLogo().getValue() != false ? MONOCHROME_BLACK : MOJANG_RED;
    final static private int field_32251 = 240;
    final static private float LOGO_RIGHT_HALF_V = 60.0f;
    final static private int field_32253 = 60;
    final static private int field_32254 = 120;
    final static private float LOGO_OVERLAP = 0.0625f;
    final static private float PROGRESS_LERP_DELTA = 0.95f;
    final static public long RELOAD_COMPLETE_FADE_DURATION = 1000L;
    final static public long RELOAD_START_FADE_DURATION = 500L;
    final private MinecraftClient client;
    final private ResourceReload reload;
    final private Consumer<Optional<Throwable>> exceptionHandler;
    final private boolean reloading;
    private float progress;
    private long reloadCompleteTime = -1L;
    private long reloadStartTime = -1L;

    public SplashOverlay(MinecraftClient client, ResourceReload monitor, Consumer<Optional<Throwable>> exceptionHandler, boolean reloading) {
        this.client = client;
        this.reload = monitor;
        this.exceptionHandler = exceptionHandler;
        this.reloading = reloading;
    }

    public static void init(TextureManager textureManager) {
        textureManager.registerTexture(LOGO, new LogoTexture());
    }

    private static int withAlpha(int color, int alpha) {
        return color & 0xFFFFFF | alpha << 24;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        float h;
        int k;
        float g;
        int i = context.getScaledWindowWidth();
        int j = context.getScaledWindowHeight();
        long l = Util.getMeasuringTimeMs();
        if (this.reloading && this.reloadStartTime == -1L) {
            this.reloadStartTime = l;
        }
        float f = this.reloadCompleteTime > -1L ? (float)(l - this.reloadCompleteTime) / 1000.0f : -1.0f;
        float f2 = g = this.reloadStartTime > -1L ? (float)(l - this.reloadStartTime) / 500.0f : -1.0f;
        if (f >= 1.0f) {
            if (this.client.currentScreen != null) {
                this.client.currentScreen.renderWithTooltip(context, 0, 0, deltaTicks);
            }
            k = MathHelper.ceil((1.0f - MathHelper.clamp(f - 1.0f, 0.0f, 1.0f)) * 255.0f);
            context.createNewRootLayer();
            context.fill(0, 0, i, j, SplashOverlay.withAlpha(BRAND_ARGB.getAsInt(), k));
            h = 1.0f - MathHelper.clamp(f - 1.0f, 0.0f, 1.0f);
        } else if (this.reloading) {
            if (this.client.currentScreen != null && g < 1.0f) {
                this.client.currentScreen.renderWithTooltip(context, mouseX, mouseY, deltaTicks);
            }
            k = MathHelper.ceil(MathHelper.clamp((double)g, 0.15, 1.0) * 255.0);
            context.createNewRootLayer();
            context.fill(0, 0, i, j, SplashOverlay.withAlpha(BRAND_ARGB.getAsInt(), k));
            h = MathHelper.clamp(g, 0.0f, 1.0f);
        } else {
            k = BRAND_ARGB.getAsInt();
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.client.getFramebuffer().getColorAttachment(), k);
            h = 1.0f;
        }
        k = (int)((double)context.getScaledWindowWidth() * 0.5);
        int m = (int)((double)context.getScaledWindowHeight() * 0.5);
        double d = Math.min((double)context.getScaledWindowWidth() * 0.75, (double)context.getScaledWindowHeight()) * 0.25;
        int n = (int)(d * 0.5);
        double e = d * 4.0;
        int o = (int)(e * 0.5);
        int p = ColorHelper.getWhite(h);
        context.drawTexture(RenderPipelines.MOJANG_LOGO, LOGO, k - o, m - n, -0.0625f, 0.0f, o, (int)d, 120, 60, 120, 120, p);
        context.drawTexture(RenderPipelines.MOJANG_LOGO, LOGO, k, m - n, 0.0625f, 60.0f, o, (int)d, 120, 60, 120, 120, p);
        int q = (int)((double)context.getScaledWindowHeight() * 0.8325);
        float r = this.reload.getProgress();
        this.progress = MathHelper.clamp(this.progress * 0.95f + r * 0.050000012f, 0.0f, 1.0f);
        if (f < 1.0f) {
            this.renderProgressBar(context, i / 2 - o, q - 5, i / 2 + o, q + 5, 1.0f - MathHelper.clamp(f, 0.0f, 1.0f));
        }
        if (f >= 2.0f) {
            this.client.setOverlay(null);
        }
        if (this.reloadCompleteTime == -1L && this.reload.isComplete() && (!this.reloading || g >= 2.0f)) {
            try {
                this.reload.throwException();
                this.exceptionHandler.accept(Optional.empty());
            }
            catch (Throwable throwable) {
                this.exceptionHandler.accept(Optional.of(throwable));
            }
            this.reloadCompleteTime = Util.getMeasuringTimeMs();
            if (this.client.currentScreen != null) {
                this.client.currentScreen.init(this.client, context.getScaledWindowWidth(), context.getScaledWindowHeight());
            }
        }
    }

    private void renderProgressBar(DrawContext context, int minX, int minY, int maxX, int maxY, float opacity) {
        int i = MathHelper.ceil((float)(maxX - minX - 2) * this.progress);
        int j = Math.round(opacity * 255.0f);
        int k = ColorHelper.getArgb(j, 255, 255, 255);
        context.fill(minX + 2, minY + 2, minX + i, maxY - 2, k);
        context.fill(minX + 1, minY, maxX - 1, minY + 1, k);
        context.fill(minX + 1, maxY, maxX - 1, maxY - 1, k);
        context.fill(minX, minY, minX + 1, maxY, k);
        context.fill(maxX, minY, maxX - 1, maxY, k);
    }

    @Override
    public boolean pausesGame() {
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    static class LogoTexture
    extends ReloadableTexture {
        public LogoTexture() {
            super(LOGO);
        }

        @Override
        public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
            TextureContents textureContents;
            block5: {
                ResourceFactory resourceFactory = MinecraftClient.getInstance().getDefaultResourcePack().getFactory();
                InputStream inputStream = resourceFactory.open(LOGO);
                try {
                    textureContents = new TextureContents(NativeImage.read(inputStream), new TextureResourceMetadata(true, true));
                    if (inputStream == null) break block5;
                }
                catch (Throwable throwable) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                inputStream.close();
            }
            return textureContents;
        }
    }
}

