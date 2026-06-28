/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapDecoder
 *  com.mojang.serialization.MapEncoder
 *  com.mojang.serialization.MapLike
 *  io.netty.buffer.ByteBuf
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.component.type;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class NbtComponent {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public NbtComponent DEFAULT = new NbtComponent(new NbtCompound());
    final static private String ID_KEY = "id";
    final static public Codec<NbtComponent> CODEC = Codec.withAlternative(NbtCompound.CODEC, StringNbtReader.STRINGIFIED_CODEC).xmap(NbtComponent::new, component -> component.nbt);
    final static public Codec<NbtComponent> CODEC_WITH_ID = CODEC.validate(component -> component.getNbt().getString(ID_KEY).isPresent() ? DataResult.success((Object)component) : DataResult.error(() -> "Missing id for entity in: " + String.valueOf(component)));
    @Deprecated
    final static public PacketCodec<ByteBuf, NbtComponent> PACKET_CODEC = PacketCodecs.NBT_COMPOUND.xmap(NbtComponent::new, component -> component.nbt);
    final private NbtCompound nbt;

    private NbtComponent(NbtCompound nbt) {
        this.nbt = nbt;
    }

    public static NbtComponent of(NbtCompound nbt) {
        return new NbtComponent(nbt.net_minecraft_nbt_NbtCompound_copy());
    }

    public boolean matches(NbtCompound nbt) {
        return NbtHelper.matches(nbt, this.nbt, true);
    }

    public static void set(ComponentType<NbtComponent> type, ItemStack stack, Consumer<NbtCompound> nbtSetter) {
        NbtComponent nbtComponent = stack.getOrDefault(type, DEFAULT).apply(nbtSetter);
        if (nbtComponent.nbt.isEmpty()) {
            stack.remove(type);
        } else {
            stack.set(type, nbtComponent);
        }
    }

    public static void set(ComponentType<NbtComponent> type, ItemStack stack, NbtCompound nbt) {
        if (!nbt.isEmpty()) {
            stack.set(type, NbtComponent.of(nbt));
        } else {
            stack.remove(type);
        }
    }

    public NbtComponent apply(Consumer<NbtCompound> nbtConsumer) {
        NbtCompound nbtCompound = this.nbt.net_minecraft_nbt_NbtCompound_copy();
        nbtConsumer.accept(nbtCompound);
        return new NbtComponent(nbtCompound);
    }

    @Nullable
    public Identifier getId() {
        return this.nbt.get(ID_KEY, Identifier.CODEC).orElse(null);
    }

    @Nullable
    public <T> T getRegistryValueOfId(RegistryWrapper.WrapperLookup registries, RegistryKey<? extends Registry<T>> registryRef) {
        Identifier identifier = this.getId();
        if (identifier == null) {
            return null;
        }
        return registries.getOptional(registryRef).flatMap(registry -> registry.getOptional(RegistryKey.of(registryRef, identifier))).map(RegistryEntry::value).orElse(null);
    }

    public void applyToEntity(Entity entity) {
        try (ErrorReporter.Logging logging = new ErrorReporter.Logging(entity.getErrorReporterContext(), LOGGER);){
            NbtWriteView nbtWriteView = NbtWriteView.create(logging, entity.getRegistryManager());
            entity.writeData(nbtWriteView);
            NbtCompound nbtCompound = nbtWriteView.getNbt();
            UUID uUID = entity.getUuid();
            nbtCompound.copyFrom(this.nbt);
            entity.readData(NbtReadView.create(logging, entity.getRegistryManager(), nbtCompound));
            entity.setUuid(uUID);
        }
    }

    /*
     * Exception decompiling
     */
    public boolean applyToBlockEntity(BlockEntity blockEntity, RegistryWrapper.WrapperLookup registries) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK], 5[CATCHBLOCK]], but top level block is 2[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public <T> DataResult<NbtComponent> with(DynamicOps<NbtElement> ops, MapEncoder<T> encoder, T value) {
        return encoder.encode(value, ops, ops.mapBuilder()).build((Object)this.nbt).map(nbt -> new NbtComponent((NbtCompound)nbt));
    }

    public <T> DataResult<T> get(MapDecoder<T> decoder) {
        return this.get(NbtOps.INSTANCE, decoder);
    }

    public <T> DataResult<T> get(DynamicOps<NbtElement> ops, MapDecoder<T> decoder) {
        MapLike mapLike = (MapLike)ops.getMap((Object)this.nbt).getOrThrow();
        return decoder.decode(ops, mapLike);
    }

    public int getSize() {
        return this.nbt.getSize();
    }

    public boolean isEmpty() {
        return this.nbt.isEmpty();
    }

    public NbtCompound copyNbt() {
        return this.nbt.net_minecraft_nbt_NbtCompound_copy();
    }

    public boolean contains(String key) {
        return this.nbt.contains(key);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof NbtComponent) {
            NbtComponent nbtComponent = (NbtComponent)o;
            return this.nbt.equals(nbtComponent.nbt);
        }
        return false;
    }

    public int hashCode() {
        return this.nbt.hashCode();
    }

    public String toString() {
        return this.nbt.toString();
    }

    @Deprecated
    public NbtCompound getNbt() {
        return this.nbt;
    }

    private static String method_71393() {
        return "(rollback)";
    }
}

