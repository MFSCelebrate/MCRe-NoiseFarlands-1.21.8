/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.component.type.BookContent;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.RawFilteredPair;

public record WritableBookContentComponent(List<RawFilteredPair<String>> pages) implements BookContent<String, WritableBookContentComponent>
{
    final static public WritableBookContentComponent DEFAULT = new WritableBookContentComponent(List.of());
    final static public int MAX_PAGE_LENGTH = 1024;
    final static public int MAX_PAGE_COUNT = 100;
    final static private Codec<RawFilteredPair<String>> PAGE_CODEC = RawFilteredPair.createCodec(Codec.string(0, 1024));
    final static public Codec<List<RawFilteredPair<String>>> PAGES_CODEC = PAGE_CODEC.sizeLimitedListOf(100);
    final static public Codec<WritableBookContentComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)PAGES_CODEC.optionalFieldOf("pages", List.of()).forGetter(WritableBookContentComponent::pages)).apply((Applicative)instance, WritableBookContentComponent::new));
    final static public PacketCodec<ByteBuf, WritableBookContentComponent> PACKET_CODEC = RawFilteredPair.createPacketCodec(PacketCodecs.string(1024)).collect(PacketCodecs.toList(100)).xmap(WritableBookContentComponent::new, WritableBookContentComponent::pages);

    public WritableBookContentComponent {
        if (pages.size() > 100) {
            throw new IllegalArgumentException("Got " + pages.size() + " pages, but maximum is 100");
        }
    }

    public Stream<String> stream(boolean shouldFilter) {
        return this.pages.stream().map(page -> (String)page.get(shouldFilter));
    }

    @Override
    public WritableBookContentComponent net_minecraft_component_type_WritableBookContentComponent_withPages(List<RawFilteredPair<String>> list) {
        return new WritableBookContentComponent(list);
    }

    @Override
    public Object java_lang_Object_withPages(List pages) {
        return this.java_lang_Object_withPages(pages);
    }
}

