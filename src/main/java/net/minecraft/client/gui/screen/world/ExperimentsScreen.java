/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.WorldScreenOptionGrid;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.ScrollableLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ExperimentsScreen
extends Screen {
    final static private Text TITLE = Text.translatable("selectWorld.experiments");
    final static private Text INFO_TEXT = Text.translatable("selectWorld.experiments.info").formatted(Formatting.RED);
    final static private int EXPERIMENTS_LIST_WIDTH = 310;
    final static private int EXPERIMENTS_LIST_HEIGHT = 130;
    final private ThreePartsLayoutWidget experimentToggleList = new ThreePartsLayoutWidget(this);
    final private Screen parent;
    final private ResourcePackManager resourcePackManager;
    final private Consumer<ResourcePackManager> applier;
    final private Object2BooleanMap<ResourcePackProfile> experiments = new Object2BooleanLinkedOpenHashMap();
    @Nullable
    private ScrollableLayoutWidget experimentsList;

    public ExperimentsScreen(Screen parent, ResourcePackManager resourcePackManager, Consumer<ResourcePackManager> applier) {
        super(TITLE);
        this.parent = parent;
        this.resourcePackManager = resourcePackManager;
        this.applier = applier;
        for (ResourcePackProfile resourcePackProfile : resourcePackManager.getProfiles()) {
            if (resourcePackProfile.getSource() != ResourcePackSource.FEATURE) continue;
            this.experiments.put((Object)resourcePackProfile, resourcePackManager.getEnabledProfiles().contains(resourcePackProfile));
        }
    }

    @Override
    protected void init() {
        this.experimentToggleList.addHeader(TITLE, this.textRenderer);
        DirectionalLayoutWidget directionalLayoutWidget = this.experimentToggleList.addBody(DirectionalLayoutWidget.vertical());
        directionalLayoutWidget.add(new MultilineTextWidget(INFO_TEXT, this.textRenderer).setMaxWidth(310), positioner -> positioner.net_minecraft_client_gui_widget_Positioner_marginBottom(15));
        WorldScreenOptionGrid.Builder builder = WorldScreenOptionGrid.builder(299).withTooltipBox(2, true).setRowSpacing(4);
        this.experiments.forEach((pack, enabled2) -> builder.add(ExperimentsScreen.getDataPackName(pack), () -> this.experiments.getBoolean(pack), enabled -> this.experiments.put(pack, enabled.booleanValue())).tooltip(pack.getDescription()));
        LayoutWidget layoutWidget = builder.build().getLayout();
        this.experimentsList = new ScrollableLayoutWidget(this.client, layoutWidget, 130);
        this.experimentsList.setWidth(310);
        directionalLayoutWidget.add(this.experimentsList);
        DirectionalLayoutWidget directionalLayoutWidget2 = this.experimentToggleList.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.applyAndClose()).build());
        directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).build());
        this.experimentToggleList.forEachChild(widget -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(widget);
        });
        this.refreshWidgetPositions();
    }

    private static Text getDataPackName(ResourcePackProfile packProfile) {
        String string = "dataPack." + packProfile.getId() + ".name";
        return I18n.hasTranslation(string) ? Text.translatable(string) : packProfile.getDisplayName();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.experimentsList.setHeight(130);
        this.experimentToggleList.refreshPositions();
        int i = this.height - this.experimentToggleList.getFooterHeight() - this.experimentsList.getNavigationFocus().getBottom();
        this.experimentsList.setHeight(this.experimentsList.getHeight() + i);
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(super.getNarratedTitle(), INFO_TEXT);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private void applyAndClose() {
        ArrayList<ResourcePackProfile> list = new ArrayList<ResourcePackProfile>(this.resourcePackManager.getEnabledProfiles());
        ArrayList list2 = new ArrayList();
        this.experiments.forEach((pack, enabled) -> {
            list.remove(pack);
            if (enabled.booleanValue()) {
                list2.add(pack);
            }
        });
        list.addAll(Lists.reverse(list2));
        this.resourcePackManager.setEnabledProfiles(list.stream().map(ResourcePackProfile::getId).toList());
        this.applier.accept(this.resourcePackManager);
    }
}

