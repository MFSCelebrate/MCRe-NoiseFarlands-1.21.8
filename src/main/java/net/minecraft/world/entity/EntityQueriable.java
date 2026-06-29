/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.entity;

import java.util.UUID;
import net.minecraft.world.entity.UniquelyIdentifiable;
import org.jetbrains.annotations.Nullable;

public interface EntityQueriable<IdentifiedType extends UniquelyIdentifiable> {
    @Nullable
    public IdentifiedType getEntity(UUID var1);
}

