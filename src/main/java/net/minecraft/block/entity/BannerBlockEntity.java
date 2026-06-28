/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BannerBlockEntity
extends BlockEntity
implements Nameable {
    final static public int MAX_PATTERN_COUNT = 6;
    final static private String PATTERNS_KEY = "patterns";
    @Nullable
    private Text customName;
    final private DyeColor baseColor;
    private BannerPatternsComponent patterns = BannerPatternsComponent.DEFAULT;

    public BannerBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, ((AbstractBannerBlock)state.getBlock()).getColor());
    }

    public BannerBlockEntity(BlockPos pos, BlockState state, DyeColor baseColor) {
        super(BlockEntityType.BANNER, pos, state);
        this.baseColor = baseColor;
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return Text.translatable("block.minecraft.banner");
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.customName;
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (!this.patterns.equals(BannerPatternsComponent.DEFAULT)) {
            view.put(PATTERNS_KEY, BannerPatternsComponent.CODEC, this.patterns);
        }
        view.putNullable("CustomName", TextCodecs.CODEC, this.customName);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.customName = BannerBlockEntity.tryParseCustomName(view, "CustomName");
        this.patterns = view.read(PATTERNS_KEY, BannerPatternsComponent.CODEC).orElse(BannerPatternsComponent.DEFAULT);
    }

    public BlockEntityUpdateS2CPacket net_minecraft_network_packet_s2c_play_BlockEntityUpdateS2CPacket_toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createNbt(registries);
    }

    public BannerPatternsComponent getPatterns() {
        return this.patterns;
    }

    public ItemStack getPickStack() {
        ItemStack itemStack = new ItemStack(BannerBlock.getForColor(this.baseColor));
        itemStack.applyComponentsFrom(this.createComponentMap());
        return itemStack;
    }

    public DyeColor getColorForState() {
        return this.baseColor;
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.patterns = components.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
        this.customName = components.get(DataComponentTypes.CUSTOM_NAME);
    }

    @Override
    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.BANNER_PATTERNS, this.patterns);
        builder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
    }

    @Override
    public void removeFromCopiedStackData(WriteView view) {
        view.remove(PATTERNS_KEY);
        view.remove("CustomName");
    }

    public Packet net_minecraft_network_packet_Packet_toUpdatePacket() {
        return this.net_minecraft_network_packet_s2c_play_BlockEntityUpdateS2CPacket_toUpdatePacket();
    }
}

