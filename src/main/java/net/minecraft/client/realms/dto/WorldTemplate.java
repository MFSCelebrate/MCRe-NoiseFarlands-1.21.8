/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldTemplate
extends ValueObject {
    final static private Logger LOGGER = LogUtils.getLogger();
    public String id = "";
    public String name = "";
    public String version = "";
    public String author = "";
    public String link = "";
    @Nullable
    public String image;
    public String trailer = "";
    public String recommendedPlayers = "";
    public WorldTemplateType type = WorldTemplateType.WORLD_TEMPLATE;

    public static WorldTemplate parse(JsonObject node) {
        WorldTemplate worldTemplate = new WorldTemplate();
        try {
            worldTemplate.id = JsonUtils.getNullableStringOr("id", node, "");
            worldTemplate.name = JsonUtils.getNullableStringOr("name", node, "");
            worldTemplate.version = JsonUtils.getNullableStringOr("version", node, "");
            worldTemplate.author = JsonUtils.getNullableStringOr("author", node, "");
            worldTemplate.link = JsonUtils.getNullableStringOr("link", node, "");
            worldTemplate.image = JsonUtils.getNullableStringOr("image", node, null);
            worldTemplate.trailer = JsonUtils.getNullableStringOr("trailer", node, "");
            worldTemplate.recommendedPlayers = JsonUtils.getNullableStringOr("recommendedPlayers", node, "");
            worldTemplate.type = WorldTemplateType.valueOf(JsonUtils.getNullableStringOr("type", node, WorldTemplateType.WORLD_TEMPLATE.name()));
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse WorldTemplate: {}", (Object)exception.getMessage());
        }
        return worldTemplate;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class WorldTemplateType
    extends Enum<WorldTemplateType> {
        final static public WorldTemplateType WORLD_TEMPLATE = new WorldTemplateType();
        final static public WorldTemplateType MINIGAME = new WorldTemplateType();
        final static public WorldTemplateType ADVENTUREMAP = new WorldTemplateType();
        final static public WorldTemplateType EXPERIENCE = new WorldTemplateType();
        final static public WorldTemplateType INSPIRATION = new WorldTemplateType();
        final static private WorldTemplateType[] field_19452;

        public static WorldTemplateType[] values() {
            return (WorldTemplateType[])field_19452.clone();
        }

        public static WorldTemplateType valueOf(String name) {
            return Enum.valueOf(WorldTemplateType.class, name);
        }

        private static WorldTemplateType[] method_36851() {
            return new WorldTemplateType[]{WORLD_TEMPLATE, MINIGAME, ADVENTUREMAP, EXPERIENCE, INSPIRATION};
        }

        static {
            field_19452 = WorldTemplateType.method_36851();
        }
    }
}

