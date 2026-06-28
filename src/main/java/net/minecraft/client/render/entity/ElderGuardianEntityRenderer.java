/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.GuardianEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ElderGuardianEntityRenderer
extends GuardianEntityRenderer {
    final static public Identifier TEXTURE = Identifier.ofVanilla("textures/entity/guardian_elder.png");

    public ElderGuardianEntityRenderer(EntityRendererFactory.Context context) {
        super(context, 1.2f, EntityModelLayers.ELDER_GUARDIAN);
    }

    @Override
    public Identifier getTexture(GuardianEntityRenderState guardianEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((GuardianEntityRenderState)state);
    }
}

