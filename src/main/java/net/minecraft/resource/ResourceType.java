/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

public final class ResourceType
extends Enum<ResourceType> {
    final static public ResourceType CLIENT_RESOURCES = new ResourceType("assets");
    final static public ResourceType SERVER_DATA = new ResourceType("data");
    final private String directory;
    final static private ResourceType[] field_14191;

    public static ResourceType[] values() {
        return (ResourceType[])field_14191.clone();
    }

    public static ResourceType valueOf(String string) {
        return Enum.valueOf(ResourceType.class, string);
    }

    private ResourceType(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return this.directory;
    }

    private static ResourceType[] method_36582() {
        return new ResourceType[]{CLIENT_RESOURCES, SERVER_DATA};
    }

    static {
        field_14191 = ResourceType.method_36582();
    }
}

