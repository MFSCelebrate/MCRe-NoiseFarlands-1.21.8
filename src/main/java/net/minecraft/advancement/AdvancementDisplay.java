/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.advancement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.AssetInfo;

public class AdvancementDisplay {
    final static public Codec<AdvancementDisplay> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ItemStack.VALIDATED_CODEC.fieldOf("icon").forGetter(AdvancementDisplay::getIcon), (App)TextCodecs.CODEC.fieldOf("title").forGetter(AdvancementDisplay::getTitle), (App)TextCodecs.CODEC.fieldOf("description").forGetter(AdvancementDisplay::getDescription), (App)AssetInfo.CODEC.optionalFieldOf("background").forGetter(AdvancementDisplay::getBackground), (App)AdvancementFrame.CODEC.optionalFieldOf("frame", (Object)AdvancementFrame.TASK).forGetter(AdvancementDisplay::getFrame), (App)Codec.BOOL.optionalFieldOf("show_toast", (Object)true).forGetter(AdvancementDisplay::shouldShowToast), (App)Codec.BOOL.optionalFieldOf("announce_to_chat", (Object)true).forGetter(AdvancementDisplay::shouldAnnounceToChat), (App)Codec.BOOL.optionalFieldOf("hidden", (Object)false).forGetter(AdvancementDisplay::isHidden)).apply((Applicative)instance, AdvancementDisplay::new));
    final static public PacketCodec<RegistryByteBuf, AdvancementDisplay> PACKET_CODEC = PacketCodec.of(AdvancementDisplay::toPacket, AdvancementDisplay::fromPacket);
    final private Text title;
    final private Text description;
    final private ItemStack icon;
    final private Optional<AssetInfo> background;
    final private AdvancementFrame frame;
    final private boolean showToast;
    final private boolean announceToChat;
    final private boolean hidden;
    private float x;
    private float y;

    public AdvancementDisplay(ItemStack icon, Text title, Text description, Optional<AssetInfo> background, AdvancementFrame frame, boolean showToast, boolean announceToChat, boolean hidden) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.background = background;
        this.frame = frame;
        this.showToast = showToast;
        this.announceToChat = announceToChat;
        this.hidden = hidden;
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Text getTitle() {
        return this.title;
    }

    public Text getDescription() {
        return this.description;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public Optional<AssetInfo> getBackground() {
        return this.background;
    }

    public AdvancementFrame getFrame() {
        return this.frame;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public boolean shouldShowToast() {
        return this.showToast;
    }

    public boolean shouldAnnounceToChat() {
        return this.announceToChat;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    private void toPacket(RegistryByteBuf buf) {
        TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.title);
        TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.description);
        ItemStack.PACKET_CODEC.encode(buf, this.icon);
        buf.writeEnumConstant(this.frame);
        int i = 0;
        if (this.background.isPresent()) {
            i |= 1;
        }
        if (this.showToast) {
            i |= 2;
        }
        if (this.hidden) {
            i |= 4;
        }
        buf.net_minecraft_network_PacketByteBuf_writeInt(i);
        this.background.map(AssetInfo::id).ifPresent(buf::writeIdentifier);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.x);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.y);
    }

    private static AdvancementDisplay fromPacket(RegistryByteBuf buf) {
        Text text = (Text)TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf);
        Text text2 = (Text)TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf);
        ItemStack itemStack = (ItemStack)ItemStack.PACKET_CODEC.decode(buf);
        AdvancementFrame advancementFrame = buf.readEnumConstant(AdvancementFrame.class);
        int i = buf.readInt();
        Optional<AssetInfo> optional = (i & 1) != 0 ? Optional.of(new AssetInfo(buf.readIdentifier())) : Optional.empty();
        boolean bl = (i & 2) != 0;
        boolean bl2 = (i & 4) != 0;
        AdvancementDisplay advancementDisplay = new AdvancementDisplay(itemStack, text, text2, optional, advancementFrame, bl, false, bl2);
        advancementDisplay.setPos(buf.readFloat(), buf.readFloat());
        return advancementDisplay;
    }
}

