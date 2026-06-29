/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class UpdateStructureBlockC2SPacket
implements Packet<ServerPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, UpdateStructureBlockC2SPacket> CODEC = Packet.createCodec(UpdateStructureBlockC2SPacket::write, UpdateStructureBlockC2SPacket::new);
    final static private int IGNORE_ENTITIES_MASK = 1;
    final static private int SHOW_AIR_MASK = 2;
    final static private int SHOW_BOUNDING_BOX_MASK = 4;
    final static private int STRICT_MASK = 8;
    final private BlockPos pos;
    final private StructureBlockBlockEntity.Action action;
    final private StructureBlockMode mode;
    final private String templateName;
    final private BlockPos offset;
    final private Vec3i size;
    final private BlockMirror mirror;
    final private BlockRotation rotation;
    final private String metadata;
    final private boolean ignoreEntities;
    final private boolean strict;
    final private boolean showAir;
    final private boolean showBoundingBox;
    final private float integrity;
    final private long seed;

    public UpdateStructureBlockC2SPacket(BlockPos pos, StructureBlockBlockEntity.Action action, StructureBlockMode mode, String templateName, BlockPos offset, Vec3i size, BlockMirror mirror, BlockRotation rotation, String metadata, boolean ignoreEntities, boolean strict, boolean showAir, boolean showBoundingBox, float integrity, long seed) {
        this.pos = pos;
        this.action = action;
        this.mode = mode;
        this.templateName = templateName;
        this.offset = offset;
        this.size = size;
        this.mirror = mirror;
        this.rotation = rotation;
        this.metadata = metadata;
        this.ignoreEntities = ignoreEntities;
        this.strict = strict;
        this.showAir = showAir;
        this.showBoundingBox = showBoundingBox;
        this.integrity = integrity;
        this.seed = seed;
    }

    private UpdateStructureBlockC2SPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.action = buf.readEnumConstant(StructureBlockBlockEntity.Action.class);
        this.mode = buf.readEnumConstant(StructureBlockMode.class);
        this.templateName = buf.readString();
        int i = 48;
        this.offset = new BlockPos(MathHelper.clamp(buf.readByte(), -48, 48), MathHelper.clamp(buf.readByte(), -48, 48), MathHelper.clamp(buf.readByte(), -48, 48));
        int j = 48;
        this.size = new Vec3i(MathHelper.clamp(buf.readByte(), 0, 48), MathHelper.clamp(buf.readByte(), 0, 48), MathHelper.clamp(buf.readByte(), 0, 48));
        this.mirror = buf.readEnumConstant(BlockMirror.class);
        this.rotation = buf.readEnumConstant(BlockRotation.class);
        this.metadata = buf.readString(128);
        this.integrity = MathHelper.clamp(buf.readFloat(), 0.0f, 1.0f);
        this.seed = buf.readVarLong();
        byte k = buf.readByte();
        this.ignoreEntities = (k & 1) != 0;
        this.strict = (k & 8) != 0;
        this.showAir = (k & 2) != 0;
        this.showBoundingBox = (k & 4) != 0;
    }

    private void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeEnumConstant(this.action);
        buf.writeEnumConstant(this.mode);
        buf.writeString(this.templateName);
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.offset.getX());
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.offset.getY());
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.offset.getZ());
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.size.getX());
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.size.getY());
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.size.getZ());
        buf.writeEnumConstant(this.mirror);
        buf.writeEnumConstant(this.rotation);
        buf.writeString(this.metadata);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.integrity);
        buf.writeVarLong(this.seed);
        int i = 0;
        if (this.ignoreEntities) {
            i |= 1;
        }
        if (this.showAir) {
            i |= 2;
        }
        if (this.showBoundingBox) {
            i |= 4;
        }
        if (this.strict) {
            i |= 8;
        }
        buf.net_minecraft_network_PacketByteBuf_writeByte(i);
    }

    @Override
    public PacketType<UpdateStructureBlockC2SPacket> getPacketType() {
        return PlayPackets.SET_STRUCTURE_BLOCK;
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onUpdateStructureBlock(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public StructureBlockBlockEntity.Action getAction() {
        return this.action;
    }

    public StructureBlockMode getMode() {
        return this.mode;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public BlockPos getOffset() {
        return this.offset;
    }

    public Vec3i getSize() {
        return this.size;
    }

    public BlockMirror getMirror() {
        return this.mirror;
    }

    public BlockRotation getRotation() {
        return this.rotation;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public boolean shouldIgnoreEntities() {
        return this.ignoreEntities;
    }

    public boolean isStrict() {
        return this.strict;
    }

    public boolean shouldShowAir() {
        return this.showAir;
    }

    public boolean shouldShowBoundingBox() {
        return this.showBoundingBox;
    }

    public float getIntegrity() {
        return this.integrity;
    }

    public long getSeed() {
        return this.seed;
    }
}

