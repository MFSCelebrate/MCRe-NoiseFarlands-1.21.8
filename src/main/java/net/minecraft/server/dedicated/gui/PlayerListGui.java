/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.dedicated.gui;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;

public class PlayerListGui
extends JList<String> {
    final private MinecraftServer server;
    private int tick;

    public PlayerListGui(MinecraftServer server) {
        this.server = server;
        server.addServerGuiTickable(this::tick);
    }

    public void tick() {
        if (this.tick++ % 20 == 0) {
            Vector<String> vector = new Vector<String>();
            for (int i = 0; i < this.server.net_minecraft_server_PlayerManager_getPlayerManager().getPlayerList().size(); ++i) {
                vector.add(this.server.net_minecraft_server_PlayerManager_getPlayerManager().getPlayerList().get(i).getGameProfile().getName());
            }
            this.setListData(vector);
        }
    }
}

