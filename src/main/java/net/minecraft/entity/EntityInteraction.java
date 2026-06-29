/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

public interface EntityInteraction {
    final static public EntityInteraction ZOMBIE_VILLAGER_CURED = EntityInteraction.create("zombie_villager_cured");
    final static public EntityInteraction GOLEM_KILLED = EntityInteraction.create("golem_killed");
    final static public EntityInteraction VILLAGER_HURT = EntityInteraction.create("villager_hurt");
    final static public EntityInteraction VILLAGER_KILLED = EntityInteraction.create("villager_killed");
    final static public EntityInteraction TRADE = EntityInteraction.create("trade");

    public static EntityInteraction create(final String key) {
        return new EntityInteraction(){

            public String toString() {
                return key;
            }
        };
    }
}

