/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class WorldLoadingState {
    final private ClientPlayerEntity player;
    final private ClientWorld world;
    final private WorldRenderer renderer;
    private Step currentStep = Step.WAITING_FOR_SERVER;

    public WorldLoadingState(ClientPlayerEntity player, ClientWorld world, WorldRenderer renderer) {
        this.player = player;
        this.world = world;
        this.renderer = renderer;
    }

    public void tick() {
        switch (this.currentStep.ordinal()) {
            case 0: 
            case 2: {
                break;
            }
            case 1: {
                BlockPos blockPos = this.player.getBlockPos();
                boolean bl = this.world.isOutOfHeightLimit(blockPos.getY());
                if (!bl && !this.renderer.isRenderingReady(blockPos) && !this.player.isSpectator() && this.player.isAlive()) break;
                this.currentStep = Step.LEVEL_READY;
            }
        }
    }

    public boolean isReady() {
        return this.currentStep == Step.LEVEL_READY;
    }

    public void handleChunksComingPacket() {
        if (this.currentStep == Step.WAITING_FOR_SERVER) {
            this.currentStep = Step.WAITING_FOR_PLAYER_CHUNK;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Step
    extends Enum<Step> {
        final static public Step WAITING_FOR_SERVER = new Step();
        final static public Step WAITING_FOR_PLAYER_CHUNK = new Step();
        final static public Step LEVEL_READY = new Step();
        final static private Step[] field_46587;

        public static Step[] values() {
            return (Step[])field_46587.clone();
        }

        public static Step valueOf(String string) {
            return Enum.valueOf(Step.class, string);
        }

        private static Step[] method_54138() {
            return new Step[]{WAITING_FOR_SERVER, WAITING_FOR_PLAYER_CHUNK, LEVEL_READY};
        }

        static {
            field_46587 = Step.method_54138();
        }
    }
}

