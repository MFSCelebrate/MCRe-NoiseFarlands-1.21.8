/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

public class CommandBlockMinecartEntity
extends AbstractMinecartEntity {
    final static TrackedData<String> COMMAND = DataTracker.registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.STRING);
    final static TrackedData<Text> LAST_OUTPUT = DataTracker.registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.TEXT_COMPONENT);
    final private CommandBlockExecutor commandExecutor = new CommandExecutor();
    final static private int EXECUTE_TICK_COOLDOWN = 4;
    private int lastExecuted;

    public CommandBlockMinecartEntity(EntityType<? extends CommandBlockMinecartEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item asItem() {
        return Items.MINECART;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(Items.COMMAND_BLOCK_MINECART);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(COMMAND, "");
        builder.add(LAST_OUTPUT, ScreenTexts.EMPTY);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.commandExecutor.readData(view);
        this.getDataTracker().set(COMMAND, this.getCommandExecutor().getCommand());
        this.getDataTracker().set(LAST_OUTPUT, this.getCommandExecutor().getLastOutput());
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        this.commandExecutor.writeData(view);
    }

    @Override
    public BlockState getDefaultContainedBlock() {
        return Blocks.COMMAND_BLOCK.getDefaultState();
    }

    public CommandBlockExecutor getCommandExecutor() {
        return this.commandExecutor;
    }

    @Override
    public void onActivatorRail(int x, int y, int z, boolean powered) {
        if (powered && this.age - this.lastExecuted >= 4) {
            this.getCommandExecutor().execute(this.net_minecraft_world_World_getWorld());
            this.lastExecuted = this.age;
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        return this.commandExecutor.interact(player);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (LAST_OUTPUT.equals(data)) {
            try {
                this.commandExecutor.setLastOutput(this.getDataTracker().get(LAST_OUTPUT));
            }
            catch (Throwable throwable) {}
        } else if (COMMAND.equals(data)) {
            this.commandExecutor.setCommand(this.getDataTracker().get(COMMAND));
        }
    }

    public class CommandExecutor
    extends CommandBlockExecutor {
        @Override
        public ServerWorld getWorld() {
            return (ServerWorld)CommandBlockMinecartEntity.this.net_minecraft_world_World_getWorld();
        }

        @Override
        public void markDirty() {
            CommandBlockMinecartEntity.this.getDataTracker().set(COMMAND, this.getCommand());
            CommandBlockMinecartEntity.this.getDataTracker().set(LAST_OUTPUT, this.getLastOutput());
        }

        @Override
        public Vec3d getPos() {
            return CommandBlockMinecartEntity.this.getPos();
        }

        public CommandBlockMinecartEntity getMinecart() {
            return CommandBlockMinecartEntity.this;
        }

        @Override
        public ServerCommandSource getSource() {
            return new ServerCommandSource(this, CommandBlockMinecartEntity.this.getPos(), CommandBlockMinecartEntity.this.getRotationClient(), this.getWorld(), 2, this.getName().getString(), CommandBlockMinecartEntity.this.getDisplayName(), this.getWorld().getServer(), CommandBlockMinecartEntity.this);
        }

        @Override
        public boolean isEditable() {
            return !CommandBlockMinecartEntity.this.isRemoved();
        }
    }
}

