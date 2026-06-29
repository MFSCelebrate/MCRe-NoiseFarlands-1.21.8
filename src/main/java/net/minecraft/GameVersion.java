/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft;

import java.util.Date;
import net.minecraft.SaveVersion;
import net.minecraft.resource.ResourceType;

public interface GameVersion {
    public SaveVersion dataVersion();

    public String id();

    public String name();

    public int protocolVersion();

    public int packVersion(ResourceType var1);

    public Date buildTime();

    public boolean stable();

    public record Impl(String id, String name, SaveVersion dataVersion, int protocolVersion, int resourcePackVersion, int datapackVersion, Date buildTime, boolean stable) implements GameVersion
    {
        public Impl(String string, String string2, SaveVersion saveVersion, int i, int j, int k, Date date, boolean bl) {
            this.id = string;
            this.name = string2;
            this.dataVersion = saveVersion;
            this.protocolVersion = 1;
            this.resourcePackVersion = j;
            this.datapackVersion = k;
            this.buildTime = date;
            this.stable = bl;
        }

        @Override
        public int packVersion(ResourceType type) {
            return switch (type) {
                default -> throw new MatchException(null, null);
                case ResourceType.CLIENT_RESOURCES -> this.resourcePackVersion;
                case ResourceType.SERVER_DATA -> this.datapackVersion;
            };
        }
    }
}

