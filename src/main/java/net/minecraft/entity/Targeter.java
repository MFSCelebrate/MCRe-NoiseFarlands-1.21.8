/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface Targeter {
    @Nullable
    public LivingEntity getTarget();
}

