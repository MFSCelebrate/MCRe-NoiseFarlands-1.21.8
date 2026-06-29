/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.entity.ai.brain;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.ScheduleBuilder;
import net.minecraft.entity.ai.brain.ScheduleRule;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Schedule {
    final static public int WORK_TIME = 2000;
    final static public int field_30693 = 7000;
    final static public Schedule EMPTY = Schedule.register("empty").withActivity(0, Activity.IDLE).build();
    final static public Schedule SIMPLE = Schedule.register("simple").withActivity(5000, Activity.WORK).withActivity(11000, Activity.REST).build();
    final static public Schedule VILLAGER_BABY = Schedule.register("villager_baby").withActivity(10, Activity.IDLE).withActivity(3000, Activity.PLAY).withActivity(6000, Activity.IDLE).withActivity(10000, Activity.PLAY).withActivity(12000, Activity.REST).build();
    final static public Schedule VILLAGER_DEFAULT = Schedule.register("villager_default").withActivity(10, Activity.IDLE).withActivity(2000, Activity.WORK).withActivity(9000, Activity.MEET).withActivity(11000, Activity.IDLE).withActivity(12000, Activity.REST).build();
    final private Map<Activity, ScheduleRule> scheduleRules = Maps.newHashMap();

    protected static ScheduleBuilder register(String id) {
        Schedule schedule = Registry.register(Registries.SCHEDULE, id, new Schedule());
        return new ScheduleBuilder(schedule);
    }

    protected void addActivity(Activity activity) {
        if (!this.scheduleRules.containsKey(activity)) {
            this.scheduleRules.put(activity, new ScheduleRule());
        }
    }

    protected ScheduleRule getRule(Activity activity) {
        return this.scheduleRules.get(activity);
    }

    protected List<ScheduleRule> getOtherRules(Activity activity) {
        return this.scheduleRules.entrySet().stream().filter(rule -> rule.getKey() != activity).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public Activity getActivityForTime(int time) {
        return this.scheduleRules.entrySet().stream().max(Comparator.comparingDouble(rule -> ((ScheduleRule)rule.getValue()).getPriority(time))).map(Map.Entry::getKey).orElse(Activity.IDLE);
    }
}

