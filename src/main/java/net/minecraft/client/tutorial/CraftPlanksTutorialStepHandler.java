/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.tutorial.TutorialStepHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CraftPlanksTutorialStepHandler
implements TutorialStepHandler {
    final static private int DELAY = 1200;
    final static private Text TITLE = Text.translatable("tutorial.craft_planks.title");
    final static private Text DESCRIPTION = Text.translatable("tutorial.craft_planks.description");
    final private TutorialManager manager;
    @Nullable
    private TutorialToast toast;
    private int ticks;

    public CraftPlanksTutorialStepHandler(TutorialManager manager) {
        this.manager = manager;
    }

    @Override
    public void tick() {
        ClientPlayerEntity clientPlayerEntity;
        ++this.ticks;
        if (!this.manager.isInSurvival()) {
            this.manager.setStep(TutorialStep.NONE);
            return;
        }
        MinecraftClient minecraftClient = this.manager.getClient();
        if (this.ticks == 1 && (clientPlayerEntity = minecraftClient.player) != null) {
            if (clientPlayerEntity.getInventory().contains(ItemTags.PLANKS)) {
                this.manager.setStep(TutorialStep.NONE);
                return;
            }
            if (CraftPlanksTutorialStepHandler.hasCrafted(clientPlayerEntity, ItemTags.PLANKS)) {
                this.manager.setStep(TutorialStep.NONE);
                return;
            }
        }
        if (this.ticks >= 1200 && this.toast == null) {
            this.toast = new TutorialToast(minecraftClient.textRenderer, TutorialToast.Type.WOODEN_PLANKS, TITLE, DESCRIPTION, false);
            minecraftClient.getToastManager().add(this.toast);
        }
    }

    @Override
    public void destroy() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }
    }

    @Override
    public void onSlotUpdate(ItemStack stack) {
        if (stack.isIn(ItemTags.PLANKS)) {
            this.manager.setStep(TutorialStep.NONE);
        }
    }

    public static boolean hasCrafted(ClientPlayerEntity player, TagKey<Item> tag) {
        for (RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(tag)) {
            if (player.getStatHandler().getStat(Stats.CRAFTED.getOrCreateStat(registryEntry.value())) <= 0) continue;
            return true;
        }
        return false;
    }
}

