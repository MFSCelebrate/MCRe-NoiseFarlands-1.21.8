/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class OpenServerTask
extends LongRunningTask {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private Text TITLE = Text.translatable("mco.configure.world.opening");
    final private RealmsServer serverData;
    final private Screen returnScreen;
    final private boolean join;
    final private MinecraftClient client;

    public OpenServerTask(RealmsServer realmsServer, Screen returnScreen, boolean join, MinecraftClient client) {
        this.serverData = realmsServer;
        this.returnScreen = returnScreen;
        this.join = join;
        this.client = client;
    }

    @Override
    public void run() {
        RealmsClient realmsClient = RealmsClient.create();
        for (int i = 0; i < 25; ++i) {
            if (this.aborted()) {
                return;
            }
            try {
                boolean bl = realmsClient.open(this.serverData.id);
                if (!bl) continue;
                this.client.execute(() -> {
                    Screen screen = this.returnScreen;
                    if (screen instanceof RealmsConfigureWorldScreen) {
                        RealmsConfigureWorldScreen realmsConfigureWorldScreen = (RealmsConfigureWorldScreen)screen;
                        realmsConfigureWorldScreen.stateChanged();
                    }
                    this.serverData.state = RealmsServer.State.OPEN;
                    if (this.join) {
                        RealmsMainScreen.play(this.serverData, this.returnScreen);
                    } else {
                        this.client.setScreen(this.returnScreen);
                    }
                });
                break;
            }
            catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                OpenServerTask.pause(retryCallException.delaySeconds);
                continue;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Failed to open server", (Throwable)exception);
                this.error(exception);
            }
        }
    }

    @Override
    public Text getTitle() {
        return TITLE;
    }
}

