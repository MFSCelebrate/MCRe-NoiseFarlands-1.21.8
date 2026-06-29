/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectFunction
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedGeometry;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.Geometry;
import net.minecraft.client.render.model.MissingModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ReferencedModelsCollector {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private Object2ObjectMap<Identifier, Holder> modelCache = new Object2ObjectOpenHashMap();
    final private Holder missingModel;
    final private Object2ObjectFunction<Identifier, Holder> holder;
    final private ResolvableModel.Resolver resolver;
    final private Queue<Holder> queue = new ArrayDeque<Holder>();

    public ReferencedModelsCollector(Map<Identifier, UnbakedModel> unbakedModels, UnbakedModel missingModel) {
        this.missingModel = new Holder(MissingModel.ID, missingModel, true);
        this.modelCache.put((Object)MissingModel.ID, (Object)this.missingModel);
        this.holder = id -> {
            Identifier identifier = (Identifier)id;
            UnbakedModel unbakedModel = (UnbakedModel)unbakedModels.get(identifier);
            if (unbakedModel == null) {
                LOGGER.warn("Missing block model: {}", (Object)identifier);
                return this.missingModel;
            }
            return this.schedule(identifier, unbakedModel);
        };
        this.resolver = this::resolve;
    }

    private static boolean isRootModel(UnbakedModel model) {
        return model.parent() == null;
    }

    private Holder resolve(Identifier id) {
        return (Holder)this.modelCache.computeIfAbsent((Object)id, this.holder);
    }

    private Holder schedule(Identifier id, UnbakedModel model) {
        boolean bl = ReferencedModelsCollector.isRootModel(model);
        Holder holder = new Holder(id, model, bl);
        if (!bl) {
            this.queue.add(holder);
        }
        return holder;
    }

    public void resolve(ResolvableModel model) {
        model.resolve(this.resolver);
    }

    public void addSpecialModel(Identifier id, UnbakedModel model) {
        if (!ReferencedModelsCollector.isRootModel(model)) {
            LOGGER.warn("Trying to add non-root special model {}, ignoring", (Object)id);
            return;
        }
        Holder holder = (Holder)this.modelCache.put((Object)id, (Object)this.schedule(id, model));
        if (holder != null) {
            LOGGER.warn("Duplicate special model {}", (Object)id);
        }
    }

    public BakedSimpleModel getMissingModel() {
        return this.missingModel;
    }

    public Map<Identifier, BakedSimpleModel> collectModels() {
        ArrayList<Holder> list = new ArrayList<Holder>();
        this.resolveAll(list);
        ReferencedModelsCollector.checkIfValid(list);
        ImmutableMap.Builder builder = ImmutableMap.builder();
        this.modelCache.forEach((id, model) -> {
            if (model.valid) {
                builder.put(id, model);
            } else {
                LOGGER.warn("Model {} ignored due to cyclic dependency", id);
            }
        });
        return builder.build();
    }

    private void resolveAll(List<Holder> models) {
        Holder holder;
        while ((holder = this.queue.poll()) != null) {
            Holder holder2;
            Identifier identifier = Objects.requireNonNull(holder.model.parent());
            holder.parent = holder2 = this.resolve(identifier);
            if (holder2.valid) {
                holder.valid = true;
                continue;
            }
            models.add(holder);
        }
    }

    private static void checkIfValid(List<Holder> models) {
        boolean bl = true;
        while (bl) {
            bl = false;
            Iterator<Holder> iterator = models.iterator();
            while (iterator.hasNext()) {
                Holder holder = iterator.next();
                if (!Objects.requireNonNull(holder.parent).valid) continue;
                holder.valid = true;
                iterator.remove();
                bl = true;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Holder
    implements BakedSimpleModel {
        final static private Property<Boolean> AMBIENT_OCCLUSION_PROPERTY = Holder.createProperty(0);
        final static private Property<UnbakedModel.GuiLight> GUI_LIGHT_PROPERTY = Holder.createProperty(1);
        final static private Property<Geometry> GEOMETRY_PROPERTY = Holder.createProperty(2);
        final static private Property<ModelTransformation> TRANSFORMATIONS_PROPERTY = Holder.createProperty(3);
        final static private Property<ModelTextures> TEXTURE_PROPERTY = Holder.createProperty(4);
        final static private Property<Sprite> PARTICLE_TEXTURE_PROPERTY = Holder.createProperty(5);
        final static private Property<BakedGeometry> BAKED_GEOMETRY_PROPERTY = Holder.createProperty(6);
        final static private int PROPERTY_COUNT = 7;
        final private Identifier id;
        boolean valid;
        @Nullable
        Holder parent;
        final UnbakedModel model;
        final private AtomicReferenceArray<Object> properties = new AtomicReferenceArray(7);
        final private Map<ModelBakeSettings, BakedGeometry> bakeCache = new ConcurrentHashMap<ModelBakeSettings, BakedGeometry>();

        private static <T> Property<T> createProperty(int i) {
            Objects.checkIndex(i, 7);
            return new Property(i);
        }

        Holder(Identifier id, UnbakedModel model, boolean valid) {
            this.id = id;
            this.model = model;
            this.valid = valid;
        }

        @Override
        public UnbakedModel getModel() {
            return this.model;
        }

        @Override
        @Nullable
        public BakedSimpleModel getParent() {
            return this.parent;
        }

        @Override
        public String name() {
            return this.id.toString();
        }

        @Nullable
        private <T> T getProperty(Property<T> property) {
            return (T)this.properties.get(property.index);
        }

        private <T> T setProperty(Property<T> property, T value) {
            T object = this.properties.compareAndExchange(property.index, null, value);
            if (object == null) {
                return value;
            }
            return object;
        }

        private <T> T getProperty(Property<T> property, Function<BakedSimpleModel, T> fallback) {
            T object = this.getProperty(property);
            if (object != null) {
                return object;
            }
            return this.setProperty(property, fallback.apply(this));
        }

        @Override
        public boolean getAmbientOcclusion() {
            return this.getProperty(AMBIENT_OCCLUSION_PROPERTY, BakedSimpleModel::getAmbientOcclusion);
        }

        @Override
        public UnbakedModel.GuiLight getGuiLight() {
            return this.getProperty(GUI_LIGHT_PROPERTY, BakedSimpleModel::getGuiLight);
        }

        @Override
        public ModelTransformation getTransformations() {
            return this.getProperty(TRANSFORMATIONS_PROPERTY, BakedSimpleModel::copyTransformations);
        }

        @Override
        public Geometry getGeometry() {
            return this.getProperty(GEOMETRY_PROPERTY, BakedSimpleModel::getGeometry);
        }

        @Override
        public ModelTextures getTextures() {
            return this.getProperty(TEXTURE_PROPERTY, BakedSimpleModel::getTextures);
        }

        @Override
        public Sprite getParticleTexture(ModelTextures textures, Baker baker) {
            Sprite sprite = this.getProperty(PARTICLE_TEXTURE_PROPERTY);
            if (sprite != null) {
                return sprite;
            }
            return this.setProperty(PARTICLE_TEXTURE_PROPERTY, BakedSimpleModel.getParticleTexture(textures, baker, this));
        }

        private BakedGeometry getBakedGeometry(ModelTextures textures, Baker baker, ModelBakeSettings settings) {
            BakedGeometry bakedGeometry = this.getProperty(BAKED_GEOMETRY_PROPERTY);
            if (bakedGeometry != null) {
                return bakedGeometry;
            }
            return this.setProperty(BAKED_GEOMETRY_PROPERTY, this.getGeometry().bake(textures, baker, settings, this));
        }

        @Override
        public BakedGeometry bakeGeometry(ModelTextures textures, Baker baker, ModelBakeSettings settings) {
            if (settings == ModelRotation.X0_Y0) {
                return this.getBakedGeometry(textures, baker, settings);
            }
            return this.bakeCache.computeIfAbsent(settings, settings1 -> {
                Geometry geometry = this.getGeometry();
                return geometry.bake(textures, baker, (ModelBakeSettings)settings1, this);
            });
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Property<T>
    extends Record {
        final int index;

        Property(int i) {
            this.index = i;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Property.class, "index", "index"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Property.class, "index", "index"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Property.class, "index", "index"}, this, object);
        }

        public int index() {
            return this.index;
        }
    }
}

