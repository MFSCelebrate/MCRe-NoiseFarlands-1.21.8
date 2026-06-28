/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface CreateWorldCallback {
    public boolean create(CreateWorldScreen var1, CombinedDynamicRegistries<ServerDynamicRegistryType> var2, LevelProperties var3, @Nullable Path var4);
}

