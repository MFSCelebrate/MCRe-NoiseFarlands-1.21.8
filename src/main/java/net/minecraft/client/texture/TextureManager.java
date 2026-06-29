/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.logging.LogUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.gui.screen.BuyRealmsScreen;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.DynamicTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.ReloadableTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class TextureManager
implements ResourceReloader,
TextureTickListener,
AutoCloseable {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public Identifier MISSING_IDENTIFIER = Identifier.ofVanilla("");
    final private Map<Identifier, AbstractTexture> textures = new HashMap<Identifier, AbstractTexture>();
    final private Set<TextureTickListener> tickListeners = new HashSet<TextureTickListener>();
    final private ResourceManager resourceContainer;

    public TextureManager(ResourceManager resourceManager) {
        this.resourceContainer = resourceManager;
        NativeImage nativeImage = MissingSprite.createImage();
        this.registerTexture(MissingSprite.getMissingSpriteId(), new NativeImageBackedTexture(() -> "(intentionally-)Missing Texture", nativeImage));
    }

    public void registerTexture(Identifier id, ReloadableTexture texture) {
        try {
            texture.reload(this.loadTexture(id, texture));
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Uploading texture");
            CrashReportSection crashReportSection = crashReport.addElement("Uploaded texture");
            crashReportSection.add("Resource location", texture.getId());
            crashReportSection.add("Texture id", id);
            throw new CrashException(crashReport);
        }
        this.registerTexture(id, (AbstractTexture)texture);
    }

    private TextureContents loadTexture(Identifier id, ReloadableTexture texture) {
        try {
            return TextureManager.loadTexture(this.resourceContainer, id, texture);
        }
        catch (Exception exception) {
            LOGGER.error("Failed to load texture {} into slot {}", new Object[]{texture.getId(), id, exception});
            return TextureContents.createMissing();
        }
    }

    public void registerTexture(Identifier id) {
        this.registerTexture(id, (AbstractTexture)new ResourceTexture(id));
    }

    public void registerTexture(Identifier id, AbstractTexture texture) {
        AbstractTexture abstractTexture = this.textures.put(id, texture);
        if (abstractTexture != texture) {
            if (abstractTexture != null) {
                this.closeTexture(id, abstractTexture);
            }
            if (texture instanceof TextureTickListener) {
                TextureTickListener textureTickListener = (TextureTickListener)((Object)texture);
                this.tickListeners.add(textureTickListener);
            }
        }
    }

    private void closeTexture(Identifier id, AbstractTexture texture) {
        this.tickListeners.remove(texture);
        try {
            texture.close();
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to close texture {}", (Object)id, (Object)exception);
        }
    }

    public AbstractTexture getTexture(Identifier id) {
        AbstractTexture abstractTexture = this.textures.get(id);
        if (abstractTexture != null) {
            return abstractTexture;
        }
        ResourceTexture resourceTexture = new ResourceTexture(id);
        this.registerTexture(id, resourceTexture);
        return resourceTexture;
    }

    @Override
    public void tick() {
        for (TextureTickListener textureTickListener : this.tickListeners) {
            textureTickListener.tick();
        }
    }

    public void destroyTexture(Identifier id) {
        AbstractTexture abstractTexture = this.textures.remove(id);
        if (abstractTexture != null) {
            this.closeTexture(id, abstractTexture);
        }
    }

    @Override
    public void close() {
        this.textures.forEach(this::closeTexture);
        this.textures.clear();
        this.tickListeners.clear();
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager resourceManager, Executor executor, Executor executor2) {
        ArrayList list = new ArrayList();
        this.textures.forEach((id, texture) -> {
            if (texture instanceof ReloadableTexture) {
                ReloadableTexture reloadableTexture = (ReloadableTexture)texture;
                list.add(TextureManager.reloadTexture(resourceManager, id, reloadableTexture, executor));
            }
        });
        return ((CompletableFuture)CompletableFuture.allOf((CompletableFuture[])list.stream().map(ReloadedTexture::newContents).toArray(CompletableFuture[]::new)).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(v -> {
            BuyRealmsScreen.refreshImages(this.resourceContainer);
            for (ReloadedTexture reloadedTexture : list) {
                reloadedTexture.texture.reload(reloadedTexture.newContents.join());
            }
        }, executor2);
    }

    public void dumpDynamicTextures(Path path) {
        try {
            Files.createDirectories(path, new FileAttribute[0]);
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to create directory {}", (Object)path, (Object)iOException);
            return;
        }
        this.textures.forEach((id, texture) -> {
            if (texture instanceof DynamicTexture) {
                DynamicTexture dynamicTexture = (DynamicTexture)((Object)texture);
                try {
                    dynamicTexture.save((Identifier)id, path);
                }
                catch (IOException iOException) {
                    LOGGER.error("Failed to dump texture {}", id, (Object)iOException);
                }
            }
        });
    }

    private static TextureContents loadTexture(ResourceManager resourceManager, Identifier textureId, ReloadableTexture texture) throws IOException {
        try {
            return texture.loadContents(resourceManager);
        }
        catch (FileNotFoundException fileNotFoundException) {
            if (textureId != MISSING_IDENTIFIER) {
                LOGGER.warn("Missing resource {} referenced from {}", (Object)texture.getId(), (Object)textureId);
            }
            return TextureContents.createMissing();
        }
    }

    private static ReloadedTexture reloadTexture(ResourceManager resourceManager, Identifier textureId, ReloadableTexture texture, Executor prepareExecutor) {
        return new ReloadedTexture(texture, CompletableFuture.supplyAsync(() -> {
            try {
                return TextureManager.loadTexture(resourceManager, textureId, texture);
            }
            catch (IOException iOException) {
                throw new UncheckedIOException(iOException);
            }
        }, prepareExecutor));
    }

    @Environment(value=EnvType.CLIENT)
    static final class ReloadedTexture
    extends Record {
        final ReloadableTexture texture;
        final CompletableFuture<TextureContents> newContents;

        ReloadedTexture(ReloadableTexture reloadableTexture, CompletableFuture<TextureContents> completableFuture) {
            this.texture = reloadableTexture;
            this.newContents = completableFuture;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ReloadedTexture.class, "texture;newContents", "texture", "newContents"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ReloadedTexture.class, "texture;newContents", "texture", "newContents"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ReloadedTexture.class, "texture;newContents", "texture", "newContents"}, this, object);
        }

        public ReloadableTexture texture() {
            return this.texture;
        }

        public CompletableFuture<TextureContents> newContents() {
            return this.newContents;
        }
    }
}

