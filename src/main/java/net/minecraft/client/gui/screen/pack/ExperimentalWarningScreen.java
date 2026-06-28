/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.pack;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Colors;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ExperimentalWarningScreen
extends Screen {
    final static private Text TITLE = Text.translatable("selectWorld.experimental.title");
    final static private Text MESSAGE = Text.translatable("selectWorld.experimental.message");
    final static private Text DETAILS = Text.translatable("selectWorld.experimental.details");
    final static private int field_42498 = 10;
    final static private int field_42499 = 100;
    final private BooleanConsumer callback;
    final Collection<ResourcePackProfile> enabledProfiles;
    final private GridWidget grid = new GridWidget().setColumnSpacing(10).setRowSpacing(20);

    public ExperimentalWarningScreen(Collection<ResourcePackProfile> enabledProfiles, BooleanConsumer callback) {
        super(TITLE);
        this.enabledProfiles = enabledProfiles;
        this.callback = callback;
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(super.getNarratedTitle(), MESSAGE);
    }

    @Override
    protected void init() {
        super.init();
        GridWidget.Adder adder = this.grid.createAdder(2);
        Positioner positioner = adder.copyPositioner().alignHorizontalCenter();
        adder.add(new TextWidget(this.title, this.textRenderer), 2, positioner);
        MultilineTextWidget multilineTextWidget = adder.add(new MultilineTextWidget(MESSAGE, this.textRenderer).setCentered(true), 2, positioner);
        multilineTextWidget.setMaxWidth(310);
        adder.add(ButtonWidget.builder(DETAILS, button -> this.client.setScreen(new DetailsScreen())).width(100).build(), 2, positioner);
        adder.add(ButtonWidget.builder(ScreenTexts.PROCEED, button -> this.callback.accept(true)).build());
        adder.add(ButtonWidget.builder(ScreenTexts.BACK, button -> this.callback.accept(false)).build());
        this.grid.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.grid.refreshPositions();
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        SimplePositioningWidget.setPos(this.grid, 0, 0, this.width, this.height, 0.5f, 0.5f);
    }

    @Override
    public void close() {
        this.callback.accept(false);
    }

    @Environment(value=EnvType.CLIENT)
    class DetailsScreen
    extends Screen {
        final static private Text TITLE = Text.translatable("selectWorld.experimental.details.title");
        final ThreePartsLayoutWidget layout;
        @Nullable
        private PackListWidget packListWidget;

        DetailsScreen() {
            super(TITLE);
            this.layout = new ThreePartsLayoutWidget(this);
        }

        @Override
        protected void init() {
            this.layout.addHeader(TITLE, this.textRenderer);
            this.packListWidget = this.layout.addBody(new PackListWidget(this, this.client, ExperimentalWarningScreen.this.enabledProfiles));
            this.layout.addFooter(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).build());
            this.layout.forEachChild(child -> {
                ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
            });
            this.refreshWidgetPositions();
        }

        @Override
        protected void refreshWidgetPositions() {
            if (this.packListWidget != null) {
                this.packListWidget.position(this.width, this.layout);
            }
            this.layout.refreshPositions();
        }

        @Override
        public void close() {
            this.client.setScreen(ExperimentalWarningScreen.this);
        }

        @Environment(value=EnvType.CLIENT)
        class PackListWidget
        extends AlwaysSelectedEntryListWidget<PackListWidgetEntry> {
            public PackListWidget(DetailsScreen detailsScreen, MinecraftClient client, Collection<ResourcePackProfile> enabledProfiles) {
                super(client, detailsScreen.width, detailsScreen.layout.getContentHeight(), detailsScreen.layout.getHeaderHeight(), (client.textRenderer.fontHeight + 2) * 3);
                for (ResourcePackProfile resourcePackProfile : enabledProfiles) {
                    String string = FeatureFlags.printMissingFlags(FeatureFlags.VANILLA_FEATURES, resourcePackProfile.getRequestedFeatures());
                    if (string.isEmpty()) continue;
                    MutableText text = Texts.setStyleIfAbsent(resourcePackProfile.getDisplayName().copy(), Style.EMPTY.withBold(true));
                    MutableText text2 = Text.translatable("selectWorld.experimental.details.entry", string);
                    this.addEntry(detailsScreen.new PackListWidgetEntry(text, text2, MultilineText.create(detailsScreen.textRenderer, (Text)text2, this.getRowWidth())));
                }
            }

            @Override
            public int getRowWidth() {
                return this.width * 3 / 4;
            }
        }

        @Environment(value=EnvType.CLIENT)
        class PackListWidgetEntry
        extends AlwaysSelectedEntryListWidget.Entry<PackListWidgetEntry> {
            final private Text displayName;
            final private Text details;
            final private MultilineText multilineDetails;

            PackListWidgetEntry(Text displayName, Text details, MultilineText multilineDetails) {
                this.displayName = displayName;
                this.details = details;
                this.multilineDetails = multilineDetails;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
                context.drawTextWithShadow(((DetailsScreen)DetailsScreen.this).client.textRenderer, this.displayName, x, y, Colors.WHITE);
                this.multilineDetails.drawWithShadow(context, x, y + 12, ((DetailsScreen)DetailsScreen.this).textRenderer.fontHeight, -1);
            }

            @Override
            public Text getNarration() {
                return Text.translatable("narrator.select", ScreenTexts.joinSentences(this.displayName, this.details));
            }
        }
    }
}

