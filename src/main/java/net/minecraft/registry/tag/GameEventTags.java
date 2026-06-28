/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.GameEvent;

public class GameEventTags {
    final static public TagKey<GameEvent> VIBRATIONS = GameEventTags.of("vibrations");
    final static public TagKey<GameEvent> WARDEN_CAN_LISTEN = GameEventTags.of("warden_can_listen");
    final static public TagKey<GameEvent> SHRIEKER_CAN_LISTEN = GameEventTags.of("shrieker_can_listen");
    final static public TagKey<GameEvent> IGNORE_VIBRATIONS_SNEAKING = GameEventTags.of("ignore_vibrations_sneaking");
    final static public TagKey<GameEvent> ALLAY_CAN_LISTEN = GameEventTags.of("allay_can_listen");

    private static TagKey<GameEvent> of(String id) {
        return TagKey.of(RegistryKeys.GAME_EVENT, Identifier.ofVanilla(id));
    }
}

