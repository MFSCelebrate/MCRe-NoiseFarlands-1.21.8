/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record SpriteMapper(Identifier sheet, String prefix) {
    public SpriteIdentifier map(Identifier id) {
        return new SpriteIdentifier(this.sheet, id.withPrefixedPath(this.prefix + "/"));
    }

    public SpriteIdentifier mapVanilla(String id) {
        return this.map(Identifier.ofVanilla(id));
    }
}

