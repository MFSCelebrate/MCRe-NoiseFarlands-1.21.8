/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap
 */
package net.minecraft.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.ai.brain.ScheduleRuleEntry;

public class ScheduleRule {
    final private List<ScheduleRuleEntry> entries = Lists.newArrayList();
    private int prioritizedEntryIndex;

    public ImmutableList<ScheduleRuleEntry> getEntries() {
        return ImmutableList.copyOf(this.entries);
    }

    public ScheduleRule add(int startTime, float priority) {
        this.entries.add(new ScheduleRuleEntry(startTime, priority));
        this.sort();
        return this;
    }

    public ScheduleRule add(Collection<ScheduleRuleEntry> entries) {
        this.entries.addAll(entries);
        this.sort();
        return this;
    }

    private void sort() {
        Int2ObjectAVLTreeMap int2ObjectSortedMap = new Int2ObjectAVLTreeMap();
        this.entries.forEach(arg_0 -> ScheduleRule.method_19228((Int2ObjectSortedMap)int2ObjectSortedMap, arg_0));
        this.entries.clear();
        this.entries.addAll((Collection<ScheduleRuleEntry>)int2ObjectSortedMap.values());
        this.prioritizedEntryIndex = 0;
    }

    public float getPriority(int time) {
        ScheduleRuleEntry scheduleRuleEntry3;
        if (this.entries.size() <= 0) {
            return 0.0f;
        }
        ScheduleRuleEntry scheduleRuleEntry = this.entries.get(this.prioritizedEntryIndex);
        ScheduleRuleEntry scheduleRuleEntry2 = this.entries.get(this.entries.size() - 1);
        boolean bl = time < scheduleRuleEntry.getStartTime();
        int i = bl ? 0 : this.prioritizedEntryIndex;
        float f = bl ? scheduleRuleEntry2.getPriority() : scheduleRuleEntry.getPriority();
        int j = i;
        while (j < this.entries.size() && (scheduleRuleEntry3 = this.entries.get(j)).getStartTime() <= time) {
            this.prioritizedEntryIndex = j++;
            f = scheduleRuleEntry3.getPriority();
        }
        return f;
    }

    private static void method_19228(Int2ObjectSortedMap entry, ScheduleRuleEntry scheduleRuleEntry) {
        entry.put(scheduleRuleEntry.getStartTime(), (Object)scheduleRuleEntry);
    }
}

