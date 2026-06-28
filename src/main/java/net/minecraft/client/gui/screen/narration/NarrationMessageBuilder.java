/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.narration;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.Narration;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public interface NarrationMessageBuilder {
    default public void put(NarrationPart part, Text text) {
        this.put(part, Narration.string(text.getString()));
    }

    default public void put(NarrationPart part, String string) {
        this.put(part, Narration.string(string));
    }

    default public void put(NarrationPart part, Text ... texts) {
        this.put(part, Narration.texts((List<Text>)ImmutableList.copyOf((Object[])texts)));
    }

    public void put(NarrationPart var1, Narration<?> var2);

    public NarrationMessageBuilder nextMessage();
}

