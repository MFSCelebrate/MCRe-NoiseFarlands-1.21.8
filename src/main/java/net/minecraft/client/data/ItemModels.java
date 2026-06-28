/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.BasicItemModel;
import net.minecraft.client.render.item.model.CompositeItemModel;
import net.minecraft.client.render.item.model.ConditionItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.item.model.SpecialItemModel;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.render.item.property.bool.HasComponentProperty;
import net.minecraft.client.render.item.property.bool.UsingItemProperty;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.item.property.select.ContextDimensionProperty;
import net.minecraft.client.render.item.property.select.ItemBlockStateProperty;
import net.minecraft.client.render.item.property.select.LocalTimeProperty;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.render.item.tint.ConstantTintSource;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.component.ComponentType;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class ItemModels {
    public static ItemModel.Unbaked basic(Identifier model) {
        return new BasicItemModel.Unbaked(model, List.of());
    }

    public static ItemModel.Unbaked tinted(Identifier model, TintSource ... tints) {
        return new BasicItemModel.Unbaked(model, List.of(tints));
    }

    public static TintSource constantTintSource(int value) {
        return new ConstantTintSource(value);
    }

    public static ItemModel.Unbaked composite(ItemModel.Unbaked ... models) {
        return new CompositeItemModel.Unbaked(List.of(models));
    }

    public static ItemModel.Unbaked special(Identifier base, SpecialModelRenderer.Unbaked specialModel) {
        return new SpecialItemModel.Unbaked(base, specialModel);
    }

    public static RangeDispatchItemModel.Entry rangeDispatchEntry(ItemModel.Unbaked model, float threshold) {
        return new RangeDispatchItemModel.Entry(threshold, model);
    }

    public static ItemModel.Unbaked rangeDispatch(NumericProperty property, ItemModel.Unbaked fallback, RangeDispatchItemModel.Entry ... entries) {
        return new RangeDispatchItemModel.Unbaked(property, 1.0f, List.of(entries), Optional.of(fallback));
    }

    public static ItemModel.Unbaked rangeDispatch(NumericProperty property, float scale, ItemModel.Unbaked fallback, RangeDispatchItemModel.Entry ... entries) {
        return new RangeDispatchItemModel.Unbaked(property, scale, List.of(entries), Optional.of(fallback));
    }

    public static ItemModel.Unbaked rangeDispatch(NumericProperty property, ItemModel.Unbaked fallback, List<RangeDispatchItemModel.Entry> entries) {
        return new RangeDispatchItemModel.Unbaked(property, 1.0f, entries, Optional.of(fallback));
    }

    public static ItemModel.Unbaked rangeDispatch(NumericProperty property, List<RangeDispatchItemModel.Entry> entries) {
        return new RangeDispatchItemModel.Unbaked(property, 1.0f, entries, Optional.empty());
    }

    public static ItemModel.Unbaked rangeDispatch(NumericProperty property, float scale, List<RangeDispatchItemModel.Entry> entries) {
        return new RangeDispatchItemModel.Unbaked(property, scale, entries, Optional.empty());
    }

    public static ItemModel.Unbaked condition(BooleanProperty property, ItemModel.Unbaked onTrue, ItemModel.Unbaked onFalse) {
        return new ConditionItemModel.Unbaked(property, onTrue, onFalse);
    }

    public static <T> SelectItemModel.SwitchCase<T> switchCase(T value, ItemModel.Unbaked model) {
        return new SelectItemModel.SwitchCase<T>(List.of(value), model);
    }

    public static <T> SelectItemModel.SwitchCase<T> switchCase(List<T> values, ItemModel.Unbaked model) {
        return new SelectItemModel.SwitchCase<T>(values, model);
    }

    @SafeVarargs
    public static <T> ItemModel.Unbaked select(SelectProperty<T> property, ItemModel.Unbaked fallback, SelectItemModel.SwitchCase<T> ... cases) {
        return ItemModels.select(property, fallback, List.of(cases));
    }

    public static <T> ItemModel.Unbaked select(SelectProperty<T> property, ItemModel.Unbaked fallback, List<SelectItemModel.SwitchCase<T>> cases) {
        return new SelectItemModel.Unbaked(new SelectItemModel.UnbakedSwitch<SelectProperty<T>, T>(property, cases), Optional.of(fallback));
    }

    @SafeVarargs
    public static <T> ItemModel.Unbaked select(SelectProperty<T> property, SelectItemModel.SwitchCase<T> ... cases) {
        return ItemModels.select(property, List.of(cases));
    }

    public static <T> ItemModel.Unbaked select(SelectProperty<T> property, List<SelectItemModel.SwitchCase<T>> cases) {
        return new SelectItemModel.Unbaked(new SelectItemModel.UnbakedSwitch<SelectProperty<T>, T>(property, cases), Optional.empty());
    }

    public static BooleanProperty usingItemProperty() {
        return new UsingItemProperty();
    }

    public static BooleanProperty hasComponentProperty(ComponentType<?> component) {
        return new HasComponentProperty(component, false);
    }

    public static ItemModel.Unbaked overworldSelect(ItemModel.Unbaked overworldModel, ItemModel.Unbaked fallback) {
        return ItemModels.select(new ContextDimensionProperty(), fallback, ItemModels.switchCase(World.OVERWORLD, overworldModel));
    }

    public static <T extends Comparable<T>> ItemModel.Unbaked select(Property<T> property, ItemModel.Unbaked fallback, Map<T, ItemModel.Unbaked> valuesToModels) {
        List<SelectItemModel.SwitchCase<T>> list = valuesToModels.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(entry -> {
            String string = property.name((Comparable)entry.getKey());
            return new SelectItemModel.SwitchCase<String>(List.of(string), (ItemModel.Unbaked)entry.getValue());
        }).toList();
        return ItemModels.select(new ItemBlockStateProperty(property.getName()), fallback, list);
    }

    public static ItemModel.Unbaked christmasSelect(ItemModel.Unbaked regularModel, ItemModel.Unbaked christmasModel) {
        return ItemModels.select(LocalTimeProperty.create("MM-dd", "", Optional.empty()), christmasModel, List.of(ItemModels.switchCase(List.of("12-24", "12-25", "12-26"), regularModel)));
    }
}

