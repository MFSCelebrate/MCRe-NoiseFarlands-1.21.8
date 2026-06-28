/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.glfw.GLFW
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LoadingWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.realms.RepeatedNarrator;
import net.minecraft.client.realms.exception.RealmsDefaultUncaughtExceptionHandler;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsLongRunningMcoTaskScreen
extends RealmsScreen {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private RepeatedNarrator NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));
    final private List<LongRunningTask> tasks;
    final private Screen parent;
    final protected DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical();
    private volatile Text title;
    @Nullable
    private LoadingWidget loading;

    public RealmsLongRunningMcoTaskScreen(Screen parent, LongRunningTask ... tasks) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
        this.tasks = List.of(tasks);
        if (this.tasks.isEmpty()) {
            throw new IllegalArgumentException("No tasks added");
        }
        this.title = this.tasks.get(0).getTitle();
        Runnable runnable = () -> {
            for (LongRunningTask longRunningTask : tasks) {
                this.setTitle(longRunningTask.getTitle());
                if (longRunningTask.aborted()) break;
                longRunningTask.run();
                if (!longRunningTask.aborted()) continue;
                return;
            }
        };
        Thread thread = new Thread(runnable, "Realms-long-running-task");
        thread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
        thread.start();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.loading != null) {
            NARRATOR.narrate(this.client.getNarratorManager(), this.loading.getMessage());
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onCancel();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void init() {
        this.layout.getMainPositioner().alignHorizontalCenter();
        this.layout.add(RealmsLongRunningMcoTaskScreen.createRealmsLogoIconWidget());
        this.loading = new LoadingWidget(this.textRenderer, this.title);
        this.layout.add(this.loading, positioner -> positioner.net_minecraft_client_gui_widget_Positioner_marginTop(10).net_minecraft_client_gui_widget_Positioner_marginBottom(30));
        this.layout.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.onCancel()).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
    }

    protected void onCancel() {
        for (LongRunningTask longRunningTask : this.tasks) {
            longRunningTask.abortTask();
        }
        this.client.setScreen(this.parent);
    }

    public void setTitle(Text title) {
        if (this.loading != null) {
            this.loading.setMessage(title);
        }
        this.title = title;
    }
}

