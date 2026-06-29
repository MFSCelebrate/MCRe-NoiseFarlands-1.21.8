/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.screen.ingame;

import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CyclingSlotIcon;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class SmithingScreen
extends ForgingScreen<SmithingScreenHandler> {
    final static private Identifier ERROR_TEXTURE = Identifier.ofVanilla("container/smithing/error");
    final static private Identifier EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM_TEXTURE = Identifier.ofVanilla("container/slot/smithing_template_armor_trim");
    final static private Identifier EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE_TEXTURE = Identifier.ofVanilla("container/slot/smithing_template_netherite_upgrade");
    final static private Text MISSING_TEMPLATE_TOOLTIP = Text.translatable("container.upgrade.missing_template_tooltip");
    final static private Text ERROR_TOOLTIP = Text.translatable("container.upgrade.error_tooltip");
    final static private List<Identifier> EMPTY_SLOT_TEXTURES = List.of(EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM_TEXTURE, EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE_TEXTURE);
    final static private int field_42057 = 44;
    final static private int field_42058 = 15;
    final static private int field_42059 = 28;
    final static private int field_42060 = 21;
    final static private int field_42061 = 65;
    final static private int field_42062 = 46;
    final static private int field_42063 = 115;
    final static private int field_42068 = 210;
    final static private int field_42047 = 25;
    final static private Vector3f ARMOR_STAND_TRANSLATION = new Vector3f(0.0f, 1.0f, 0.0f);
    final static private Quaternionf ARMOR_STAND_ROTATION = new Quaternionf().rotationXYZ(0.43633232f, 0.0f, (float)Math.PI);
    final static private int field_42049 = 25;
    final static private int field_59946 = 121;
    final static private int field_59947 = 20;
    final static private int field_59948 = 161;
    final static private int field_59949 = 80;
    final private CyclingSlotIcon templateSlotIcon = new CyclingSlotIcon(0);
    final private CyclingSlotIcon baseSlotIcon = new CyclingSlotIcon(1);
    final private CyclingSlotIcon additionsSlotIcon = new CyclingSlotIcon(2);
    @Nullable
    private ArmorStandEntity armorStand;

    public SmithingScreen(SmithingScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title, Identifier.ofVanilla("textures/gui/container/smithing.png"));
        this.titleX = 44;
        this.titleY = 15;
    }

    @Override
    protected void setup() {
        this.armorStand = new ArmorStandEntity(this.client.world, 0.0, 0.0, 0.0);
        this.armorStand.setHideBasePlate(true);
        this.armorStand.setShowArms(true);
        this.armorStand.bodyYaw = 210.0f;
        this.armorStand.setPitch(25.0f);
        this.armorStand.headYaw = this.armorStand.getYaw();
        this.armorStand.lastHeadYaw = this.armorStand.getYaw();
        this.equipArmorStand(((SmithingScreenHandler)this.handler).getSlot(3).getStack());
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        Optional<SmithingTemplateItem> optional = this.getSmithingTemplate();
        this.templateSlotIcon.updateTexture(EMPTY_SLOT_TEXTURES);
        this.baseSlotIcon.updateTexture(optional.map(SmithingTemplateItem::getEmptyBaseSlotTextures).orElse(List.of()));
        this.additionsSlotIcon.updateTexture(optional.map(SmithingTemplateItem::getEmptyAdditionsSlotTextures).orElse(List.of()));
    }

    private Optional<SmithingTemplateItem> getSmithingTemplate() {
        Item item;
        ItemStack itemStack = ((SmithingScreenHandler)this.handler).getSlot(0).getStack();
        if (!itemStack.isEmpty() && (item = itemStack.getItem()) instanceof SmithingTemplateItem) {
            SmithingTemplateItem smithingTemplateItem = (SmithingTemplateItem)item;
            return Optional.of(smithingTemplateItem);
        }
        return Optional.empty();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.renderSlotTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        super.drawBackground(context, deltaTicks, mouseX, mouseY);
        this.templateSlotIcon.render(this.handler, context, deltaTicks, this.x, this.y);
        this.baseSlotIcon.render(this.handler, context, deltaTicks, this.x, this.y);
        this.additionsSlotIcon.render(this.handler, context, deltaTicks, this.x, this.y);
        int i = this.x + 121;
        int j = this.y + 20;
        int k = this.x + 161;
        int l = this.y + 80;
        InventoryScreen.drawEntity(context, i, j, k, l, 25.0f, ARMOR_STAND_TRANSLATION, ARMOR_STAND_ROTATION, null, (LivingEntity)this.armorStand);
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        if (slotId == 3) {
            this.equipArmorStand(stack);
        }
    }

    private void equipArmorStand(ItemStack stack) {
        if (this.armorStand == null) {
            return;
        }
        for (EquipmentSlot equipmentSlot : EquipmentSlot.VALUES) {
            this.armorStand.equipStack(equipmentSlot, ItemStack.EMPTY);
        }
        if (!stack.isEmpty()) {
            EquipmentSlot equipmentSlot;
            EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
            equipmentSlot = equippableComponent != null ? equippableComponent.slot() : EquipmentSlot.OFFHAND;
            this.armorStand.equipStack(equipmentSlot, stack.copy());
        }
    }

    @Override
    protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
        if (this.hasInvalidRecipe()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ERROR_TEXTURE, x + 65, y + 46, 28, 21);
        }
    }

    private void renderSlotTooltip(DrawContext context, int mouseX, int mouseY) {
        Optional<Text> optional = Optional.empty();
        if (this.hasInvalidRecipe() && this.isPointWithinBounds(65, 46, 28, 21, mouseX, mouseY)) {
            optional = Optional.of(ERROR_TOOLTIP);
        }
        if (this.focusedSlot != null) {
            ItemStack itemStack = ((SmithingScreenHandler)this.handler).getSlot(0).getStack();
            ItemStack itemStack2 = this.focusedSlot.getStack();
            if (itemStack.isEmpty()) {
                if (this.focusedSlot.id == 0) {
                    optional = Optional.of(MISSING_TEMPLATE_TOOLTIP);
                }
            } else {
                Item item = itemStack.getItem();
                if (item instanceof SmithingTemplateItem) {
                    SmithingTemplateItem smithingTemplateItem = (SmithingTemplateItem)item;
                    if (itemStack2.isEmpty()) {
                        if (this.focusedSlot.id == 1) {
                            optional = Optional.of(smithingTemplateItem.getBaseSlotDescription());
                        } else if (this.focusedSlot.id == 2) {
                            optional = Optional.of(smithingTemplateItem.getAdditionsSlotDescription());
                        }
                    }
                }
            }
        }
        optional.ifPresent(text -> context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines((StringVisitable)text, 115), mouseX, mouseY));
    }

    private boolean hasInvalidRecipe() {
        return ((SmithingScreenHandler)this.handler).hasInvalidRecipe();
    }
}

