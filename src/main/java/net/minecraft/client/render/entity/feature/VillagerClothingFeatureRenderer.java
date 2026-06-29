/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerResourceMetadata;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.VillagerDataRenderState;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

@Environment(value=EnvType.CLIENT)
public class VillagerClothingFeatureRenderer<S extends LivingEntityRenderState, M extends EntityModel<S>>
extends FeatureRenderer<S, M> {
    final static private Int2ObjectMap<Identifier> LEVEL_TO_ID = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), levelToId -> {
        levelToId.put(1, (Object)Identifier.ofVanilla("stone"));
        levelToId.put(2, (Object)Identifier.ofVanilla("iron"));
        levelToId.put(3, (Object)Identifier.ofVanilla("gold"));
        levelToId.put(4, (Object)Identifier.ofVanilla("emerald"));
        levelToId.put(5, (Object)Identifier.ofVanilla("diamond"));
    });
    final private Object2ObjectMap<RegistryKey<VillagerType>, VillagerResourceMetadata.HatType> villagerTypeToHat = new Object2ObjectOpenHashMap();
    final private Object2ObjectMap<RegistryKey<VillagerProfession>, VillagerResourceMetadata.HatType> professionToHat = new Object2ObjectOpenHashMap();
    final private ResourceManager resourceManager;
    final private String entityType;

    public VillagerClothingFeatureRenderer(FeatureRendererContext<S, M> context, ResourceManager resourceManager, String entityType) {
        super(context);
        this.resourceManager = resourceManager;
        this.entityType = entityType;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S livingEntityRenderState, float f, float g) {
        if (((LivingEntityRenderState)livingEntityRenderState).invisible) {
            return;
        }
        VillagerData villagerData = ((VillagerDataRenderState)livingEntityRenderState).getVillagerData();
        if (villagerData == null) {
            return;
        }
        RegistryEntry<VillagerType> registryEntry = villagerData.type();
        RegistryEntry<VillagerProfession> registryEntry2 = villagerData.profession();
        VillagerResourceMetadata.HatType hatType = this.getHatType(this.villagerTypeToHat, "type", registryEntry);
        VillagerResourceMetadata.HatType hatType2 = this.getHatType(this.professionToHat, "profession", registryEntry2);
        Object entityModel = this.getContextModel();
        ((ModelWithHat)entityModel).setHatVisible(hatType2 == VillagerResourceMetadata.HatType.NONE || hatType2 == VillagerResourceMetadata.HatType.PARTIAL && hatType != VillagerResourceMetadata.HatType.FULL);
        Identifier identifier = this.getTexture("type", registryEntry);
        VillagerClothingFeatureRenderer.renderModel(entityModel, identifier, matrixStack, vertexConsumerProvider, 1, livingEntityRenderState, -1);
        ((ModelWithHat)entityModel).setHatVisible(true);
        if (!registryEntry2.matchesKey(VillagerProfession.NONE) && !((LivingEntityRenderState)livingEntityRenderState).baby) {
            Identifier identifier2 = this.getTexture("profession", registryEntry2);
            VillagerClothingFeatureRenderer.renderModel(entityModel, identifier2, matrixStack, vertexConsumerProvider, 1, livingEntityRenderState, -1);
            if (!registryEntry2.matchesKey(VillagerProfession.NITWIT)) {
                Identifier identifier3 = this.getTexture("profession_level", (Identifier)LEVEL_TO_ID.get(MathHelper.clamp(villagerData.level(), 1, LEVEL_TO_ID.size())));
                VillagerClothingFeatureRenderer.renderModel(entityModel, identifier3, matrixStack, vertexConsumerProvider, 1, livingEntityRenderState, -1);
            }
        }
    }

    private Identifier getTexture(String keyType, Identifier keyId) {
        return keyId.withPath(path -> "textures/entity/" + this.entityType + "/" + keyType + "/" + path + ".png");
    }

    private Identifier getTexture(String keyType, RegistryEntry<?> entry) {
        return entry.getKey().map(key -> this.getTexture(keyType, key.getValue())).orElse(MissingSprite.getMissingSpriteId());
    }

    public <K> VillagerResourceMetadata.HatType getHatType(Object2ObjectMap<RegistryKey<K>, VillagerResourceMetadata.HatType> metadataMap, String keyType, RegistryEntry<K> entry) {
        RegistryKey registryKey = entry.getKey().orElse(null);
        if (registryKey == null) {
            return VillagerResourceMetadata.HatType.NONE;
        }
        return (VillagerResourceMetadata.HatType)metadataMap.computeIfAbsent((Object)registryKey, object -> this.resourceManager.getResource(this.getTexture(keyType, registryKey.getValue())).flatMap(resource -> {
            try {
                return resource.getMetadata().decode(VillagerResourceMetadata.SERIALIZER).map(VillagerResourceMetadata::hatType);
            }
            catch (IOException iOException) {
                return Optional.empty();
            }
        }).orElse(VillagerResourceMetadata.HatType.NONE));
    }
}

