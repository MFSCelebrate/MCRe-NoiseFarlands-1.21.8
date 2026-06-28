/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.option;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class HotbarStorageEntry {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int HOTBAR_SIZE = PlayerInventory.getHotbarSize();
    final static public Codec<HotbarStorageEntry> CODEC = Codec.PASSTHROUGH.listOf().validate(stacks -> Util.decodeFixedLengthList(stacks, HOTBAR_SIZE)).xmap(HotbarStorageEntry::new, entry -> entry.stacks);
    final static private DynamicOps<NbtElement> NBT_OPS = NbtOps.INSTANCE;
    final static private Dynamic<?> EMPTY_STACK = new Dynamic(NBT_OPS, (Object)((NbtElement)ItemStack.OPTIONAL_CODEC.encodeStart(NBT_OPS, (Object)ItemStack.EMPTY).getOrThrow()));
    private List<Dynamic<?>> stacks;

    private HotbarStorageEntry(List<Dynamic<?>> stacks) {
        this.stacks = stacks;
    }

    public HotbarStorageEntry() {
        this(Collections.nCopies(HOTBAR_SIZE, EMPTY_STACK));
    }

    public List<ItemStack> deserialize(RegistryWrapper.WrapperLookup registries) {
        return this.stacks.stream().map(stack -> ItemStack.OPTIONAL_CODEC.parse(RegistryOps.withRegistry(stack, registries)).resultOrPartial(error -> LOGGER.warn("Could not parse hotbar item: {}", error)).orElse(ItemStack.EMPTY)).toList();
    }

    public void serialize(PlayerInventory playerInventory, DynamicRegistryManager registryManager) {
        RegistryOps<NbtElement> registryOps = registryManager.getOps(NBT_OPS);
        ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize((int)HOTBAR_SIZE);
        for (int i = 0; i < HOTBAR_SIZE; ++i) {
            ItemStack itemStack = playerInventory.getStack(i);
            Optional<Dynamic> optional = ItemStack.OPTIONAL_CODEC.encodeStart(registryOps, (Object)itemStack).resultOrPartial(error -> LOGGER.warn("Could not encode hotbar item: {}", error)).map(nbt -> new Dynamic(NBT_OPS, nbt));
            builder.add(optional.orElse(EMPTY_STACK));
        }
        this.stacks = builder.build();
    }

    public boolean isEmpty() {
        for (Dynamic<?> dynamic : this.stacks) {
            if (HotbarStorageEntry.isEmpty(dynamic)) continue;
            return false;
        }
        return true;
    }

    private static boolean isEmpty(Dynamic<?> stack) {
        return EMPTY_STACK.equals(stack);
    }
}

