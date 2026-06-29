/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.renderer.v1.sprite.FabricSpriteAtlasTexture
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.sprite.FabricSpriteAtlasTexture;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.DynamicTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SpriteAtlasTexture
extends AbstractTexture
implements DynamicTexture,
TextureTickListener,
FabricSpriteAtlasTexture {
    final static private Logger LOGGER = LogUtils.getLogger();
    @Deprecated
    final static public Identifier BLOCK_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/blocks.png");
    @Deprecated
    final static public Identifier PARTICLE_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/particles.png");
    private List<SpriteContents> spritesToLoad = List.of();
    private List<Sprite.TickableAnimation> animatedSprites = List.of();
    private Map<Identifier, Sprite> sprites = Map.of();
    @Nullable
    private Sprite missingSprite;
    final private Identifier id;
    final private int maxTextureSize;
    private int width;
    private int height;
    private int mipLevel;

    public SpriteAtlasTexture(Identifier id) {
        this.id = id;
        this.maxTextureSize = RenderSystem.getDevice().getMaxTextureSize();
    }

    private void method_72240(int i, int j, int k) {
        LOGGER.info("Created: {}x{}x{} {}-atlas", new Object[]{i, j, k, this.id});
        GpuDevice gpuDevice = RenderSystem.getDevice();
        this.close();
        this.glTexture = gpuDevice.createTexture(this.id::toString, 7, TextureFormat.RGBA8, i, j, 1, k + 1);
        this.glTextureView = gpuDevice.createTextureView(this.glTexture);
        this.width = i;
        this.height = j;
        this.mipLevel = k;
    }

    public void upload(SpriteLoader.StitchResult stitchResult) {
        this.method_72240(stitchResult.width(), stitchResult.height(), stitchResult.mipLevel());
        this.clear();
        this.setFilter(false, this.mipLevel > 1);
        this.sprites = Map.copyOf(stitchResult.regions());
        this.missingSprite = this.sprites.get(MissingSprite.getMissingSpriteId());
        if (this.missingSprite == null) {
            throw new IllegalStateException("Atlas '" + String.valueOf(this.id) + "' (" + this.sprites.size() + " sprites) has no missing texture sprite");
        }
        ArrayList<SpriteContents> list = new ArrayList<SpriteContents>();
        ArrayList<Sprite.TickableAnimation> list2 = new ArrayList<Sprite.TickableAnimation>();
        for (Sprite sprite : stitchResult.regions().values()) {
            list.add(sprite.getContents());
            try {
                sprite.upload(this.glTexture);
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Stitching texture atlas");
                CrashReportSection crashReportSection = crashReport.addElement("Texture being stitched together");
                crashReportSection.add("Atlas path", this.id);
                crashReportSection.add("Sprite", sprite);
                throw new CrashException(crashReport);
            }
            Sprite.TickableAnimation tickableAnimation = sprite.createAnimation();
            if (tickableAnimation == null) continue;
            list2.add(tickableAnimation);
        }
        this.spritesToLoad = List.copyOf(list);
        this.animatedSprites = List.copyOf(list2);
    }

    @Override
    public void save(Identifier id, Path path) throws IOException {
        String string = id.toUnderscoreSeparatedString();
        TextureUtil.writeAsPNG(path, string, this.getGlTexture(), this.mipLevel, color -> color);
        SpriteAtlasTexture.dumpAtlasInfos(path, string, this.sprites);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static void dumpAtlasInfos(Path path, String id, Map<Identifier, Sprite> sprites) {
        Path path2 = path.resolve(id + ".txt");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(path2, new OpenOption[0]);){
                for (Map.Entry entry : sprites.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
                    Sprite sprite = (Sprite)entry.getValue();
                    writer.write(String.format(Locale.ROOT, "%s\tx=%d\ty=%d\tw=%d\th=%d%n", entry.getKey(), sprite.getX(), sprite.getY(), sprite.getContents().getWidth(), sprite.getContents().getHeight()));
                }
                if (writer == null) return;
            }
            return;
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to write file {}", (Object)path2, (Object)iOException);
        }
    }

    public void tickAnimatedSprites() {
        if (this.glTexture == null) {
            return;
        }
        for (Sprite.TickableAnimation tickableAnimation : this.animatedSprites) {
            tickableAnimation.tick(this.glTexture);
        }
    }

    @Override
    public void tick() {
        this.tickAnimatedSprites();
    }

    public Sprite getSprite(Identifier id) {
        Sprite sprite = this.sprites.getOrDefault(id, this.missingSprite);
        if (sprite == null) {
            throw new IllegalStateException("Tried to lookup sprite, but atlas is not initialized");
        }
        return sprite;
    }

    public void clear() {
        this.spritesToLoad.forEach(SpriteContents::close);
        this.animatedSprites.forEach(Sprite.TickableAnimation::close);
        this.spritesToLoad = List.of();
        this.animatedSprites = List.of();
        this.sprites = Map.of();
        this.missingSprite = null;
    }

    public Identifier getId() {
        return this.id;
    }

    public int getMaxTextureSize() {
        return this.maxTextureSize;
    }

    int getWidth() {
        return this.width;
    }

    int getHeight() {
        return this.height;
    }
}

