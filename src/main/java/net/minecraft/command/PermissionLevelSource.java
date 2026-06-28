/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import net.minecraft.command.PermissionLevelPredicate;

public interface PermissionLevelSource {
    public boolean hasPermissionLevel(int var1);

    default public boolean hasElevatedPermissions() {
        return this.hasPermissionLevel(2);
    }

    public record PermissionLevelSourcePredicate<T extends PermissionLevelSource>(int requiredLevel) implements PermissionLevelPredicate<T>
    {
        @Override
        public boolean test(T permissionLevelSource) {
            return permissionLevelSource.hasPermissionLevel(this.requiredLevel);
        }

        @Override
        public boolean test(Object permissionLevelSource) {
            return this.test((T)((PermissionLevelSource)permissionLevelSource));
        }
    }
}

