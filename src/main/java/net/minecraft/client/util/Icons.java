/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.client.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import org.apache.commons.lang3.ArrayUtils;

@Environment(value=EnvType.CLIENT)
public final class Icons
extends Enum<Icons> {
    final static public Icons RELEASE = new Icons("icons");
    final static public Icons SNAPSHOT = new Icons("icons", "snapshot");
    final private String[] path;
    final static private Icons[] field_44653;

    public static Icons[] values() {
        return (Icons[])field_44653.clone();
    }

    public static Icons valueOf(String string) {
        return Enum.valueOf(Icons.class, string);
    }

    private Icons(String ... path) {
        this.path = path;
    }

    public List<InputSupplier<InputStream>> getIcons(ResourcePack resourcePack) throws IOException {
        return List.of(this.getIcon(resourcePack, "icon_16x16.png"), this.getIcon(resourcePack, "icon_32x32.png"), this.getIcon(resourcePack, "icon_48x48.png"), this.getIcon(resourcePack, "icon_128x128.png"), this.getIcon(resourcePack, "icon_256x256.png"));
    }

    public InputSupplier<InputStream> getMacIcon(ResourcePack resourcePack) throws IOException {
        return this.getIcon(resourcePack, "minecraft.icns");
    }

    private InputSupplier<InputStream> getIcon(ResourcePack resourcePack, String fileName) throws IOException {
        CharSequence[] strings = (String[])ArrayUtils.add((Object[])this.path, (Object)fileName);
        InputSupplier<InputStream> inputSupplier = resourcePack.openRoot((String[])strings);
        if (inputSupplier == null) {
            throw new FileNotFoundException(String.join((CharSequence)"/", strings));
        }
        return inputSupplier;
    }

    private static Icons[] method_51417() {
        return new Icons[]{RELEASE, SNAPSHOT};
    }

    static {
        field_44653 = Icons.method_51417();
    }
}

