/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.vehicle;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HopperMinecartEntity
extends StorageMinecartEntity
implements Hopper {
    final static private boolean DEFAULT_ENABLED = true;
    private boolean enabled = true;
    private boolean hopperTicked = false;

    public HopperMinecartEntity(EntityType<? extends HopperMinecartEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public BlockState getDefaultContainedBlock() {
        return Blocks.HOPPER.getDefaultState();
    }

    @Override
    public int getDefaultBlockOffset() {
        return 1;
    }

    @Override
    public int size() {
        return 5;
    }

    @Override
    public void onActivatorRail(int x, int y, int z, boolean powered) {
        boolean bl;
        boolean bl2 = bl = !powered;
        if (bl != this.isEnabled()) {
            this.setEnabled(bl);
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public double getHopperX() {
        return this.getX();
    }

    @Override
    public double getHopperY() {
        return this.getY() + 0.5;
    }

    @Override
    public double getHopperZ() {
        return this.getZ();
    }

    @Override
    public boolean canBlockFromAbove() {
        return false;
    }

    @Override
    public void tick() {
        this.hopperTicked = false;
        super.tick();
        this.tickHopper();
    }

    @Override
    protected double moveAlongTrack(BlockPos pos, RailShape shape, double remainingMovement) {
        double d = super.moveAlongTrack(pos, shape, remainingMovement);
        this.tickHopper();
        return d;
    }

    private void tickHopper() {
        if (!this.net_minecraft_world_World_getWorld().isClient && this.isAlive() && this.isEnabled() && !this.hopperTicked && this.canOperate()) {
            this.hopperTicked = true;
            this.markDirty();
        }
    }

    public boolean canOperate() {
        if (HopperBlockEntity.extract(this.net_minecraft_world_World_getWorld(), this)) {
            return true;
        }
        List<Entity> list = this.net_minecraft_world_World_getWorld().getEntitiesByClass(ItemEntity.class, this.getBoundingBox().expand(0.25, 0.0, 0.25), EntityPredicates.VALID_ENTITY);
        for (ItemEntity itemEntity : list) {
            if (!HopperBlockEntity.extract(this, itemEntity)) continue;
            return true;
        }
        return false;
    }

    @Override
    protected Item asItem() {
        return Items.HOPPER_MINECART;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(Items.HOPPER_MINECART);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putBoolean("Enabled", this.enabled);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.enabled = view.getBoolean("Enabled", true);
    }

    @Override
    public ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new HopperScreenHandler(syncId, playerInventory, this);
    }
}

