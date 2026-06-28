/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinWidget
extends ClickableWidget {
    final static private float field_45997 = 2.125f;
    final static private float field_59833 = 0.97f;
    final static private float field_45999 = 2.5f;
    final static private float field_46000 = -5.0f;
    final static private float field_46001 = 30.0f;
    final static private float field_46002 = 50.0f;
    final private PlayerEntityModel wideModel;
    final private PlayerEntityModel slimModel;
    final private Supplier<SkinTextures> skinSupplier;
    private float xRotation = -5.0f;
    private float yRotation = 30.0f;

    public PlayerSkinWidget(int width, int height, LoadedEntityModels entityModels, Supplier<SkinTextures> skinSupplier) {
        super(0, 0, width, height, ScreenTexts.EMPTY);
        this.wideModel = new PlayerEntityModel(entityModels.getModelPart(EntityModelLayers.PLAYER), false);
        this.slimModel = new PlayerEntityModel(entityModels.getModelPart(EntityModelLayers.PLAYER_SLIM), true);
        this.skinSupplier = skinSupplier;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        float f = 0.97f * (float)this.getHeight() / 2.125f;
        float g = -1.0625f;
        SkinTextures skinTextures = this.skinSupplier.get();
        PlayerEntityModel playerEntityModel = skinTextures.model() == SkinTextures.Model.SLIM ? this.slimModel : this.wideModel;
        context.addPlayerSkin(playerEntityModel, skinTextures.texture(), f, this.xRotation, this.yRotation, -1.0625f, this.getX(), this.getY(), this.getRight(), this.getBottom());
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        this.xRotation = MathHelper.clamp(this.xRotation - (float)deltaY * 2.5f, -50.0f, 50.0f);
        this.yRotation += (float)deltaX * 2.5f;
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public boolean isNarratable() {
        return false;
    }

    @Override
    @Nullable
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        return null;
    }
}

