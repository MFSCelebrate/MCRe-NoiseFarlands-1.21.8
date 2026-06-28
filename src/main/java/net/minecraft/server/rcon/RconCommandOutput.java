/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.rcon;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class RconCommandOutput
implements CommandOutput {
    final static private String RCON_NAME = "Rcon";
    final static private Text RCON_NAME_TEXT = Text.literal("Rcon");
    final private StringBuffer buffer = new StringBuffer();
    final private MinecraftServer server;

    public RconCommandOutput(MinecraftServer server) {
        this.server = server;
    }

    public void clear() {
        this.buffer.setLength(0);
    }

    public String asString() {
        return this.buffer.toString();
    }

    public ServerCommandSource createRconCommandSource() {
        ServerWorld serverWorld = this.server.getOverworld();
        return new ServerCommandSource(this, Vec3d.of(serverWorld.getSpawnPos()), Vec2f.ZERO, serverWorld, 4, RCON_NAME, RCON_NAME_TEXT, this.server, null);
    }

    @Override
    public void sendMessage(Text message) {
        this.buffer.append(message.getString());
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return true;
    }

    @Override
    public boolean shouldTrackOutput() {
        return true;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return this.server.shouldBroadcastRconToOps();
    }
}

