/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FlatLevelGeneratorPresetTags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.structure.StructureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PresetsScreen
extends Screen {
    final static Identifier SLOT_TEXTURE = Identifier.ofVanilla("container/slot");
    final static Logger LOGGER = LogUtils.getLogger();
    final static private int ICON_SIZE = 18;
    final static private int BUTTON_HEIGHT = 20;
    final static private int ICON_BACKGROUND_OFFSET_X = 1;
    final static private int ICON_BACKGROUND_OFFSET_Y = 1;
    final static private int ICON_OFFSET_X = 2;
    final static private int ICON_OFFSET_Y = 2;
    final static private RegistryKey<Biome> BIOME_KEY = BiomeKeys.PLAINS;
    final static public Text UNKNOWN_PRESET_TEXT = Text.translatable("flat_world_preset.unknown");
    final private CustomizeFlatLevelScreen parent;
    private Text shareText;
    private Text listText;
    private SuperflatPresetsListWidget listWidget;
    private ButtonWidget selectPresetButton;
    TextFieldWidget customPresetField;
    FlatChunkGeneratorConfig config;

    public PresetsScreen(CustomizeFlatLevelScreen parent) {
        super(Text.translatable("createWorld.customize.presets.title"));
        this.parent = parent;
    }

    @Nullable
    private static FlatChunkGeneratorLayer parseLayerString(RegistryEntryLookup<Block> blockLookup, String layer, int layerStartHeight) {
        Optional<RegistryEntry.Reference<Block>> optional;
        int i;
        String string;
        List list = Splitter.on((char)'*').limit(2).splitToList((CharSequence)layer);
        if (list.size() == 2) {
            string = (String)list.get(1);
            try {
                i = Math.max(Integer.parseInt((String)list.get(0)), 0);
            }
            catch (NumberFormatException numberFormatException) {
                LOGGER.error("Error while parsing flat world string", (Throwable)numberFormatException);
                return null;
            }
        } else {
            string = (String)list.get(0);
            i = 1;
        }
        int j = Math.min(layerStartHeight + i, DimensionType.MAX_HEIGHT);
        int k = j - layerStartHeight;
        try {
            optional = blockLookup.getOptional(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(string)));
        }
        catch (Exception exception) {
            LOGGER.error("Error while parsing flat world string", (Throwable)exception);
            return null;
        }
        if (optional.isEmpty()) {
            LOGGER.error("Error while parsing flat world string => Unknown block, {}", (Object)string);
            return null;
        }
        return new FlatChunkGeneratorLayer(k, optional.get().value());
    }

    private static List<FlatChunkGeneratorLayer> parsePresetLayersString(RegistryEntryLookup<Block> blockLookup, String layers) {
        ArrayList list = Lists.newArrayList();
        String[] strings = layers.split(",");
        int i = 0;
        for (String string : strings) {
            FlatChunkGeneratorLayer flatChunkGeneratorLayer = PresetsScreen.parseLayerString(blockLookup, string, i);
            if (flatChunkGeneratorLayer == null) {
                return Collections.emptyList();
            }
            int j = DimensionType.MAX_HEIGHT - i;
            if (j <= 0) continue;
            list.add(flatChunkGeneratorLayer.withMaxThickness(j));
            i += flatChunkGeneratorLayer.getThickness();
        }
        return list;
    }

    public static FlatChunkGeneratorConfig parsePresetString(RegistryEntryLookup<Block> blockLookup, RegistryEntryLookup<Biome> biomeLookup, RegistryEntryLookup<StructureSet> structureSetLookup, RegistryEntryLookup<PlacedFeature> placedFeatureLookup, String preset, FlatChunkGeneratorConfig config) {
        RegistryEntry.Reference<Biome> reference;
        Iterator iterator = Splitter.on((char)';').split((CharSequence)preset).iterator();
        if (!iterator.hasNext()) {
            return FlatChunkGeneratorConfig.getDefaultConfig(biomeLookup, structureSetLookup, placedFeatureLookup);
        }
        List<FlatChunkGeneratorLayer> list = PresetsScreen.parsePresetLayersString(blockLookup, (String)iterator.next());
        if (list.isEmpty()) {
            return FlatChunkGeneratorConfig.getDefaultConfig(biomeLookup, structureSetLookup, placedFeatureLookup);
        }
        RegistryEntry<Biome> registryEntry = reference = biomeLookup.getOrThrow(BIOME_KEY);
        if (iterator.hasNext()) {
            String string = (String)iterator.next();
            registryEntry = Optional.ofNullable(Identifier.tryParse(string)).map(biomeId -> RegistryKey.of(RegistryKeys.BIOME, biomeId)).flatMap(biomeLookup::getOptional).orElseGet(() -> {
                LOGGER.warn("Invalid biome: {}", (Object)string);
                return reference;
            });
        }
        return config.with(list, config.getStructureOverrides(), registryEntry);
    }

    static String getGeneratorConfigString(FlatChunkGeneratorConfig config) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < config.getLayers().size(); ++i) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(config.getLayers().get(i));
        }
        stringBuilder.append(";");
        stringBuilder.append(config.getBiome().getKey().map(RegistryKey::getValue).orElseThrow(() -> new IllegalStateException("Biome not registered")));
        return stringBuilder.toString();
    }

    @Override
    protected void init() {
        this.shareText = Text.translatable("createWorld.customize.presets.share");
        this.listText = Text.translatable("createWorld.customize.presets.list");
        this.customPresetField = new TextFieldWidget(this.textRenderer, 50, 40, this.width - 100, 20, this.shareText);
        this.customPresetField.setMaxLength(1230);
        GeneratorOptionsHolder generatorOptionsHolder = this.parent.parent.getWorldCreator().getGeneratorOptionsHolder();
        DynamicRegistryManager.Immutable dynamicRegistryManager = generatorOptionsHolder.getCombinedRegistryManager();
        FeatureSet featureSet = generatorOptionsHolder.dataConfiguration().enabledFeatures();
        RegistryWrapper.Impl registryEntryLookup = dynamicRegistryManager.net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.BIOME);
        RegistryWrapper.Impl registryEntryLookup2 = dynamicRegistryManager.net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.STRUCTURE_SET);
        RegistryWrapper.Impl registryEntryLookup3 = dynamicRegistryManager.net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.PLACED_FEATURE);
        RegistryWrapper.Impl registryEntryLookup4 = dynamicRegistryManager.net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.BLOCK).withFeatureFilter(featureSet);
        this.customPresetField.setText(PresetsScreen.getGeneratorConfigString(this.parent.getConfig()));
        this.config = this.parent.getConfig();
        this.addSelectableChild(this.customPresetField);
        this.listWidget = this.addDrawableChild(new SuperflatPresetsListWidget(dynamicRegistryManager, featureSet));
        this.selectPresetButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("createWorld.customize.presets.select"), buttonWidget -> {
            FlatChunkGeneratorConfig flatChunkGeneratorConfig = PresetsScreen.parsePresetString(registryEntryLookup4, registryEntryLookup, registryEntryLookup2, registryEntryLookup3, this.customPresetField.getText(), this.config);
            this.parent.setConfig(flatChunkGeneratorConfig);
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 155, this.height - 28, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 5, this.height - 28, 150, 20).build());
        this.updateSelectButton(this.listWidget.getSelectedOrNull() != null);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return this.listWidget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.customPresetField.getText();
        this.init(client, width, height);
        this.customPresetField.setText(string);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, Colors.WHITE);
        context.drawTextWithShadow(this.textRenderer, this.shareText, 51, 30, Colors.LIGHT_GRAY);
        context.drawTextWithShadow(this.textRenderer, this.listText, 51, 68, Colors.LIGHT_GRAY);
        this.customPresetField.render(context, mouseX, mouseY, deltaTicks);
    }

    public void updateSelectButton(boolean hasSelected) {
        this.selectPresetButton.active = hasSelected || this.customPresetField.getText().length() > 1;
    }

    @Environment(value=EnvType.CLIENT)
    class SuperflatPresetsListWidget
    extends AlwaysSelectedEntryListWidget<SuperflatPresetEntry> {
        public SuperflatPresetsListWidget(DynamicRegistryManager dynamicRegistryManager, FeatureSet featureSet) {
            super(PresetsScreen.this.client, PresetsScreen.this.width, PresetsScreen.this.height - 117, 80, 24);
            for (RegistryEntry<FlatLevelGeneratorPreset> registryEntry : dynamicRegistryManager.net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET).iterateEntries(FlatLevelGeneratorPresetTags.VISIBLE)) {
                Set set = registryEntry.value().settings().getLayers().stream().map(layer -> layer.getBlockState().getBlock()).filter(block -> !block.isEnabled(featureSet)).collect(Collectors.toSet());
                if (!set.isEmpty()) {
                    LOGGER.info("Discarding flat world preset {} since it contains experimental blocks {}", (Object)registryEntry.getKey().map(key -> key.getValue().toString()).orElse("<unknown>"), set);
                    continue;
                }
                this.addEntry(new SuperflatPresetEntry(registryEntry));
            }
        }

        @Override
        public void setSelected(@Nullable SuperflatPresetEntry superflatPresetEntry) {
            super.setSelected(superflatPresetEntry);
            PresetsScreen.this.updateSelectButton(superflatPresetEntry != null);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (super.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (KeyCodes.isToggle(keyCode) && this.getSelectedOrNull() != null) {
                ((SuperflatPresetEntry)this.getSelectedOrNull()).setPreset();
            }
            return false;
        }

        @Environment(value=EnvType.CLIENT)
        public class SuperflatPresetEntry
        extends AlwaysSelectedEntryListWidget.Entry<SuperflatPresetEntry> {
            final static private Identifier STATS_ICONS_TEXTURE = Identifier.ofVanilla("textures/gui/container/stats_icons.png");
            final private FlatLevelGeneratorPreset preset;
            final private Text text;

            public SuperflatPresetEntry(RegistryEntry<FlatLevelGeneratorPreset> preset) {
                this.preset = preset.value();
                this.text = preset.getKey().map(key -> Text.translatable(key.getValue().toTranslationKey("flat_world_preset"))).orElse(UNKNOWN_PRESET_TEXT);
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
                this.renderIcon(context, x, y, this.preset.displayItem().value());
                context.drawTextWithShadow(PresetsScreen.this.textRenderer, this.text, x + 18 + 5, y + 6, Colors.WHITE);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                this.setPreset();
                return super.mouseClicked(mouseX, mouseY, button);
            }

            void setPreset() {
                SuperflatPresetsListWidget.this.setSelected(this);
                PresetsScreen.this.config = this.preset.settings();
                PresetsScreen.this.customPresetField.setText(PresetsScreen.getGeneratorConfigString(PresetsScreen.this.config));
                PresetsScreen.this.customPresetField.setCursorToStart(false);
            }

            private void renderIcon(DrawContext context, int x, int y, Item iconItem) {
                this.drawIconBackground(context, x + 1, y + 1);
                context.drawItemWithoutEntity(new ItemStack(iconItem), x + 2, y + 2);
            }

            private void drawIconBackground(DrawContext context, int x, int y) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_TEXTURE, x, y, 18, 18);
            }

            @Override
            public Text getNarration() {
                return Text.translatable("narrator.select", this.text);
            }
        }
    }
}

