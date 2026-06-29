/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DownloadingTerrainScreen
extends Screen {
    final static private Text TEXT = Text.translatable("multiplayer.downloadingTerrain");
    final static private long MIN_LOAD_TIME_MS = 30000L;
    final private long loadStartTime;
    final private BooleanSupplier shouldClose;
    final private WorldEntryReason worldEntryReason;
    @Nullable
    private Sprite backgroundSprite;

    public DownloadingTerrainScreen(BooleanSupplier shouldClose, WorldEntryReason worldEntryReason) {
        super(NarratorManager.EMPTY);
        this.shouldClose = shouldClose;
        this.worldEntryReason = worldEntryReason;
        this.loadStartTime = Util.getMeasuringTimeMs();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected boolean hasUsageText() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, TEXT, this.width / 2, this.height / 2 - 50, Colors.WHITE);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        switch (this.worldEntryReason.ordinal()) {
            case 2: {
                this.renderPanoramaBackground(context, deltaTicks);
                this.applyBlur(context);
                this.renderDarkening(context);
                break;
            }
            case 0: {
                context.drawSpriteStretched(RenderPipelines.GUI_OPAQUE_TEX_BG, this.getBackgroundSprite(), 0, 0, context.getScaledWindowWidth(), context.getScaledWindowHeight());
                break;
            }
            case 1: {
                TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
                TextureSetup textureSetup = TextureSetup.of(textureManager.getTexture(EndPortalBlockEntityRenderer.SKY_TEXTURE).getGlTextureView(), textureManager.getTexture(EndPortalBlockEntityRenderer.PORTAL_TEXTURE).getGlTextureView());
                context.fill(RenderPipelines.END_PORTAL, textureSetup, 0, 0, this.width, this.height);
            }
        }
    }

    private Sprite getBackgroundSprite() {
        if (this.backgroundSprite != null) {
            return this.backgroundSprite;
        }
        this.backgroundSprite = this.client.getBlockRenderManager().getModels().getModelParticleSprite(Blocks.NETHER_PORTAL.getDefaultState());
        return this.backgroundSprite;
    }

    @Override
    public void tick() {
        if (this.shouldClose.getAsBoolean() || Util.getMeasuringTimeMs() > this.loadStartTime + 30000L) {
            this.close();
        }
    }

    @Override
    public void close() {
        this.client.getNarratorManager().narrateSystemImmediately(Text.translatable("narrator.ready_to_play"));
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class WorldEntryReason
    extends Enum<WorldEntryReason> {
        final static public WorldEntryReason NETHER_PORTAL = new WorldEntryReason();
        final static public WorldEntryReason END_PORTAL = new WorldEntryReason();
        final static public WorldEntryReason OTHER = new WorldEntryReason();
        final static private WorldEntryReason[] field_51490;

        public static WorldEntryReason[] values() {
            return (WorldEntryReason[])field_51490.clone();
        }

        public static WorldEntryReason valueOf(String string) {
            return Enum.valueOf(WorldEntryReason.class, string);
        }

        private static WorldEntryReason[] method_59839() {
            return new WorldEntryReason[]{NETHER_PORTAL, END_PORTAL, OTHER};
        }

        static {
            field_51490 = WorldEntryReason.method_59839();
        }
    }
}

