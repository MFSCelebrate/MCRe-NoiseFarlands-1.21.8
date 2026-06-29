/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.hash.Hashing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WorldIcon
implements AutoCloseable {
    final static private Identifier UNKNOWN_SERVER_ID = Identifier.ofVanilla("textures/misc/unknown_server.png");
    final static private int ICON_WIDTH = 64;
    final static private int ICON_HEIGHT = 64;
    final private TextureManager textureManager;
    final private Identifier id;
    @Nullable
    private NativeImageBackedTexture texture;
    private boolean closed;

    private WorldIcon(TextureManager textureManager, Identifier id) {
        this.textureManager = textureManager;
        this.id = id;
    }

    public static WorldIcon forWorld(TextureManager textureManager, String worldName) {
        return new WorldIcon(textureManager, Identifier.ofVanilla("worlds/" + Util.replaceInvalidChars(worldName, Identifier::isPathCharacterValid) + "/" + String.valueOf(Hashing.sha1().hashUnencodedChars((CharSequence)worldName)) + "/icon"));
    }

    public static WorldIcon forServer(TextureManager textureManager, String serverAddress) {
        return new WorldIcon(textureManager, Identifier.ofVanilla("servers/" + String.valueOf(Hashing.sha1().hashUnencodedChars((CharSequence)serverAddress)) + "/icon"));
    }

    public void load(NativeImage image) {
        if (image.getWidth() != 64 || image.getHeight() != 64) {
            image.close();
            throw new IllegalArgumentException("Icon must be 64x64, but was " + image.getWidth() + "x" + image.getHeight());
        }
        try {
            this.assertOpen();
            if (this.texture == null) {
                this.texture = new NativeImageBackedTexture(() -> "Favicon " + String.valueOf(this.id), image);
            } else {
                this.texture.setImage(image);
                this.texture.upload();
            }
            this.textureManager.registerTexture(this.id, this.texture);
        }
        catch (Throwable throwable) {
            image.close();
            this.destroy();
            throw throwable;
        }
    }

    public void destroy() {
        this.assertOpen();
        if (this.texture != null) {
            this.textureManager.destroyTexture(this.id);
            this.texture.close();
            this.texture = null;
        }
    }

    public Identifier getTextureId() {
        return this.texture != null ? this.id : UNKNOWN_SERVER_ID;
    }

    @Override
    public void close() {
        this.destroy();
        this.closed = true;
    }

    private void assertOpen() {
        if (this.closed) {
            throw new IllegalStateException("Icon already closed");
        }
    }
}

