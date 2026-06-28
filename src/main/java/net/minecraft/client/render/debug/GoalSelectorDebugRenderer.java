/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.custom.DebugGoalSelectorCustomPayload;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class GoalSelectorDebugRenderer
implements DebugRenderer.Renderer {
    final static private int RANGE = 160;
    final private MinecraftClient client;
    final private Int2ObjectMap<Entity> goalSelectors = new Int2ObjectOpenHashMap();

    @Override
    public void clear() {
        this.goalSelectors.clear();
    }

    public void setGoalSelectorList(int index, BlockPos pos, List<DebugGoalSelectorCustomPayload.Goal> goals) {
        this.goalSelectors.put(index, (Object)new Entity(pos, goals));
    }

    public void removeGoalSelectorList(int index) {
        this.goalSelectors.remove(index);
    }

    public GoalSelectorDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        Camera camera = this.client.gameRenderer.getCamera();
        BlockPos blockPos = BlockPos.ofFloored(camera.getPos().x, 0.0, camera.getPos().z);
        for (Entity entity : this.goalSelectors.values()) {
            BlockPos blockPos2 = entity.entityPos;
            if (!blockPos.isWithinDistance(blockPos2, 160.0)) continue;
            for (int i = 0; i < entity.goals.size(); ++i) {
                DebugGoalSelectorCustomPayload.Goal goal = entity.goals.get(i);
                double d = (double)blockPos2.getX() + 0.5;
                double e = (double)blockPos2.getY() + 2.0 + (double)i * 0.25;
                double f = (double)blockPos2.getZ() + 0.5;
                int j = goal.isRunning() ? Colors.GREEN : -3355444;
                DebugRenderer.drawString(matrices, vertexConsumers, goal.name(), d, e, f, j);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Entity
    extends Record {
        final BlockPos entityPos;
        final List<DebugGoalSelectorCustomPayload.Goal> goals;

        Entity(BlockPos blockPos, List<DebugGoalSelectorCustomPayload.Goal> list) {
            this.entityPos = blockPos;
            this.goals = list;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entity.class, "entityPos;goals", "entityPos", "goals"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entity.class, "entityPos;goals", "entityPos", "goals"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entity.class, "entityPos;goals", "entityPos", "goals"}, this, object);
        }

        public BlockPos entityPos() {
            return this.entityPos;
        }

        public List<DebugGoalSelectorCustomPayload.Goal> goals() {
            return this.goals;
        }
    }
}

