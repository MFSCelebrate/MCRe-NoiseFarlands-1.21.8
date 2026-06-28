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
import net.minecraft.client.render.entity.AbstractHorseEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.DonkeyEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseSaddleEntityModel;
import net.minecraft.client.render.entity.state.DonkeyEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class AbstractDonkeyEntityRenderer<T extends AbstractDonkeyEntity>
extends AbstractHorseEntityRenderer<T, DonkeyEntityRenderState, DonkeyEntityModel> {
    final private Identifier texture;

    public AbstractDonkeyEntityRenderer(EntityRendererFactory.Context context, Type type) {
        super(context, new DonkeyEntityModel(context.getPart(type.adultModelLayer)), new DonkeyEntityModel(context.getPart(type.babyModelLayer)));
        this.texture = type.texture;
        this.addFeature(new SaddleFeatureRenderer<DonkeyEntityRenderState, DonkeyEntityModel, HorseSaddleEntityModel>(this, context.getEquipmentRenderer(), type.saddleLayerType, donkeyEntityRenderState -> donkeyEntityRenderState.saddleStack, new HorseSaddleEntityModel(context.getPart(type.adultSaddleModelLayer)), new HorseSaddleEntityModel(context.getPart(type.babySaddleModelLayer))));
    }

    @Override
    public Identifier getTexture(DonkeyEntityRenderState donkeyEntityRenderState) {
        return this.texture;
    }

    @Override
    public DonkeyEntityRenderState net_minecraft_client_render_entity_state_DonkeyEntityRenderState_createRenderState() {
        return new DonkeyEntityRenderState();
    }

    @Override
    public void updateRenderState(T abstractDonkeyEntity, DonkeyEntityRenderState donkeyEntityRenderState, float f) {
        super.updateRenderState(abstractDonkeyEntity, donkeyEntityRenderState, f);
        donkeyEntityRenderState.hasChest = ((AbstractDonkeyEntity)abstractDonkeyEntity).hasChest();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((DonkeyEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_DonkeyEntityRenderState_createRenderState();
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type> {
        final static public Type DONKEY = new Type(Identifier.ofVanilla("textures/entity/horse/donkey.png"), EntityModelLayers.DONKEY, EntityModelLayers.DONKEY_BABY, EquipmentModel.LayerType.DONKEY_SADDLE, EntityModelLayers.DONKEY_SADDLE, EntityModelLayers.DONKEY_BABY_SADDLE);
        final static public Type MULE = new Type(Identifier.ofVanilla("textures/entity/horse/mule.png"), EntityModelLayers.MULE, EntityModelLayers.MULE_BABY, EquipmentModel.LayerType.MULE_SADDLE, EntityModelLayers.MULE_SADDLE, EntityModelLayers.MULE_BABY_SADDLE);
        final Identifier texture;
        final EntityModelLayer adultModelLayer;
        final EntityModelLayer babyModelLayer;
        final EquipmentModel.LayerType saddleLayerType;
        final EntityModelLayer adultSaddleModelLayer;
        final EntityModelLayer babySaddleModelLayer;
        final static private Type[] field_56103;

        public static Type[] values() {
            return (Type[])field_56103.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(Identifier texture, EntityModelLayer adultModelLayer, EntityModelLayer babyModelLayer, EquipmentModel.LayerType saddleLayerType, EntityModelLayer adultSaddleModelLayer, EntityModelLayer babySaddleModelLayer) {
            this.texture = texture;
            this.adultModelLayer = adultModelLayer;
            this.babyModelLayer = babyModelLayer;
            this.saddleLayerType = saddleLayerType;
            this.adultSaddleModelLayer = adultSaddleModelLayer;
            this.babySaddleModelLayer = babySaddleModelLayer;
        }

        private static Type[] method_66849() {
            return new Type[]{DONKEY, MULE};
        }

        static {
            field_56103 = Type.method_66849();
        }
    }
}

