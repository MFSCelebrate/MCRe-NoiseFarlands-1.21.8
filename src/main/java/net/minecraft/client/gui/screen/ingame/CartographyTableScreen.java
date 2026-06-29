/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CartographyTableScreen
extends HandledScreen<CartographyTableScreenHandler> {
    final static private Identifier ERROR_TEXTURE = Identifier.ofVanilla("container/cartography_table/error");
    final static private Identifier SCALED_MAP_TEXTURE = Identifier.ofVanilla("container/cartography_table/scaled_map");
    final static private Identifier DUPLICATED_MAP_TEXTURE = Identifier.ofVanilla("container/cartography_table/duplicated_map");
    final static private Identifier MAP_TEXTURE = Identifier.ofVanilla("container/cartography_table/map");
    final static private Identifier LOCKED_TEXTURE = Identifier.ofVanilla("container/cartography_table/locked");
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/cartography_table.png");
    final private MapRenderState mapRenderState = new MapRenderState();

    public CartographyTableScreen(CartographyTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.titleY -= 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        MapState mapState;
        int i = this.x;
        int j = this.y;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);
        ItemStack itemStack = ((CartographyTableScreenHandler)this.handler).getSlot(1).getStack();
        boolean bl = itemStack.isOf(Items.MAP);
        boolean bl2 = itemStack.isOf(Items.PAPER);
        boolean bl3 = itemStack.isOf(Items.GLASS_PANE);
        ItemStack itemStack2 = ((CartographyTableScreenHandler)this.handler).getSlot(0).getStack();
        MapIdComponent mapIdComponent = itemStack2.get(DataComponentTypes.MAP_ID);
        boolean bl4 = false;
        if (mapIdComponent != null) {
            mapState = FilledMapItem.getMapState(mapIdComponent, (World)this.client.world);
            if (mapState != null) {
                if (mapState.locked) {
                    bl4 = true;
                    if (bl2 || bl3) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ERROR_TEXTURE, i + 35, j + 31, 28, 21);
                    }
                }
                if (bl2 && mapState.scale >= 4) {
                    bl4 = true;
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ERROR_TEXTURE, i + 35, j + 31, 28, 21);
                }
            }
        } else {
            mapState = null;
        }
        this.drawMap(context, mapIdComponent, mapState, bl, bl2, bl3, bl4);
    }

    private void drawMap(DrawContext context, @Nullable MapIdComponent mapId, @Nullable MapState mapState, boolean cloneMode, boolean expandMode, boolean lockMode, boolean cannotExpand) {
        int i = this.x;
        int j = this.y;
        if (expandMode && !cannotExpand) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCALED_MAP_TEXTURE, i + 67, j + 13, 66, 66);
            this.drawMap(context, mapId, mapState, i + 85, j + 31, 0.226f);
        } else if (cloneMode) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, DUPLICATED_MAP_TEXTURE, i + 67 + 16, j + 13, 50, 66);
            this.drawMap(context, mapId, mapState, i + 86, j + 16, 0.34f);
            context.createNewRootLayer();
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, DUPLICATED_MAP_TEXTURE, i + 67, j + 13 + 16, 50, 66);
            this.drawMap(context, mapId, mapState, i + 70, j + 32, 0.34f);
        } else if (lockMode) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MAP_TEXTURE, i + 67, j + 13, 66, 66);
            this.drawMap(context, mapId, mapState, i + 71, j + 17, 0.45f);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, LOCKED_TEXTURE, i + 118, j + 60, 10, 14);
        } else {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MAP_TEXTURE, i + 67, j + 13, 66, 66);
            this.drawMap(context, mapId, mapState, i + 71, j + 17, 0.45f);
        }
    }

    private void drawMap(DrawContext context, @Nullable MapIdComponent mapId, @Nullable MapState mapState, int x, int y, float scale) {
        if (mapId != null && mapState != null) {
            context.getMatrices().pushMatrix();
            context.getMatrices().translate((float)x, (float)y);
            context.getMatrices().scale(scale, scale);
            this.client.getMapRenderer().update(mapId, mapState, this.mapRenderState);
            context.drawMap(this.mapRenderState);
            context.getMatrices().popMatrix();
        }
    }
}

