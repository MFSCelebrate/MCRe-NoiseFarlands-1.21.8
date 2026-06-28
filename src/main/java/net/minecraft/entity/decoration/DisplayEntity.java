/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.slf4j.Logger
 */
package net.minecraft.entity.decoration;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Texts;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

public abstract class DisplayEntity
extends Entity {
    final static Logger LOGGER = LogUtils.getLogger();
    final static public int field_42384 = -1;
    final static private TrackedData<Integer> START_INTERPOLATION = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
    final static private TrackedData<Integer> INTERPOLATION_DURATION = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
    final static private TrackedData<Integer> TELEPORT_DURATION = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
    final static private TrackedData<Vector3f> TRANSLATION = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.VECTOR_3F);
    final static private TrackedData<Vector3f> SCALE = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.VECTOR_3F);
    final static private TrackedData<Quaternionf> LEFT_ROTATION = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.QUATERNION_F);
    final static private TrackedData<Quaternionf> RIGHT_ROTATION = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.QUATERNION_F);
    final static private TrackedData<Byte> BILLBOARD = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.BYTE);
    final static private TrackedData<Integer> BRIGHTNESS = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
    final static private TrackedData<Float> VIEW_RANGE = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.FLOAT);
    final static private TrackedData<Float> SHADOW_RADIUS = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.FLOAT);
    final static private TrackedData<Float> SHADOW_STRENGTH = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.FLOAT);
    final static private TrackedData<Float> WIDTH = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.FLOAT);
    final static private TrackedData<Float> HEIGHT = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.FLOAT);
    final static private TrackedData<Integer> GLOW_COLOR_OVERRIDE = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
    final static private IntSet RENDERING_DATA_IDS = IntSet.of((int[])new int[]{TRANSLATION.id(), SCALE.id(), LEFT_ROTATION.id(), RIGHT_ROTATION.id(), BILLBOARD.id(), BRIGHTNESS.id(), SHADOW_RADIUS.id(), SHADOW_STRENGTH.id()});
    final static private int DEFAULT_INTERPOLATION_DURATION = 0;
    final static private int DEFAULT_START_INTERPOLATION = 0;
    final static private int field_56423 = 0;
    final static private float field_42376 = 0.0f;
    final static private float field_42377 = 1.0f;
    final static private float field_57575 = 1.0f;
    final static private float field_57576 = 0.0f;
    final static private float field_57577 = 0.0f;
    final static private int field_42378 = -1;
    final static public String TELEPORT_DURATION_KEY = "teleport_duration";
    final static public String INTERPOLATION_DURATION_KEY = "interpolation_duration";
    final static public String START_INTERPOLATION_KEY = "start_interpolation";
    final static public String TRANSFORMATION_NBT_KEY = "transformation";
    final static public String BILLBOARD_NBT_KEY = "billboard";
    final static public String BRIGHTNESS_NBT_KEY = "brightness";
    final static public String VIEW_RANGE_NBT_KEY = "view_range";
    final static public String SHADOW_RADIUS_NBT_KEY = "shadow_radius";
    final static public String SHADOW_STRENGTH_NBT_KEY = "shadow_strength";
    final static public String WIDTH_NBT_KEY = "width";
    final static public String HEIGHT_NBT_KEY = "height";
    final static public String GLOW_COLOR_OVERRIDE_NBT_KEY = "glow_color_override";
    private long interpolationStart = Integer.MIN_VALUE;
    private int interpolationDuration;
    private float lerpProgress;
    private Box visibilityBoundingBox;
    private boolean tooSmallToRender = true;
    protected boolean renderingDataSet;
    private boolean startInterpolationSet;
    private boolean interpolationDurationSet;
    @Nullable
    private RenderState renderProperties;
    final private PositionInterpolator interpolator = new PositionInterpolator((Entity)this, 0);

    public DisplayEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.noClip = true;
        this.visibilityBoundingBox = this.getBoundingBox();
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (HEIGHT.equals(data) || WIDTH.equals(data)) {
            this.updateVisibilityBoundingBox();
        }
        if (START_INTERPOLATION.equals(data)) {
            this.startInterpolationSet = true;
        }
        if (TELEPORT_DURATION.equals(data)) {
            this.interpolator.setLerpDuration(this.getTeleportDuration());
        }
        if (INTERPOLATION_DURATION.equals(data)) {
            this.interpolationDurationSet = true;
        }
        if (RENDERING_DATA_IDS.contains(data.id())) {
            this.renderingDataSet = true;
        }
    }

    @Override
    public final boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    public static AffineTransformation getTransformation(DataTracker dataTracker) {
        Vector3f vector3f = dataTracker.get(TRANSLATION);
        Quaternionf quaternionf = dataTracker.get(LEFT_ROTATION);
        Vector3f vector3f2 = dataTracker.get(SCALE);
        Quaternionf quaternionf2 = dataTracker.get(RIGHT_ROTATION);
        return new AffineTransformation(vector3f, quaternionf, vector3f2, quaternionf2);
    }

    @Override
    public void tick() {
        Entity entity = this.getVehicle();
        if (entity != null && entity.isRemoved()) {
            this.stopRiding();
        }
        if (this.net_minecraft_world_World_getWorld().isClient) {
            if (this.startInterpolationSet) {
                this.startInterpolationSet = false;
                int i = this.getStartInterpolation();
                this.interpolationStart = this.age + i;
            }
            if (this.interpolationDurationSet) {
                this.interpolationDurationSet = false;
                this.interpolationDuration = this.getInterpolationDuration();
            }
            if (this.renderingDataSet) {
                this.renderingDataSet = false;
                boolean bl = this.interpolationDuration != 0;
                this.renderProperties = bl && this.renderProperties != null ? this.getLerpedRenderState(this.renderProperties, this.lerpProgress) : this.copyRenderState();
                this.refreshData(bl, this.lerpProgress);
            }
            this.interpolator.tick();
        }
    }

    @Override
    public PositionInterpolator getInterpolator() {
        return this.interpolator;
    }

    protected abstract void refreshData(boolean var1, float var2);

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(TELEPORT_DURATION, 0);
        builder.add(START_INTERPOLATION, 0);
        builder.add(INTERPOLATION_DURATION, 0);
        builder.add(TRANSLATION, new Vector3f());
        builder.add(SCALE, new Vector3f(1.0f, 1.0f, 1.0f));
        builder.add(RIGHT_ROTATION, new Quaternionf());
        builder.add(LEFT_ROTATION, new Quaternionf());
        builder.add(BILLBOARD, BillboardMode.FIXED.getIndex());
        builder.add(BRIGHTNESS, -1);
        builder.add(VIEW_RANGE, Float.valueOf(1.0f));
        builder.add(SHADOW_RADIUS, Float.valueOf(0.0f));
        builder.add(SHADOW_STRENGTH, Float.valueOf(1.0f));
        builder.add(WIDTH, Float.valueOf(0.0f));
        builder.add(HEIGHT, Float.valueOf(0.0f));
        builder.add(GLOW_COLOR_OVERRIDE, -1);
    }

    @Override
    protected void readCustomData(ReadView view) {
        this.setTransformation(view.read(TRANSFORMATION_NBT_KEY, AffineTransformation.ANY_CODEC).orElse(AffineTransformation.identity()));
        this.setInterpolationDuration(view.getInt(INTERPOLATION_DURATION_KEY, 0));
        this.setStartInterpolation(view.getInt(START_INTERPOLATION_KEY, 0));
        int i = view.getInt(TELEPORT_DURATION_KEY, 0);
        this.setTeleportDuration(MathHelper.clamp(i, 0, 59));
        this.setBillboardMode(view.read(BILLBOARD_NBT_KEY, BillboardMode.CODEC).orElse(BillboardMode.FIXED));
        this.setViewRange(view.getFloat(VIEW_RANGE_NBT_KEY, 1.0f));
        this.setShadowRadius(view.getFloat(SHADOW_RADIUS_NBT_KEY, 0.0f));
        this.setShadowStrength(view.getFloat(SHADOW_STRENGTH_NBT_KEY, 1.0f));
        this.setDisplayWidth(view.getFloat(WIDTH_NBT_KEY, 0.0f));
        this.setDisplayHeight(view.getFloat(HEIGHT_NBT_KEY, 0.0f));
        this.setGlowColorOverride(view.getInt(GLOW_COLOR_OVERRIDE_NBT_KEY, -1));
        this.setBrightness(view.read(BRIGHTNESS_NBT_KEY, Brightness.CODEC).orElse(null));
    }

    public final void setTransformation(AffineTransformation transformation) {
        this.dataTracker.set(TRANSLATION, transformation.getTranslation());
        this.dataTracker.set(LEFT_ROTATION, transformation.getLeftRotation());
        this.dataTracker.set(SCALE, transformation.getScale());
        this.dataTracker.set(RIGHT_ROTATION, transformation.getRightRotation());
    }

    @Override
    protected void writeCustomData(WriteView view) {
        view.put(TRANSFORMATION_NBT_KEY, AffineTransformation.ANY_CODEC, DisplayEntity.getTransformation(this.dataTracker));
        view.put(BILLBOARD_NBT_KEY, BillboardMode.CODEC, this.getBillboardMode());
        view.putInt(INTERPOLATION_DURATION_KEY, this.getInterpolationDuration());
        view.putInt(TELEPORT_DURATION_KEY, this.getTeleportDuration());
        view.putFloat(VIEW_RANGE_NBT_KEY, this.getViewRange());
        view.putFloat(SHADOW_RADIUS_NBT_KEY, this.getShadowRadius());
        view.putFloat(SHADOW_STRENGTH_NBT_KEY, this.getShadowStrength());
        view.putFloat(WIDTH_NBT_KEY, this.getDisplayWidth());
        view.putFloat(HEIGHT_NBT_KEY, this.getDisplayHeight());
        view.putInt(GLOW_COLOR_OVERRIDE_NBT_KEY, this.getGlowColorOverride());
        view.putNullable(BRIGHTNESS_NBT_KEY, Brightness.CODEC, this.getBrightnessUnpacked());
    }

    public Box getVisibilityBoundingBox() {
        return this.visibilityBoundingBox;
    }

    public boolean shouldRender() {
        return !this.tooSmallToRender;
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.IGNORE;
    }

    @Override
    public boolean canAvoidTraps() {
        return true;
    }

    @Nullable
    public RenderState getRenderState() {
        return this.renderProperties;
    }

    public final void setInterpolationDuration(int interpolationDuration) {
        this.dataTracker.set(INTERPOLATION_DURATION, interpolationDuration);
    }

    public final int getInterpolationDuration() {
        return this.dataTracker.get(INTERPOLATION_DURATION);
    }

    public final void setStartInterpolation(int startInterpolation) {
        this.dataTracker.set(START_INTERPOLATION, startInterpolation, true);
    }

    public final int getStartInterpolation() {
        return this.dataTracker.get(START_INTERPOLATION);
    }

    public final void setTeleportDuration(int teleportDuration) {
        this.dataTracker.set(TELEPORT_DURATION, teleportDuration);
    }

    public final int getTeleportDuration() {
        return this.dataTracker.get(TELEPORT_DURATION);
    }

    public final void setBillboardMode(BillboardMode billboardMode) {
        this.dataTracker.set(BILLBOARD, billboardMode.getIndex());
    }

    public final BillboardMode getBillboardMode() {
        return BillboardMode.FROM_INDEX.apply(this.dataTracker.get(BILLBOARD).byteValue());
    }

    public final void setBrightness(@Nullable Brightness brightness) {
        this.dataTracker.set(BRIGHTNESS, brightness != null ? brightness.pack() : -1);
    }

    @Nullable
    public final Brightness getBrightnessUnpacked() {
        int i = this.dataTracker.get(BRIGHTNESS);
        return i != -1 ? Brightness.unpack(i) : null;
    }

    public final int getBrightness() {
        return this.dataTracker.get(BRIGHTNESS);
    }

    public final void setViewRange(float viewRange) {
        this.dataTracker.set(VIEW_RANGE, Float.valueOf(viewRange));
    }

    public final float getViewRange() {
        return this.dataTracker.get(VIEW_RANGE).floatValue();
    }

    public final void setShadowRadius(float shadowRadius) {
        this.dataTracker.set(SHADOW_RADIUS, Float.valueOf(shadowRadius));
    }

    public final float getShadowRadius() {
        return this.dataTracker.get(SHADOW_RADIUS).floatValue();
    }

    public final void setShadowStrength(float shadowStrength) {
        this.dataTracker.set(SHADOW_STRENGTH, Float.valueOf(shadowStrength));
    }

    public final float getShadowStrength() {
        return this.dataTracker.get(SHADOW_STRENGTH).floatValue();
    }

    public final void setDisplayWidth(float width) {
        this.dataTracker.set(WIDTH, Float.valueOf(width));
    }

    public final float getDisplayWidth() {
        return this.dataTracker.get(WIDTH).floatValue();
    }

    public final void setDisplayHeight(float height) {
        this.dataTracker.set(HEIGHT, Float.valueOf(height));
    }

    public final int getGlowColorOverride() {
        return this.dataTracker.get(GLOW_COLOR_OVERRIDE);
    }

    public final void setGlowColorOverride(int glowColorOverride) {
        this.dataTracker.set(GLOW_COLOR_OVERRIDE, glowColorOverride);
    }

    public float getLerpProgress(float tickProgress) {
        float h;
        int i = this.interpolationDuration;
        if (i <= 0) {
            return 1.0f;
        }
        float f = (long)this.age - this.interpolationStart;
        float g = f + tickProgress;
        this.lerpProgress = h = MathHelper.clamp(MathHelper.getLerpProgress(g, 0.0f, i), 0.0f, 1.0f);
        return h;
    }

    public final float getDisplayHeight() {
        return this.dataTracker.get(HEIGHT).floatValue();
    }

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
        this.updateVisibilityBoundingBox();
    }

    private void updateVisibilityBoundingBox() {
        float f = this.getDisplayWidth();
        float g = this.getDisplayHeight();
        this.tooSmallToRender = f == 0.0f || g == 0.0f;
        float h = f / 2.0f;
        double d = this.getX();
        double e = this.getY();
        double i = this.getZ();
        this.visibilityBoundingBox = new Box(d - (double)h, e, i - (double)h, d + (double)h, e + (double)g, i + (double)h);
    }

    @Override
    public boolean shouldRender(double distance) {
        return distance < MathHelper.square((double)this.getViewRange() * 64.0 * DisplayEntity.getRenderDistanceMultiplier());
    }

    @Override
    public int getTeamColorValue() {
        int i = this.getGlowColorOverride();
        return i != -1 ? i : super.getTeamColorValue();
    }

    private RenderState copyRenderState() {
        return new RenderState(AbstractInterpolator.constant(DisplayEntity.getTransformation(this.dataTracker)), this.getBillboardMode(), this.getBrightness(), FloatLerper.constant(this.getShadowRadius()), FloatLerper.constant(this.getShadowStrength()), this.getGlowColorOverride());
    }

    private RenderState getLerpedRenderState(RenderState state, float lerpProgress) {
        AffineTransformation affineTransformation = state.transformation.interpolate(lerpProgress);
        float f = state.shadowRadius.lerp(lerpProgress);
        float g = state.shadowStrength.lerp(lerpProgress);
        return new RenderState(new AffineTransformationInterpolator(affineTransformation, DisplayEntity.getTransformation(this.dataTracker)), this.getBillboardMode(), this.getBrightness(), new FloatLerperImpl(f, this.getShadowRadius()), new FloatLerperImpl(g, this.getShadowStrength()), this.getGlowColorOverride());
    }

    public static final class RenderState
    extends Record {
        final AbstractInterpolator<AffineTransformation> transformation;
        final private BillboardMode billboardConstraints;
        final private int brightnessOverride;
        final FloatLerper shadowRadius;
        final FloatLerper shadowStrength;
        final private int glowColorOverride;

        public RenderState(AbstractInterpolator<AffineTransformation> abstractInterpolator, BillboardMode billboardMode, int i, FloatLerper floatLerper, FloatLerper floatLerper2, int j) {
            this.transformation = abstractInterpolator;
            this.billboardConstraints = billboardMode;
            this.brightnessOverride = 1;
            this.shadowRadius = floatLerper;
            this.shadowStrength = floatLerper2;
            this.glowColorOverride = j;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{RenderState.class, "transformation;billboardConstraints;brightnessOverride;shadowRadius;shadowStrength;glowColorOverride", "transformation", "billboardConstraints", "brightnessOverride", "shadowRadius", "shadowStrength", "glowColorOverride"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RenderState.class, "transformation;billboardConstraints;brightnessOverride;shadowRadius;shadowStrength;glowColorOverride", "transformation", "billboardConstraints", "brightnessOverride", "shadowRadius", "shadowStrength", "glowColorOverride"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RenderState.class, "transformation;billboardConstraints;brightnessOverride;shadowRadius;shadowStrength;glowColorOverride", "transformation", "billboardConstraints", "brightnessOverride", "shadowRadius", "shadowStrength", "glowColorOverride"}, this, object);
        }

        public AbstractInterpolator<AffineTransformation> transformation() {
            return this.transformation;
        }

        public BillboardMode billboardConstraints() {
            return this.billboardConstraints;
        }

        public int brightnessOverride() {
            return this.brightnessOverride;
        }

        public FloatLerper shadowRadius() {
            return this.shadowRadius;
        }

        public FloatLerper shadowStrength() {
            return this.shadowStrength;
        }

        public int glowColorOverride() {
            return this.glowColorOverride;
        }
    }

    public static final class BillboardMode
    extends Enum<BillboardMode>
    implements StringIdentifiable {
        final static public BillboardMode FIXED = new BillboardMode(0, "fixed");
        final static public BillboardMode VERTICAL = new BillboardMode(1, "vertical");
        final static public BillboardMode HORIZONTAL = new BillboardMode(2, "horizontal");
        final static public BillboardMode CENTER = new BillboardMode(3, "center");
        final static public Codec<BillboardMode> CODEC;
        final static public IntFunction<BillboardMode> FROM_INDEX;
        final private byte index;
        final private String name;
        final static private BillboardMode[] field_42414;

        public static BillboardMode[] values() {
            return (BillboardMode[])field_42414.clone();
        }

        public static BillboardMode valueOf(String string) {
            return Enum.valueOf(BillboardMode.class, string);
        }

        private BillboardMode(byte index, String name) {
            this.name = name;
            this.index = index;
        }

        @Override
        public String asString() {
            return this.name;
        }

        byte getIndex() {
            return this.index;
        }

        private static BillboardMode[] method_48882() {
            return new BillboardMode[]{FIXED, VERTICAL, HORIZONTAL, CENTER};
        }

        static {
            field_42414 = BillboardMode.method_48882();
            CODEC = StringIdentifiable.createCodec(BillboardMode::values);
            FROM_INDEX = ValueLists.createIndexToValueFunction(BillboardMode::getIndex, BillboardMode.values(), ValueLists.OutOfBoundsHandling.ZERO);
        }
    }

    @FunctionalInterface
    public static interface AbstractInterpolator<T> {
        public static <T> AbstractInterpolator<T> constant(T value) {
            return delta -> value;
        }

        public T interpolate(float var1);
    }

    @FunctionalInterface
    public static interface FloatLerper {
        public static FloatLerper constant(float value) {
            return delta -> value;
        }

        public float lerp(float var1);
    }

    record AffineTransformationInterpolator(AffineTransformation previous, AffineTransformation current) implements AbstractInterpolator<AffineTransformation>
    {
        @Override
        public AffineTransformation net_minecraft_util_math_AffineTransformation_interpolate(float f) {
            if ((double)f >= 1.0) {
                return this.current;
            }
            return this.previous.interpolate(this.current, f);
        }

        @Override
        public Object java_lang_Object_interpolate(float delta) {
            return this.net_minecraft_util_math_AffineTransformation_interpolate(delta);
        }
    }

    record FloatLerperImpl(float previous, float current) implements FloatLerper
    {
        @Override
        public float lerp(float delta) {
            return MathHelper.lerp(delta, this.previous, this.current);
        }
    }

    record ArgbLerper(int previous, int current) implements IntLerper
    {
        @Override
        public int lerp(float delta) {
            return ColorHelper.lerp(delta, this.previous, this.current);
        }
    }

    record IntLerperImpl(int previous, int current) implements IntLerper
    {
        @Override
        public int lerp(float delta) {
            return MathHelper.lerp(delta, this.previous, this.current);
        }
    }

    @FunctionalInterface
    public static interface IntLerper {
        public static IntLerper constant(int value) {
            return delta -> value;
        }

        public int lerp(float var1);
    }

    public static class TextDisplayEntity
    extends DisplayEntity {
        final static public String TEXT_NBT_KEY = "text";
        final static private String LINE_WIDTH_NBT_KEY = "line_width";
        final static private String TEXT_OPACITY_NBT_KEY = "text_opacity";
        final static private String BACKGROUND_NBT_KEY = "background";
        final static private String SHADOW_NBT_KEY = "shadow";
        final static private String SEE_THROUGH_NBT_KEY = "see_through";
        final static private String DEFAULT_BACKGROUND_NBT_KEY = "default_background";
        final static private String ALIGNMENT_NBT_KEY = "alignment";
        final static public byte SHADOW_FLAG = 1;
        final static public byte SEE_THROUGH_FLAG = 2;
        final static public byte DEFAULT_BACKGROUND_FLAG = 4;
        final static public byte LEFT_ALIGNMENT_FLAG = 8;
        final static public byte RIGHT_ALIGNMENT_FLAG = 16;
        final static private byte INITIAL_TEXT_OPACITY = -1;
        final static public int INITIAL_BACKGROUND = 0x40000000;
        final static private int DEFAULT_LINE_WIDTH = 200;
        final static private TrackedData<Text> TEXT = DataTracker.registerData(TextDisplayEntity.class, TrackedDataHandlerRegistry.TEXT_COMPONENT);
        final static private TrackedData<Integer> LINE_WIDTH = DataTracker.registerData(TextDisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
        final static private TrackedData<Integer> BACKGROUND = DataTracker.registerData(TextDisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
        final static private TrackedData<Byte> TEXT_OPACITY = DataTracker.registerData(TextDisplayEntity.class, TrackedDataHandlerRegistry.BYTE);
        final static private TrackedData<Byte> TEXT_DISPLAY_FLAGS = DataTracker.registerData(TextDisplayEntity.class, TrackedDataHandlerRegistry.BYTE);
        final static private IntSet TEXT_RENDERING_DATA_IDS = IntSet.of((int[])new int[]{TEXT.id(), LINE_WIDTH.id(), BACKGROUND.id(), TEXT_OPACITY.id(), TEXT_DISPLAY_FLAGS.id()});
        @Nullable
        private TextLines textLines;
        @Nullable
        private Data data;

        public TextDisplayEntity(EntityType<?> entityType, World world) {
            super(entityType, world);
        }

        @Override
        protected void initDataTracker(DataTracker.Builder builder) {
            super.initDataTracker(builder);
            builder.add(TEXT, Text.empty());
            builder.add(LINE_WIDTH, 200);
            builder.add(BACKGROUND, 0x40000000);
            builder.add(TEXT_OPACITY, -1);
            builder.add(TEXT_DISPLAY_FLAGS, 0);
        }

        @Override
        public void onTrackedDataSet(TrackedData<?> data) {
            super.onTrackedDataSet(data);
            if (TEXT_RENDERING_DATA_IDS.contains(data.id())) {
                this.renderingDataSet = true;
            }
        }

        public final Text getText() {
            return this.dataTracker.get(TEXT);
        }

        public final void setText(Text text) {
            this.dataTracker.set(TEXT, text);
        }

        public final int getLineWidth() {
            return this.dataTracker.get(LINE_WIDTH);
        }

        public final void setLineWidth(int lineWidth) {
            this.dataTracker.set(LINE_WIDTH, lineWidth);
        }

        public final byte getTextOpacity() {
            return this.dataTracker.get(TEXT_OPACITY);
        }

        public final void setTextOpacity(byte textOpacity) {
            this.dataTracker.set(TEXT_OPACITY, textOpacity);
        }

        public final int getBackground() {
            return this.dataTracker.get(BACKGROUND);
        }

        public final void setBackground(int background) {
            this.dataTracker.set(BACKGROUND, background);
        }

        public final byte getDisplayFlags() {
            return this.dataTracker.get(TEXT_DISPLAY_FLAGS);
        }

        public final void setDisplayFlags(byte flags) {
            this.dataTracker.set(TEXT_DISPLAY_FLAGS, flags);
        }

        private static byte readFlag(byte flags, ReadView view, String nbtKey, byte flag) {
            if (view.getBoolean(nbtKey, false)) {
                return (byte)(flags | flag);
            }
            return flags;
        }

        @Override
        protected void readCustomData(ReadView view) {
            super.readCustomData(view);
            this.setLineWidth(view.getInt(LINE_WIDTH_NBT_KEY, 200));
            this.setTextOpacity(view.getByte(TEXT_OPACITY_NBT_KEY, -1));
            this.setBackground(view.getInt(BACKGROUND_NBT_KEY, 0x40000000));
            byte b = TextDisplayEntity.readFlag(0, view, SHADOW_NBT_KEY, 1);
            b = TextDisplayEntity.readFlag(b, view, SEE_THROUGH_NBT_KEY, 2);
            b = TextDisplayEntity.readFlag(b, view, DEFAULT_BACKGROUND_NBT_KEY, 4);
            Optional<TextAlignment> optional = view.read(ALIGNMENT_NBT_KEY, TextAlignment.CODEC);
            if (optional.isPresent()) {
                b = switch (optional.get().ordinal()) {
                    default -> throw new MatchException(null, null);
                    case 0 -> b;
                    case 1 -> (byte)(b | 8);
                    case 2 -> (byte)(b | 0x10);
                };
            }
            this.setDisplayFlags(b);
            Optional<Text> optional2 = view.read(TEXT_NBT_KEY, TextCodecs.CODEC);
            if (optional2.isPresent()) {
                try {
                    World world = this.net_minecraft_world_World_getWorld();
                    if (world instanceof ServerWorld) {
                        ServerWorld serverWorld = (ServerWorld)world;
                        ServerCommandSource serverCommandSource = this.getCommandSource(serverWorld).withLevel(2);
                        MutableText text = Texts.parse(serverCommandSource, optional2.get(), (Entity)this, 0);
                        this.setText(text);
                    } else {
                        this.setText(Text.empty());
                    }
                }
                catch (Exception exception) {
                    LOGGER.warn("Failed to parse display entity text {}", optional2, (Object)exception);
                }
            }
        }

        private static void writeFlag(byte flags, WriteView view, String nbtKey, byte flag) {
            view.putBoolean(nbtKey, (flags & flag) != 0);
        }

        @Override
        protected void writeCustomData(WriteView view) {
            super.writeCustomData(view);
            view.put(TEXT_NBT_KEY, TextCodecs.CODEC, this.getText());
            view.putInt(LINE_WIDTH_NBT_KEY, this.getLineWidth());
            view.putInt(BACKGROUND_NBT_KEY, this.getBackground());
            view.putByte(TEXT_OPACITY_NBT_KEY, this.getTextOpacity());
            byte b = this.getDisplayFlags();
            TextDisplayEntity.writeFlag(b, view, SHADOW_NBT_KEY, 1);
            TextDisplayEntity.writeFlag(b, view, SEE_THROUGH_NBT_KEY, 2);
            TextDisplayEntity.writeFlag(b, view, DEFAULT_BACKGROUND_NBT_KEY, 4);
            view.put(ALIGNMENT_NBT_KEY, TextAlignment.CODEC, TextDisplayEntity.getAlignment(b));
        }

        @Override
        protected void refreshData(boolean shouldLerp, float lerpProgress) {
            this.data = shouldLerp && this.data != null ? this.getLerpedRenderState(this.data, lerpProgress) : this.copyData();
            this.textLines = null;
        }

        @Nullable
        public Data getData() {
            return this.data;
        }

        private Data copyData() {
            return new Data(this.getText(), this.getLineWidth(), IntLerper.constant(this.getTextOpacity()), IntLerper.constant(this.getBackground()), this.getDisplayFlags());
        }

        private Data getLerpedRenderState(Data data, float lerpProgress) {
            int i = data.backgroundColor.lerp(lerpProgress);
            int j = data.textOpacity.lerp(lerpProgress);
            return new Data(this.getText(), this.getLineWidth(), new IntLerperImpl(j, this.getTextOpacity()), new ArgbLerper(1, this.getBackground()), this.getDisplayFlags());
        }

        public TextLines splitLines(LineSplitter splitter) {
            if (this.textLines == null) {
                this.textLines = this.data != null ? splitter.split(this.data.text(), this.data.lineWidth()) : new TextLines(List.of(), 0);
            }
            return this.textLines;
        }

        public static TextAlignment getAlignment(byte flags) {
            if ((flags & LEFT_ALIGNMENT_FLAG) != 0) {
                return TextAlignment.LEFT;
            }
            if ((flags & RIGHT_ALIGNMENT_FLAG) != 0) {
                return TextAlignment.RIGHT;
            }
            return TextAlignment.CENTER;
        }

        public static final class TextAlignment
        extends Enum<TextAlignment>
        implements StringIdentifiable {
            final static public TextAlignment CENTER = new TextAlignment("center");
            final static public TextAlignment LEFT = new TextAlignment("left");
            final static public TextAlignment RIGHT = new TextAlignment("right");
            final static public Codec<TextAlignment> CODEC;
            final private String name;
            final static private TextAlignment[] field_42455;

            public static TextAlignment[] values() {
                return (TextAlignment[])field_42455.clone();
            }

            public static TextAlignment valueOf(String string) {
                return Enum.valueOf(TextAlignment.class, string);
            }

            private TextAlignment(String name) {
                this.name = name;
            }

            @Override
            public String asString() {
                return this.name;
            }

            private static TextAlignment[] method_48920() {
                return new TextAlignment[]{CENTER, LEFT, RIGHT};
            }

            static {
                field_42455 = TextAlignment.method_48920();
                CODEC = StringIdentifiable.createCodec(TextAlignment::values);
            }
        }

        public static final class Data
        extends Record {
            final private Text text;
            final private int lineWidth;
            final IntLerper textOpacity;
            final IntLerper backgroundColor;
            final private byte flags;

            public Data(Text text, int i, IntLerper intLerper, IntLerper intLerper2, byte b) {
                this.text = text;
                this.lineWidth = i;
                this.textOpacity = intLerper;
                this.backgroundColor = intLerper2;
                this.flags = b;
            }

            @Override
            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{Data.class, "text;lineWidth;textOpacity;backgroundColor;flags", "text", "lineWidth", "textOpacity", "backgroundColor", "flags"}, this);
            }

            @Override
            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Data.class, "text;lineWidth;textOpacity;backgroundColor;flags", "text", "lineWidth", "textOpacity", "backgroundColor", "flags"}, this);
            }

            @Override
            public final boolean equals(Object object) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Data.class, "text;lineWidth;textOpacity;backgroundColor;flags", "text", "lineWidth", "textOpacity", "backgroundColor", "flags"}, this, object);
            }

            public Text text() {
                return this.text;
            }

            public int lineWidth() {
                return this.lineWidth;
            }

            public IntLerper textOpacity() {
                return this.textOpacity;
            }

            public IntLerper backgroundColor() {
                return this.backgroundColor;
            }

            public byte flags() {
                return this.flags;
            }
        }

        public record TextLines(List<TextLine> lines, int width) {
        }

        @FunctionalInterface
        public static interface LineSplitter {
            public TextLines split(Text var1, int var2);
        }

        public record TextLine(OrderedText contents, int width) {
        }
    }

    public static class BlockDisplayEntity
    extends DisplayEntity {
        final static public String BLOCK_STATE_NBT_KEY = "block_state";
        final static private TrackedData<BlockState> BLOCK_STATE = DataTracker.registerData(BlockDisplayEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE);
        @Nullable
        private Data data;

        public BlockDisplayEntity(EntityType<?> entityType, World world) {
            super(entityType, world);
        }

        @Override
        protected void initDataTracker(DataTracker.Builder builder) {
            super.initDataTracker(builder);
            builder.add(BLOCK_STATE, Blocks.AIR.getDefaultState());
        }

        @Override
        public void onTrackedDataSet(TrackedData<?> data) {
            super.onTrackedDataSet(data);
            if (data.equals(BLOCK_STATE)) {
                this.renderingDataSet = true;
            }
        }

        public final BlockState getBlockState() {
            return this.dataTracker.get(BLOCK_STATE);
        }

        public final void setBlockState(BlockState state) {
            this.dataTracker.set(BLOCK_STATE, state);
        }

        @Override
        protected void readCustomData(ReadView view) {
            super.readCustomData(view);
            this.setBlockState(view.read(BLOCK_STATE_NBT_KEY, BlockState.CODEC).orElse(Blocks.AIR.getDefaultState()));
        }

        @Override
        protected void writeCustomData(WriteView view) {
            super.writeCustomData(view);
            view.put(BLOCK_STATE_NBT_KEY, BlockState.CODEC, this.getBlockState());
        }

        @Nullable
        public Data getData() {
            return this.data;
        }

        @Override
        protected void refreshData(boolean shouldLerp, float lerpProgress) {
            this.data = new Data(this.getBlockState());
        }

        public record Data(BlockState blockState) {
        }
    }

    public static class ItemDisplayEntity
    extends DisplayEntity {
        final static private String ITEM_NBT_KEY = "item";
        final static private String ITEM_DISPLAY_NBT_KEY = "item_display";
        final static private TrackedData<ItemStack> ITEM = DataTracker.registerData(ItemDisplayEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
        final static private TrackedData<Byte> ITEM_DISPLAY = DataTracker.registerData(ItemDisplayEntity.class, TrackedDataHandlerRegistry.BYTE);
        final private StackReference stackReference = StackReference.of(this::getItemStack, this::setItemStack);
        @Nullable
        private Data data;

        public ItemDisplayEntity(EntityType<?> entityType, World world) {
            super(entityType, world);
        }

        @Override
        protected void initDataTracker(DataTracker.Builder builder) {
            super.initDataTracker(builder);
            builder.add(ITEM, ItemStack.EMPTY);
            builder.add(ITEM_DISPLAY, ItemDisplayContext.NONE.getIndex());
        }

        @Override
        public void onTrackedDataSet(TrackedData<?> data) {
            super.onTrackedDataSet(data);
            if (ITEM.equals(data) || ITEM_DISPLAY.equals(data)) {
                this.renderingDataSet = true;
            }
        }

        public final ItemStack getItemStack() {
            return this.dataTracker.get(ITEM);
        }

        public final void setItemStack(ItemStack stack) {
            this.dataTracker.set(ITEM, stack);
        }

        public final void setItemDisplayContext(ItemDisplayContext context) {
            this.dataTracker.set(ITEM_DISPLAY, context.getIndex());
        }

        public final ItemDisplayContext getItemDisplayContext() {
            return ItemDisplayContext.FROM_INDEX.apply(this.dataTracker.get(ITEM_DISPLAY).byteValue());
        }

        @Override
        protected void readCustomData(ReadView view) {
            super.readCustomData(view);
            this.setItemStack(view.read(ITEM_NBT_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY));
            this.setItemDisplayContext(view.read(ITEM_DISPLAY_NBT_KEY, ItemDisplayContext.CODEC).orElse(ItemDisplayContext.NONE));
        }

        @Override
        protected void writeCustomData(WriteView view) {
            super.writeCustomData(view);
            ItemStack itemStack = this.getItemStack();
            if (!itemStack.isEmpty()) {
                view.put(ITEM_NBT_KEY, ItemStack.CODEC, itemStack);
            }
            view.put(ITEM_DISPLAY_NBT_KEY, ItemDisplayContext.CODEC, this.getItemDisplayContext());
        }

        @Override
        public StackReference getStackReference(int mappedIndex) {
            if (mappedIndex == 0) {
                return this.stackReference;
            }
            return StackReference.EMPTY;
        }

        @Nullable
        public Data getData() {
            return this.data;
        }

        @Override
        protected void refreshData(boolean shouldLerp, float lerpProgress) {
            ItemStack itemStack = this.getItemStack();
            itemStack.setHolder(this);
            this.data = new Data(itemStack, this.getItemDisplayContext());
        }

        public record Data(ItemStack itemStack, ItemDisplayContext itemTransform) {
        }
    }
}

