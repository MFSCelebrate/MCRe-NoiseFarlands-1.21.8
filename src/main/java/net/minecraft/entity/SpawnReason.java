/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

public final class SpawnReason
extends Enum<SpawnReason> {
    final static public SpawnReason NATURAL = new SpawnReason();
    final static public SpawnReason CHUNK_GENERATION = new SpawnReason();
    final static public SpawnReason SPAWNER = new SpawnReason();
    final static public SpawnReason STRUCTURE = new SpawnReason();
    final static public SpawnReason BREEDING = new SpawnReason();
    final static public SpawnReason MOB_SUMMONED = new SpawnReason();
    final static public SpawnReason JOCKEY = new SpawnReason();
    final static public SpawnReason EVENT = new SpawnReason();
    final static public SpawnReason CONVERSION = new SpawnReason();
    final static public SpawnReason REINFORCEMENT = new SpawnReason();
    final static public SpawnReason TRIGGERED = new SpawnReason();
    final static public SpawnReason BUCKET = new SpawnReason();
    final static public SpawnReason SPAWN_ITEM_USE = new SpawnReason();
    final static public SpawnReason COMMAND = new SpawnReason();
    final static public SpawnReason DISPENSER = new SpawnReason();
    final static public SpawnReason PATROL = new SpawnReason();
    final static public SpawnReason TRIAL_SPAWNER = new SpawnReason();
    final static public SpawnReason LOAD = new SpawnReason();
    final static public SpawnReason DIMENSION_TRAVEL = new SpawnReason();
    final static private SpawnReason[] field_16464;

    public static SpawnReason[] values() {
        return (SpawnReason[])field_16464.clone();
    }

    public static SpawnReason valueOf(String string) {
        return Enum.valueOf(SpawnReason.class, string);
    }

    public static boolean isAnySpawner(SpawnReason reason) {
        return reason == SPAWNER || reason == TRIAL_SPAWNER;
    }

    public static boolean isTrialSpawner(SpawnReason reason) {
        return reason == TRIAL_SPAWNER;
    }

    private static SpawnReason[] method_36610() {
        return new SpawnReason[]{NATURAL, CHUNK_GENERATION, SPAWNER, STRUCTURE, BREEDING, MOB_SUMMONED, JOCKEY, EVENT, CONVERSION, REINFORCEMENT, TRIGGERED, BUCKET, SPAWN_ITEM_USE, COMMAND, DISPENSER, PATROL, TRIAL_SPAWNER, LOAD, DIMENSION_TRAVEL};
    }

    static {
        field_16464 = SpawnReason.method_36610();
    }
}

