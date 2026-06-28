/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record InitialWorldOptions(WorldCreator.Mode selectedGameMode, Set<GameRules.Key<GameRules.BooleanRule>> disabledGameRules, @Nullable RegistryKey<FlatLevelGeneratorPreset> flatLevelPreset) {
}

