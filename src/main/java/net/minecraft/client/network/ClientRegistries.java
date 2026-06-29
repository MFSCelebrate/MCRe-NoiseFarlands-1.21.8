/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ClientRegistries {
    @Nullable
    private DynamicRegistries dynamicRegistries;
    @Nullable
    private Tags tags;

    public void putDynamicRegistry(RegistryKey<? extends Registry<?>> registryRef, List<SerializableRegistries.SerializedRegistryEntry> entries) {
        if (this.dynamicRegistries == null) {
            this.dynamicRegistries = new DynamicRegistries();
        }
        this.dynamicRegistries.put(registryRef, entries);
    }

    public void putTags(Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> tags) {
        if (this.tags == null) {
            this.tags = new Tags();
        }
        tags.forEach(this.tags::put);
    }

    private static <T> Registry.PendingTagLoad<T> startTagReload(DynamicRegistryManager.Immutable registryManager, RegistryKey<? extends Registry<? extends T>> registryRef, TagPacketSerializer.Serialized tags) {
        RegistryWrapper.Impl registry = registryManager.net_minecraft_registry_RegistryWrapper$Impl_getOrThrow((RegistryKey)registryRef);
        return registry.startTagReload(tags.toRegistryTags(registry));
    }

    private DynamicRegistryManager createRegistryManager(ResourceFactory resourceFactory, DynamicRegistries dynamicRegistries, boolean local) {
        DynamicRegistryManager.Immutable immutable2;
        CombinedDynamicRegistries<ClientDynamicRegistryType> combinedDynamicRegistries = ClientDynamicRegistryType.createCombinedDynamicRegistries();
        DynamicRegistryManager.Immutable immutable = combinedDynamicRegistries.getPrecedingRegistryManagers(ClientDynamicRegistryType.REMOTE);
        HashMap map = new HashMap();
        dynamicRegistries.dynamicRegistries.forEach((registryRef, entries) -> map.put((RegistryKey<? extends Registry<?>>)registryRef, new RegistryLoader.ElementsAndTags((List<SerializableRegistries.SerializedRegistryEntry>)entries, TagPacketSerializer.Serialized.NONE)));
        ArrayList list = new ArrayList();
        if (this.tags != null) {
            this.tags.forEach((registryRef, tags) -> {
                if (tags.isEmpty()) {
                    return;
                }
                if (SerializableRegistries.isSynced(registryRef)) {
                    map.compute((RegistryKey<? extends Registry<?>>)registryRef, (key, value) -> {
                        List<SerializableRegistries.SerializedRegistryEntry> list = value != null ? value.elements() : List.of();
                        return new RegistryLoader.ElementsAndTags(list, (TagPacketSerializer.Serialized)tags);
                    });
                } else if (!local) {
                    list.add(ClientRegistries.startTagReload(immutable, registryRef, tags));
                }
            });
        }
        List<RegistryWrapper.Impl<?>> list2 = TagGroupLoader.collectRegistries(immutable, list);
        try {
            immutable2 = RegistryLoader.loadFromNetwork(map, resourceFactory, list2, RegistryLoader.SYNCED_REGISTRIES).toImmutable();
        }
        catch (Exception exception) {
            CrashReport crashReport = CrashReport.create(exception, "Network Registry Load");
            ClientRegistries.addCrashReportSection(crashReport, map, list);
            throw new CrashException(crashReport);
        }
        DynamicRegistryManager.Immutable dynamicRegistryManager = combinedDynamicRegistries.with(ClientDynamicRegistryType.REMOTE, immutable2).getCombinedRegistryManager();
        list.forEach(Registry.PendingTagLoad::apply);
        return dynamicRegistryManager;
    }

    private static void addCrashReportSection(CrashReport crashReport, Map<RegistryKey<? extends Registry<?>>, RegistryLoader.ElementsAndTags> data, List<Registry.PendingTagLoad<?>> tags) {
        CrashReportSection crashReportSection = crashReport.addElement("Received Elements and Tags");
        crashReportSection.add("Dynamic Registries", () -> data.entrySet().stream().sorted(Comparator.comparing(entry -> ((RegistryKey)entry.getKey()).getValue())).map(entry -> String.format(Locale.ROOT, "\n\t\t%s: elements=%d tags=%d", ((RegistryKey)entry.getKey()).getValue(), ((RegistryLoader.ElementsAndTags)entry.getValue()).elements().size(), ((RegistryLoader.ElementsAndTags)entry.getValue()).tags().size())).collect(Collectors.joining()));
        crashReportSection.add("Static Registries", () -> tags.stream().sorted(Comparator.comparing(tag -> tag.getKey().getValue())).map(tag -> String.format(Locale.ROOT, "\n\t\t%s: tags=%d", tag.getKey().getValue(), tag.size())).collect(Collectors.joining()));
    }

    private void loadTags(Tags tags, DynamicRegistryManager.Immutable registryManager, boolean local) {
        tags.forEach((registryRef, serialized) -> {
            if (local || SerializableRegistries.isSynced(registryRef)) {
                ClientRegistries.startTagReload(registryManager, registryRef, serialized).apply();
            }
        });
    }

    public DynamicRegistryManager.Immutable createRegistryManager(ResourceFactory resourceFactory, DynamicRegistryManager.Immutable registryManager, boolean local) {
        DynamicRegistryManager dynamicRegistryManager;
        if (this.dynamicRegistries != null) {
            dynamicRegistryManager = this.createRegistryManager(resourceFactory, this.dynamicRegistries, local);
        } else {
            if (this.tags != null) {
                this.loadTags(this.tags, registryManager, !local);
            }
            dynamicRegistryManager = registryManager;
        }
        return dynamicRegistryManager.toImmutable();
    }

    @Environment(value=EnvType.CLIENT)
    static class DynamicRegistries {
        final Map<RegistryKey<? extends Registry<?>>, List<SerializableRegistries.SerializedRegistryEntry>> dynamicRegistries = new HashMap();

        DynamicRegistries() {
        }

        public void put(RegistryKey<? extends Registry<?>> registryRef, List<SerializableRegistries.SerializedRegistryEntry> entries) {
            this.dynamicRegistries.computeIfAbsent(registryRef, registries -> new ArrayList()).addAll(entries);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Tags {
        final private Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> tags = new HashMap();

        Tags() {
        }

        public void put(RegistryKey<? extends Registry<?>> registryRef, TagPacketSerializer.Serialized tags) {
            this.tags.put(registryRef, tags);
        }

        public void forEach(BiConsumer<? super RegistryKey<? extends Registry<?>>, ? super TagPacketSerializer.Serialized> consumer) {
            this.tags.forEach(consumer);
        }
    }
}

