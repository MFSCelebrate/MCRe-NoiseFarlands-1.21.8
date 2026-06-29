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
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CaveSpiderEntityRenderer
extends SpiderEntityRenderer<CaveSpiderEntity> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/spider/cave_spider.png");

    public CaveSpiderEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.CAVE_SPIDER);
        this.shadowRadius = 0.56f;
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return TEXTURE;
    }
}

