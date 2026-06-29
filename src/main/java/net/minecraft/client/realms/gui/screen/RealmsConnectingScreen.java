/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.realms.ServiceQuality;
import net.minecraft.client.realms.dto.RealmsServerAddress;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RealmsConnectingScreen
extends RealmsLongRunningMcoTaskScreen {
    final private LongRunningTask connectTask;
    final private RealmsServerAddress serverAddress;
    final private DirectionalLayoutWidget footerLayout = DirectionalLayoutWidget.vertical();

    public RealmsConnectingScreen(Screen parent, RealmsServerAddress serverAddress, LongRunningTask connectTask) {
        super(parent, connectTask);
        this.connectTask = connectTask;
        this.serverAddress = serverAddress;
    }

    @Override
    public void init() {
        super.init();
        if (this.serverAddress.regionData() == null || this.serverAddress.regionData().region() == null) {
            return;
        }
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(10);
        TextWidget textWidget = new TextWidget(Text.translatable("mco.connect.region", Text.translatable(this.serverAddress.regionData().region().translationKey)), this.textRenderer);
        directionalLayoutWidget.add(textWidget);
        Identifier identifier = this.serverAddress.regionData().serviceQuality() != null ? this.serverAddress.regionData().serviceQuality().getIcon() : ServiceQuality.UNKNOWN.getIcon();
        directionalLayoutWidget.add(IconWidget.create(10, 8, identifier), Positioner::alignTop);
        this.footerLayout.add(directionalLayoutWidget, positioner -> positioner.net_minecraft_client_gui_widget_Positioner_marginTop(40));
        this.footerLayout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        super.refreshWidgetPositions();
        int i = this.layout.getY() + this.layout.getHeight();
        ScreenRect screenRect = new ScreenRect(0, i, this.width, this.height - i);
        this.footerLayout.refreshPositions();
        SimplePositioningWidget.setPos(this.footerLayout, screenRect, 0.5f, 0.0f);
    }

    @Override
    public void tick() {
        super.tick();
        this.connectTask.tick();
    }

    @Override
    protected void onCancel() {
        this.connectTask.abortTask();
        super.onCancel();
    }
}

