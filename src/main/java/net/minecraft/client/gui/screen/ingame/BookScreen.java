/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import java.lang.runtime.SwitchBootstraps;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BookScreen
extends Screen {
    final static public int field_32328 = 16;
    final static public int field_32329 = 36;
    final static public int field_32330 = 30;
    final static private int field_52807 = 256;
    final static private int field_52808 = 256;
    final static private Text TITLE_TEXT = Text.translatable("book.view.title");
    final static public Contents EMPTY_PROVIDER = new Contents(List.of());
    final static public Identifier BOOK_TEXTURE = Identifier.ofVanilla("textures/gui/book.png");
    final static protected int MAX_TEXT_WIDTH = 114;
    final static protected int MAX_TEXT_HEIGHT = 128;
    final static protected int WIDTH = 192;
    final static protected int HEIGHT = 192;
    private Contents contents;
    private int pageIndex;
    private List<OrderedText> cachedPage = Collections.emptyList();
    private int cachedPageIndex = -1;
    private Text pageIndexText = ScreenTexts.EMPTY;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    final private boolean pageTurnSound;

    public BookScreen(Contents pageProvider) {
        this(pageProvider, true);
    }

    public BookScreen() {
        this(EMPTY_PROVIDER, false);
    }

    private BookScreen(Contents contents, boolean playPageTurnSound) {
        super(TITLE_TEXT);
        this.contents = contents;
        this.pageTurnSound = playPageTurnSound;
    }

    public void setPageProvider(Contents pageProvider) {
        this.contents = pageProvider;
        this.pageIndex = MathHelper.clamp(this.pageIndex, 0, pageProvider.getPageCount());
        this.updatePageButtons();
        this.cachedPageIndex = -1;
    }

    public boolean setPage(int index) {
        int i = MathHelper.clamp(index, 0, this.contents.getPageCount() - 1);
        if (i != this.pageIndex) {
            this.pageIndex = i;
            this.updatePageButtons();
            this.cachedPageIndex = -1;
            return true;
        }
        return false;
    }

    protected boolean jumpToPage(int page) {
        return this.setPage(page);
    }

    @Override
    protected void init() {
        this.addCloseButton();
        this.addPageButtons();
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinLines(super.getNarratedTitle(), this.getPageIndicatorText(), this.contents.getPage(this.pageIndex));
    }

    private Text getPageIndicatorText() {
        return Text.translatable("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1));
    }

    protected void addCloseButton() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 100, 196, 200, 20).build());
    }

    protected void addPageButtons() {
        int i = (this.width - 192) / 2;
        int j = 2;
        this.nextPageButton = this.addDrawableChild(new PageTurnWidget(i + 116, 159, true, button -> this.goToNextPage(), this.pageTurnSound));
        this.previousPageButton = this.addDrawableChild(new PageTurnWidget(i + 43, 159, false, button -> this.goToPreviousPage(), this.pageTurnSound));
        this.updatePageButtons();
    }

    private int getPageCount() {
        return this.contents.getPageCount();
    }

    protected void goToPreviousPage() {
        if (this.pageIndex > 0) {
            --this.pageIndex;
        }
        this.updatePageButtons();
    }

    protected void goToNextPage() {
        if (this.pageIndex < this.getPageCount() - 1) {
            ++this.pageIndex;
        }
        this.updatePageButtons();
    }

    private void updatePageButtons() {
        this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
        this.previousPageButton.visible = this.pageIndex > 0;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        switch (keyCode) {
            case 266: {
                this.previousPageButton.onPress();
                return true;
            }
            case 267: {
                this.nextPageButton.onPress();
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        int i = (this.width - 192) / 2;
        int j = 2;
        if (this.cachedPageIndex != this.pageIndex) {
            Text stringVisitable = this.contents.getPage(this.pageIndex);
            this.cachedPage = this.textRenderer.wrapLines(stringVisitable, 114);
            this.pageIndexText = this.getPageIndicatorText();
        }
        this.cachedPageIndex = this.pageIndex;
        int k = this.textRenderer.getWidth(this.pageIndexText);
        context.drawText(this.textRenderer, this.pageIndexText, i - k + 192 - 44, 18, Colors.BLACK, false);
        int l = Math.min(128 / this.textRenderer.fontHeight, this.cachedPage.size());
        for (int m = 0; m < l; ++m) {
            OrderedText orderedText = this.cachedPage.get(m);
            context.drawText(this.textRenderer, orderedText, i + 36, 32 + m * this.textRenderer.fontHeight, -16777216, false);
        }
        Style style = this.getTextStyleAt(mouseX, mouseY);
        if (style != null) {
            context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.renderInGameBackground(context);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, BOOK_TEXTURE, (this.width - 192) / 2, 2, 0.0f, 0.0f, 192, 192, 256, 256);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Style style;
        if (button == 0 && (style = this.getTextStyleAt(mouseX, mouseY)) != null && this.handleTextClick(style)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    @Override
    protected void handleClickEvent(MinecraftClient client, ClickEvent clickEvent) {
        ClientPlayerEntity clientPlayerEntity = Objects.requireNonNull(client.player, "Player not available");
        ClickEvent clickEvent2 = clickEvent;
        Objects.requireNonNull(clickEvent2);
        ClickEvent clickEvent3 = clickEvent2;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ClickEvent.ChangePage.class, ClickEvent.RunCommand.class}, (Object)clickEvent3, n)) {
            case 0: {
                ClickEvent.ChangePage changePage = (ClickEvent.ChangePage)clickEvent3;
                try {
                    int n2;
                    int i = n2 = changePage.page();
                    this.jumpToPage(i - 1);
                    return;
                }
                catch (Throwable throwable) {
                    throw new MatchException(throwable.toString(), throwable);
                }
            }
            case 1: {
                String string2;
                ClickEvent.RunCommand runCommand = (ClickEvent.RunCommand)clickEvent3;
                {
                    String string;
                    string2 = string = runCommand.command();
                    this.method_72151();
                }
                BookScreen.handleRunCommand(clientPlayerEntity, string2, null);
                return;
            }
        }
        BookScreen.handleClickEvent(clickEvent, client, this);
    }

    protected void method_72151() {
    }

    @Nullable
    public Style getTextStyleAt(double x, double y) {
        if (this.cachedPage.isEmpty()) {
            return null;
        }
        int i = MathHelper.floor(x - (double)((this.width - 192) / 2) - 36.0);
        int j = MathHelper.floor(y - 2.0 - 30.0);
        if (i < 0 || j < 0) {
            return null;
        }
        int k = Math.min(128 / this.textRenderer.fontHeight, this.cachedPage.size());
        if (i <= 114 && j < this.client.textRenderer.fontHeight * k + k) {
            int l = j / this.client.textRenderer.fontHeight;
            if (l >= 0 && l < this.cachedPage.size()) {
                OrderedText orderedText = this.cachedPage.get(l);
                return this.client.textRenderer.getTextHandler().getStyleAt(orderedText, i);
            }
            return null;
        }
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    public record Contents(List<Text> pages) {
        public int getPageCount() {
            return this.pages.size();
        }

        public Text getPage(int index) {
            if (index >= 0 && index < this.getPageCount()) {
                return this.pages.get(index);
            }
            return ScreenTexts.EMPTY;
        }

        @Nullable
        public static Contents create(ItemStack stack) {
            boolean bl = MinecraftClient.getInstance().shouldFilterText();
            WrittenBookContentComponent writtenBookContentComponent = stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
            if (writtenBookContentComponent != null) {
                return new Contents(writtenBookContentComponent.getPages(bl));
            }
            WritableBookContentComponent writableBookContentComponent = stack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
            if (writableBookContentComponent != null) {
                return new Contents(writableBookContentComponent.stream(bl).map(Text::literal).toList());
            }
            return null;
        }
    }
}

