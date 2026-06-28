/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.renderer.v1.model.FabricBlockModels
 */
package net.minecraft.client.render.block;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockModels;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.Sprite;

@Environment(value=EnvType.CLIENT)
public class BlockModels
implements FabricBlockModels {
    private Map<BlockState, BlockStateModel> models = Map.of();
    final private BakedModelManager modelManager;

    public BlockModels(BakedModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public Sprite getModelParticleSprite(BlockState state) {
        return this.getModel(state).particleSprite();
    }

    public BlockStateModel getModel(BlockState state) {
        BlockStateModel blockStateModel = this.models.get(state);
        if (blockStateModel == null) {
            blockStateModel = this.modelManager.getMissingModel();
        }
        return blockStateModel;
    }

    public BakedModelManager getModelManager() {
        return this.modelManager;
    }

    public void setModels(Map<BlockState, BlockStateModel> models) {
        this.models = models;
    }
}

