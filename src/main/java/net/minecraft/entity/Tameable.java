/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface Tameable {
    @Nullable
    public LazyEntityReference<LivingEntity> getOwnerReference();

    public World net_minecraft_world_World_getWorld();

    @Nullable
    default public LivingEntity getOwner() {
        return LazyEntityReference.resolve(this.getOwnerReference(), this.net_minecraft_world_World_getWorld(), LivingEntity.class);
    }

    @Nullable
    default public LivingEntity getTopLevelOwner() {
        ObjectArraySet set = new ObjectArraySet();
        LivingEntity livingEntity = this.getOwner();
        set.add(this);
        while (livingEntity instanceof Tameable) {
            Tameable tameable = (Tameable)((Object)livingEntity);
            LivingEntity livingEntity2 = tameable.getOwner();
            if (set.contains(livingEntity2)) {
                return null;
            }
            set.add(livingEntity);
            livingEntity = tameable.getOwner();
        }
        return livingEntity;
    }
}

