/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BellBlockModel
extends Model {
    final static private String BELL_BODY = "bell_body";
    final private ModelPart bellBody;

    public BellBlockModel(ModelPart root) {
        super(root, RenderLayer::getEntitySolid);
        this.bellBody = root.getChild(BELL_BODY);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(BELL_BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -6.0f, -3.0f, 6.0f, 7.0f, 6.0f), ModelTransform.origin(8.0f, 12.0f, 8.0f));
        modelPartData2.addChild("bell_base", ModelPartBuilder.create().uv(0, 13).cuboid(4.0f, 4.0f, 4.0f, 8.0f, 2.0f, 8.0f), ModelTransform.origin(-8.0f, -12.0f, -8.0f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    public void update(BellBlockEntity blockEntity, float tickProgress) {
        float f = (float)blockEntity.ringTicks + tickProgress;
        float g = 0.0f;
        float h = 0.0f;
        if (blockEntity.ringing) {
            float i = MathHelper.sin(f / (float)Math.PI) / (4.0f + f / 3.0f);
            if (blockEntity.lastSideHit == Direction.NORTH) {
                g = -i;
            } else if (blockEntity.lastSideHit == Direction.SOUTH) {
                g = i;
            } else if (blockEntity.lastSideHit == Direction.EAST) {
                h = -i;
            } else if (blockEntity.lastSideHit == Direction.WEST) {
                h = i;
            }
        }
        this.bellBody.pitch = g;
        this.bellBody.roll = h;
    }
}

