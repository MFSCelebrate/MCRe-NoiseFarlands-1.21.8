/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.search;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.search.IdentifierSearchProvider;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.client.search.TextSearchProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class SearchManager {
    final static private Key RECIPE_OUTPUT = new Key();
    final static private Key ITEM_TOOLTIP = new Key();
    final static private Key ITEM_TAG = new Key();
    private CompletableFuture<SearchProvider<ItemStack>> itemTooltipReloadFuture = CompletableFuture.completedFuture(SearchProvider.empty());
    private CompletableFuture<SearchProvider<ItemStack>> itemTagReloadFuture = CompletableFuture.completedFuture(SearchProvider.empty());
    private CompletableFuture<SearchProvider<RecipeResultCollection>> recipeOutputReloadFuture = CompletableFuture.completedFuture(SearchProvider.empty());
    final private Map<Key, Runnable> reloaders = new IdentityHashMap<Key, Runnable>();

    private void addReloader(Key key, Runnable reloader) {
        reloader.run();
        this.reloaders.put(key, reloader);
    }

    public void refresh() {
        for (Runnable runnable : this.reloaders.values()) {
            runnable.run();
        }
    }

    private static Stream<String> collectItemTooltips(Stream<ItemStack> stacks, Item.TooltipContext context, TooltipType type) {
        return stacks.flatMap(stack -> stack.getTooltip(context, null, type).stream()).map(tooltip -> Formatting.strip(tooltip.getString()).trim()).filter(string -> !string.isEmpty());
    }

    public void addRecipeOutputReloader(ClientRecipeBook recipeBook, World world) {
        this.addReloader(RECIPE_OUTPUT, () -> {
            List<RecipeResultCollection> list = recipeBook.getOrderedResults();
            DynamicRegistryManager dynamicRegistryManager = world.getRegistryManager();
            RegistryWrapper.Impl registry = dynamicRegistryManager.net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.ITEM);
            Item.TooltipContext tooltipContext = Item.TooltipContext.create(dynamicRegistryManager);
            ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters(world);
            TooltipType.Default tooltipType = TooltipType.Default.BASIC;
            CompletableFuture<SearchProvider<RecipeResultCollection>> completableFuture = this.recipeOutputReloadFuture;
            this.recipeOutputReloadFuture = CompletableFuture.supplyAsync(() -> SearchManager.method_60361(contextParameterMap, tooltipContext, tooltipType, (Registry)registry, list), Util.getMainWorkerExecutor());
            completableFuture.cancel(true);
        });
    }

    public SearchProvider<RecipeResultCollection> getRecipeOutputReloadFuture() {
        return this.recipeOutputReloadFuture.join();
    }

    public void addItemTagReloader(List<ItemStack> stacks) {
        this.addReloader(ITEM_TAG, () -> {
            CompletableFuture<SearchProvider<ItemStack>> completableFuture = this.itemTagReloadFuture;
            this.itemTagReloadFuture = CompletableFuture.supplyAsync(() -> new IdentifierSearchProvider<ItemStack>(stack -> stack.streamTags().map(TagKey::id), stacks), Util.getMainWorkerExecutor());
            completableFuture.cancel(true);
        });
    }

    public SearchProvider<ItemStack> getItemTagReloadFuture() {
        return this.itemTagReloadFuture.join();
    }

    public void addItemTooltipReloader(RegistryWrapper.WrapperLookup registries, List<ItemStack> stacks) {
        this.addReloader(ITEM_TOOLTIP, () -> {
            Item.TooltipContext tooltipContext = Item.TooltipContext.create(registries);
            TooltipType.Default tooltipType = TooltipType.Default.BASIC.withCreative();
            CompletableFuture<SearchProvider<ItemStack>> completableFuture = this.itemTooltipReloadFuture;
            this.itemTooltipReloadFuture = CompletableFuture.supplyAsync(() -> new TextSearchProvider<ItemStack>(stack -> SearchManager.collectItemTooltips(Stream.of(stack), tooltipContext, tooltipType), stack -> stack.getRegistryEntry().getKey().map(RegistryKey::getValue).stream(), stacks), Util.getMainWorkerExecutor());
            completableFuture.cancel(true);
        });
    }

    public SearchProvider<ItemStack> getItemTooltipReloadFuture() {
        return this.itemTooltipReloadFuture.join();
    }

    private static SearchProvider method_60361(ContextParameterMap contextParameterMap, Item.TooltipContext tooltipContext, TooltipType tooltipType, Registry registry, List list) {
        return new TextSearchProvider<RecipeResultCollection>(resultCollection -> SearchManager.collectItemTooltips(resultCollection.getAllRecipes().stream().flatMap(display -> display.getStacks(contextParameterMap).stream()), tooltipContext, tooltipType), resultCollection -> resultCollection.getAllRecipes().stream().flatMap(display -> display.getStacks(contextParameterMap).stream()).map(stack -> registry.getId(stack.getItem())), list);
    }

    @Environment(value=EnvType.CLIENT)
    static class Key {
        Key() {
        }
    }
}

