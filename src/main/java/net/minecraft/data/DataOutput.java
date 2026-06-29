/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class DataOutput {
    final private Path path;

    public DataOutput(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return this.path;
    }

    public Path resolvePath(OutputType outputType) {
        return this.getPath().resolve(outputType.path);
    }

    public PathResolver getResolver(OutputType outputType, String directoryName) {
        return new PathResolver(this, outputType, directoryName);
    }

    public PathResolver getResolver(RegistryKey<? extends Registry<?>> registryRef) {
        return this.getResolver(OutputType.DATA_PACK, RegistryKeys.getPath(registryRef));
    }

    public PathResolver getTagResolver(RegistryKey<? extends Registry<?>> registryRef) {
        return this.getResolver(OutputType.DATA_PACK, RegistryKeys.getTagPath(registryRef));
    }

    public static final class OutputType
    extends Enum<OutputType> {
        final static public OutputType DATA_PACK = new OutputType("data");
        final static public OutputType RESOURCE_PACK = new OutputType("assets");
        final static public OutputType REPORTS = new OutputType("reports");
        final String path;
        final static private OutputType[] field_39371;

        public static OutputType[] values() {
            return (OutputType[])field_39371.clone();
        }

        public static OutputType valueOf(String string) {
            return Enum.valueOf(OutputType.class, string);
        }

        private OutputType(String path) {
            this.path = path;
        }

        private static OutputType[] method_44109() {
            return new OutputType[]{DATA_PACK, RESOURCE_PACK, REPORTS};
        }

        static {
            field_39371 = OutputType.method_44109();
        }
    }

    public static class PathResolver {
        final private Path rootPath;
        final private String directoryName;

        PathResolver(DataOutput dataGenerator, OutputType outputType, String directoryName) {
            this.rootPath = dataGenerator.resolvePath(outputType);
            this.directoryName = directoryName;
        }

        public Path resolve(Identifier id, String fileExtension) {
            return this.rootPath.resolve(id.getNamespace()).resolve(this.directoryName).resolve(id.getPath() + "." + fileExtension);
        }

        public Path resolveJson(Identifier id) {
            return this.rootPath.resolve(id.getNamespace()).resolve(this.directoryName).resolve(id.getPath() + ".json");
        }

        public Path resolveJson(RegistryKey<?> key) {
            return this.rootPath.resolve(key.getValue().getNamespace()).resolve(this.directoryName).resolve(key.getValue().getPath() + ".json");
        }
    }
}

