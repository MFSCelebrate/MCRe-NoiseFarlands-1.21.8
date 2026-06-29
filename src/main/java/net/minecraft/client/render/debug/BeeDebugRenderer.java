/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.PathfindingDebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.custom.DebugBeeCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugHiveCustomPayload;
import net.minecraft.util.NameGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BeeDebugRenderer
implements DebugRenderer.Renderer {
    final static private boolean field_32841 = true;
    final static private boolean field_32842 = true;
    final static private boolean field_32843 = true;
    final static private boolean field_32844 = true;
    final static private boolean field_32845 = true;
    final static private boolean field_32846 = false;
    final static private boolean field_32847 = true;
    final static private boolean field_32848 = true;
    final static private boolean field_32849 = true;
    final static private boolean field_32850 = true;
    final static private boolean field_32851 = true;
    final static private boolean field_32852 = true;
    final static private boolean field_32853 = true;
    final static private boolean field_32854 = true;
    final static private int HIVE_RANGE = 30;
    final static private int BEE_RANGE = 30;
    final static private int TARGET_ENTITY_RANGE = 8;
    final static private int field_32858 = 20;
    final static private float DEFAULT_DRAWN_STRING_SIZE = 0.02f;
    final static private int ORANGE = -23296;
    final static private int GRAY = -3355444;
    final static private int PINK = -98404;
    final private MinecraftClient client;
    final private Map<BlockPos, Hive> hives = new HashMap<BlockPos, Hive>();
    final private Map<UUID, DebugBeeCustomPayload.Bee> bees = new HashMap<UUID, DebugBeeCustomPayload.Bee>();
    @Nullable
    private UUID targetedEntity;

    public BeeDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void clear() {
        this.hives.clear();
        this.bees.clear();
        this.targetedEntity = null;
    }

    public void addHive(DebugHiveCustomPayload.HiveInfo hive, long time) {
        this.hives.put(hive.pos(), new Hive(hive, time));
    }

    public void addBee(DebugBeeCustomPayload.Bee bee) {
        this.bees.put(bee.uuid(), bee);
    }

    public void removeBee(int id) {
        this.bees.values().removeIf(bee -> bee.entityId() == id);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        this.removeOutdatedHives();
        this.removeInvalidBees();
        this.render(matrices, vertexConsumers);
        if (!this.client.player.isSpectator()) {
            this.updateTargetedEntity();
        }
    }

    private void removeInvalidBees() {
        this.bees.entrySet().removeIf(bee -> this.client.world.getEntityById(((DebugBeeCustomPayload.Bee)bee.getValue()).entityId()) == null);
    }

    private void removeOutdatedHives() {
        long l = this.client.world.getTime() - 20L;
        this.hives.entrySet().removeIf(hive -> ((Hive)hive.getValue()).lastSeen() < l);
    }

    private void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        BlockPos blockPos = this.getCameraPos().getBlockPos();
        this.bees.values().forEach(bee -> {
            if (this.isInRange((DebugBeeCustomPayload.Bee)bee)) {
                this.drawBee(matrices, vertexConsumers, (DebugBeeCustomPayload.Bee)bee);
            }
        });
        this.drawFlowers(matrices, vertexConsumers);
        for (BlockPos blockPos2 : this.hives.keySet()) {
            if (!blockPos.isWithinDistance(blockPos2, 30.0)) continue;
            BeeDebugRenderer.drawHive(matrices, vertexConsumers, blockPos2);
        }
        Map<BlockPos, Set<UUID>> map = this.getBlacklistingBees();
        this.hives.values().forEach(hive -> {
            if (blockPos.isWithinDistance(hive.info.pos(), 30.0)) {
                Set set = (Set)map.get(hive.info.pos());
                this.drawHiveInfo(matrices, vertexConsumers, hive.info, set == null ? Sets.newHashSet() : set);
            }
        });
        this.getBeesByHive().forEach((hive, bees) -> {
            if (blockPos.isWithinDistance((Vec3i)hive, 30.0)) {
                this.drawHiveBees(matrices, vertexConsumers, (BlockPos)hive, (List<String>)bees);
            }
        });
    }

    private Map<BlockPos, Set<UUID>> getBlacklistingBees() {
        HashMap map = Maps.newHashMap();
        this.bees.values().forEach(bee -> bee.disallowedHives().forEach(pos -> map.computeIfAbsent(pos, pos2 -> Sets.newHashSet()).add(bee.uuid())));
        return map;
    }

    private void drawFlowers(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        HashMap map = Maps.newHashMap();
        this.bees.values().forEach(bee -> {
            if (bee.flowerPos() != null) {
                map.computeIfAbsent(bee.flowerPos(), flower -> new HashSet()).add(bee.uuid());
            }
        });
        map.forEach((flowerPos, bees) -> {
            Set set = bees.stream().map(NameGenerator::name).collect(Collectors.toSet());
            int i = 1;
            BeeDebugRenderer.drawString(matrices, vertexConsumers, set.toString(), flowerPos, i++, -256);
            BeeDebugRenderer.drawString(matrices, vertexConsumers, "Flower", flowerPos, i++, -1);
            float f = 0.05f;
            DebugRenderer.drawBox(matrices, vertexConsumers, flowerPos, 0.05f, 0.8f, 0.8f, 0.0f, 0.3f);
        });
    }

    private static String toString(Collection<UUID> bees) {
        if (bees.isEmpty()) {
            return "-";
        }
        if (bees.size() > 3) {
            return bees.size() + " bees";
        }
        return bees.stream().map(NameGenerator::name).collect(Collectors.toSet()).toString();
    }

    private static void drawHive(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos) {
        float f = 0.05f;
        DebugRenderer.drawBox(matrices, vertexConsumers, pos, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }

    private void drawHiveBees(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos, List<String> bees) {
        float f = 0.05f;
        DebugRenderer.drawBox(matrices, vertexConsumers, pos, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
        BeeDebugRenderer.drawString(matrices, vertexConsumers, String.valueOf(bees), pos, 0, -256);
        BeeDebugRenderer.drawString(matrices, vertexConsumers, "Ghost Hive", pos, 1, -65536);
    }

    private void drawHiveInfo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, DebugHiveCustomPayload.HiveInfo hive, Collection<UUID> blacklistingBees) {
        int i = 0;
        if (!blacklistingBees.isEmpty()) {
            BeeDebugRenderer.drawString(matrices, vertexConsumers, "Blacklisted by " + BeeDebugRenderer.toString(blacklistingBees), hive, i++, -65536);
        }
        BeeDebugRenderer.drawString(matrices, vertexConsumers, "Out: " + BeeDebugRenderer.toString(this.getBeesForHive(hive.pos())), hive, i++, -3355444);
        if (hive.occupantCount() == 0) {
            BeeDebugRenderer.drawString(matrices, vertexConsumers, "In: -", hive, i++, -256);
        } else if (hive.occupantCount() == 1) {
            BeeDebugRenderer.drawString(matrices, vertexConsumers, "In: 1 bee", hive, i++, -256);
        } else {
            BeeDebugRenderer.drawString(matrices, vertexConsumers, "In: " + hive.occupantCount() + " bees", hive, i++, -256);
        }
        BeeDebugRenderer.drawString(matrices, vertexConsumers, "Honey: " + hive.honeyLevel(), hive, i++, -23296);
        BeeDebugRenderer.drawString(matrices, vertexConsumers, hive.hiveType() + (hive.sedated() ? " (sedated)" : ""), hive, i++, -1);
    }

    private void drawPath(MatrixStack matrices, VertexConsumerProvider vertexConsumers, DebugBeeCustomPayload.Bee bee) {
        if (bee.path() != null) {
            PathfindingDebugRenderer.drawPath(matrices, vertexConsumers, bee.path(), 0.5f, false, false, this.getCameraPos().getPos().getX(), this.getCameraPos().getPos().getY(), this.getCameraPos().getPos().getZ());
        }
    }

    private void drawBee(MatrixStack matrices, VertexConsumerProvider vertexConsumers, DebugBeeCustomPayload.Bee bee) {
        boolean bl = this.isTargeted(bee);
        int i = 0;
        BeeDebugRenderer.drawString(matrices, vertexConsumers, bee.pos(), i++, bee.toString(), -1, 0.03f);
        if (bee.hivePos() == null) {
            BeeDebugRenderer.drawString(matrices, vertexConsumers, bee.pos(), i++, "No hive", -98404, 0.02f);
        } else {
            BeeDebugRenderer.drawString(matrices, vertexConsumers, bee.pos(), i++, "Hive: " + this.getPositionString(bee, bee.hivePos()), -256, 0.02f);
        }
        if (bee.flowerPos() == null) {
            BeeDebugRenderer.drawString(matrices, vertexConsumers, bee.pos(), i++, "No flower", -98404, 0.02f);
        } else {
            BeeDebugRenderer.drawString(matrices, vertexConsumers, bee.pos(), i++, "Flower: " + this.getPositionString(bee, bee.flowerPos()), -256, 0.02f);
        }
        for (String string : bee.goals()) {
            BeeDebugRenderer.drawString(matrices, vertexConsumers, bee.pos(), i++, string, -16711936, 0.02f);
        }
        if (bl) {
            this.drawPath(matrices, vertexConsumers, bee);
        }
        if (bee.travelTicks() > 0) {
            int j = bee.travelTicks() < 2400 ? -3355444 : -23296;
            BeeDebugRenderer.drawString(matrices, vertexConsumers, bee.pos(), i++, "Travelling: " + bee.travelTicks() + " ticks", j, 0.02f);
        }
    }

    private static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, DebugHiveCustomPayload.HiveInfo hive, int line, int color) {
        BeeDebugRenderer.drawString(matrices, vertexConsumers, string, hive.pos(), line, color);
    }

    private static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, BlockPos pos, int line, int color) {
        double d = 1.3;
        double e = 0.2;
        double f = (double)pos.getX() + 0.5;
        double g = (double)pos.getY() + 1.3 + (double)line * 0.2;
        double h = (double)pos.getZ() + 0.5;
        DebugRenderer.drawString(matrices, vertexConsumers, string, f, g, h, color, 0.02f, true, 0.0f, true);
    }

    private static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Position pos, int line, String string, int color, float size) {
        double d = 2.4;
        double e = 0.25;
        BlockPos blockPos = BlockPos.ofFloored(pos);
        double f = (double)blockPos.getX() + 0.5;
        double g = pos.getY() + 2.4 + (double)line * 0.25;
        double h = (double)blockPos.getZ() + 0.5;
        float i = 0.5f;
        DebugRenderer.drawString(matrices, vertexConsumers, string, f, g, h, color, size, false, 0.5f, true);
    }

    private Camera getCameraPos() {
        return this.client.gameRenderer.getCamera();
    }

    private Set<String> getBeeNamesForHive(DebugHiveCustomPayload.HiveInfo hive) {
        return this.getBeesForHive(hive.pos()).stream().map(NameGenerator::name).collect(Collectors.toSet());
    }

    private String getPositionString(DebugBeeCustomPayload.Bee bee, BlockPos pos) {
        double d = Math.sqrt(pos.getSquaredDistance(bee.pos()));
        double e = (double)Math.round(d * 10.0) / 10.0;
        return pos.toShortString() + " (dist " + e + ")";
    }

    private boolean isTargeted(DebugBeeCustomPayload.Bee bee) {
        return Objects.equals(this.targetedEntity, bee.uuid());
    }

    private boolean isInRange(DebugBeeCustomPayload.Bee bee) {
        ClientPlayerEntity playerEntity = this.client.player;
        BlockPos blockPos = BlockPos.ofFloored(playerEntity.getX(), bee.pos().getY(), playerEntity.getZ());
        BlockPos blockPos2 = BlockPos.ofFloored(bee.pos());
        return blockPos.isWithinDistance(blockPos2, 30.0);
    }

    private Collection<UUID> getBeesForHive(BlockPos hivePos) {
        return this.bees.values().stream().filter(bee -> bee.isHiveAt(hivePos)).map(DebugBeeCustomPayload.Bee::uuid).collect(Collectors.toSet());
    }

    private Map<BlockPos, List<String>> getBeesByHive() {
        HashMap map = Maps.newHashMap();
        for (DebugBeeCustomPayload.Bee bee : this.bees.values()) {
            if (bee.hivePos() == null || this.hives.containsKey(bee.hivePos())) continue;
            map.computeIfAbsent(bee.hivePos(), hive -> Lists.newArrayList()).add(bee.getName());
        }
        return map;
    }

    private void updateTargetedEntity() {
        DebugRenderer.getTargetedEntity(this.client.getCameraEntity(), 8).ifPresent(entity -> {
            this.targetedEntity = entity.getUuid();
        });
    }

    @Environment(value=EnvType.CLIENT)
    static final class Hive
    extends Record {
        final DebugHiveCustomPayload.HiveInfo info;
        final private long lastSeen;

        Hive(DebugHiveCustomPayload.HiveInfo hiveInfo, long l) {
            this.info = hiveInfo;
            this.lastSeen = l;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Hive.class, "info;lastSeen", "info", "lastSeen"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Hive.class, "info;lastSeen", "info", "lastSeen"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Hive.class, "info;lastSeen", "info", "lastSeen"}, this, object);
        }

        public DebugHiveCustomPayload.HiveInfo info() {
            return this.info;
        }

        public long lastSeen() {
            return this.lastSeen;
        }
    }
}

