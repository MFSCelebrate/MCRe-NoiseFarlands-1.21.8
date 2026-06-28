/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class Goal {
    final private EnumSet<Control> controls = EnumSet.noneOf(Control.class);

    public abstract boolean canStart();

    public boolean shouldContinue() {
        return this.canStart();
    }

    public boolean canStop() {
        return true;
    }

    public void start() {
    }

    public void stop() {
    }

    public boolean shouldRunEveryTick() {
        return false;
    }

    public void tick() {
    }

    public void setControls(EnumSet<Control> controls) {
        this.controls.clear();
        this.controls.addAll(controls);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    public EnumSet<Control> getControls() {
        return this.controls;
    }

    protected int getTickCount(int ticks) {
        return this.shouldRunEveryTick() ? ticks : Goal.toGoalTicks(ticks);
    }

    protected static int toGoalTicks(int serverTicks) {
        return MathHelper.ceilDiv(serverTicks, 2);
    }

    protected static ServerWorld getServerWorld(Entity entity) {
        return (ServerWorld)entity.net_minecraft_world_World_getWorld();
    }

    protected static ServerWorld castToServerWorld(World world) {
        return (ServerWorld)world;
    }

    public static final class Control
    extends Enum<Control> {
        final static public Control MOVE = new Control();
        final static public Control LOOK = new Control();
        final static public Control JUMP = new Control();
        final static public Control TARGET = new Control();
        final static private Control[] field_18409;

        public static Control[] values() {
            return (Control[])field_18409.clone();
        }

        public static Control valueOf(String string) {
            return Enum.valueOf(Control.class, string);
        }

        private static Control[] method_36621() {
            return new Control[]{MOVE, LOOK, JUMP, TARGET};
        }

        static {
            field_18409 = Control.method_36621();
        }
    }
}

