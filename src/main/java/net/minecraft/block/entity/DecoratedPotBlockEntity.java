/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Sherds;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class DecoratedPotBlockEntity
extends BlockEntity
implements LootableInventory,
SingleStackInventory.SingleStackBlockEntityInventory {
    final static public String SHERDS_NBT_KEY = "sherds";
    final static public String ITEM_NBT_KEY = "item";
    final static public int field_46660 = 1;
    public long lastWobbleTime;
    @Nullable
    public WobbleType lastWobbleType;
    private Sherds sherds;
    private ItemStack stack = ItemStack.EMPTY;
    @Nullable
    protected RegistryKey<LootTable> lootTableId;
    protected long lootTableSeed;

    public DecoratedPotBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.DECORATED_POT, pos, state);
        this.sherds = Sherds.DEFAULT;
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (!this.sherds.equals(Sherds.DEFAULT)) {
            view.put(SHERDS_NBT_KEY, Sherds.CODEC, this.sherds);
        }
        if (!this.writeLootTable(view) && !this.stack.isEmpty()) {
            view.put(ITEM_NBT_KEY, ItemStack.CODEC, this.stack);
        }
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.sherds = view.read(SHERDS_NBT_KEY, Sherds.CODEC).orElse(Sherds.DEFAULT);
        this.stack = !this.readLootTable(view) ? view.read(ITEM_NBT_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
    }

    public BlockEntityUpdateS2CPacket net_minecraft_network_packet_s2c_play_BlockEntityUpdateS2CPacket_toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createComponentlessNbt(registries);
    }

    public Direction getHorizontalFacing() {
        return this.getCachedState().get(Properties.HORIZONTAL_FACING);
    }

    public Sherds getSherds() {
        return this.sherds;
    }

    public static ItemStack getStackWith(Sherds sherds) {
        ItemStack itemStack = Items.DECORATED_POT.getDefaultStack();
        itemStack.set(DataComponentTypes.POT_DECORATIONS, sherds);
        return itemStack;
    }

    @Override
    @Nullable
    public RegistryKey<LootTable> getLootTable() {
        return this.lootTableId;
    }

    @Override
    public void setLootTable(@Nullable RegistryKey<LootTable> lootTable) {
        this.lootTableId = lootTable;
    }

    @Override
    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
    }

    @Override
    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.POT_DECORATIONS, this.sherds);
        builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(List.of(this.stack)));
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.sherds = components.getOrDefault(DataComponentTypes.POT_DECORATIONS, Sherds.DEFAULT);
        this.stack = components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyFirstStack();
    }

    @Override
    public void removeFromCopiedStackData(WriteView view) {
        super.removeFromCopiedStackData(view);
        view.remove(SHERDS_NBT_KEY);
        view.remove(ITEM_NBT_KEY);
    }

    @Override
    public ItemStack getStack() {
        this.generateLoot(null);
        return this.stack;
    }

    @Override
    public ItemStack decreaseStack(int count) {
        this.generateLoot(null);
        ItemStack itemStack = this.stack.split(count);
        if (this.stack.isEmpty()) {
            this.stack = ItemStack.EMPTY;
        }
        return itemStack;
    }

    @Override
    public void setStack(ItemStack stack) {
        this.generateLoot(null);
        this.stack = stack;
    }

    @Override
    public BlockEntity asBlockEntity() {
        return this;
    }

    public void wobble(WobbleType wobbleType) {
        if (this.world == null || this.world.isClient()) {
            return;
        }
        this.world.addSyncedBlockEvent(this.getPos(), this.getCachedState().getBlock(), 1, wobbleType.ordinal());
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (this.world != null && type == 1 && data >= 0 && data < WobbleType.values().length) {
            this.lastWobbleTime = this.world.getTime();
            this.lastWobbleType = WobbleType.values()[data];
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
    }

    public Packet net_minecraft_network_packet_Packet_toUpdatePacket() {
        return this.net_minecraft_network_packet_s2c_play_BlockEntityUpdateS2CPacket_toUpdatePacket();
    }

    public static final class WobbleType
    extends Enum<WobbleType> {
        final static public WobbleType POSITIVE = new WobbleType(7);
        final static public WobbleType NEGATIVE = new WobbleType(10);
        final public int lengthInTicks;
        final static private WobbleType[] field_46667;

        public static WobbleType[] values() {
            return (WobbleType[])field_46667.clone();
        }

        public static WobbleType valueOf(String string) {
            return Enum.valueOf(WobbleType.class, string);
        }

        private WobbleType(int lengthInTicks) {
            this.lengthInTicks = lengthInTicks;
        }

        private static WobbleType[] method_54302() {
            return new WobbleType[]{POSITIVE, NEGATIVE};
        }

        static {
            field_46667 = WobbleType.method_54302();
        }
    }
}

