/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.particle;

public class ParticleGroup {
    final private int maxCount;
    final static public ParticleGroup SPORE_BLOSSOM_AIR = new ParticleGroup(1000);

    public ParticleGroup(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getMaxCount() {
        return this.maxCount;
    }
}

