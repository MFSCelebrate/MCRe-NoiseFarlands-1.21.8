/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain;

public class ScheduleRuleEntry {
    final private int startTime;
    final private float priority;

    public ScheduleRuleEntry(int startTime, float priority) {
        this.startTime = startTime;
        this.priority = priority;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public float getPriority() {
        return this.priority;
    }
}

