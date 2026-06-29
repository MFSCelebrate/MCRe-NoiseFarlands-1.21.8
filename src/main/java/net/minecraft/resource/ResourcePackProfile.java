/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.resource;

import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackPosition;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.metadata.PackFeatureSetMetadata;
import net.minecraft.resource.metadata.PackOverlaysMetadata;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Range;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ResourcePackProfile {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private ResourcePackInfo info;
    final private PackFactory packFactory;
    final private Metadata metaData;
    final private ResourcePackPosition position;

    @Nullable
    public static ResourcePackProfile create(ResourcePackInfo info, PackFactory packFactory, ResourceType type, ResourcePackPosition position) {
        int i = SharedConstants.getGameVersion().packVersion(type);
        Metadata metadata = ResourcePackProfile.loadMetadata(info, packFactory, 1);
        return metadata != null ? new ResourcePackProfile(info, packFactory, metadata, position) : null;
    }

    public ResourcePackProfile(ResourcePackInfo info, PackFactory packFactory, Metadata metaData, ResourcePackPosition position) {
        this.info = info;
        this.packFactory = packFactory;
        this.metaData = metaData;
        this.position = position;
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static Metadata loadMetadata(ResourcePackInfo info, PackFactory packFactory, int currentPackFormat) {
        try {
            PackResourceMetadata packResourceMetadata;
            ResourcePack resourcePack = packFactory.open(info);
            try {
                packResourceMetadata = resourcePack.parseMetadata(PackResourceMetadata.SERIALIZER);
                if (packResourceMetadata == null) {
                    LOGGER.warn("Missing metadata in pack {}", (Object)info.id());
                    Metadata metadata = null;
                    if (resourcePack == null) return metadata;
                    resourcePack.close();
                    return metadata;
                }
            }
            catch (Throwable throwable) {
                if (resourcePack == null) throw throwable;
                try {
                    resourcePack.close();
                    throw throwable;
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            {
                PackFeatureSetMetadata packFeatureSetMetadata = resourcePack.parseMetadata(PackFeatureSetMetadata.SERIALIZER);
                FeatureSet featureSet = packFeatureSetMetadata != null ? packFeatureSetMetadata.flags() : FeatureSet.empty();
                Range<Integer> range = ResourcePackProfile.getSupportedFormats(info.id(), packResourceMetadata);
                ResourcePackCompatibility resourcePackCompatibility = ResourcePackCompatibility.from(range, currentPackFormat);
                PackOverlaysMetadata packOverlaysMetadata = resourcePack.parseMetadata(PackOverlaysMetadata.SERIALIZER);
                List<String> list = packOverlaysMetadata != null ? packOverlaysMetadata.getAppliedOverlays(currentPackFormat) : List.of();
                Metadata metadata = new Metadata(packResourceMetadata.description(), resourcePackCompatibility, featureSet, list);
                if (resourcePack == null) return metadata;
                resourcePack.close();
                return metadata;
            }
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to read pack {} metadata", (Object)info.id(), (Object)exception);
            return null;
        }
    }

    private static Range<Integer> getSupportedFormats(String packId, PackResourceMetadata metadata) {
        int i = metadata.packFormat();
        if (metadata.supportedFormats().isEmpty()) {
            return new Range<Integer>(i);
        }
        Range<Integer> range = metadata.supportedFormats().get();
        if (!range.contains(i)) {
            LOGGER.warn("Pack {} declared support for versions {} but declared main format is {}, defaulting to {}", new Object[]{packId, range, i, i});
            return new Range<Integer>(i);
        }
        return range;
    }

    public ResourcePackInfo getInfo() {
        return this.info;
    }

    public Text getDisplayName() {
        return this.info.title();
    }

    public Text getDescription() {
        return this.metaData.description();
    }

    public Text getInformationText(boolean enabled) {
        return this.info.getInformationText(enabled, this.metaData.description);
    }

    public ResourcePackCompatibility getCompatibility() {
        return this.metaData.compatibility();
    }

    public FeatureSet getRequestedFeatures() {
        return this.metaData.requestedFeatures();
    }

    public ResourcePack createResourcePack() {
        return this.packFactory.openWithOverlays(this.info, this.metaData);
    }

    public String getId() {
        return this.info.id();
    }

    public ResourcePackPosition getPosition() {
        return this.position;
    }

    public boolean isRequired() {
        return this.position.required();
    }

    public boolean isPinned() {
        return this.position.fixedPosition();
    }

    public InsertionPosition getInitialPosition() {
        return this.position.defaultPosition();
    }

    public ResourcePackSource getSource() {
        return this.info.source();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourcePackProfile)) {
            return false;
        }
        ResourcePackProfile resourcePackProfile = (ResourcePackProfile)o;
        return this.info.equals(resourcePackProfile.info);
    }

    public int hashCode() {
        return this.info.hashCode();
    }

    public static interface PackFactory {
        public ResourcePack open(ResourcePackInfo var1);

        public ResourcePack openWithOverlays(ResourcePackInfo var1, Metadata var2);
    }

    public static final class Metadata
    extends Record {
        final Text description;
        final private ResourcePackCompatibility compatibility;
        final private FeatureSet requestedFeatures;
        final private List<String> overlays;

        public Metadata(Text text, ResourcePackCompatibility resourcePackCompatibility, FeatureSet featureSet, List<String> list) {
            this.description = text;
            this.compatibility = resourcePackCompatibility;
            this.requestedFeatures = featureSet;
            this.overlays = list;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Metadata.class, "description;compatibility;requestedFeatures;overlays", "description", "compatibility", "requestedFeatures", "overlays"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Metadata.class, "description;compatibility;requestedFeatures;overlays", "description", "compatibility", "requestedFeatures", "overlays"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Metadata.class, "description;compatibility;requestedFeatures;overlays", "description", "compatibility", "requestedFeatures", "overlays"}, this, object);
        }

        public Text description() {
            return this.description;
        }

        public ResourcePackCompatibility compatibility() {
            return this.compatibility;
        }

        public FeatureSet requestedFeatures() {
            return this.requestedFeatures;
        }

        public List<String> overlays() {
            return this.overlays;
        }
    }

    public static final class InsertionPosition
    extends Enum<InsertionPosition> {
        final static public InsertionPosition TOP = new InsertionPosition();
        final static public InsertionPosition BOTTOM = new InsertionPosition();
        final static private InsertionPosition[] field_14282;

        public static InsertionPosition[] values() {
            return (InsertionPosition[])field_14282.clone();
        }

        public static InsertionPosition valueOf(String string) {
            return Enum.valueOf(InsertionPosition.class, string);
        }

        public <T> int insert(List<T> items, T item, Function<T, ResourcePackPosition> profileGetter, boolean listInverted) {
            ResourcePackPosition resourcePackPosition;
            int i;
            InsertionPosition insertionPosition;
            InsertionPosition insertionPosition2 = insertionPosition = listInverted ? this.inverse() : this;
            if (insertionPosition == BOTTOM) {
                ResourcePackPosition resourcePackPosition2;
                int i2;
                for (i2 = 0; i2 < items.size() && (resourcePackPosition2 = profileGetter.apply(items.get(i2))).fixedPosition() && resourcePackPosition2.defaultPosition() == this; ++i2) {
                }
                items.add(i2, item);
                return i2;
            }
            for (i = items.size() - 1; i >= 0 && (resourcePackPosition = profileGetter.apply(items.get(i))).fixedPosition() && resourcePackPosition.defaultPosition() == this; --i) {
            }
            items.add(i + 1, item);
            return i + 1;
        }

        public InsertionPosition inverse() {
            return this == TOP ? BOTTOM : TOP;
        }

        private static InsertionPosition[] method_36583() {
            return new InsertionPosition[]{TOP, BOTTOM};
        }

        static {
            field_14282 = InsertionPosition.method_36583();
        }
    }
}

