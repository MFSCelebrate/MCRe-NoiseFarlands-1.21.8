/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.recipebook;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.context.ContextParameterMap;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GhostRecipe {
    final private Reference2ObjectMap<Slot, CyclingItem> items = new Reference2ObjectArrayMap();
    final private CurrentIndexProvider currentIndexProvider;

    public GhostRecipe(CurrentIndexProvider currentIndexProvider) {
        this.currentIndexProvider = currentIndexProvider;
    }

    public void clear() {
        this.items.clear();
    }

    private void addItems(Slot slot, ContextParameterMap context, SlotDisplay display, boolean resultSlot) {
        List<ItemStack> list = display.getStacks(context);
        if (!list.isEmpty()) {
            this.items.put((Object)slot, (Object)new CyclingItem(list, resultSlot));
        }
    }

    protected void addInputs(Slot slot, ContextParameterMap context, SlotDisplay display) {
        this.addItems(slot, context, display, false);
    }

    protected void addResults(Slot slot, ContextParameterMap context, SlotDisplay display) {
        this.addItems(slot, context, display, true);
    }

    public void draw(DrawContext context, MinecraftClient client, boolean resultHasPadding) {
        this.items.forEach((slot, item) -> {
            int i = slot.x;
            int j = slot.y;
            if (item.isResultSlot && resultHasPadding) {
                context.fill(i - 4, j - 4, i + 20, j + 20, 0x30FF0000);
            } else {
                context.fill(i, j, i + 16, j + 16, 0x30FF0000);
            }
            ItemStack itemStack = item.get(this.currentIndexProvider.currentIndex());
            context.drawItemWithoutEntity(itemStack, i, j);
            context.fill(i, j, i + 16, j + 16, 0x30FFFFFF);
            if (item.isResultSlot) {
                context.drawStackOverlay(minecraftClient.textRenderer, itemStack, i, j);
            }
        });
    }

    public void drawTooltip(DrawContext context, MinecraftClient client, int x, int y, @Nullable Slot slot) {
        if (slot == null) {
            return;
        }
        CyclingItem cyclingItem = (CyclingItem)this.items.get((Object)slot);
        if (cyclingItem != null) {
            ItemStack itemStack = cyclingItem.get(this.currentIndexProvider.currentIndex());
            context.drawTooltip(client.textRenderer, Screen.getTooltipFromItem(client, itemStack), x, y, itemStack.get(DataComponentTypes.TOOLTIP_STYLE));
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class CyclingItem
    extends Record {
        final private List<ItemStack> items;
        final boolean isResultSlot;

        CyclingItem(List<ItemStack> list, boolean bl) {
            this.items = list;
            this.isResultSlot = bl;
        }

        public ItemStack get(int index) {
            int i = this.items.size();
            if (i == 0) {
                return ItemStack.EMPTY;
            }
            return this.items.get(index % i);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{CyclingItem.class, "items;isResultSlot", "items", "isResultSlot"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CyclingItem.class, "items;isResultSlot", "items", "isResultSlot"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CyclingItem.class, "items;isResultSlot", "items", "isResultSlot"}, this, object);
        }

        public List<ItemStack> items() {
            return this.items;
        }

        public boolean isResultSlot() {
            return this.isResultSlot;
        }
    }
}

