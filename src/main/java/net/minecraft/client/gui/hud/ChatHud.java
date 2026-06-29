/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Nullables;
import net.minecraft.util.collection.ArrayListDeque;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ChatHud {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int MAX_MESSAGES = 100;
    final static private int MISSING_MESSAGE_INDEX = -1;
    final static private int field_39772 = 4;
    final static private int field_39773 = 4;
    final static private int OFFSET_FROM_BOTTOM = 40;
    final static private int REMOVAL_QUEUE_TICKS = 60;
    final static private Text DELETED_MARKER_TEXT = Text.translatable("chat.deleted_marker").formatted(Formatting.GRAY, Formatting.ITALIC);
    final private MinecraftClient client;
    final private ArrayListDeque<String> messageHistory = new ArrayListDeque(100);
    final private List<ChatHudLine> messages = Lists.newArrayList();
    final private List<ChatHudLine.Visible> visibleMessages = Lists.newArrayList();
    private int scrolledLines;
    private boolean hasUnreadNewMessages;
    final private List<RemovalQueuedMessage> removalQueue = new ArrayList<RemovalQueuedMessage>();

    public ChatHud(MinecraftClient client) {
        this.client = client;
        this.messageHistory.addAll(client.getCommandHistoryManager().getHistory());
    }

    public void tickRemovalQueueIfExists() {
        if (!this.removalQueue.isEmpty()) {
            this.tickRemovalQueue();
        }
    }

    private int method_71990(int i, int j, boolean bl, int k, class_11511 arg) {
        int l = this.getLineHeight();
        int m = 0;
        for (int n = Math.min(this.visibleMessages.size() - this.scrolledLines, i) - 1; n >= 0; --n) {
            float f;
            int o = n + this.scrolledLines;
            ChatHudLine.Visible visible = this.visibleMessages.get(o);
            if (visible == null) continue;
            int p = j - visible.addedTime();
            float f2 = f = bl ? 1.0f : (float)ChatHud.getMessageOpacityMultiplier(p);
            if (!(f > 1.0E-5f)) continue;
            ++m;
            int q = k - n * l;
            int r = q - l;
            arg.accept(0, r, q, visible, n, f);
        }
        return m;
    }

    public void render(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused) {
        int s;
        int r;
        if (this.isChatHidden()) {
            return;
        }
        int i = this.getVisibleLineCount();
        int j2 = this.visibleMessages.size();
        if (j2 <= 0) {
            return;
        }
        Profiler profiler = Profilers.get();
        profiler.push("chat");
        float f = (float)this.getChatScale();
        int k2 = MathHelper.ceil((float)this.getWidth() / f);
        int l2 = context.getScaledWindowHeight();
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(f, f);
        context.getMatrices().translate(4.0f, 0.0f);
        int m2 = MathHelper.floor((float)(l2 - 40) / f);
        int n2 = this.getMessageIndex(this.toChatLineX(mouseX), this.toChatLineY(mouseY));
        float g2 = this.client.options.getChatOpacity().getValue().floatValue() * 0.9f + 0.1f;
        float h2 = this.client.options.getTextBackgroundOpacity().getValue().floatValue();
        double d = this.client.options.getChatLineSpacing().getValue();
        int o2 = (int)Math.round(-8.0 * (d + 1.0) + 4.0 * d);
        this.method_71990(i, currentTick, focused, m2, (l, m, n, visible, o, h) -> {
            context.fill(l - 4, m, l + k2 + 4 + 4, n, ColorHelper.withAlpha(h * h2, Colors.BLACK));
            MessageIndicator messageIndicator = visible.indicator();
            if (messageIndicator != null) {
                int p = ColorHelper.withAlpha(h * g2, messageIndicator.indicatorColor());
                context.fill(l - 4, m, l - 2, n, p);
                if (o == n2 && messageIndicator.icon() != null) {
                    int q = this.getIndicatorX(visible);
                    int r = n + o2 + this.client.textRenderer.fontHeight;
                    this.drawIndicatorIcon(context, q, r, messageIndicator.icon());
                }
            }
        });
        int p = this.method_71990(i, currentTick, focused, m2, (j, k, l, visible, m, g) -> {
            int n = l + o2;
            context.drawTextWithShadow(this.client.textRenderer, visible.content(), j, n, ColorHelper.withAlpha(g * g2, Colors.WHITE));
        });
        long q = this.client.getMessageHandler().getUnprocessedMessageCount();
        if (q > 0L) {
            r = (int)(128.0f * g2);
            s = (int)(255.0f * h2);
            context.getMatrices().pushMatrix();
            context.getMatrices().translate(0.0f, (float)m2);
            context.fill(-2, 0, k2 + 4, 9, s << 24);
            context.drawTextWithShadow(this.client.textRenderer, Text.translatable("chat.queue", q), 0, 1, ColorHelper.withAlpha(r, Colors.WHITE));
            context.getMatrices().popMatrix();
        }
        if (focused) {
            r = this.getLineHeight();
            s = j2 * r;
            int t = p * r;
            int u = this.scrolledLines * t / j2 - m2;
            int v = t * t / s;
            if (s != t) {
                int w = u > 0 ? 170 : 96;
                int x = this.hasUnreadNewMessages ? 0xCC3333 : 0x3333AA;
                int y = k2 + 4;
                context.fill(y, -u, y + 2, -u - v, ColorHelper.withAlpha(w, x));
                context.fill(y + 2, -u, y + 1, -u - v, ColorHelper.withAlpha(w, 0xCCCCCC));
            }
        }
        context.getMatrices().popMatrix();
        profiler.pop();
    }

    private void drawIndicatorIcon(DrawContext context, int x, int y, MessageIndicator.Icon icon) {
        int i = y - icon.height - 1;
        icon.draw(context, x, i);
    }

    private int getIndicatorX(ChatHudLine.Visible line) {
        return this.client.textRenderer.getWidth(line.content()) + 4;
    }

    private boolean isChatHidden() {
        return this.client.options.getChatVisibility().getValue() == ChatVisibility.HIDDEN;
    }

    private static double getMessageOpacityMultiplier(int age) {
        double d = (double)age / 200.0;
        d = 1.0 - d;
        d *= 10.0;
        d = MathHelper.clamp(d, 0.0, 1.0);
        d *= d;
        return d;
    }

    public void clear(boolean clearHistory) {
        this.client.getMessageHandler().processAll();
        this.removalQueue.clear();
        this.visibleMessages.clear();
        this.messages.clear();
        if (clearHistory) {
            this.messageHistory.clear();
            this.messageHistory.addAll(this.client.getCommandHistoryManager().getHistory());
        }
    }

    public void addMessage(Text message) {
        this.addMessage(message, null, this.client.isConnectedToLocalServer() ? MessageIndicator.singlePlayer() : MessageIndicator.system());
    }

    public void addMessage(Text message, @Nullable MessageSignatureData signatureData, @Nullable MessageIndicator indicator) {
        ChatHudLine chatHudLine = new ChatHudLine(this.client.inGameHud.getTicks(), message, signatureData, indicator);
        this.logChatMessage(chatHudLine);
        this.addVisibleMessage(chatHudLine);
        this.addMessage(chatHudLine);
    }

    private void logChatMessage(ChatHudLine message) {
        String string = message.content().getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
        String string2 = Nullables.map(message.indicator(), MessageIndicator::loggedName);
        if (string2 != null) {
            LOGGER.info("[{}] [CHAT] {}", (Object)string2, (Object)string);
        } else {
            LOGGER.info("[CHAT] {}", (Object)string);
        }
    }

    private void addVisibleMessage(ChatHudLine message) {
        int i = MathHelper.floor((double)this.getWidth() / this.getChatScale());
        MessageIndicator.Icon icon = message.getIcon();
        if (icon != null) {
            i -= icon.width + 4 + 2;
        }
        List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(message.content(), i, this.client.textRenderer);
        boolean bl = this.isChatFocused();
        for (int j = 0; j < list.size(); ++j) {
            OrderedText orderedText = list.get(j);
            if (bl && this.scrolledLines > 0) {
                this.hasUnreadNewMessages = true;
                this.scroll(1);
            }
            boolean bl2 = j == list.size() - 1;
            this.visibleMessages.add(0, new ChatHudLine.Visible(message.creationTick(), orderedText, message.indicator(), bl2));
        }
        while (this.visibleMessages.size() > 100) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }
    }

    private void addMessage(ChatHudLine message) {
        this.messages.add(0, message);
        while (this.messages.size() > 100) {
            this.messages.remove(this.messages.size() - 1);
        }
    }

    private void tickRemovalQueue() {
        int i = this.client.inGameHud.getTicks();
        this.removalQueue.removeIf(message -> {
            if (i >= message.deletableAfter()) {
                return this.queueForRemoval(message.signature()) == null;
            }
            return false;
        });
    }

    public void removeMessage(MessageSignatureData signature) {
        RemovalQueuedMessage removalQueuedMessage = this.queueForRemoval(signature);
        if (removalQueuedMessage != null) {
            this.removalQueue.add(removalQueuedMessage);
        }
    }

    @Nullable
    private RemovalQueuedMessage queueForRemoval(MessageSignatureData signature) {
        int i = this.client.inGameHud.getTicks();
        ListIterator<ChatHudLine> listIterator = this.messages.listIterator();
        while (listIterator.hasNext()) {
            ChatHudLine chatHudLine = listIterator.next();
            if (!signature.equals(chatHudLine.signature())) continue;
            int j = chatHudLine.creationTick() + 60;
            if (i >= j) {
                listIterator.set(this.createRemovalMarker(chatHudLine));
                this.refresh();
                return null;
            }
            return new RemovalQueuedMessage(signature, j);
        }
        return null;
    }

    private ChatHudLine createRemovalMarker(ChatHudLine original) {
        return new ChatHudLine(original.creationTick(), DELETED_MARKER_TEXT, null, MessageIndicator.system());
    }

    public void reset() {
        this.resetScroll();
        this.refresh();
    }

    private void refresh() {
        this.visibleMessages.clear();
        for (ChatHudLine chatHudLine : Lists.reverse(this.messages)) {
            this.addVisibleMessage(chatHudLine);
        }
    }

    public ArrayListDeque<String> getMessageHistory() {
        return this.messageHistory;
    }

    public void addToMessageHistory(String message) {
        if (!message.equals(this.messageHistory.peekLast())) {
            if (this.messageHistory.size() >= 100) {
                this.messageHistory.removeFirst();
            }
            this.messageHistory.addLast(message);
        }
        if (message.startsWith("/")) {
            this.client.getCommandHistoryManager().add(message);
        }
    }

    public void resetScroll() {
        this.scrolledLines = 0;
        this.hasUnreadNewMessages = false;
    }

    public void scroll(int scroll) {
        this.scrolledLines += scroll;
        int i = this.visibleMessages.size();
        if (this.scrolledLines > i - this.getVisibleLineCount()) {
            this.scrolledLines = i - this.getVisibleLineCount();
        }
        if (this.scrolledLines <= 0) {
            this.scrolledLines = 0;
            this.hasUnreadNewMessages = false;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY) {
        if (!this.isChatFocused() || this.client.options.hudHidden || this.isChatHidden()) {
            return false;
        }
        MessageHandler messageHandler = this.client.getMessageHandler();
        if (messageHandler.getUnprocessedMessageCount() == 0L) {
            return false;
        }
        double d = mouseX - 2.0;
        double e = (double)this.client.getWindow().getScaledHeight() - mouseY - 40.0;
        if (d <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()) && e < 0.0 && e > (double)MathHelper.floor(-9.0 * this.getChatScale())) {
            messageHandler.process();
            return true;
        }
        return false;
    }

    @Nullable
    public Style getTextStyleAt(double x, double y) {
        double e;
        double d = this.toChatLineX(x);
        int i = this.getMessageLineIndex(d, e = this.toChatLineY(y));
        if (i >= 0 && i < this.visibleMessages.size()) {
            ChatHudLine.Visible visible = this.visibleMessages.get(i);
            return this.client.textRenderer.getTextHandler().getStyleAt(visible.content(), MathHelper.floor(d));
        }
        return null;
    }

    @Nullable
    public MessageIndicator getIndicatorAt(double mouseX, double mouseY) {
        ChatHudLine.Visible visible;
        MessageIndicator messageIndicator;
        double e;
        double d = this.toChatLineX(mouseX);
        int i = this.getMessageIndex(d, e = this.toChatLineY(mouseY));
        if (i >= 0 && i < this.visibleMessages.size() && (messageIndicator = (visible = this.visibleMessages.get(i)).indicator()) != null && this.isXInsideIndicatorIcon(d, visible, messageIndicator)) {
            return messageIndicator;
        }
        return null;
    }

    private boolean isXInsideIndicatorIcon(double x, ChatHudLine.Visible line, MessageIndicator indicator) {
        if (x < 0.0) {
            return true;
        }
        MessageIndicator.Icon icon = indicator.icon();
        if (icon != null) {
            int i = this.getIndicatorX(line);
            int j = i + icon.width;
            return x >= (double)i && x <= (double)j;
        }
        return false;
    }

    private double toChatLineX(double x) {
        return x / this.getChatScale() - 4.0;
    }

    private double toChatLineY(double y) {
        double d = (double)this.client.getWindow().getScaledHeight() - y - 40.0;
        return d / (this.getChatScale() * (double)this.getLineHeight());
    }

    private int getMessageIndex(double chatLineX, double chatLineY) {
        int i = this.getMessageLineIndex(chatLineX, chatLineY);
        if (i == -1) {
            return -1;
        }
        while (i >= 0) {
            if (this.visibleMessages.get(i).endOfEntry()) {
                return i;
            }
            --i;
        }
        return i;
    }

    private int getMessageLineIndex(double chatLineX, double chatLineY) {
        int j;
        if (!this.isChatFocused() || this.isChatHidden()) {
            return -1;
        }
        if (chatLineX < -4.0 || chatLineX > (double)MathHelper.floor((double)this.getWidth() / this.getChatScale())) {
            return -1;
        }
        int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
        if (chatLineY >= 0.0 && chatLineY < (double)i && (j = MathHelper.floor(chatLineY + (double)this.scrolledLines)) >= 0 && j < this.visibleMessages.size()) {
            return j;
        }
        return -1;
    }

    public boolean isChatFocused() {
        return this.client.currentScreen instanceof ChatScreen;
    }

    public int getWidth() {
        return ChatHud.getWidth(this.client.options.getChatWidth().getValue());
    }

    public int getHeight() {
        return ChatHud.getHeight(this.isChatFocused() ? this.client.options.getChatHeightFocused().getValue() : this.client.options.getChatHeightUnfocused().getValue());
    }

    public double getChatScale() {
        return this.client.options.getChatScale().getValue();
    }

    public static int getWidth(double widthOption) {
        int i = 320;
        int j = 40;
        return MathHelper.floor(widthOption * 280.0 + 40.0);
    }

    public static int getHeight(double heightOption) {
        int i = 180;
        int j = 20;
        return MathHelper.floor(heightOption * 160.0 + 20.0);
    }

    public static double getDefaultUnfocusedHeight() {
        int i = 180;
        int j = 20;
        return 70.0 / (double)(ChatHud.getHeight(1.0) - 20);
    }

    public int getVisibleLineCount() {
        return this.getHeight() / this.getLineHeight();
    }

    private int getLineHeight() {
        return (int)((double)this.client.textRenderer.fontHeight * (this.client.options.getChatLineSpacing().getValue() + 1.0));
    }

    public ChatState toChatState() {
        return new ChatState(List.copyOf(this.messages), List.copyOf(this.messageHistory), List.copyOf(this.removalQueue));
    }

    public void restoreChatState(ChatState state) {
        this.messageHistory.clear();
        this.messageHistory.addAll(state.messageHistory);
        this.removalQueue.clear();
        this.removalQueue.addAll(state.removalQueue);
        this.messages.clear();
        this.messages.addAll(state.messages);
        this.refresh();
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface class_11511 {
        public void accept(int var1, int var2, int var3, ChatHudLine.Visible var4, int var5, float var6);
    }

    @Environment(value=EnvType.CLIENT)
    record RemovalQueuedMessage(MessageSignatureData signature, int deletableAfter) {
    }

    @Environment(value=EnvType.CLIENT)
    public static class ChatState {
        final List<ChatHudLine> messages;
        final List<String> messageHistory;
        final List<RemovalQueuedMessage> removalQueue;

        public ChatState(List<ChatHudLine> messages, List<String> messageHistory, List<RemovalQueuedMessage> removalQueue) {
            this.messages = messages;
            this.messageHistory = messageHistory;
            this.removalQueue = removalQueue;
        }
    }
}

