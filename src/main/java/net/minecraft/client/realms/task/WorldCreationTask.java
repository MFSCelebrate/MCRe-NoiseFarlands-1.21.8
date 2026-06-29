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
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldCreationTask
extends LongRunningTask {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private Text TITLE = Text.translatable("mco.create.world.wait");
    final private String name;
    final private String motd;
    final private long worldId;

    public WorldCreationTask(long worldId, String name, String motd) {
        this.worldId = worldId;
        this.name = name;
        this.motd = motd;
    }

    @Override
    public void run() {
        RealmsClient realmsClient = RealmsClient.create();
        try {
            realmsClient.initializeWorld(this.worldId, this.name, this.motd);
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't create world", (Throwable)realmsServiceException);
            this.error(realmsServiceException);
        }
        catch (Exception exception) {
            LOGGER.error("Could not create world", (Throwable)exception);
            this.error(exception);
        }
    }

    @Override
    public Text getTitle() {
        return TITLE;
    }
}

