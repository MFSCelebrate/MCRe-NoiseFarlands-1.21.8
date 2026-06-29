/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import java.util.function.Consumer;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public interface Spawner {
    public void setEntityType(EntityType<?> var1, Random var2);

    public static void appendSpawnDataToTooltip(NbtComponent nbtComponent, Consumer<Text> textConsumer, String spawnDataKey) {
        Text text = Spawner.getSpawnedEntityText(nbtComponent, spawnDataKey);
        if (text != null) {
            textConsumer.accept(text);
        } else {
            textConsumer.accept(ScreenTexts.EMPTY);
            textConsumer.accept(Text.translatable("block.minecraft.spawner.desc1").formatted(Formatting.GRAY));
            textConsumer.accept(ScreenTexts.space().append(Text.translatable("block.minecraft.spawner.desc2").formatted(Formatting.BLUE)));
        }
    }

    @Nullable
    public static Text getSpawnedEntityText(NbtComponent nbtComponent, String spawnDataKey) {
        return nbtComponent.getNbt().getCompound(spawnDataKey).flatMap(spawnDataNbt -> spawnDataNbt.getCompound("entity")).flatMap(entityNbt -> entityNbt.get("id", EntityType.CODEC)).map(entityType -> Text.translatable(entityType.getTranslationKey()).formatted(Formatting.GRAY)).orElse(null);
    }
}

