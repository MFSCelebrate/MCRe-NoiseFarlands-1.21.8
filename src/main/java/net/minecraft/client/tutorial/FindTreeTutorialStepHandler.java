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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.tutorial.TutorialStepHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class FindTreeTutorialStepHandler
implements TutorialStepHandler {
    final static private int DELAY = 6000;
    final static private Text TITLE = Text.translatable("tutorial.find_tree.title");
    final static private Text DESCRIPTION = Text.translatable("tutorial.find_tree.description");
    final private TutorialManager manager;
    @Nullable
    private TutorialToast toast;
    private int ticks;

    public FindTreeTutorialStepHandler(TutorialManager manager) {
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
        if (this.ticks == 1 && (clientPlayerEntity = minecraftClient.player) != null && (FindTreeTutorialStepHandler.hasItem(clientPlayerEntity) || FindTreeTutorialStepHandler.hasBrokenTreeBlocks(clientPlayerEntity))) {
            this.manager.setStep(TutorialStep.CRAFT_PLANKS);
            return;
        }
        if (this.ticks >= 6000 && this.toast == null) {
            this.toast = new TutorialToast(minecraftClient.textRenderer, TutorialToast.Type.TREE, TITLE, DESCRIPTION, false);
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
    public void onTarget(ClientWorld world, HitResult hitResult) {
        BlockState blockState;
        if (hitResult.getType() == HitResult.Type.BLOCK && (blockState = world.getBlockState(((BlockHitResult)hitResult).getBlockPos())).isIn(BlockTags.COMPLETES_FIND_TREE_TUTORIAL)) {
            this.manager.setStep(TutorialStep.PUNCH_TREE);
        }
    }

    @Override
    public void onSlotUpdate(ItemStack stack) {
        if (stack.isIn(ItemTags.COMPLETES_FIND_TREE_TUTORIAL)) {
            this.manager.setStep(TutorialStep.CRAFT_PLANKS);
        }
    }

    private static boolean hasItem(ClientPlayerEntity player) {
        return player.getInventory().containsAny(stack -> stack.isIn(ItemTags.COMPLETES_FIND_TREE_TUTORIAL));
    }

    public static boolean hasBrokenTreeBlocks(ClientPlayerEntity player) {
        for (RegistryEntry<Block> registryEntry : Registries.BLOCK.iterateEntries(BlockTags.COMPLETES_FIND_TREE_TUTORIAL)) {
            Block block = registryEntry.value();
            if (player.getStatHandler().getStat(Stats.MINED.getOrCreateStat(block)) <= 0) continue;
            return true;
        }
        return false;
    }
}

