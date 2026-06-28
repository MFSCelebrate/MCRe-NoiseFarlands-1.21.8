/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BookModel
extends Model {
    final static private String LEFT_PAGES = "left_pages";
    final static private String RIGHT_PAGES = "right_pages";
    final static private String FLIP_PAGE1 = "flip_page1";
    final static private String FLIP_PAGE2 = "flip_page2";
    final private ModelPart leftCover;
    final private ModelPart rightCover;
    final private ModelPart leftPages;
    final private ModelPart rightPages;
    final private ModelPart leftFlippingPage;
    final private ModelPart rightFlippingPage;

    public BookModel(ModelPart root) {
        super(root, RenderLayer::getEntitySolid);
        this.leftCover = root.getChild(EntityModelPartNames.LEFT_LID);
        this.rightCover = root.getChild(EntityModelPartNames.RIGHT_LID);
        this.leftPages = root.getChild(LEFT_PAGES);
        this.rightPages = root.getChild(RIGHT_PAGES);
        this.leftFlippingPage = root.getChild(FLIP_PAGE1);
        this.rightFlippingPage = root.getChild(FLIP_PAGE2);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.LEFT_LID, ModelPartBuilder.create().uv(0, 0).cuboid(-6.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f), ModelTransform.origin(0.0f, 0.0f, -1.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_LID, ModelPartBuilder.create().uv(16, 0).cuboid(0.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f), ModelTransform.origin(0.0f, 0.0f, 1.0f));
        modelPartData.addChild("seam", ModelPartBuilder.create().uv(12, 0).cuboid(-1.0f, -5.0f, 0.0f, 2.0f, 10.0f, 0.005f), ModelTransform.rotation(0.0f, 1.5707964f, 0.0f));
        modelPartData.addChild(LEFT_PAGES, ModelPartBuilder.create().uv(0, 10).cuboid(0.0f, -4.0f, -0.99f, 5.0f, 8.0f, 1.0f), ModelTransform.NONE);
        modelPartData.addChild(RIGHT_PAGES, ModelPartBuilder.create().uv(12, 10).cuboid(0.0f, -4.0f, -0.01f, 5.0f, 8.0f, 1.0f), ModelTransform.NONE);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(24, 10).cuboid(0.0f, -4.0f, 0.0f, 5.0f, 8.0f, 0.005f);
        modelPartData.addChild(FLIP_PAGE1, modelPartBuilder, ModelTransform.NONE);
        modelPartData.addChild(FLIP_PAGE2, modelPartBuilder, ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    public void setPageAngles(float pageTurnAmount, float leftFlipAmount, float rightFlipAmount, float pageTurnSpeed) {
        float f = (MathHelper.sin(pageTurnAmount * 0.02f) * 0.1f + 1.25f) * pageTurnSpeed;
        this.leftCover.yaw = (float)Math.PI + f;
        this.rightCover.yaw = -f;
        this.leftPages.yaw = f;
        this.rightPages.yaw = -f;
        this.leftFlippingPage.yaw = f - f * 2.0f * leftFlipAmount;
        this.rightFlippingPage.yaw = f - f * 2.0f * rightFlipAmount;
        this.leftPages.originX = MathHelper.sin(f);
        this.rightPages.originX = MathHelper.sin(f);
        this.leftFlippingPage.originX = MathHelper.sin(f);
        this.rightFlippingPage.originX = MathHelper.sin(f);
    }
}

