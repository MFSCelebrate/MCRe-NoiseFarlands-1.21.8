/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.block.entity;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;

public class JigsawBlockEntity
extends BlockEntity {
    final static public Codec<RegistryKey<StructurePool>> STRUCTURE_POOL_KEY_CODEC = RegistryKey.createCodec(RegistryKeys.TEMPLATE_POOL);
    final static public Identifier DEFAULT_NAME = Identifier.ofVanilla("empty");
    final static private int DEFAULT_PLACEMENT_PRIORITY = 0;
    final static private int DEFAULT_SELECTION_PRIORITY = 0;
    final static public String TARGET_KEY = "target";
    final static public String POOL_KEY = "pool";
    final static public String JOINT_KEY = "joint";
    final static public String PLACEMENT_PRIORITY_KEY = "placement_priority";
    final static public String SELECTION_PRIORITY_KEY = "selection_priority";
    final static public String NAME_KEY = "name";
    final static public String FINAL_STATE_KEY = "final_state";
    final static public String DEFAULT_FINAL_STATE = "minecraft:air";
    private Identifier name = DEFAULT_NAME;
    private Identifier target = DEFAULT_NAME;
    private RegistryKey<StructurePool> pool = StructurePools.EMPTY;
    private Joint joint = Joint.ROLLABLE;
    private String finalState = "minecraft:air";
    private int placementPriority = 0;
    private int selectionPriority = 0;

    public JigsawBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.JIGSAW, pos, state);
    }

    public Identifier getName() {
        return this.name;
    }

    public Identifier getTarget() {
        return this.target;
    }

    public RegistryKey<StructurePool> getPool() {
        return this.pool;
    }

    public String getFinalState() {
        return this.finalState;
    }

    public Joint getJoint() {
        return this.joint;
    }

    public int getPlacementPriority() {
        return this.placementPriority;
    }

    public int getSelectionPriority() {
        return this.selectionPriority;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public void setTarget(Identifier target) {
        this.target = target;
    }

    public void setPool(RegistryKey<StructurePool> pool) {
        this.pool = pool;
    }

    public void setFinalState(String finalState) {
        this.finalState = finalState;
    }

    public void setJoint(Joint joint) {
        this.joint = joint;
    }

    public void setPlacementPriority(int placementPriority) {
        this.placementPriority = placementPriority;
    }

    public void setSelectionPriority(int selectionPriority) {
        this.selectionPriority = selectionPriority;
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.put(NAME_KEY, Identifier.CODEC, this.name);
        view.put(TARGET_KEY, Identifier.CODEC, this.target);
        view.put(POOL_KEY, STRUCTURE_POOL_KEY_CODEC, this.pool);
        view.putString(FINAL_STATE_KEY, this.finalState);
        view.put(JOINT_KEY, Joint.CODEC, this.joint);
        view.putInt(PLACEMENT_PRIORITY_KEY, this.placementPriority);
        view.putInt(SELECTION_PRIORITY_KEY, this.selectionPriority);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.name = view.read(NAME_KEY, Identifier.CODEC).orElse(DEFAULT_NAME);
        this.target = view.read(TARGET_KEY, Identifier.CODEC).orElse(DEFAULT_NAME);
        this.pool = view.read(POOL_KEY, STRUCTURE_POOL_KEY_CODEC).orElse(StructurePools.EMPTY);
        this.finalState = view.getString(FINAL_STATE_KEY, DEFAULT_FINAL_STATE);
        this.joint = view.read(JOINT_KEY, Joint.CODEC).orElseGet(() -> StructureTemplate.getJointFromFacing(this.getCachedState()));
        this.placementPriority = view.getInt(PLACEMENT_PRIORITY_KEY, 0);
        this.selectionPriority = view.getInt(SELECTION_PRIORITY_KEY, 0);
    }

    public BlockEntityUpdateS2CPacket net_minecraft_network_packet_s2c_play_BlockEntityUpdateS2CPacket_toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createComponentlessNbt(registries);
    }

    public void generate(ServerWorld world, int maxDepth, boolean keepJigsaws) {
        BlockPos blockPos = this.getPos().net_minecraft_util_math_BlockPos_offset(this.getCachedState().get(JigsawBlock.ORIENTATION).getFacing());
        RegistryWrapper.Impl registry = world.getRegistryManager().net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.TEMPLATE_POOL);
        RegistryEntry.Reference registryEntry = registry.getOrThrow(this.pool);
        StructurePoolBasedGenerator.generate(world, registryEntry, this.target, maxDepth, blockPos, keepJigsaws);
    }

    public Packet net_minecraft_network_packet_Packet_toUpdatePacket() {
        return this.net_minecraft_network_packet_s2c_play_BlockEntityUpdateS2CPacket_toUpdatePacket();
    }

    public static final class Joint
    extends Enum<Joint>
    implements StringIdentifiable {
        final static public Joint ROLLABLE = new Joint("rollable");
        final static public Joint ALIGNED = new Joint("aligned");
        final static public StringIdentifiable.EnumCodec<Joint> CODEC;
        final private String name;
        final static private Joint[] field_23332;

        public static Joint[] values() {
            return (Joint[])field_23332.clone();
        }

        public static Joint valueOf(String string) {
            return Enum.valueOf(Joint.class, string);
        }

        private Joint(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public Text asText() {
            return Text.translatable("jigsaw_block.joint." + this.name);
        }

        private static Joint[] method_36716() {
            return new Joint[]{ROLLABLE, ALIGNED};
        }

        static {
            field_23332 = Joint.method_36716();
            CODEC = StringIdentifiable.createCodec(Joint::values);
        }
    }
}

