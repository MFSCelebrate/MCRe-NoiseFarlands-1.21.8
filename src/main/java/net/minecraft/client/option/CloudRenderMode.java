/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TranslatableOption;

@Environment(value=EnvType.CLIENT)
public final class CloudRenderMode
extends Enum<CloudRenderMode>
implements TranslatableOption,
StringIdentifiable {
    final static public CloudRenderMode OFF = new CloudRenderMode(0, "false", "options.off");
    final static public CloudRenderMode FAST = new CloudRenderMode(1, "fast", "options.clouds.fast");
    final static public CloudRenderMode FANCY = new CloudRenderMode(2, "true", "options.clouds.fancy");
    final static public Codec<CloudRenderMode> CODEC;
    final private int id;
    final private String serializedId;
    final private String translationKey;
    final static private CloudRenderMode[] field_18168;

    public static CloudRenderMode[] values() {
        return (CloudRenderMode[])field_18168.clone();
    }

    public static CloudRenderMode valueOf(String string) {
        return Enum.valueOf(CloudRenderMode.class, string);
    }

    private CloudRenderMode(int id, String serializedId, String translationKey) {
        this.id = id;
        this.serializedId = serializedId;
        this.translationKey = translationKey;
    }

    @Override
    public String asString() {
        return this.serializedId;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    private static CloudRenderMode[] method_36860() {
        return new CloudRenderMode[]{OFF, FAST, FANCY};
    }

    static {
        field_18168 = CloudRenderMode.method_36860();
        CODEC = StringIdentifiable.createCodec(CloudRenderMode::values);
    }
}

