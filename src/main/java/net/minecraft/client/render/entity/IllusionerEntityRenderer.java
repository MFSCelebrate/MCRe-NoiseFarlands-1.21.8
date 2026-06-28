/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.IllusionerEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class IllusionerEntityRenderer
extends IllagerEntityRenderer<IllusionerEntity, IllusionerEntityRenderState> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/illusioner.png");

    public IllusionerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel(context.getPart(EntityModelLayers.ILLUSIONER)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<IllusionerEntityRenderState, IllagerEntityModel<IllusionerEntityRenderState>>(this, (FeatureRendererContext)this){

            @Override
            public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, IllusionerEntityRenderState illusionerEntityRenderState, float f, float g) {
                if (illusionerEntityRenderState.spellcasting || illusionerEntityRenderState.attacking) {
                    super.render(matrixStack, vertexConsumerProvider, 1, illusionerEntityRenderState, f, g);
                }
            }
        });
        ((IllagerEntityModel)this.model).getHat().visible = true;
    }

    @Override
    public Identifier getTexture(IllusionerEntityRenderState illusionerEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public IllusionerEntityRenderState net_minecraft_client_render_entity_state_IllusionerEntityRenderState_createRenderState() {
        return new IllusionerEntityRenderState();
    }

    @Override
    public void updateRenderState(IllusionerEntity illusionerEntity, IllusionerEntityRenderState illusionerEntityRenderState, float f) {
        super.updateRenderState(illusionerEntity, illusionerEntityRenderState, f);
        Vec3d[] vec3ds = illusionerEntity.getMirrorCopyOffsets(f);
        illusionerEntityRenderState.mirrorCopyOffsets = Arrays.copyOf(vec3ds, vec3ds.length);
        illusionerEntityRenderState.spellcasting = illusionerEntity.isSpellcasting();
    }

    @Override
    public void render(IllusionerEntityRenderState illusionerEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (illusionerEntityRenderState.invisible) {
            Vec3d[] vec3ds = illusionerEntityRenderState.mirrorCopyOffsets;
            for (int j = 0; j < vec3ds.length; ++j) {
                matrixStack.push();
                matrixStack.translate(vec3ds[j].x + (double)MathHelper.cos((float)j + illusionerEntityRenderState.age * 0.5f) * 0.025, vec3ds[j].y + (double)MathHelper.cos((float)j + illusionerEntityRenderState.age * 0.75f) * 0.0125, vec3ds[j].z + (double)MathHelper.cos((float)j + illusionerEntityRenderState.age * 0.7f) * 0.025);
                super.render(illusionerEntityRenderState, matrixStack, vertexConsumerProvider, 1);
                matrixStack.pop();
            }
        } else {
            super.render(illusionerEntityRenderState, matrixStack, vertexConsumerProvider, 1);
        }
    }

    @Override
    protected boolean isVisible(IllusionerEntityRenderState illusionerEntityRenderState) {
        return true;
    }

    @Override
    protected Box getBoundingBox(IllusionerEntity illusionerEntity) {
        return super.getBoundingBox(illusionerEntity).expand(3.0, 0.0, 3.0);
    }

    @Override
    protected boolean isVisible(LivingEntityRenderState state) {
        return this.isVisible((IllusionerEntityRenderState)state);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((IllusionerEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_IllusionerEntityRenderState_createRenderState();
    }
}

