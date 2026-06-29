/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.glfw.GLFW
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.lang.runtime.SwitchBootstraps;
import java.net.URI;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.Navigable;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.narration.ScreenNarrator;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.sound.MusicSound;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(value = EnvType.CLIENT)
public abstract class Screen extends AbstractParentElement implements Drawable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text SCREEN_USAGE_TEXT = Text.translatable("narrator.screen.usage");
    public static final Identifier MENU_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/menu_background.png");
    public static final Identifier HEADER_SEPARATOR_TEXTURE = Identifier.ofVanilla("textures/gui/header_separator.png");
    public static final Identifier FOOTER_SEPARATOR_TEXTURE = Identifier.ofVanilla("textures/gui/footer_separator.png");
    private static final Identifier INWORLD_MENU_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/inworld_menu_background.png");
    public static final Identifier INWORLD_HEADER_SEPARATOR_TEXTURE = Identifier.ofVanilla("textures/gui/inworld_header_separator.png");
    public static final Identifier INWORLD_FOOTER_SEPARATOR_TEXTURE = Identifier.ofVanilla("textures/gui/inworld_footer_separator.png");
    protected static final float field_60460 = 2000.0f;
    protected final Text title;
    private final List<Element> children = Lists.newArrayList();
    private final List<Selectable> selectables = Lists.newArrayList();
    @Nullable protected MinecraftClient client;
    private boolean screenInitialized;
    public int width;
    public int height;
    private final List<Drawable> drawables = Lists.newArrayList();
    protected TextRenderer textRenderer;
    private static final long SCREEN_INIT_NARRATION_DELAY;
    private static final long NARRATOR_MODE_CHANGE_DELAY;
    private static final long MOUSE_MOVE_NARRATION_DELAY = 750L;
    private static final long MOUSE_PRESS_SCROLL_NARRATION_DELAY = 200L;
    private static final long KEY_PRESS_NARRATION_DELAY = 200L;
    private final ScreenNarrator narrator = new ScreenNarrator();
    private long elementNarrationStartTime = Long.MIN_VALUE;
    private long screenNarrationStartTime = Long.MAX_VALUE;
    @Nullable protected CyclingButtonWidget<NarratorMode> narratorToggleButton;
    @Nullable private Selectable selected;
    protected final Executor executor = runnable -> this.client.execute(() -> {
        if (this.client.currentScreen == this) {
            runnable.run();
        }
    });

    protected Screen(Text title) {
        this.title = title;
    }

    public Text getTitle() {
        return this.title;
    }

    public Text getNarratedTitle() {
        return this.getTitle();
    }

    public final void renderWithTooltip(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.createNewRootLayer();
        this.renderBackground(context, mouseX, mouseY, deltaTicks);
        context.createNewRootLayer();
        this.render(context, mouseX, mouseY, deltaTicks);
        context.renderTooltip();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        for (Drawable drawable : this.drawables) {
            drawable.render(context, mouseX, mouseY, deltaTicks);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        GuiNavigation.Tab guiNavigation;
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
            this.close();
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        switch (keyCode) {
            case 263:
                {
                    Record record = this.getArrowNavigation(NavigationDirection.LEFT);
                    break;
                }
            case 262:
                {
                    Record record = this.getArrowNavigation(NavigationDirection.RIGHT);
                    break;
                }
            case 265:
                {
                    Record record = this.getArrowNavigation(NavigationDirection.UP);
                    break;
                }
            case 264:
                {
                    Record record = this.getArrowNavigation(NavigationDirection.DOWN);
                    break;
                }
            case 258:
                {
                    Record record = this.getTabNavigation();
                    break;
                }
            default:
                {
                    Record record = guiNavigation = null;
                }
        }
        if (guiNavigation != null) {
            GuiNavigationPath guiNavigationPath = super.getNavigationPath(guiNavigation);
            if (guiNavigationPath == null && guiNavigation instanceof GuiNavigation.Tab) {
                this.blur();
                guiNavigationPath = super.getNavigationPath(guiNavigation);
            }
            if (guiNavigationPath != null) {
                this.switchFocus(guiNavigationPath);
            }
        }
        return false;
    }

    private GuiNavigation.Tab getTabNavigation() {
        boolean bl = !Screen.hasShiftDown();
        return new GuiNavigation.Tab(bl);
    }

    private GuiNavigation.Arrow getArrowNavigation(NavigationDirection direction) {
        return new GuiNavigation.Arrow(direction);
    }

    protected void setInitialFocus() {
        GuiNavigation.Tab tab;
        GuiNavigationPath guiNavigationPath;
        if (this.client.getNavigationType().isKeyboard() && (guiNavigationPath = super.getNavigationPath(tab = new GuiNavigation.Tab(true))) != null) {
            this.switchFocus(guiNavigationPath);
        }
    }

    protected void setInitialFocus(Element element) {
        GuiNavigationPath guiNavigationPath = GuiNavigationPath.of(this, element.getNavigationPath(new GuiNavigation.Down()));
        if (guiNavigationPath != null) {
            this.switchFocus(guiNavigationPath);
        }
    }

    public void blur() {
        GuiNavigationPath guiNavigationPath = this.getFocusedPath();
        if (guiNavigationPath != null) {
            guiNavigationPath.setFocused(false);
        }
    }

    @VisibleForTesting
    protected void switchFocus(GuiNavigationPath path) {
        this.blur();
        path.setFocused(true);
    }

    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void close() {
        this.client.setScreen(null);
    }

    protected <T extends Element & Drawable> T addDrawableChild(T drawableElement) {
        this.drawables.add(drawableElement);
        return this.addSelectableChild(drawableElement);
    }

    protected <T extends Drawable> T addDrawable(T drawable) {
        this.drawables.add(drawable);
        return drawable;
    }

    protected <T extends Element & Selectable> T addSelectableChild(T child) {
        this.children.add(child);
        this.selectables.add(child);
        return child;
    }

    protected void remove(Element child) {
        if (child instanceof Drawable) {
            this.drawables.remove((Drawable) ((Object) child));
        }
        if (child instanceof Selectable) {
            this.selectables.remove((Selectable) ((Object) child));
        }
        this.children.remove(child);
    }

    protected void clearChildren() {
        this.drawables.clear();
        this.children.clear();
        this.selectables.clear();
    }

    public static List<Text> getTooltipFromItem(MinecraftClient client, ItemStack stack) {
        return stack.getTooltip(Item.TooltipContext.create(client.world), client.player, client.options.advancedItemTooltips ? TooltipType.Default.ADVANCED : TooltipType.Default.BASIC);
    }

    protected void insertText(String text, boolean override) {}

    public boolean handleTextClick(Style style) {
        ClickEvent clickEvent = style.getClickEvent();
        if (Screen.hasShiftDown()) {
            if (style.getInsertion() != null) {
                this.insertText(style.getInsertion(), false);
            }
        } else if (clickEvent != null) {
            this.handleClickEvent(this.client, clickEvent);
            return true;
        }
        return false;
    }

    protected void handleClickEvent(MinecraftClient client, ClickEvent clickEvent) {
        Screen.handleClickEvent(clickEvent, client, this);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    protected static void handleClickEvent(ClickEvent clickEvent, MinecraftClient client, @Nullable
                    Screen screenAfterRun) {
        ClientPlayerEntity clientPlayerEntity = Objects.requireNonNull(client.player, "Player not available");
        ClickEvent clickEvent2 = clickEvent;
        Objects.requireNonNull(clickEvent2);
        ClickEvent clickEvent3 = clickEvent2;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object
                []{ClickEvent.RunCommand.class, ClickEvent.ShowDialog.class, ClickEvent.Custom.class}, (Object) clickEvent3, n)) {
            case 0:
                {
                    String string2;
                    ClickEvent.RunCommand runCommand = (ClickEvent.RunCommand) clickEvent3;
                    try {
                        String string;
                        string2 = string = runCommand.command();
                    } catch (Throwable throwable) {
                        throw new MatchException(throwable.toString(), throwable);
                    }
                    Screen.handleRunCommand(clientPlayerEntity, string2, screenAfterRun);
                    return;
                }
            case 1:
                {
                    ClickEvent.ShowDialog showDialog = (ClickEvent.ShowDialog) clickEvent3;
                    clientPlayerEntity.networkHandler.showDialog(showDialog.dialog(), screenAfterRun);
                    return;
                }
            case 2:
                {
                    ClickEvent.Custom custom = (ClickEvent.Custom) clickEvent3;
                    clientPlayerEntity.networkHandler.sendPacket(new CustomClickActionC2SPacket(custom.id(), custom.payload()));
                    if (client.currentScreen == screenAfterRun) return;
                    client.setScreen(screenAfterRun);
                    return;
                }
        }
        Screen.handleBasicClickEvent(clickEvent, client, screenAfterRun);
    }

    /*
     * Loose catch block
     */
    protected static void handleBasicClickEvent(ClickEvent clickEvent, MinecraftClient client, @Nullable
                    Screen screenAfterRun) {
        boolean bl = true;
        try {
            ClickEvent event = clickEvent;
            if (event instanceof ClickEvent.OpenUrl openUrl) {
                URI uri = openUrl.uri();
                bl = Screen.handleOpenUri(client, screenAfterRun, uri);
            } else if (event instanceof ClickEvent.OpenFile openFile) {
                Util.getOperatingSystem().open(openFile.file());
                bl = true;
            } else if (event instanceof ClickEvent.SuggestCommand suggestCommand) {
                String command = suggestCommand.command();
                if (screenAfterRun != null) {
                    screenAfterRun.insertText(command, true);
                }
                bl = true;
            } else if (event instanceof ClickEvent.CopyToClipboard copyToClipboard) {
                String text = copyToClipboard.value();
                client.keyboard.setClipboard(text);
                bl = true;
            } else {
                LOGGER.error("Don't know how to handle {}", (Object) clickEvent);
                bl = true;
            }
        } catch (Throwable throwable) {
            throw new MatchException(throwable.toString(), throwable);
        }
        if (bl && client.currentScreen != screenAfterRun) {
            client.setScreen(screenAfterRun);
        }
    }

    protected static boolean handleOpenUri(MinecraftClient client, @Nullable
                    Screen screen, URI uri) {
        if (!client.options.getChatLinks().getValue().booleanValue()) {
            return false;
        }
        if (client.options.getChatLinksPrompt().getValue().booleanValue()) {
            client.setScreen(new ConfirmLinkScreen(confirmed -> {
                if (confirmed) {
                    Util.getOperatingSystem().open(uri);
                }
                client.setScreen(screen);
            }, uri.toString(), false));
        } else {
            Util.getOperatingSystem().open(uri);
        }
        return true;
    }

    protected static void handleRunCommand(ClientPlayerEntity player, String command, @Nullable
                    Screen screenAfterRun) {
        player.networkHandler.runClickEventCommand(CommandManager.stripLeadingSlash(command), screenAfterRun);
    }

    public final void init(MinecraftClient client, int width, int height) {
        this.client = client;
        this.textRenderer = client.textRenderer;
        this.width = width;
        this.height = height;
        if (!this.screenInitialized) {
            this.init();
            this.setInitialFocus();
        } else {
            this.refreshWidgetPositions();
        }
        this.screenInitialized = true;
        this.narrateScreenIfNarrationEnabled(false);
        this.setElementNarrationDelay(SCREEN_INIT_NARRATION_DELAY);
    }

    protected void clearAndInit() {
        this.clearChildren();
        this.blur();
        this.init();
        this.setInitialFocus();
    }

    protected void setWidgetAlpha(float alpha) {
        for (Element element : this.children()) {
            if (!(element instanceof ClickableWidget)) continue;
            ClickableWidget clickableWidget = (ClickableWidget) element;
            clickableWidget.setAlpha(alpha);
        }
    }

    @Override
    public List<? extends Element> children() {
        return this.children;
    }

    protected void init() {}

    public void tick() {}

    public void removed() {}

    public void onDisplayed() {}

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.client.world == null) {
            this.renderPanoramaBackground(context, deltaTicks);
        }
        this.applyBlur(context);
        this.renderDarkening(context);
    }

    protected void applyBlur(DrawContext context) {
        float f = this.client.options.getMenuBackgroundBlurrinessValue();
        if (f >= 1.0f) {
            context.applyBlur();
        }
    }

    protected void renderPanoramaBackground(DrawContext context, float deltaTicks) {
        this.client.gameRenderer.getRotatingPanoramaRenderer().render(context, this.width, this.height, true);
    }

    protected void renderDarkening(DrawContext context) {
        this.renderDarkening(context, 0, 0, this.width, this.height);
    }

    protected void renderDarkening(DrawContext context, int x, int y, int width, int height) {
        Screen.renderBackgroundTexture(context, this.client.world == null ? MENU_BACKGROUND_TEXTURE : INWORLD_MENU_BACKGROUND_TEXTURE, x, y, 0.0f, 0.0f, width, height);
    }

    public static void renderBackgroundTexture(DrawContext context, Identifier texture, int x, int y, float u, float v, int width, int height) {
        int i = 32;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, 32, 32);
    }

    public void renderInGameBackground(DrawContext context) {
        context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
    }

    public boolean shouldPause() {
        return true;
    }

    public static boolean hasControlDown() {
        if (MinecraftClient.IS_SYSTEM_MAC) {
            return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SUPER) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SUPER);
        }
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean hasShiftDown() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean hasAltDown() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_ALT) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_ALT);
    }

    public static boolean isCut(int code) {
        return code == 88 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isPaste(int code) {
        return code == 86 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isCopy(int code) {
        return code == 67 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isSelectAll(int code) {
        return code == 65 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    protected void refreshWidgetPositions() {
        this.clearAndInit();
    }

    public void resize(MinecraftClient client, int width, int height) {
        this.width = width;
        this.height = height;
        this.refreshWidgetPositions();
    }

    public void addCrashReportSection(CrashReport report) {
        CrashReportSection crashReportSection = report.addElement("Affected screen", 1);
        crashReportSection.add("Screen name", () -> this.getClass().getCanonicalName());
    }

    protected boolean isValidCharacterForName(String name, char character, int cursorPos) {
        int i = name.indexOf(58);
        int j = name.indexOf(47);
        if (character == ':') {
            return (j == -1 || cursorPos <= j) && i == -1;
        }
        if (character == '/') {
            return cursorPos > i;
        }
        return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '.';
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return true;
    }

    public void onFilesDropped(List<Path> paths) {}

    private void setScreenNarrationDelay(long delayMs, boolean restartElementNarration) {
        this.screenNarrationStartTime = Util.getMeasuringTimeMs() + delayMs;
        if (restartElementNarration) {
            this.elementNarrationStartTime = Long.MIN_VALUE;
        }
    }

    private void setElementNarrationDelay(long delayMs) {
        this.elementNarrationStartTime = Util.getMeasuringTimeMs() + delayMs;
    }

    public void applyMouseMoveNarratorDelay() {
        this.setScreenNarrationDelay(750L, false);
    }

    public void applyMousePressScrollNarratorDelay() {
        this.setScreenNarrationDelay(200L, true);
    }

    public void applyKeyPressNarratorDelay() {
        this.setScreenNarrationDelay(200L, true);
    }

    private boolean isNarratorActive() {
        return this.client.getNarratorManager().isActive();
    }

    public void updateNarrator() {
        long l;
        if (this.isNarratorActive() && (l = Util.getMeasuringTimeMs()) > this.screenNarrationStartTime && l > this.elementNarrationStartTime) {
            this.narrateScreen(true);
            this.screenNarrationStartTime = Long.MAX_VALUE;
        }
    }

    public void narrateScreenIfNarrationEnabled(boolean onlyChangedNarrations) {
        if (this.isNarratorActive()) {
            this.narrateScreen(onlyChangedNarrations);
        }
    }

    private void narrateScreen(boolean onlyChangedNarrations) {
        this.narrator.buildNarrations(this::addScreenNarrations);
        String string = this.narrator.buildNarratorText(!onlyChangedNarrations);
        if (!string.isEmpty()) {
            this.client.getNarratorManager().narrateSystemImmediately(string);
        }
    }

    protected boolean hasUsageText() {
        return true;
    }

    protected void addScreenNarrations(NarrationMessageBuilder messageBuilder) {
        messageBuilder.put(NarrationPart.TITLE, this.getNarratedTitle());
        if (this.hasUsageText()) {
            messageBuilder.put(NarrationPart.USAGE, SCREEN_USAGE_TEXT);
        }
        this.addElementNarrations(messageBuilder);
    }

    protected void addElementNarrations(NarrationMessageBuilder builder) {
        List<
                Selectable> list = this.selectables.stream().flatMap(selectable -> selectable.getNarratedParts().stream()).filter(Selectable
                ::isNarratable).sorted(Comparator.comparingInt(Navigable
                ::getNavigationOrder)).toList();
        SelectedElementNarrationData selectedElementNarrationData = Screen.findSelectedElementData(list, this.selected);
        if (selectedElementNarrationData != null) {
            if (selectedElementNarrationData.selectType.isFocused()) {
                this.selected = selectedElementNarrationData.selectable;
            }
            if (list.size() > 1) {
                builder.put(NarrationPart.POSITION, (Text) Text.translatable("narrator.position.screen", selectedElementNarrationData.index + 1, list.size()));
                if (selectedElementNarrationData.selectType == Selectable.SelectionType.FOCUSED) {
                    builder.put(NarrationPart.USAGE, this.getUsageNarrationText());
                }
            }
            selectedElementNarrationData.selectable.appendNarrations(builder.nextMessage());
        }
    }

    protected Text getUsageNarrationText() {
        return Text.translatable("narration.component_list.usage");
    }

    @Nullable
    public static SelectedElementNarrationData findSelectedElementData(List<
                    ? extends Selectable> selectables, @Nullable Selectable selectable) {
        SelectedElementNarrationData selectedElementNarrationData = null;
        SelectedElementNarrationData selectedElementNarrationData2 = null;
        int j = selectables.size();
        for (int i = 0; i < j; ++i) {
            Selectable selectable2 = selectables.get(i);
            Selectable.SelectionType selectionType = selectable2.getType();
            if (selectionType.isFocused()) {
                if (selectable2 == selectable) {
                    selectedElementNarrationData2 = new SelectedElementNarrationData(selectable2, i, selectionType);
                    continue;
                }
                return new SelectedElementNarrationData(selectable2, i, selectionType);
            }
            if (selectionType.compareTo(selectedElementNarrationData != null ? selectedElementNarrationData.selectType : Selectable.SelectionType.NONE) <= 0)
                continue;
            selectedElementNarrationData = new SelectedElementNarrationData(selectable2, i, selectionType);
        }
        return selectedElementNarrationData != null ? selectedElementNarrationData : selectedElementNarrationData2;
    }

    public void refreshNarrator(boolean previouslyDisabled) {
        if (previouslyDisabled) {
            this.setScreenNarrationDelay(NARRATOR_MODE_CHANGE_DELAY, false);
        }
        if (this.narratorToggleButton != null) {
            this.narratorToggleButton.setValue(this.client.options.getNarrator().getValue());
        }
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    public boolean showsStatusEffects() {
        return false;
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return new ScreenRect(0, 0, this.width, this.height);
    }

    @Nullable
    public MusicSound getMusic() {
        return null;
    }

    static {
        NARRATOR_MODE_CHANGE_DELAY = SCREEN_INIT_NARRATION_DELAY = TimeUnit.SECONDS.toMillis(2L);
    }

    @Environment(value = EnvType.CLIENT)
    public static class SelectedElementNarrationData {
        public final Selectable selectable;
        public final int index;
        public final Selectable.SelectionType selectType;

        public SelectedElementNarrationData(Selectable selectable, int index, Selectable.SelectionType selectType) {
            this.selectable = selectable;
            this.index = index;
            this.selectType = selectType;
        }
    }
}
