/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.report;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.network.message.MessageTrustStatus;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ChatAbuseReport;
import net.minecraft.client.session.report.MessagesListAdder;
import net.minecraft.client.session.report.log.ReceivedMessage;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Nullables;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChatSelectionScreen
extends Screen {
    final static Identifier CHECKMARK_ICON_TEXTURE = Identifier.ofVanilla("icon/checkmark");
    final static private Text TITLE_TEXT = Text.translatable("gui.chatSelection.title");
    final static private Text CONTEXT_TEXT = Text.translatable("gui.chatSelection.context");
    @Nullable
    final private Screen parent;
    final private AbuseReportContext reporter;
    private ButtonWidget doneButton;
    private MultilineText contextMessage;
    @Nullable
    private SelectionListWidget selectionList;
    final ChatAbuseReport.Builder report;
    final private Consumer<ChatAbuseReport.Builder> newReportConsumer;
    private MessagesListAdder listAdder;

    public ChatSelectionScreen(@Nullable Screen parent, AbuseReportContext reporter, ChatAbuseReport.Builder report, Consumer<ChatAbuseReport.Builder> newReportConsumer) {
        super(TITLE_TEXT);
        this.parent = parent;
        this.reporter = reporter;
        this.report = report.copy();
        this.newReportConsumer = newReportConsumer;
    }

    @Override
    protected void init() {
        this.listAdder = new MessagesListAdder(this.reporter, this::isSentByReportedPlayer);
        this.contextMessage = MultilineText.create(this.textRenderer, CONTEXT_TEXT, this.width - 16);
        this.selectionList = this.addDrawableChild(new SelectionListWidget(this.client, (this.contextMessage.count() + 1) * this.textRenderer.fontHeight));
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).dimensions(this.width / 2 - 155, this.height - 32, 150, 20).build());
        this.doneButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
            this.newReportConsumer.accept(this.report);
            this.close();
        }).dimensions(this.width / 2 - 155 + 160, this.height - 32, 150, 20).build());
        this.setDoneButtonActivation();
        this.addMessages();
        this.selectionList.setScrollY(this.selectionList.getMaxScrollY());
    }

    private boolean isSentByReportedPlayer(ReceivedMessage message) {
        return message.isSentFrom(this.report.getReportedPlayerUuid());
    }

    private void addMessages() {
        int i = this.selectionList.getDisplayedItemCount();
        this.listAdder.add(i, this.selectionList);
    }

    void addMoreMessages() {
        this.addMessages();
    }

    void setDoneButtonActivation() {
        this.doneButton.active = !this.report.getSelectedMessages().isEmpty();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, Colors.WHITE);
        AbuseReportLimits abuseReportLimits = this.reporter.getSender().getLimits();
        int i = this.report.getSelectedMessages().size();
        int j = abuseReportLimits.maxReportedMessageCount();
        MutableText text = Text.translatable("gui.chatSelection.selected", i, j);
        context.drawCenteredTextWithShadow(this.textRenderer, text, this.width / 2, 26, Colors.WHITE);
        this.contextMessage.drawCenterWithShadow(context, this.width / 2, this.selectionList.getContextMessageY());
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(super.getNarratedTitle(), CONTEXT_TEXT);
    }

    @Environment(value=EnvType.CLIENT)
    public class SelectionListWidget
    extends AlwaysSelectedEntryListWidget<Entry>
    implements MessagesListAdder.MessagesList {
        @Nullable
        private SenderEntryPair lastSenderEntryPair;

        public SelectionListWidget(MinecraftClient client, int contextMessagesHeight) {
            super(client, ChatSelectionScreen.this.width, ChatSelectionScreen.this.height - contextMessagesHeight - 80, 40, 16);
        }

        @Override
        public void setScrollY(double scrollY) {
            double d = this.getScrollY();
            super.setScrollY(scrollY);
            if ((float)this.getMaxScrollY() > 1.0E-5f && scrollY <= (double)1.0E-5f && !MathHelper.approximatelyEquals(scrollY, d)) {
                ChatSelectionScreen.this.addMoreMessages();
            }
        }

        @Override
        public void addMessage(int index, ReceivedMessage.ChatMessage message) {
            boolean bl = message.isSentFrom(ChatSelectionScreen.this.report.getReportedPlayerUuid());
            MessageTrustStatus messageTrustStatus = message.trustStatus();
            MessageIndicator messageIndicator = messageTrustStatus.createIndicator(message.message());
            MessageEntry entry = new MessageEntry(index, message.getContent(), message.getNarration(), messageIndicator, bl, true);
            this.addEntryToTop(entry);
            this.addSenderEntry(message, bl);
        }

        private void addSenderEntry(ReceivedMessage.ChatMessage message, boolean fromReportedPlayer) {
            SenderEntry entry = new SenderEntry(message.profile(), message.getHeadingText(), fromReportedPlayer);
            this.addEntryToTop(entry);
            SenderEntryPair senderEntryPair = new SenderEntryPair(message.getSenderUuid(), entry);
            if (this.lastSenderEntryPair != null && this.lastSenderEntryPair.senderEquals(senderEntryPair)) {
                this.removeEntryWithoutScrolling(this.lastSenderEntryPair.entry());
            }
            this.lastSenderEntryPair = senderEntryPair;
        }

        @Override
        public void addText(Text text) {
            this.addEntryToTop(new SeparatorEntry());
            this.addEntryToTop(new TextEntry(text));
            this.addEntryToTop(new SeparatorEntry());
            this.lastSenderEntryPair = null;
        }

        @Override
        public int getRowWidth() {
            return Math.min(350, this.width - 50);
        }

        public int getDisplayedItemCount() {
            return MathHelper.ceilDiv(this.height, this.itemHeight);
        }

        @Override
        protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
            Entry entry = (Entry)this.getEntry(index);
            if (this.shouldHighlight(entry)) {
                boolean bl = this.getSelectedOrNull() == entry;
                int i = this.isFocused() && bl ? -1 : -8355712;
                this.drawSelectionHighlight(context, y, entryWidth, entryHeight, i, -16777216);
            }
            entry.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, this.getHoveredEntry() == entry, delta);
        }

        private boolean shouldHighlight(Entry entry) {
            if (entry.canSelect()) {
                boolean bl = this.getSelectedOrNull() == entry;
                boolean bl2 = this.getSelectedOrNull() == null;
                boolean bl3 = this.getHoveredEntry() == entry;
                return bl || bl2 && bl3 && entry.isHighlightedOnHover();
            }
            return false;
        }

        @Override
        @Nullable
        protected Entry net_minecraft_client_gui_screen_report_ChatSelectionScreen$SelectionListWidget$Entry_getNeighboringEntry(NavigationDirection navigationDirection) {
            return this.getNeighboringEntry(navigationDirection, Entry::canSelect);
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            super.setSelected(entry);
            Entry entry2 = this.net_minecraft_client_gui_screen_report_ChatSelectionScreen$SelectionListWidget$Entry_getNeighboringEntry(NavigationDirection.UP);
            if (entry2 == null) {
                ChatSelectionScreen.this.addMoreMessages();
            }
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            Entry entry = (Entry)this.getSelectedOrNull();
            if (entry != null && entry.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        public int getContextMessageY() {
            return this.getBottom() + ((ChatSelectionScreen)ChatSelectionScreen.this).textRenderer.fontHeight;
        }

        @Override
        @Nullable
        protected EntryListWidget.Entry net_minecraft_client_gui_widget_EntryListWidget$Entry_getNeighboringEntry(NavigationDirection direction) {
            return this.net_minecraft_client_gui_screen_report_ChatSelectionScreen$SelectionListWidget$Entry_getNeighboringEntry(direction);
        }

        @Environment(value=EnvType.CLIENT)
        public class MessageEntry
        extends Entry {
            final static private int CHECKMARK_WIDTH = 9;
            final static private int CHECKMARK_HEIGHT = 8;
            final static private int CHAT_MESSAGE_LEFT_MARGIN = 11;
            final static private int INDICATOR_LEFT_MARGIN = 4;
            final private int index;
            final private StringVisitable truncatedContent;
            final private Text narration;
            @Nullable
            final private List<OrderedText> fullContent;
            @Nullable
            final private MessageIndicator.Icon indicatorIcon;
            @Nullable
            final private List<OrderedText> originalContent;
            final private boolean fromReportedPlayer;
            final private boolean isChatMessage;

            public MessageEntry(int index, Text message, @Nullable Text narration, MessageIndicator indicator, boolean fromReportedPlayer, boolean isChatMessage) {
                this.index = index;
                this.indicatorIcon = Nullables.map(indicator, MessageIndicator::icon);
                this.originalContent = indicator != null && indicator.text() != null ? ChatSelectionScreen.this.textRenderer.wrapLines(indicator.text(), SelectionListWidget.this.getRowWidth()) : null;
                this.fromReportedPlayer = fromReportedPlayer;
                this.isChatMessage = isChatMessage;
                StringVisitable stringVisitable = ChatSelectionScreen.this.textRenderer.trimToWidth(message, this.getTextWidth() - ChatSelectionScreen.this.textRenderer.getWidth(ScreenTexts.ELLIPSIS));
                if (message != stringVisitable) {
                    this.truncatedContent = StringVisitable.concat(stringVisitable, ScreenTexts.ELLIPSIS);
                    this.fullContent = ChatSelectionScreen.this.textRenderer.wrapLines(message, SelectionListWidget.this.getRowWidth());
                } else {
                    this.truncatedContent = message;
                    this.fullContent = null;
                }
                this.narration = narration;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
                if (this.isSelected() && this.fromReportedPlayer) {
                    this.drawCheckmark(context, y, x, entryHeight);
                }
                int i = x + this.getIndent();
                int j = y + 1 + (entryHeight - ((ChatSelectionScreen)ChatSelectionScreen.this).textRenderer.fontHeight) / 2;
                context.drawTextWithShadow(ChatSelectionScreen.this.textRenderer, Language.getInstance().reorder(this.truncatedContent), i, j, this.fromReportedPlayer ? -1 : -1593835521);
                if (this.fullContent != null && hovered) {
                    context.drawTooltip(this.fullContent, mouseX, mouseY);
                }
                int k = ChatSelectionScreen.this.textRenderer.getWidth(this.truncatedContent);
                this.renderIndicator(context, i + k + 4, y, entryHeight, mouseX, mouseY);
            }

            private void renderIndicator(DrawContext context, int x, int y, int entryHeight, int mouseX, int mouseY) {
                if (this.indicatorIcon != null) {
                    int i = y + (entryHeight - this.indicatorIcon.height) / 2;
                    this.indicatorIcon.draw(context, x, i);
                    if (this.originalContent != null && mouseX >= x && mouseX <= x + this.indicatorIcon.width && mouseY >= i && mouseY <= i + this.indicatorIcon.height) {
                        context.drawTooltip(this.originalContent, mouseX, mouseY);
                    }
                }
            }

            private void drawCheckmark(DrawContext context, int y, int x, int entryHeight) {
                int i = x;
                int j = y + (entryHeight - 8) / 2;
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, CHECKMARK_ICON_TEXTURE, i, j, 9, 8);
            }

            private int getTextWidth() {
                int i = this.indicatorIcon != null ? this.indicatorIcon.width + 4 : 0;
                return SelectionListWidget.this.getRowWidth() - this.getIndent() - 4 - i;
            }

            private int getIndent() {
                return this.isChatMessage ? 11 : 0;
            }

            @Override
            public Text getNarration() {
                return this.isSelected() ? Text.translatable("narrator.select", this.narration) : this.narration;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                SelectionListWidget.this.setSelected((Entry)null);
                return this.toggle();
            }

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                if (KeyCodes.isToggle(keyCode)) {
                    return this.toggle();
                }
                return false;
            }

            @Override
            public boolean isSelected() {
                return ChatSelectionScreen.this.report.isMessageSelected(this.index);
            }

            @Override
            public boolean canSelect() {
                return true;
            }

            @Override
            public boolean isHighlightedOnHover() {
                return this.fromReportedPlayer;
            }

            private boolean toggle() {
                if (this.fromReportedPlayer) {
                    ChatSelectionScreen.this.report.toggleMessageSelection(this.index);
                    ChatSelectionScreen.this.setDoneButtonActivation();
                    return true;
                }
                return false;
            }
        }

        @Environment(value=EnvType.CLIENT)
        public class SenderEntry
        extends Entry {
            final static private int PLAYER_SKIN_SIZE = 12;
            final static private int field_49545 = 4;
            final private Text headingText;
            final private Supplier<SkinTextures> skinTexturesSupplier;
            final private boolean fromReportedPlayer;

            public SenderEntry(GameProfile gameProfile, Text headingText, boolean fromReportedPlayer) {
                this.headingText = headingText;
                this.fromReportedPlayer = fromReportedPlayer;
                this.skinTexturesSupplier = SelectionListWidget.this.client.getSkinProvider().getSkinTexturesSupplier(gameProfile);
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
                int i = x - 12 + 4;
                int j = y + (entryHeight - 12) / 2;
                PlayerSkinDrawer.draw(context, this.skinTexturesSupplier.get(), i, j, 12);
                int k = y + 1 + (entryHeight - ((ChatSelectionScreen)ChatSelectionScreen.this).textRenderer.fontHeight) / 2;
                context.drawTextWithShadow(ChatSelectionScreen.this.textRenderer, this.headingText, i + 12 + 4, k, this.fromReportedPlayer ? Colors.WHITE : -1593835521);
            }
        }

        @Environment(value=EnvType.CLIENT)
        record SenderEntryPair(UUID sender, Entry entry) {
            public boolean senderEquals(SenderEntryPair pair) {
                return pair.sender.equals(this.sender);
            }
        }

        @Environment(value=EnvType.CLIENT)
        public static abstract class Entry
        extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            @Override
            public Text getNarration() {
                return ScreenTexts.EMPTY;
            }

            public boolean isSelected() {
                return false;
            }

            public boolean canSelect() {
                return false;
            }

            public boolean isHighlightedOnHover() {
                return this.canSelect();
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                return this.canSelect();
            }
        }

        @Environment(value=EnvType.CLIENT)
        public static class SeparatorEntry
        extends Entry {
            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            }
        }

        @Environment(value=EnvType.CLIENT)
        public class TextEntry
        extends Entry {
            final private Text text;

            public TextEntry(Text text) {
                this.text = text;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
                int i = y + entryHeight / 2;
                int j = x + entryWidth - 8;
                int k = ChatSelectionScreen.this.textRenderer.getWidth(this.text);
                int l = (x + j - k) / 2;
                int m = i - ((ChatSelectionScreen)ChatSelectionScreen.this).textRenderer.fontHeight / 2;
                context.drawTextWithShadow(ChatSelectionScreen.this.textRenderer, this.text, l, m, Colors.LIGHT_GRAY);
            }

            @Override
            public Text getNarration() {
                return this.text;
            }
        }
    }
}

