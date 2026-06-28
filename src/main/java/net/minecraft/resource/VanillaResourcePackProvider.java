/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.resource;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.path.SymlinkFinder;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class VanillaResourcePackProvider
implements ResourcePackProvider {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public String VANILLA_KEY = "vanilla";
    final static public String TESTS_KEY = "tests";
    final static public VersionedIdentifier VANILLA_ID = VersionedIdentifier.createVanilla("core");
    final private ResourceType type;
    final private DefaultResourcePack resourcePack;
    final private Identifier id;
    final private SymlinkFinder symlinkFinder;

    public VanillaResourcePackProvider(ResourceType type, DefaultResourcePack resourcePack, Identifier id, SymlinkFinder symlinkFinder) {
        this.type = type;
        this.resourcePack = resourcePack;
        this.id = id;
        this.symlinkFinder = symlinkFinder;
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        ResourcePackProfile resourcePackProfile = this.createDefault(this.resourcePack);
        if (resourcePackProfile != null) {
            profileAdder.accept(resourcePackProfile);
        }
        this.forEachProfile(profileAdder);
    }

    @Nullable
    protected abstract ResourcePackProfile createDefault(ResourcePack var1);

    protected abstract Text getDisplayName(String var1);

    public DefaultResourcePack getResourcePack() {
        return this.resourcePack;
    }

    private void forEachProfile(Consumer<ResourcePackProfile> consumer) {
        HashMap<String, Function> map = new HashMap<String, Function>();
        this.forEachProfile(map::put);
        map.forEach((id, packFactory) -> {
            ResourcePackProfile resourcePackProfile = (ResourcePackProfile)packFactory.apply(id);
            if (resourcePackProfile != null) {
                consumer.accept(resourcePackProfile);
            }
        });
    }

    protected void forEachProfile(BiConsumer<String, Function<String, ResourcePackProfile>> consumer) {
        this.resourcePack.forEachNamespacedPath(this.type, this.id, namespacedPath -> this.forEachProfile((Path)namespacedPath, consumer));
    }

    protected void forEachProfile(@Nullable Path namespacedPath, BiConsumer<String, Function<String, ResourcePackProfile>> consumer) {
        if (namespacedPath != null && Files.isDirectory(namespacedPath, new LinkOption[0])) {
            try {
                FileResourcePackProvider.forEachProfile(namespacedPath, this.symlinkFinder, (profilePath, factory) -> consumer.accept(VanillaResourcePackProvider.getFileName(profilePath), id -> this.create((String)id, (ResourcePackProfile.PackFactory)factory, this.getDisplayName((String)id))));
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to discover packs in {}", (Object)namespacedPath, (Object)iOException);
            }
        }
    }

    private static String getFileName(Path path) {
        return StringUtils.removeEnd((String)path.getFileName().toString(), (String)".zip");
    }

    @Nullable
    protected abstract ResourcePackProfile create(String var1, ResourcePackProfile.PackFactory var2, Text var3);

    protected static ResourcePackProfile.PackFactory createPackFactory(final ResourcePack pack) {
        return new ResourcePackProfile.PackFactory(){

            @Override
            public ResourcePack open(ResourcePackInfo info) {
                return pack;
            }

            @Override
            public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
                return pack;
            }
        };
    }
}

