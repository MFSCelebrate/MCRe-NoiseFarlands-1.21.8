/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.world.level.WorldGenSettings;

@Environment(value=EnvType.CLIENT)
public record WorldCreationSettings(WorldGenSettings worldGenSettings, DataConfiguration dataConfiguration) {
}

