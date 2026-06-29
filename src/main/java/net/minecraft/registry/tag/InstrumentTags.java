/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.tag;

import net.minecraft.item.Instrument;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface InstrumentTags {
    final static public TagKey<Instrument> REGULAR_GOAT_HORNS = InstrumentTags.of("regular_goat_horns");
    final static public TagKey<Instrument> SCREAMING_GOAT_HORNS = InstrumentTags.of("screaming_goat_horns");
    final static public TagKey<Instrument> GOAT_HORNS = InstrumentTags.of("goat_horns");

    private static TagKey<Instrument> of(String id) {
        return TagKey.of(RegistryKeys.INSTRUMENT, Identifier.ofVanilla(id));
    }
}

