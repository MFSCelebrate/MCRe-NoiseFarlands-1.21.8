/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class PlayerHeadModelRenderer
implements SpecialModelRenderer<Data> {
    final private Map<ProfileComponent, Data> profileCache = new HashMap<ProfileComponent, Data>();
    final private PlayerSkinProvider playerSkinProvider;
    final private SkullBlockEntityModel model;
    final private Data data;

    PlayerHeadModelRenderer(PlayerSkinProvider playerSkinProvider, SkullBlockEntityModel model, Data data) {
        this.playerSkinProvider = playerSkinProvider;
        this.model = model;
        this.data = data;
    }

    @Override
    public void render(@Nullable Data data, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, boolean bl) {
        Data data2 = Objects.requireNonNullElse(data, this.data);
        RenderLayer renderLayer = data2.layer();
        SkullBlockEntityRenderer.renderSkull(null, 180.0f, 0.0f, matrixStack, vertexConsumerProvider, i, this.model, renderLayer);
    }

    @Override
    public void collectVertices(Set<Vector3f> vertices) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.5f, 0.0f, 0.5f);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        this.model.getRootPart().collectVertices(matrixStack, vertices);
    }

    @Override
    @Nullable
    public Data net_minecraft_client_render_item_model_special_PlayerHeadModelRenderer$Data_getData(ItemStack itemStack) {
        ProfileComponent profileComponent = itemStack.get(DataComponentTypes.PROFILE);
        if (profileComponent == null) {
            return null;
        }
        Data data = this.profileCache.get(profileComponent);
        if (data != null) {
            return data;
        }
        ProfileComponent profileComponent2 = profileComponent.resolve();
        if (profileComponent2 != null) {
            return this.createData(profileComponent2);
        }
        return null;
    }

    @Nullable
    private Data createData(ProfileComponent profileComponent) {
        SkinTextures skinTextures = this.playerSkinProvider.getSkinTextures(profileComponent.gameProfile(), null);
        if (skinTextures != null) {
            Data data = Data.of(skinTextures);
            this.profileCache.put(profileComponent, data);
            return data;
        }
        return null;
    }

    @Override
    @Nullable
    public Object java_lang_Object_getData(ItemStack stack) {
        return this.net_minecraft_client_render_item_model_special_PlayerHeadModelRenderer$Data_getData(stack);
    }

    @Environment(value=EnvType.CLIENT)
    public record Data(RenderLayer layer) {
        static Data of(SkinTextures textures) {
            return new Data(SkullBlockEntityRenderer.getTranslucentRenderLayer(textures.texture()));
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Data.class, "renderType", "layer"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Data.class, "renderType", "layer"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Data.class, "renderType", "layer"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked() implements SpecialModelRenderer.Unbaked
    {
        final static public MapCodec<Unbaked> CODEC = MapCodec.unit(Unbaked::new);

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        @Nullable
        public SpecialModelRenderer<?> bake(LoadedEntityModels entityModels) {
            SkullBlockEntityModel skullBlockEntityModel = SkullBlockEntityRenderer.getModels(entityModels, SkullBlock.Type.PLAYER);
            if (skullBlockEntityModel == null) {
                return null;
            }
            return new PlayerHeadModelRenderer(MinecraftClient.getInstance().getSkinProvider(), skullBlockEntityModel, Data.of(DefaultSkinHelper.getSteve()));
        }
    }
}

