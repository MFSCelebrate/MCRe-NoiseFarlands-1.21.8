package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

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
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String STRUCTURE_DIRECTORY = "structure";
    private static final String STRUCTURES_DIRECTORY = "structures";
    private static final String NBT_FILE_EXTENSION = ".nbt";
    private static final String SNBT_FILE_EXTENSION = ".snbt";
    private final Map<Identifier, Optional<StructureTemplate>> templates = Maps.newConcurrentMap();
    private final DataFixer dataFixer;
    private ResourceManager resourceManager;
    private final Path generatedPath;
    private final List<Provider> providers;
    private final RegistryEntryLookup<Block> blockLookup;
    private static final ResourceFinder STRUCTURE_NBT_RESOURCE_FINDER = new ResourceFinder("structure", ".nbt");

    public StructureTemplateManager(ResourceManager resourceManager, LevelStorage.Session session, DataFixer dataFixer, RegistryEntryLookup<Block> blockLookup) {
        this.resourceManager = resourceManager;
        this.dataFixer = dataFixer;
        this.generatedPath = session.getDirectory(WorldSavePath.GENERATED).normalize();
        this.blockLookup = blockLookup;
        ImmutableList.Builder<Provider> builder = ImmutableList.builder();
        builder.add(new Provider(this::loadTemplateFromFile, this::streamTemplatesFromFile));
        if (SharedConstants.isDevelopment) {
            builder.add(new Provider(this::loadTemplateFromGameTestFile, this::streamTemplatesFromGameTestFile));
        }
        builder.add(new Provider(this::loadTemplateFromResource, this::streamTemplatesFromResource));
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
            } catch (Exception ignored) {}
        }
        return Optional.empty();
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.templates.clear();
    }

    private Optional<StructureTemplate> loadTemplateFromResource(Identifier id) {
        Identifier identifier = STRUCTURE_NBT_RESOURCE_FINDER.toResourcePath(id);
        return this.loadTemplate(() -> this.resourceManager.open(identifier), throwable -> LOGGER.error("Couldn't load structure {}", id, throwable));
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
        List<Identifier> list = new ArrayList<>();
        this.streamTemplates(TestInstanceUtil.testStructuresDirectoryName, "minecraft", SNBT_FILE_EXTENSION, list::add);
        return list.stream();
    }

    private Optional<StructureTemplate> loadTemplateFromFile(Identifier id) {
        if (!Files.isDirectory(this.generatedPath, new LinkOption[0])) {
            return Optional.empty();
        }
        Path path = this.getTemplatePath(id, NBT_FILE_EXTENSION);
        return this.loadTemplate(() -> new FileInputStream(path.toFile()), throwable -> LOGGER.error("Couldn't load structure from {}", path, throwable));
    }

    private Stream<Identifier> streamTemplatesFromFile() {
        if (!Files.isDirectory(this.generatedPath, new LinkOption[0])) {
            return Stream.empty();
        }
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(this.generatedPath, path -> Files.isDirectory(path, new LinkOption[0]))) {
            List<Identifier> list = new ArrayList<>();
            for (Path path : directoryStream) {
                String namespace = path.getFileName().toString();
                Path structuresPath = path.resolve(STRUCTURES_DIRECTORY);
                this.streamTemplates(structuresPath, namespace, NBT_FILE_EXTENSION, list::add);
            }
            return list.stream();
        } catch (IOException e) {
            LOGGER.error("Failed to list folder contents", e);
            return Stream.empty();
        }
    }

    private void streamTemplates(Path directory, String namespace, String fileExtension, Consumer<Identifier> idConsumer) {
        int extLen = fileExtension.length();
        Function<String, String> stripExt = filename -> filename.substring(0, filename.length() - extLen);
        try (Stream<Path> stream = Files.find(directory, Integer.MAX_VALUE, (path, attrs) -> attrs.isRegularFile() && path.toString().endsWith(fileExtension), new FileVisitOption[0])) {
            stream.forEach(path -> {
                try {
                    String relative = this.toRelativePath(directory, path);
                    idConsumer.accept(Identifier.of(namespace, stripExt.apply(relative)));
                } catch (InvalidIdentifierException e) {
                    LOGGER.error("Invalid location while listing folder {} contents", directory, e);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to list folder {} contents", directory, e);
        }
    }

    private String toRelativePath(Path root, Path path) {
        return root.relativize(path).toString().replace(File.separator, "/");
    }

    private Optional<StructureTemplate> loadTemplateFromSnbt(Identifier id, Path path) {
        if (!Files.isDirectory(path, new LinkOption[0])) {
            return Optional.empty();
        }
        Path snbtPath = PathUtil.getResourcePath(path, id.getPath(), SNBT_FILE_EXTENSION);
        try (BufferedReader reader = Files.newBufferedReader(snbtPath)) {
            String content = IOUtils.toString(reader);
            NbtCompound nbt = NbtHelper.fromNbtProviderString(content);
            return Optional.of(this.createTemplate(nbt));
        } catch (NoSuchFileException e) {
            return Optional.empty();
        } catch (CommandSyntaxException | IOException e) {
            LOGGER.error("Couldn't load structure from {}", snbtPath, e);
            return Optional.empty();
        }
    }

    // ===================== 修复后的 loadTemplate =====================
    private Optional<StructureTemplate> loadTemplate(TemplateFileOpener opener, Consumer<Throwable> exceptionConsumer) {
        InputStream inputStream = null;
        FixedBufferInputStream inputStream2 = null;
        try {
            inputStream = opener.open();
            inputStream2 = new FixedBufferInputStream(inputStream);
            return Optional.of(this.readTemplate(inputStream2));
        } catch (FileNotFoundException e) {
            return Optional.empty();
        } catch (Throwable t) {
            exceptionConsumer.accept(t);
            return Optional.empty();
        } finally {
            try {
                if (inputStream2 != null) inputStream2.close();
            } catch (Throwable ignored) {}
            try {
                if (inputStream != null) inputStream.close();
            } catch (Throwable ignored) {}
        }
    }

    private StructureTemplate readTemplate(InputStream templateInputStream) throws IOException {
        NbtCompound nbtCompound = NbtIo.readCompressed(templateInputStream, NbtSizeTracker.ofUnlimitedBytes());
        return this.createTemplate(nbtCompound);
    }

    public StructureTemplate createTemplate(NbtCompound nbt) {
        StructureTemplate structureTemplate = new StructureTemplate();
        int i = NbtHelper.getDataVersion(nbt, 500);
        structureTemplate.readNbt(this.blockLookup, DataFixTypes.STRUCTURE.update(this.dataFixer, nbt, i));
        return structureTemplate;
    }

    public boolean saveTemplate(Identifier id) {
        Optional<StructureTemplate> optional = this.templates.get(id);
        if (optional.isEmpty()) {
            return false;
        }
        StructureTemplate structureTemplate = optional.get();
        Path path = this.getTemplatePath(id, NBT_FILE_EXTENSION);
        Path parent = path.getParent();
        if (parent == null) {
            return false;
        }
        try {
            Files.createDirectories(Files.exists(parent, new LinkOption[0]) ? parent.toRealPath(new LinkOption[0]) : parent);
        } catch (IOException e) {
            LOGGER.error("Failed to create parent directory: {}", parent);
            return false;
        }
        NbtCompound nbtCompound = structureTemplate.writeNbt(new NbtCompound());
        try (FileOutputStream outputStream = new FileOutputStream(path.toFile())) {
            NbtIo.writeCompressed(nbtCompound, outputStream);
        } catch (IOException e) {
            LOGGER.error("Failed to save structure {}", id, e);
            return false;
        }
        return true;
    }

    public Path getTemplatePath(Identifier id, String extension) {
        if (id.getPath().contains("//")) {
            throw new InvalidIdentifierException("Invalid resource path: " + id);
        }
        try {
            Path path = this.generatedPath.resolve(id.getNamespace());
            Path structuresPath = path.resolve(STRUCTURES_DIRECTORY);
            Path result = PathUtil.getResourcePath(structuresPath, id.getPath(), extension);
            if (!result.startsWith(this.generatedPath) || !PathUtil.isNormal(result) || !PathUtil.isAllowedName(result)) {
                throw new InvalidIdentifierException("Invalid resource path: " + result);
            }
            return result;
        } catch (InvalidPathException e) {
            throw new InvalidIdentifierException("Invalid resource path: " + id, e);
        }
    }

    public void unloadTemplate(Identifier id) {
        this.templates.remove(id);
    }

    record Provider(Function<Identifier, Optional<StructureTemplate>> loader, Supplier<Stream<Identifier>> lister) {
    }

    @FunctionalInterface
    interface TemplateFileOpener {
        InputStream open() throws IOException;
    }
}