/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.mojang.logging.LogUtils
 *  org.apache.commons.io.IOUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.chase;

import com.google.common.base.Charsets;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.net.Socket;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ChaseCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ChaseClient {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int CONNECTION_RETRY_INTERVAL = 5;
    final private String ip;
    final private int port;
    final private MinecraftServer minecraftServer;
    private volatile boolean running;
    @Nullable
    private Socket socket;
    @Nullable
    private Thread thread;

    public ChaseClient(String ip, int port, MinecraftServer minecraftServer) {
        this.ip = ip;
        this.port = port;
        this.minecraftServer = minecraftServer;
    }

    public void start() {
        if (this.thread != null && this.thread.isAlive()) {
            LOGGER.warn("Remote control client was asked to start, but it is already running. Will ignore.");
        }
        this.running = true;
        this.thread = new Thread(this::run, "chase-client");
        this.thread.setDaemon(true);
        this.thread.start();
    }

    public void stop() {
        this.running = false;
        IOUtils.closeQuietly((Socket)this.socket);
        this.socket = null;
        this.thread = null;
    }

    /*
     * Unable to fully structure code
     */
    public void run() {
        string = this.ip + ":" + this.port;
        while (this.running) {
            try {
                ChaseClient.LOGGER.info("Connecting to remote control server {}", (Object)string);
                this.socket = new Socket(this.ip, this.port);
                ChaseClient.LOGGER.info("Connected to remote control server! Will continuously execute the command broadcasted by that server.");
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), Charsets.US_ASCII));
                    try {
                        while (this.running) {
                            string2 = bufferedReader.readLine();
                            if (string2 == null) {
                                ChaseClient.LOGGER.warn("Lost connection to remote control server {}. Will retry in {}s.", (Object)string, (Object)5);
                                break;
                            }
                            this.parseMessage(string2);
                        }
                    }
                    catch (Throwable var3_7) {
                        try {
                            bufferedReader.close();
                        }
                        catch (Throwable var4_8) {
                            var3_7.addSuppressed(var4_8);
                        }
                        throw var3_7;
                    }
lbl24:
                    // 2 sources

                    bufferedReader.close();
                }
                catch (IOException iOException) {
                    ChaseClient.LOGGER.warn("Lost connection to remote control server {}. Will retry in {}s.", (Object)string, (Object)5);
                }
            }
            catch (IOException iOException) {
                ChaseClient.LOGGER.warn("Failed to connect to remote control server {}. Will retry in {}s.", (Object)string, (Object)5);
            }
            if (!this.running) continue;
            try {
                Thread.sleep(5000L);
            }
            catch (InterruptedException var2_5) {}
        }
    }

    private void parseMessage(String message) {
        try (Scanner scanner = new Scanner(new StringReader(message));){
            scanner.useLocale(Locale.ROOT);
            String string = scanner.next();
            if ("t".equals(string)) {
                this.executeTeleportCommand(scanner);
            } else {
                LOGGER.warn("Unknown message type '{}'", (Object)string);
            }
        }
        catch (NoSuchElementException noSuchElementException) {
            LOGGER.warn("Could not parse message '{}', ignoring", (Object)message);
        }
    }

    private void executeTeleportCommand(Scanner scanner) {
        this.getTeleportPos(scanner).ifPresent(pos -> this.executeCommand(String.format(Locale.ROOT, "execute in %s run tp @s %.3f %.3f %.3f %.3f %.3f", pos.dimension.getValue(), pos.pos.x, pos.pos.y, pos.pos.z, Float.valueOf(pos.rot.y), Float.valueOf(pos.rot.x))));
    }

    private Optional<TeleportPos> getTeleportPos(Scanner scanner) {
        RegistryKey registryKey = (RegistryKey)ChaseCommand.DIMENSIONS.get((Object)scanner.next());
        if (registryKey == null) {
            return Optional.empty();
        }
        float f = scanner.nextFloat();
        float g = scanner.nextFloat();
        float h = scanner.nextFloat();
        float i = scanner.nextFloat();
        float j = scanner.nextFloat();
        return Optional.of(new TeleportPos(registryKey, new Vec3d(f, g, h), new Vec2f(j, i)));
    }

    private void executeCommand(String command) {
        this.minecraftServer.execute(() -> {
            List<ServerPlayerEntity> list = this.minecraftServer.net_minecraft_server_PlayerManager_getPlayerManager().getPlayerList();
            if (list.isEmpty()) {
                return;
            }
            ServerPlayerEntity serverPlayerEntity = list.get(0);
            ServerWorld serverWorld = this.minecraftServer.getOverworld();
            ServerCommandSource serverCommandSource = new ServerCommandSource(serverPlayerEntity.getCommandOutput(), Vec3d.of(serverWorld.getSpawnPos()), Vec2f.ZERO, serverWorld, 4, "", ScreenTexts.EMPTY, this.minecraftServer, serverPlayerEntity);
            CommandManager commandManager = this.minecraftServer.getCommandManager();
            commandManager.executeWithPrefix(serverCommandSource, command);
        });
    }

    static final class TeleportPos
    extends Record {
        final RegistryKey<World> dimension;
        final Vec3d pos;
        final Vec2f rot;

        TeleportPos(RegistryKey<World> dimension, Vec3d pos, Vec2f rot) {
            this.dimension = dimension;
            this.pos = pos;
            this.rot = rot;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{TeleportPos.class, "level;pos;rot", "dimension", "pos", "rot"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TeleportPos.class, "level;pos;rot", "dimension", "pos", "rot"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TeleportPos.class, "level;pos;rot", "dimension", "pos", "rot"}, this, object);
        }

        public RegistryKey<World> dimension() {
            return this.dimension;
        }

        public Vec3d pos() {
            return this.pos;
        }

        public Vec2f rot() {
            return this.rot;
        }
    }
}

