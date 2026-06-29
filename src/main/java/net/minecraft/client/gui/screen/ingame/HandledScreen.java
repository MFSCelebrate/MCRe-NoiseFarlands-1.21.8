/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector2i
 *  org.lwjgl.glfw.GLFW
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Sets;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.tooltip.BundleTooltipSubmenuHandler;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipSubmenuHandler;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public abstract class HandledScreen<T extends ScreenHandler>
extends Screen
implements ScreenHandlerProvider<T> {
    final static public Identifier BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/container/inventory.png");
    final static private Identifier SLOT_HIGHLIGHT_BACK_TEXTURE = Identifier.ofVanilla("container/slot_highlight_back");
    final static private Identifier SLOT_HIGHLIGHT_FRONT_TEXTURE = Identifier.ofVanilla("container/slot_highlight_front");
    final static protected int field_52802 = 256;
    final static protected int field_52803 = 256;
    final static private float field_32318 = 100.0f;
    final static private int field_32319 = 500;
    protected int backgroundWidth = 176;
    protected int backgroundHeight = 166;
    protected int titleX;
    protected int titleY;
    protected int playerInventoryTitleX;
    protected int playerInventoryTitleY;
    final private List<TooltipSubmenuHandler> tooltipSubmenuHandlers;
    final protected T handler;
    final protected Text playerInventoryTitle;
    @Nullable
    protected Slot focusedSlot;
    @Nullable
    private Slot touchDragSlotStart;
    @Nullable
    private Slot touchHoveredSlot;
    @Nullable
    private Slot lastClickedSlot;
    @Nullable
    private LetGoTouchStack letGoTouchStack;
    protected int x;
    protected int y;
    private boolean touchIsRightClickDrag;
    private ItemStack touchDragStack = ItemStack.EMPTY;
    private long touchDropTimer;
    final protected Set<Slot> cursorDragSlots = Sets.newHashSet();
    protected boolean cursorDragging;
    private int heldButtonType;
    private int heldButtonCode;
    private boolean cancelNextRelease;
    private int draggedStackRemainder;
    private long lastButtonClickTime;
    private int lastClickedButton;
    private boolean doubleClicking;
    private ItemStack quickMovingStack = ItemStack.EMPTY;

    public HandledScreen(T handler, PlayerInventory inventory, Text title) {
        super(title);
        this.handler = handler;
        this.playerInventoryTitle = inventory.getDisplayName();
        this.cancelNextRelease = true;
        this.titleX = 8;
        this.titleY = 6;
        this.playerInventoryTitleX = 8;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        this.tooltipSubmenuHandlers = new ArrayList<TooltipSubmenuHandler>();
    }

    @Override
    protected void init() {
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
        this.tooltipSubmenuHandlers.clear();
        this.addTooltipSubmenuHandler(new BundleTooltipSubmenuHandler(this.client));
    }

    protected void addTooltipSubmenuHandler(TooltipSubmenuHandler handler) {
        this.tooltipSubmenuHandlers.add(handler);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.renderMain(context, mouseX, mouseY, deltaTicks);
        this.renderCursorStack(context, mouseX, mouseY);
        this.renderLetGoTouchStack(context);
    }

    public void renderMain(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int i = this.x;
        int j = this.y;
        super.render(context, mouseX, mouseY, deltaTicks);
        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float)i, (float)j);
        this.drawForeground(context, mouseX, mouseY);
        Slot slot = this.focusedSlot;
        this.focusedSlot = this.getSlotAt(mouseX, mouseY);
        this.drawSlotHighlightBack(context);
        this.drawSlots(context);
        this.drawSlotHighlightFront(context);
        if (slot != null && slot != this.focusedSlot) {
            this.resetTooltipSubmenus(slot);
        }
        context.getMatrices().popMatrix();
    }

    public void renderCursorStack(DrawContext context, int mouseX, int mouseY) {
        ItemStack itemStack;
        ItemStack itemStack2 = itemStack = this.touchDragStack.isEmpty() ? ((ScreenHandler)this.handler).getCursorStack() : this.touchDragStack;
        if (!itemStack.isEmpty()) {
            int i = 8;
            int j = this.touchDragStack.isEmpty() ? 8 : 16;
            String string = null;
            if (!this.touchDragStack.isEmpty() && this.touchIsRightClickDrag) {
                itemStack = itemStack.copyWithCount(MathHelper.ceil((float)itemStack.getCount() / 2.0f));
            } else if (this.cursorDragging && this.cursorDragSlots.size() > 1 && (itemStack = itemStack.copyWithCount(this.draggedStackRemainder)).isEmpty()) {
                string = String.valueOf(Formatting.YELLOW) + "0";
            }
            context.createNewRootLayer();
            this.drawItem(context, itemStack, mouseX - 8, mouseY - j, string);
        }
    }

    public void renderLetGoTouchStack(DrawContext context) {
        if (this.letGoTouchStack != null) {
            float f = MathHelper.clamp((float)(Util.getMeasuringTimeMs() - this.letGoTouchStack.time) / 100.0f, 0.0f, 1.0f);
            int i = this.letGoTouchStack.end.x - this.letGoTouchStack.start.x;
            int j = this.letGoTouchStack.end.y - this.letGoTouchStack.start.y;
            int k = this.letGoTouchStack.start.x + (int)((float)i * f);
            int l = this.letGoTouchStack.start.y + (int)((float)j * f);
            context.createNewRootLayer();
            this.drawItem(context, this.letGoTouchStack.item, k, l, null);
            if (f >= 1.0f) {
                this.letGoTouchStack = null;
            }
        }
    }

    protected void drawSlots(DrawContext context) {
        for (Slot slot : ((ScreenHandler)this.handler).slots) {
            if (!slot.isEnabled()) continue;
            this.drawSlot(context, slot);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.renderInGameBackground(context);
        this.drawBackground(context, deltaTicks, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            for (TooltipSubmenuHandler tooltipSubmenuHandler : this.tooltipSubmenuHandlers) {
                if (!tooltipSubmenuHandler.isApplicableTo(this.focusedSlot) || !tooltipSubmenuHandler.onScroll(horizontalAmount, verticalAmount, this.focusedSlot.id, this.focusedSlot.getStack())) continue;
                return true;
            }
        }
        return false;
    }

    private void drawSlotHighlightBack(DrawContext context) {
        if (this.focusedSlot != null && this.focusedSlot.canBeHighlighted()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_TEXTURE, this.focusedSlot.x - 4, this.focusedSlot.y - 4, 24, 24);
        }
    }

    private void drawSlotHighlightFront(DrawContext context) {
        if (this.focusedSlot != null && this.focusedSlot.canBeHighlighted()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_TEXTURE, this.focusedSlot.x - 4, this.focusedSlot.y - 4, 24, 24);
        }
    }

    protected void drawMouseoverTooltip(DrawContext context, int x, int y) {
        if (this.focusedSlot == null || !this.focusedSlot.hasStack()) {
            return;
        }
        ItemStack itemStack = this.focusedSlot.getStack();
        if (((ScreenHandler)this.handler).getCursorStack().isEmpty() || this.isItemTooltipSticky(itemStack)) {
            context.drawTooltip(this.textRenderer, this.getTooltipFromItem(itemStack), itemStack.getTooltipData(), x, y, itemStack.get(DataComponentTypes.TOOLTIP_STYLE));
        }
    }

    private boolean isItemTooltipSticky(ItemStack item) {
        return item.getTooltipData().map(TooltipComponent::of).map(TooltipComponent::isSticky).orElse(false);
    }

    protected List<Text> getTooltipFromItem(ItemStack stack) {
        return HandledScreen.getTooltipFromItem(this.client, stack);
    }

    private void drawItem(DrawContext context, ItemStack stack, int x, int y, @Nullable String amountText) {
        context.drawItem(stack, x, y);
        context.drawStackOverlay(this.textRenderer, stack, x, y - (this.touchDragStack.isEmpty() ? 0 : 8), amountText);
    }

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, Colors.DARK_GRAY, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, Colors.DARK_GRAY, false);
    }

    protected abstract void drawBackground(DrawContext var1, float var2, int var3, int var4);

    protected void drawSlot(DrawContext context, Slot slot) {
        Identifier identifier;
        int k;
        int i = slot.x;
        int j = slot.y;
        ItemStack itemStack = slot.getStack();
        boolean bl = false;
        boolean bl2 = slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && !this.touchIsRightClickDrag;
        ItemStack itemStack2 = ((ScreenHandler)this.handler).getCursorStack();
        String string = null;
        if (slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && this.touchIsRightClickDrag && !itemStack.isEmpty()) {
            itemStack = itemStack.copyWithCount(itemStack.getCount() / 2);
        } else if (this.cursorDragging && this.cursorDragSlots.contains(slot) && !itemStack2.isEmpty()) {
            if (this.cursorDragSlots.size() == 1) {
                return;
            }
            if (ScreenHandler.canInsertItemIntoSlot(slot, itemStack2, true) && ((ScreenHandler)this.handler).canInsertIntoSlot(slot)) {
                bl = true;
                k = Math.min(itemStack2.getMaxCount(), slot.getMaxItemCount(itemStack2));
                int l = slot.getStack().isEmpty() ? 0 : slot.getStack().getCount();
                int m = ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack2) + l;
                if (m > k) {
                    m = k;
                    string = Formatting.YELLOW.toString() + k;
                }
                itemStack = itemStack2.copyWithCount(m);
            } else {
                this.cursorDragSlots.remove(slot);
                this.calculateOffset();
            }
        }
        if (itemStack.isEmpty() && slot.isEnabled() && (identifier = slot.getBackgroundSprite()) != null) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, i, j, 16, 16);
            bl2 = true;
        }
        if (!bl2) {
            if (bl) {
                context.fill(i, j, i + 16, j + 16, -2130706433);
            }
            k = slot.x + slot.y * this.backgroundWidth;
            if (slot.disablesDynamicDisplay()) {
                context.drawItemWithoutEntity(itemStack, i, j, k);
            } else {
                context.drawItem(itemStack, i, j, k);
            }
            context.drawStackOverlay(this.textRenderer, itemStack, i, j, string);
        }
    }

    private void calculateOffset() {
        ItemStack itemStack = ((ScreenHandler)this.handler).getCursorStack();
        if (itemStack.isEmpty() || !this.cursorDragging) {
            return;
        }
        if (this.heldButtonType == 2) {
            this.draggedStackRemainder = itemStack.getMaxCount();
            return;
        }
        this.draggedStackRemainder = itemStack.getCount();
        for (Slot slot : this.cursorDragSlots) {
            ItemStack itemStack2 = slot.getStack();
            int i = itemStack2.isEmpty() ? 0 : itemStack2.getCount();
            int j = Math.min(itemStack.getMaxCount(), slot.getMaxItemCount(itemStack));
            int k = Math.min(ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack) + i, j);
            this.draggedStackRemainder -= k - i;
        }
    }

    @Nullable
    private Slot getSlotAt(double mouseX, double mouseY) {
        for (Slot slot : ((ScreenHandler)this.handler).slots) {
            if (!slot.isEnabled() || !this.isPointOverSlot(slot, mouseX, mouseY)) continue;
            return slot;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        boolean bl = this.client.options.pickItemKey.matchesMouse(button) && this.client.player.isInCreativeMode();
        Slot slot = this.getSlotAt(mouseX, mouseY);
        long l = Util.getMeasuringTimeMs();
        this.doubleClicking = this.lastClickedSlot == slot && l - this.lastButtonClickTime < 250L && this.lastClickedButton == button;
        this.cancelNextRelease = false;
        if (button == 0 || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT || bl) {
            int i = this.x;
            int j = this.y;
            boolean bl2 = this.isClickOutsideBounds(mouseX, mouseY, i, j, button);
            int k = -1;
            if (slot != null) {
                k = slot.id;
            }
            if (bl2) {
                k = -999;
            }
            if (this.client.options.getTouchscreen().getValue().booleanValue() && bl2 && ((ScreenHandler)this.handler).getCursorStack().isEmpty()) {
                this.close();
                return true;
            }
            if (k != -1) {
                if (this.client.options.getTouchscreen().getValue().booleanValue()) {
                    if (slot != null && slot.hasStack()) {
                        this.touchDragSlotStart = slot;
                        this.touchDragStack = ItemStack.EMPTY;
                        this.touchIsRightClickDrag = button == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
                    } else {
                        this.touchDragSlotStart = null;
                    }
                } else if (!this.cursorDragging) {
                    if (((ScreenHandler)this.handler).getCursorStack().isEmpty()) {
                        if (bl) {
                            this.onMouseClick(slot, k, button, SlotActionType.CLONE);
                        } else {
                            boolean bl3 = k != -999 && (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT));
                            SlotActionType slotActionType = SlotActionType.PICKUP;
                            if (bl3) {
                                this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                                slotActionType = SlotActionType.QUICK_MOVE;
                            } else if (k == -999) {
                                slotActionType = SlotActionType.THROW;
                            }
                            this.onMouseClick(slot, k, button, slotActionType);
                        }
                        this.cancelNextRelease = true;
                    } else {
                        this.cursorDragging = true;
                        this.heldButtonCode = button;
                        this.cursorDragSlots.clear();
                        if (button == 0) {
                            this.heldButtonType = 0;
                        } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                            this.heldButtonType = 1;
                        } else if (bl) {
                            this.heldButtonType = 2;
                        }
                    }
                }
            }
        } else {
            this.onMouseClick(button);
        }
        this.lastClickedSlot = slot;
        this.lastButtonClickTime = l;
        this.lastClickedButton = button;
        return true;
    }

    private void onMouseClick(int button) {
        if (this.focusedSlot != null && ((ScreenHandler)this.handler).getCursorStack().isEmpty()) {
            if (this.client.options.swapHandsKey.matchesMouse(button)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 40, SlotActionType.SWAP);
                return;
            }
            for (int i = 0; i < 9; ++i) {
                if (!this.client.options.hotbarKeys[i].matchesMouse(button)) continue;
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, i, SlotActionType.SWAP);
            }
        }
    }

    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        Slot slot = this.getSlotAt(mouseX, mouseY);
        ItemStack itemStack = ((ScreenHandler)this.handler).getCursorStack();
        if (this.touchDragSlotStart != null && this.client.options.getTouchscreen().getValue().booleanValue()) {
            if (button == 0 || button == 1) {
                if (this.touchDragStack.isEmpty()) {
                    if (slot != this.touchDragSlotStart && !this.touchDragSlotStart.getStack().isEmpty()) {
                        this.touchDragStack = this.touchDragSlotStart.getStack().copy();
                    }
                } else if (this.touchDragStack.getCount() > 1 && slot != null && ScreenHandler.canInsertItemIntoSlot(slot, this.touchDragStack, false)) {
                    long l = Util.getMeasuringTimeMs();
                    if (this.touchHoveredSlot == slot) {
                        if (l - this.touchDropTimer > 500L) {
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, SlotActionType.PICKUP);
                            this.onMouseClick(slot, slot.id, 1, SlotActionType.PICKUP);
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, SlotActionType.PICKUP);
                            this.touchDropTimer = l + 750L;
                            this.touchDragStack.decrement(1);
                        }
                    } else {
                        this.touchHoveredSlot = slot;
                        this.touchDropTimer = l;
                    }
                }
            }
        } else if (this.cursorDragging && slot != null && !itemStack.isEmpty() && (itemStack.getCount() > this.cursorDragSlots.size() || this.heldButtonType == 2) && ScreenHandler.canInsertItemIntoSlot(slot, itemStack, true) && slot.canInsert(itemStack) && ((ScreenHandler)this.handler).canInsertIntoSlot(slot)) {
            this.cursorDragSlots.add(slot);
            this.calculateOffset();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Slot slot = this.getSlotAt(mouseX, mouseY);
        int i = this.x;
        int j = this.y;
        boolean bl = this.isClickOutsideBounds(mouseX, mouseY, i, j, button);
        int k = -1;
        if (slot != null) {
            k = slot.id;
        }
        if (bl) {
            k = -999;
        }
        if (this.doubleClicking && slot != null && button == 0 && ((ScreenHandler)this.handler).canInsertIntoSlot(ItemStack.EMPTY, slot)) {
            if (HandledScreen.hasShiftDown()) {
                if (!this.quickMovingStack.isEmpty()) {
                    for (Slot slot2 : ((ScreenHandler)this.handler).slots) {
                        if (slot2 == null || !slot2.canTakeItems(this.client.player) || !slot2.hasStack() || slot2.inventory != slot.inventory || !ScreenHandler.canInsertItemIntoSlot(slot2, this.quickMovingStack, true)) continue;
                        this.onMouseClick(slot2, slot2.id, button, SlotActionType.QUICK_MOVE);
                    }
                }
            } else {
                this.onMouseClick(slot, k, button, SlotActionType.PICKUP_ALL);
            }
            this.doubleClicking = false;
            this.lastButtonClickTime = 0L;
        } else {
            if (this.cursorDragging && this.heldButtonCode != button) {
                this.cursorDragging = false;
                this.cursorDragSlots.clear();
                this.cancelNextRelease = true;
                return true;
            }
            if (this.cancelNextRelease) {
                this.cancelNextRelease = false;
                return true;
            }
            if (this.touchDragSlotStart != null && this.client.options.getTouchscreen().getValue().booleanValue()) {
                if (button == 0 || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    if (this.touchDragStack.isEmpty() && slot != this.touchDragSlotStart) {
                        this.touchDragStack = this.touchDragSlotStart.getStack();
                    }
                    boolean bl2 = ScreenHandler.canInsertItemIntoSlot(slot, this.touchDragStack, false);
                    if (k != -1 && !this.touchDragStack.isEmpty() && bl2) {
                        this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, button, SlotActionType.PICKUP);
                        this.onMouseClick(slot, k, 0, SlotActionType.PICKUP);
                        if (((ScreenHandler)this.handler).getCursorStack().isEmpty()) {
                            this.letGoTouchStack = null;
                        } else {
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, button, SlotActionType.PICKUP);
                            this.letGoTouchStack = new LetGoTouchStack(this.touchDragStack, new Vector2i((int)mouseX, (int)mouseY), new Vector2i(this.touchDragSlotStart.x + i, this.touchDragSlotStart.y + j), Util.getMeasuringTimeMs());
                        }
                    } else if (!this.touchDragStack.isEmpty()) {
                        this.letGoTouchStack = new LetGoTouchStack(this.touchDragStack, new Vector2i((int)mouseX, (int)mouseY), new Vector2i(this.touchDragSlotStart.x + i, this.touchDragSlotStart.y + j), Util.getMeasuringTimeMs());
                    }
                    this.endTouchDrag();
                }
            } else if (this.cursorDragging && !this.cursorDragSlots.isEmpty()) {
                this.onMouseClick(null, -999, ScreenHandler.packQuickCraftData(0, this.heldButtonType), SlotActionType.QUICK_CRAFT);
                for (Slot slot2 : this.cursorDragSlots) {
                    this.onMouseClick(slot2, slot2.id, ScreenHandler.packQuickCraftData(1, this.heldButtonType), SlotActionType.QUICK_CRAFT);
                }
                this.onMouseClick(null, -999, ScreenHandler.packQuickCraftData(2, this.heldButtonType), SlotActionType.QUICK_CRAFT);
            } else if (!((ScreenHandler)this.handler).getCursorStack().isEmpty()) {
                if (this.client.options.pickItemKey.matchesMouse(button)) {
                    this.onMouseClick(slot, k, button, SlotActionType.CLONE);
                } else {
                    boolean bl2;
                    boolean bl3 = bl2 = k != -999 && (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT));
                    if (bl2) {
                        this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                    }
                    this.onMouseClick(slot, k, button, bl2 ? SlotActionType.QUICK_MOVE : SlotActionType.PICKUP);
                }
            }
        }
        if (((ScreenHandler)this.handler).getCursorStack().isEmpty()) {
            this.lastButtonClickTime = 0L;
        }
        this.cursorDragging = false;
        return true;
    }

    public void endTouchDrag() {
        this.touchDragStack = ItemStack.EMPTY;
        this.touchDragSlotStart = null;
    }

    private boolean isPointOverSlot(Slot slot, double pointX, double pointY) {
        return this.isPointWithinBounds(slot.x, slot.y, 16, 16, pointX, pointY);
    }

    protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        int i = this.x;
        int j = this.y;
        return (pointX -= (double)i) >= (double)(x - 1) && pointX < (double)(x + width + 1) && (pointY -= (double)j) >= (double)(y - 1) && pointY < (double)(y + height + 1);
    }

    private void resetTooltipSubmenus(Slot slot) {
        if (slot.hasStack()) {
            for (TooltipSubmenuHandler tooltipSubmenuHandler : this.tooltipSubmenuHandlers) {
                if (!tooltipSubmenuHandler.isApplicableTo(slot)) continue;
                tooltipSubmenuHandler.reset(slot);
            }
        }
    }

    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        if (slot != null) {
            slotId = slot.id;
        }
        this.onMouseClick(slot, actionType);
        this.client.interactionManager.clickSlot(((ScreenHandler)this.handler).syncId, slotId, button, actionType, this.client.player);
    }

    void onMouseClick(@Nullable Slot slot, SlotActionType actionType) {
        if (slot != null && slot.hasStack()) {
            for (TooltipSubmenuHandler tooltipSubmenuHandler : this.tooltipSubmenuHandlers) {
                if (!tooltipSubmenuHandler.isApplicableTo(slot)) continue;
                tooltipSubmenuHandler.onMouseClick(slot, actionType);
            }
        }
    }

    protected void onSlotChangedState(int slotId, int handlerId, boolean newState) {
        this.client.interactionManager.slotChangedState(slotId, handlerId, newState);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        this.handleHotbarKeyPressed(keyCode, scanCode);
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            if (this.client.options.pickItemKey.matchesKey(keyCode, scanCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.CLONE);
            } else if (this.client.options.dropKey.matchesKey(keyCode, scanCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, HandledScreen.hasControlDown() ? 1 : 0, SlotActionType.THROW);
            }
        }
        return true;
    }

    protected boolean handleHotbarKeyPressed(int keyCode, int scanCode) {
        if (((ScreenHandler)this.handler).getCursorStack().isEmpty() && this.focusedSlot != null) {
            if (this.client.options.swapHandsKey.matchesKey(keyCode, scanCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 40, SlotActionType.SWAP);
                return true;
            }
            for (int i = 0; i < 9; ++i) {
                if (!this.client.options.hotbarKeys[i].matchesKey(keyCode, scanCode)) continue;
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, i, SlotActionType.SWAP);
                return true;
            }
        }
        return false;
    }

    @Override
    public void removed() {
        if (this.client.player == null) {
            return;
        }
        ((ScreenHandler)this.handler).onClosed(this.client.player);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public final void tick() {
        super.tick();
        if (!this.client.player.isAlive() || this.client.player.isRemoved()) {
            this.client.player.closeHandledScreen();
        } else {
            this.handledScreenTick();
        }
    }

    protected void handledScreenTick() {
    }

    @Override
    public T getScreenHandler() {
        return this.handler;
    }

    @Override
    public void close() {
        this.client.player.closeHandledScreen();
        if (this.focusedSlot != null) {
            this.resetTooltipSubmenus(this.focusedSlot);
        }
        super.close();
    }

    @Environment(value=EnvType.CLIENT)
    static final class LetGoTouchStack
    extends Record {
        final ItemStack item;
        final Vector2i start;
        final Vector2i end;
        final long time;

        LetGoTouchStack(ItemStack itemStack, Vector2i vector2i, Vector2i vector2i2, long l) {
            this.item = itemStack;
            this.start = vector2i;
            this.end = vector2i2;
            this.time = l;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{LetGoTouchStack.class, "item;start;end;time", "item", "start", "end", "time"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LetGoTouchStack.class, "item;start;end;time", "item", "start", "end", "time"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LetGoTouchStack.class, "item;start;end;time", "item", "start", "end", "time"}, this, object);
        }

        public ItemStack item() {
            return this.item;
        }

        public Vector2i start() {
            return this.start;
        }

        public Vector2i end() {
            return this.end;
        }

        public long time() {
            return this.time;
        }
    }
}

