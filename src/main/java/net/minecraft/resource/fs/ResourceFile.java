/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.fs;

import java.nio.file.Path;
import java.util.Map;
import net.minecraft.resource.fs.ResourcePath;

interface ResourceFile {
    final static public ResourceFile EMPTY = new ResourceFile(){

        public String toString() {
            return "empty";
        }
    };
    final static public ResourceFile RELATIVE = new ResourceFile(){

        public String toString() {
            return "relative";
        }
    };

    public record Directory(Map<String, ResourcePath> children) implements ResourceFile
    {
    }

    public record File(Path contents) implements ResourceFile
    {
    }
}

