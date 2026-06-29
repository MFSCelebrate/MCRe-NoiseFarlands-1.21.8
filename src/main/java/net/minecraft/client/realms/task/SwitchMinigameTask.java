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
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SwitchMinigameTask
extends LongRunningTask {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private Text TITLE = Text.translatable("mco.minigame.world.starting.screen.title");
    final private long worldId;
    final private WorldTemplate worldTemplate;
    final private RealmsConfigureWorldScreen field_60863;

    public SwitchMinigameTask(long worldId, WorldTemplate worldTemplate, RealmsConfigureWorldScreen lastScreen) {
        this.worldId = worldId;
        this.worldTemplate = worldTemplate;
        this.field_60863 = lastScreen;
    }

    @Override
    public void run() {
        RealmsClient realmsClient = RealmsClient.create();
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                if (!realmsClient.putIntoMinigameMode(this.worldId, this.worldTemplate.id).booleanValue()) continue;
                SwitchMinigameTask.setScreen(this.field_60863);
                break;
            }
            catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                SwitchMinigameTask.pause(retryCallException.delaySeconds);
                continue;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't start mini game!");
                this.error(exception);
            }
        }
    }

    @Override
    public Text getTitle() {
        return TITLE;
    }
}

