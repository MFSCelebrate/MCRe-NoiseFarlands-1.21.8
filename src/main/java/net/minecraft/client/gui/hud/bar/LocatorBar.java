/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.bar;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.resource.waypoint.WaypointStyleAsset;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.Waypoint;

@Environment(value=EnvType.CLIENT)
public class LocatorBar
implements Bar {
    final static private Identifier BACKGROUND = Identifier.ofVanilla("hud/locator_bar_background");
    final static private Identifier ARROW_UP = Identifier.ofVanilla("hud/locator_bar_arrow_up");
    final static private Identifier ARROW_DOWN = Identifier.ofVanilla("hud/locator_bar_arrow_down");
    final static private int field_59852 = 9;
    final static private int field_59854 = 60;
    final static private int field_59855 = 7;
    final static private int field_59856 = 5;
    final static private int field_60309 = 1;
    final static private int field_60453 = 1;
    final private MinecraftClient client;

    public LocatorBar(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void renderBar(DrawContext context, RenderTickCounter tickCounter) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.getCenterX(this.client.getWindow()), this.getCenterY(this.client.getWindow()), 182, 5);
    }

    @Override
    public void renderAddons(DrawContext context, RenderTickCounter tickCounter) {
        int i = this.getCenterY(this.client.getWindow());
        World world = this.client.cameraEntity.net_minecraft_world_World_getWorld();
        this.client.player.networkHandler.getWaypointHandler().forEachWaypoint(this.client.cameraEntity, waypoint -> {
            if (waypoint.getSource().left().map(uuid -> uuid.equals(this.client.cameraEntity.getUuid())).orElse(false).booleanValue()) {
                return;
            }
            double d = waypoint.getRelativeYaw(world, this.client.gameRenderer.getCamera());
            if (d <= -61.0 || d > 60.0) {
                return;
            }
            int j = MathHelper.ceil((float)(context.getScaledWindowWidth() - 9) / 2.0f);
            Waypoint.Config config = waypoint.getConfig();
            WaypointStyleAsset waypointStyleAsset = this.client.getWaypointStyleAssetManager().get(config.style);
            float f = MathHelper.sqrt((float)waypoint.squaredDistanceTo(this.client.cameraEntity));
            Identifier identifier = waypointStyleAsset.getSpriteForDistance(f);
            int k = config.color.orElseGet(() -> (Integer)waypoint.getSource().map(uuid -> ColorHelper.withBrightness(ColorHelper.withAlpha(255, uuid.hashCode()), 0.9f), name -> ColorHelper.withBrightness(ColorHelper.withAlpha(255, name.hashCode()), 0.9f)));
            int l = (int)(d * 173.0 / 2.0 / 60.0);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, j + l, i - 2, 9, 9, k);
            TrackedWaypoint.Pitch pitch = waypoint.getPitch(world, this.client.gameRenderer);
            if (pitch != TrackedWaypoint.Pitch.NONE) {
                Identifier identifier2;
                int m;
                if (pitch == TrackedWaypoint.Pitch.DOWN) {
                    m = 6;
                    identifier2 = ARROW_DOWN;
                } else {
                    m = -6;
                    identifier2 = ARROW_UP;
                }
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier2, j + l + 1, i + m, 7, 5);
            }
        });
    }
}

