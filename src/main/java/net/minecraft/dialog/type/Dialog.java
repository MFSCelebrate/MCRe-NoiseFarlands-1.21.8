/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.dialog.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.dynamic.Codecs;

public interface Dialog {
    final static public Codec<Integer> WIDTH_CODEC = Codecs.rangedInt(1, 1024);
    final static public Codec<Dialog> CODEC = Registries.DIALOG_TYPE.getCodec().dispatch(Dialog::getCodec, mapCodec -> mapCodec);
    final static public Codec<RegistryEntry<Dialog>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.DIALOG, CODEC);
    final static public Codec<RegistryEntryList<Dialog>> ENTRY_LIST_CODEC = RegistryCodecs.entryList(RegistryKeys.DIALOG, CODEC);
    final static public PacketCodec<RegistryByteBuf, RegistryEntry<Dialog>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.DIALOG, PacketCodecs.unlimitedRegistryCodec(CODEC));
    final static public PacketCodec<ByteBuf, Dialog> PACKET_CODEC = PacketCodecs.unlimitedCodec(CODEC);

    public DialogCommonData common();

    public MapCodec<? extends Dialog> getCodec();

    public Optional<DialogAction> getCancelAction();
}

