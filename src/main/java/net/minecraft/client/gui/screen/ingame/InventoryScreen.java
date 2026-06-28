/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.client.gui.screen.recipebook.AbstractCraftingRecipeBookWidget;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class InventoryScreen
extends RecipeBookScreen<PlayerScreenHandler> {
    private float mouseX;
    private float mouseY;
    private boolean mouseDown;
    final private StatusEffectsDisplay statusEffectsDisplay;

    public InventoryScreen(PlayerEntity player) {
        super(player.playerScreenHandler, new AbstractCraftingRecipeBookWidget(player.playerScreenHandler), player.getInventory(), Text.translatable("container.crafting"));
        this.titleX = 97;
        this.statusEffectsDisplay = new StatusEffectsDisplay(this);
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        if (this.client.player.isInCreativeMode()) {
            this.client.setScreen(new CreativeInventoryScreen(this.client.player, this.client.player.networkHandler.getEnabledFeatures(), this.client.options.getOperatorItemsTab().getValue()));
        }
    }

    @Override
    protected void init() {
        if (this.client.player.isInCreativeMode()) {
            this.client.setScreen(new CreativeInventoryScreen(this.client.player, this.client.player.networkHandler.getEnabledFeatures(), this.client.options.getOperatorItemsTab().getValue()));
            return;
        }
        super.init();
    }

    @Override
    protected ScreenPos getRecipeBookButtonPos() {
        return new ScreenPos(this.x + 104, this.height / 2 - 22);
    }

    @Override
    protected void onRecipeBookToggled() {
        this.mouseDown = true;
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, Colors.DARK_GRAY, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.statusEffectsDisplay.drawStatusEffects(context, mouseX, mouseY);
        super.render(context, mouseX, mouseY, deltaTicks);
        this.statusEffectsDisplay.drawStatusEffectTooltip(context, mouseX, mouseY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    @Override
    public boolean showsStatusEffects() {
        return this.statusEffectsDisplay.shouldHideStatusEffectHud();
    }

    @Override
    protected boolean shouldAddPaddingToGhostResult() {
        return false;
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = this.x;
        int j = this.y;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, i, j, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);
        InventoryScreen.drawEntity(context, i + 26, j + 8, i + 75, j + 78, 30, 0.0625f, this.mouseX, this.mouseY, (LivingEntity)this.client.player);
    }

    public static void drawEntity(DrawContext context, int x1, int y1, int x2, int y2, int size, float scale, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float)(x1 + x2) / 2.0f;
        float g = (float)(y1 + y2) / 2.0f;
        context.enableScissor(x1, y1, x2, y2);
        float h = (float)Math.atan((f - mouseX) / 40.0f);
        float i = (float)Math.atan((g - mouseY) / 40.0f);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float)Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(i * 20.0f * ((float)Math.PI / 180));
        quaternionf.mul((Quaternionfc)quaternionf2);
        float j = entity.bodyYaw;
        float k = entity.getYaw();
        float l = entity.getPitch();
        float m = entity.lastHeadYaw;
        float n = entity.headYaw;
        entity.bodyYaw = 180.0f + h * 20.0f;
        entity.setYaw(180.0f + h * 40.0f);
        entity.setPitch(-i * 20.0f);
        entity.headYaw = entity.getYaw();
        entity.lastHeadYaw = entity.getYaw();
        float o = entity.getScale();
        Vector3f vector3f = new Vector3f(0.0f, entity.getHeight() / 2.0f + scale * o, 0.0f);
        float p = (float)size / o;
        InventoryScreen.drawEntity(context, x1, y1, x2, y2, p, vector3f, quaternionf, quaternionf2, entity);
        entity.bodyYaw = j;
        entity.setYaw(k);
        entity.setPitch(l);
        entity.lastHeadYaw = m;
        entity.headYaw = n;
        context.disableScissor();
    }

    public static void drawEntity(DrawContext drawer, int x1, int y1, int x2, int y2, float scale, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, LivingEntity entity) {
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        EntityRenderer<?, LivingEntity> entityRenderer = entityRenderDispatcher.getRenderer(entity);
        LivingEntity entityRenderState = entityRenderer.getAndUpdateRenderState(entity, 1.0f);
        ((EntityRenderState)((Object)entityRenderState)).hitbox = null;
        drawer.addEntity((EntityRenderState)((Object)entityRenderState), scale, translation, rotation, overrideCameraAngle, x1, y1, x2, y2);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.mouseDown) {
            this.mouseDown = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}

