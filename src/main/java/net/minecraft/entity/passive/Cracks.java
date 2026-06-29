/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.item.ItemStack;

public class Cracks {
    final static public Cracks IRON_GOLEM = new Cracks(0.75f, 0.5f, 0.25f);
    final static public Cracks WOLF_ARMOR = new Cracks(0.95f, 0.69f, 0.32f);
    final private float lowCrackThreshold;
    final private float mediumCrackThreshold;
    final private float highCrackThreshold;

    private Cracks(float lowCrackThreshold, float mediumCrackThreshold, float highCrackThreshold) {
        this.lowCrackThreshold = lowCrackThreshold;
        this.mediumCrackThreshold = mediumCrackThreshold;
        this.highCrackThreshold = highCrackThreshold;
    }

    public CrackLevel getCrackLevel(float health) {
        if (health < this.highCrackThreshold) {
            return CrackLevel.HIGH;
        }
        if (health < this.mediumCrackThreshold) {
            return CrackLevel.MEDIUM;
        }
        if (health < this.lowCrackThreshold) {
            return CrackLevel.LOW;
        }
        return CrackLevel.NONE;
    }

    public CrackLevel getCrackLevel(ItemStack stack) {
        if (!stack.isDamageable()) {
            return CrackLevel.NONE;
        }
        return this.getCrackLevel(stack.getDamage(), stack.getMaxDamage());
    }

    public CrackLevel getCrackLevel(int currentDamage, int maxDamage) {
        return this.getCrackLevel((float)(maxDamage - currentDamage) / (float)maxDamage);
    }

    public static final class CrackLevel
    extends Enum<CrackLevel> {
        final static public CrackLevel NONE = new CrackLevel();
        final static public CrackLevel LOW = new CrackLevel();
        final static public CrackLevel MEDIUM = new CrackLevel();
        final static public CrackLevel HIGH = new CrackLevel();
        final static private CrackLevel[] field_21085;

        public static CrackLevel[] values() {
            return (CrackLevel[])field_21085.clone();
        }

        public static CrackLevel valueOf(String string) {
            return Enum.valueOf(CrackLevel.class, string);
        }

        private static CrackLevel[] method_36638() {
            return new CrackLevel[]{NONE, LOW, MEDIUM, HIGH};
        }

        static {
            field_21085 = CrackLevel.method_36638();
        }
    }
}

