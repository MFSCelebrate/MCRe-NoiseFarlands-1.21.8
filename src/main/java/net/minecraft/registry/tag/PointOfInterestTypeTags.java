/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;

public class PointOfInterestTypeTags {
    final static public TagKey<PointOfInterestType> ACQUIRABLE_JOB_SITE = PointOfInterestTypeTags.of("acquirable_job_site");
    final static public TagKey<PointOfInterestType> VILLAGE = PointOfInterestTypeTags.of("village");
    final static public TagKey<PointOfInterestType> BEE_HOME = PointOfInterestTypeTags.of("bee_home");

    private PointOfInterestTypeTags() {
    }

    private static TagKey<PointOfInterestType> of(String id) {
        return TagKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Identifier.ofVanilla(id));
    }
}

