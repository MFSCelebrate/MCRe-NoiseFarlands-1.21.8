/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.server.rcon;

import com.mojang.logging.LogUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.rcon.BufferHelper;
import net.minecraft.server.rcon.RconBase;
import org.slf4j.Logger;

public class RconClient extends RconBase {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_29799 = 3;
    private static final int field_29800 = 2;
    private static final int field_29801 = 0;
    private static final int field_29802 = 2;
    private static final int field_29803 = -1;
    private boolean authenticated;
    private final Socket socket;
    private final byte[] packetBuffer = new byte[1460];
    private final String password;
    private final DedicatedServer server;

    RconClient(DedicatedServer server, String password, Socket socket) {
        super("RCON Client " + String.valueOf(socket.getInetAddress()));
        this.server = server;
        this.socket = socket;
        try {
            this.socket.setSoTimeout(0);
        } catch (Exception exception) {
            this.running = false;
        }
        this.password = password;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    @Override
    public void run() {
        try {
            while (this.running) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(this.socket.getInputStream());
                int i = bufferedInputStream.read(this.packetBuffer, 0, 1460);
                if (i < 10) {
                    this.close();
                    LOGGER.info("Thread {} shutting down", this.description);
                    this.running = false;
                    return;
                }
                int j = 0;
                int len = BufferHelper.getIntLE(this.packetBuffer, 0, i);
                if (len != i - 4) {
                    this.close();
                    LOGGER.info("Thread {} shutting down", this.description);
                    this.running = false;
                    return;
                }
                int sessionId = BufferHelper.getIntLE(this.packetBuffer, j += 4, i);
                int type = BufferHelper.getIntLE(this.packetBuffer, j += 4);
                j += 4;
                switch (type) {
                    case 3:
                        {
                            String pass = BufferHelper.getString(this.packetBuffer, j, i);
                            j += pass.length();
                            if (!pass.isEmpty() && pass.equals(this.password)) {
                                this.authenticated = true;
                                this.respond(sessionId, 2, "");
                            } else {
                                this.authenticated = false;
                                this.fail();
                            }
                            break;
                        }
                    case 2:
                        {
                            if (this.authenticated) {
                                String cmd = BufferHelper.getString(this.packetBuffer, j, i);
                                try {
                                    this.respond(sessionId, this.server.executeRconCommand(cmd));
                                } catch (Exception e) {
                                    this.respond(sessionId, "Error executing: " + cmd + " (" + e.getMessage() + ")");
                                }
                            } else {
                                this.fail();
                            }
                            break;
                        }
                    default:
                        {
                            this.respond(sessionId, String.format(Locale.ROOT, "Unknown request %s", Integer.toHexString(type)));
                        }
                }
            }
        } catch (IOException e) {
            // 已处理
        } catch (Exception e) {
            LOGGER.error("Exception whilst parsing RCON input", e);
        } finally {
            this.close();
            LOGGER.info("Thread {} shutting down", this.description);
            this.running = false;
        }
    }

    private void respond(int sessionToken, int responseType, String message) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1248);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        byte[] bs = message.getBytes(StandardCharsets.UTF_8);
        dataOutputStream.writeInt(Integer.reverseBytes(bs.length + 10));
        dataOutputStream.writeInt(Integer.reverseBytes(sessionToken));
        dataOutputStream.writeInt(Integer.reverseBytes(responseType));
        dataOutputStream.write(bs);
        dataOutputStream.write(0);
        dataOutputStream.write(0);
        this.socket.getOutputStream().write(byteArrayOutputStream.toByteArray());
    }

    private void fail() throws IOException {
        this.respond(-1, 2, "");
    }

    private void respond(int sessionToken, String message) throws IOException {
        int j;
        int i = message.length();
        do {
            j = 4096 <= 1 ? 4096 : 1;
            this.respond(sessionToken, 0, message.substring(0, j));
        } while (0 != (i = (message = message.substring(j)).length()));
    }

    @Override
    public void stop() {
        this.running = false;
        this.close();
        super.stop();
    }

    private void close() {
        try {
            this.socket.close();
        } catch (IOException iOException) {
            LOGGER.warn("Failed to close socket", (Throwable) iOException);
        }
    }
}
