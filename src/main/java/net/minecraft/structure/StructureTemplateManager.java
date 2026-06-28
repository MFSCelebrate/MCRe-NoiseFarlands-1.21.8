/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.test.TestInstanceUtil;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.path.PathUtil;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureTemplateManager {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public String STRUCTURE_DIRECTORY = "structure";
    final static private String STRUCTURES_DIRECTORY = "structures";
    final static private String NBT_FILE_EXTENSION = ".nbt";
    final static private String SNBT_FILE_EXTENSION = ".snbt";
    final private Map<Identifier, Optional<StructureTemplate>> templates = Maps.newConcurrentMap();
    final private DataFixer dataFixer;
    private ResourceManager resourceManager;
    final private Path generatedPath;
    final private List<Provider> providers;
    final private RegistryEntryLookup<Block> blockLookup;
    final static private ResourceFinder STRUCTURE_NBT_RESOURCE_FINDER = new ResourceFinder("structure", ".nbt");

    public StructureTemplateManager(ResourceManager resourceManager, LevelStorage.Session session, DataFixer dataFixer, RegistryEntryLookup<Block> blockLookup) {
        this.resourceManager = resourceManager;
        this.dataFixer = dataFixer;
        this.generatedPath = session.getDirectory(WorldSavePath.GENERATED).normalize();
        this.blockLookup = blockLookup;
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add((Object)new Provider(this::loadTemplateFromFile, this::streamTemplatesFromFile));
        if (SharedConstants.isDevelopment) {
            builder.add((Object)new Provider(this::loadTemplateFromGameTestFile, this::streamTemplatesFromGameTestFile));
        }
        builder.add((Object)new Provider(this::loadTemplateFromResource, this::streamTemplatesFromResource));
        this.providers = builder.build();
    }

    public StructureTemplate getTemplateOrBlank(Identifier id) {
        Optional<StructureTemplate> optional = this.getTemplate(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        StructureTemplate structureTemplate = new StructureTemplate();
        this.templates.put(id, Optional.of(structureTemplate));
        return structureTemplate;
    }

    public Optional<StructureTemplate> getTemplate(Identifier id) {
        return this.templates.computeIfAbsent(id, this::loadTemplate);
    }

    public Stream<Identifier> streamTemplates() {
        return this.providers.stream().flatMap(provider -> provider.lister().get()).distinct();
    }

    private Optional<StructureTemplate> loadTemplate(Identifier id) {
        for (Provider provider : this.providers) {
            try {
                Optional<StructureTemplate> optional = provider.loader().apply(id);
                if (!optional.isPresent()) continue;
                return optional;
            }
            catch (Exception exception) {
            }
        }
        return Optional.empty();
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.templates.clear();
    }

    private Optional<StructureTemplate> loadTemplateFromResource(Identifier id) {
        Identifier identifier = STRUCTURE_NBT_RESOURCE_FINDER.toResourcePath(id);
        return this.loadTemplate(() -> this.resourceManager.open(identifier), throwable -> LOGGER.error("Couldn't load structure {}", (Object)id, throwable));
    }

    private Stream<Identifier> streamTemplatesFromResource() {
        return STRUCTURE_NBT_RESOURCE_FINDER.findResources(this.resourceManager).keySet().stream().map(STRUCTURE_NBT_RESOURCE_FINDER::toResourceId);
    }

    private Optional<StructureTemplate> loadTemplateFromGameTestFile(Identifier id) {
        return this.loadTemplateFromSnbt(id, TestInstanceUtil.testStructuresDirectoryName);
    }

    private Stream<Identifier> streamTemplatesFromGameTestFile() {
        if (!Files.isDirectory(TestInstanceUtil.testStructuresDirectoryName, new LinkOption[0])) {
            return Stream.empty();
        }
        ArrayList list = new ArrayList();
        this.streamTemplates(TestInstanceUtil.testStructuresDirectoryName, "minecraft", SNBT_FILE_EXTENSION, list::add);
        return list.stream();
    }

    private Optional<StructureTemplate> loadTemplateFromFile(Identifier id) {
        if (!Files.isDirectory(this.generatedPath, new LinkOption[0])) {
            return Optional.empty();
        }
        Path path = this.getTemplatePath(id, NBT_FILE_EXTENSION);
        return this.loadTemplate(() -> new FileInputStream(path.toFile()), throwable -> LOGGER.error("Couldn't load structure from {}", (Object)path, throwable));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Stream<Identifier> streamTemplatesFromFile() {
        if (!Files.isDirectory(this.generatedPath, new LinkOption[0])) {
            return Stream.empty();
        }
        try {
            ArrayList list = new ArrayList();
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(this.generatedPath, path -> Files.isDirectory(path, new LinkOption[0]));){
                for (Path path2 : directoryStream) {
                    String string = path2.getFileName().toString();
                    Path path22 = path2.resolve(STRUCTURES_DIRECTORY);
                    this.streamTemplates(path22, string, NBT_FILE_EXTENSION, list::add);
                }
                if (directoryStream == null) return list.stream();
            }
            return list.stream();
        }
        catch (IOException iOException) {
            return Stream.empty();
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void streamTemplates(Path directory, String namespace, String fileExtension, Consumer<Identifier> idConsumer) {
        int i = fileExtension.length();
        Function<String, String> function = filename -> filename.substring(0, filename.length() - i);
        try {
            try (Stream<Path> stream = Files.find(directory, Integer.MAX_VALUE, (path, attributes) -> attributes.isRegularFile() && path.toString().endsWith(fileExtension), new FileVisitOption[0]);){
                stream.forEach(path -> {
                    try {
                        idConsumer.accept(Identifier.of(namespace, (String)function.apply(this.toRelativePath(directory, (Path)path))));
                    }
                    catch (InvalidIdentifierException invalidIdentifierException) {
                        LOGGER.error("Invalid location while listing folder {} contents", (Object)directory, (Object)invalidIdentifierException);
                    }
                });
                if (stream == null) return;
            }
            return;
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to list folder {} contents", (Object)directory, (Object)iOException);
        }
    }

    private String toRelativePath(Path root, Path path) {
        return root.relativize(path).toString().replace(File.separator, "/");
    }

    private Optional<StructureTemplate> loadTemplateFromSnbt(Identifier id, Path path) {
        Optional<StructureTemplate> optional;
        block10: {
            if (!Files.isDirectory(path, new LinkOption[0])) {
                return Optional.empty();
            }
            Path path2 = PathUtil.getResourcePath(path, id.getPath(), SNBT_FILE_EXTENSION);
            BufferedReader bufferedReader = Files.newBufferedReader(path2);
            try {
                String string = IOUtils.toString((Reader)bufferedReader);
                optional = Optional.of(this.createTemplate(NbtHelper.fromNbtProviderString(string)));
                if (bufferedReader == null) break block10;
            }
            catch (Throwable throwable) {
                try {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (NoSuchFileException noSuchFileException) {
                    return Optional.empty();
                }
                catch (CommandSyntaxException | IOException exception) {
                    LOGGER.error("Couldn't load structure from {}", (Object)path2, (Object)exception);
                    return Optional.empty();
                }
            }
            bufferedReader.close();
        }
        return optional;
    }

    /*
     * Loose catch block
     */
    private Optional<StructureTemplate> loadTemplate(TemplateFileOpener opener, Consumer<Throwable> exceptionConsumer) {
        Optional<StructureTemplate> optional;
        FixedBufferInputStream inputStream2;
        InputStream inputStream;
        block14: {
            inputStream = opener.open();
            inputStream2 = new FixedBufferInputStream(inputStream);
            optional = Optional.of(this.readTemplate(inputStream2));
            ((InputStream)inputStream2).close();
            if (inputStream == null) break block14;
            inputStream.close();
        }
        return optional;
        {
            catch (Throwable throwable) {
                try {
                    try {
                        try {
                            ((InputStream)inputStream2).close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                        throw throwable;
                    }
                    catch (Throwable throwable3) {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            }
                            catch (Throwable throwable4) {
                                throwable3.addSuppressed(throwable4);
                            }
                        }
                        throw throwable3;
                    }
                }
                catch (FileNotFoundException fileNotFoundException) {
                    return Optional.empty();
                }
                catch (Throwable throwable5) {
                    exceptionConsumer.accept(throwable5);
                    return Optional.empty();
                }
            }
        }
    }

    private StructureTemplate readTemplate(InputStream templateIInputStream) throws IOException {
        NbtCompound nbtCompound = NbtIo.readCompressed(templateIInputStream, NbtSizeTracker.ofUnlimitedBytes());
        return this.createTemplate(nbtCompound);
    }

    public StructureTemplate createTemplate(NbtCompound nbt) {
        StructureTemplate structureTemplate = new StructureTemplate();
        int i = NbtHelper.getDataVersion(nbt, 500);
        structureTemplate.readNbt(this.blockLookup, DataFixTypes.STRUCTURE.update(this.dataFixer, nbt, 1));
        return structureTemplate;
    }

    public boolean saveTemplate(Identifier id) {
        Optional<StructureTemplate> optional = this.templates.get(id);
        if (optional.isEmpty()) {
            return false;
        }
        StructureTemplate structureTemplate = optional.get();
        Path path = this.getTemplatePath(id, NBT_FILE_EXTENSION);
        Path path2 = path.getParent();
        if (path2 == null) {
            return false;
        }
        try {
            Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath(new LinkOption[0]) : path2, new FileAttribute[0]);
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to create parent directory: {}", (Object)path2);
            return false;
        }
        NbtCompound nbtCompound = structureTemplate.writeNbt(new NbtCompound());
        try (FileOutputStream outputStream = new FileOutputStream(path.toFile());){
            NbtIo.writeCompressed(nbtCompound, outputStream);
        }
        catch (Throwable throwable) {
            return false;
        }
        return true;
    }

    public Path getTemplatePath(Identifier id, String extension) {
        if (id.getPath().contains("//")) {
            throw new InvalidIdentifierException("Invalid resource path: " + String.valueOf(id));
        }
        try {
            Path path = this.generatedPath.resolve(id.getNamespace());
            Path path2 = path.resolve(STRUCTURES_DIRECTORY);
            Path path3 = PathUtil.getResourcePath(path2, id.getPath(), extension);
            if (!(path3.startsWith(this.generatedPath) && PathUtil.isNormal(path3) && PathUtil.isAllowedName(path3))) {
                throw new InvalidIdentifierException("Invalid resource path: " + String.valueOf(path3));
            }
            return path3;
        }
        catch (InvalidPathException invalidPathException) {
            throw new InvalidIdentifierException("Invalid resource path: " + String.valueOf(id), invalidPathException);
        }
    }

    public void unloadTemplate(Identifier id) {
        this.templates.remove(id);
    }

    record Provider(Function<Identifier, Optional<StructureTemplate>> loader, Supplier<Stream<Identifier>> lister) {
    }

    @FunctionalInterface
    static interface TemplateFileOpener {
        public InputStream open() throws IOException;
    }
}

