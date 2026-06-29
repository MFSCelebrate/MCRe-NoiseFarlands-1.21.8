/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Activity {
    final static public Activity CORE = Activity.register("core");
    final static public Activity IDLE = Activity.register("idle");
    final static public Activity WORK = Activity.register("work");
    final static public Activity PLAY = Activity.register("play");
    final static public Activity REST = Activity.register("rest");
    final static public Activity MEET = Activity.register("meet");
    final static public Activity PANIC = Activity.register("panic");
    final static public Activity RAID = Activity.register("raid");
    final static public Activity PRE_RAID = Activity.register("pre_raid");
    final static public Activity HIDE = Activity.register("hide");
    final static public Activity FIGHT = Activity.register("fight");
    final static public Activity CELEBRATE = Activity.register("celebrate");
    final static public Activity ADMIRE_ITEM = Activity.register("admire_item");
    final static public Activity AVOID = Activity.register("avoid");
    final static public Activity RIDE = Activity.register("ride");
    final static public Activity PLAY_DEAD = Activity.register("play_dead");
    final static public Activity LONG_JUMP = Activity.register("long_jump");
    final static public Activity RAM = Activity.register("ram");
    final static public Activity TONGUE = Activity.register("tongue");
    final static public Activity SWIM = Activity.register("swim");
    final static public Activity LAY_SPAWN = Activity.register("lay_spawn");
    final static public Activity SNIFF = Activity.register("sniff");
    final static public Activity INVESTIGATE = Activity.register("investigate");
    final static public Activity ROAR = Activity.register("roar");
    final static public Activity EMERGE = Activity.register("emerge");
    final static public Activity DIG = Activity.register("dig");
    final private String id;
    final private int hashCode;

    public Activity(String id) {
        this.id = id;
        this.hashCode = id.hashCode();
    }

    public String getId() {
        return this.id;
    }

    private static Activity register(String id) {
        return Registry.register(Registries.ACTIVITY, id, new Activity(id));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Activity activity = (Activity)o;
        return this.id.equals(activity.id);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        return this.getId();
    }
}

