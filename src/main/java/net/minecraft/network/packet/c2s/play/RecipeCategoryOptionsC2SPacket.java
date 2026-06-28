/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.book.RecipeBookType;

public class RecipeCategoryOptionsC2SPacket
implements Packet<ServerPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, RecipeCategoryOptionsC2SPacket> CODEC = Packet.createCodec(RecipeCategoryOptionsC2SPacket::write, RecipeCategoryOptionsC2SPacket::new);
    final private RecipeBookType category;
    final private boolean guiOpen;
    final private boolean filteringCraftable;

    public RecipeCategoryOptionsC2SPacket(RecipeBookType category, boolean guiOpen, boolean filteringCraftable) {
        this.category = category;
        this.guiOpen = guiOpen;
        this.filteringCraftable = filteringCraftable;
    }

    private RecipeCategoryOptionsC2SPacket(PacketByteBuf buf) {
        this.category = buf.readEnumConstant(RecipeBookType.class);
        this.guiOpen = buf.readBoolean();
        this.filteringCraftable = buf.readBoolean();
    }

    private void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.category);
        buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.guiOpen);
        buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.filteringCraftable);
    }

    @Override
    public PacketType<RecipeCategoryOptionsC2SPacket> getPacketType() {
        return PlayPackets.RECIPE_BOOK_CHANGE_SETTINGS;
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onRecipeCategoryOptions(this);
    }

    public RecipeBookType getCategory() {
        return this.category;
    }

    public boolean isGuiOpen() {
        return this.guiOpen;
    }

    public boolean isFilteringCraftable() {
        return this.filteringCraftable;
    }
}

