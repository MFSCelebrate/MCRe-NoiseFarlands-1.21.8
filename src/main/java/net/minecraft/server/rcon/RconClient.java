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

public class RconClient
extends RconBase {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int field_29799 = 3;
    final static private int field_29800 = 2;
    final static private int field_29801 = 0;
    final static private int field_29802 = 2;
    final static private int field_29803 = -1;
    private boolean authenticated;
    final private Socket socket;
    final private byte[] packetBuffer = new byte[1460];
    final private String password;
    final private DedicatedServer server;

    RconClient(DedicatedServer server, String password, Socket socket) {
        super("RCON Client " + String.valueOf(socket.getInetAddress()));
        this.server = server;
        this.socket = socket;
        try {
            this.socket.setSoTimeout(0);
        }
        catch (Exception exception) {
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
        block18: {
            block13: while (true) {
                int j;
                int i;
                block17: {
                    block16: {
                        if (!this.running) break;
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(this.socket.getInputStream());
                        i = bufferedInputStream.read(this.packetBuffer, 0, 1460);
                        if (10 <= i) break block16;
                        this.close();
                        LOGGER.info("Thread {} shutting down", (Object)this.description);
                        this.running = false;
                        return;
                    }
                    j = 0;
                    int k = BufferHelper.getIntLE(this.packetBuffer, 0, i);
                    if (k == i - 4) break block17;
                    this.close();
                    LOGGER.info("Thread {} shutting down", (Object)this.description);
                    this.running = false;
                    return;
                }
                int l = BufferHelper.getIntLE(this.packetBuffer, j += 4, i);
                int m = BufferHelper.getIntLE(this.packetBuffer, j += 4);
                j += 4;
                switch (m) {
                    case 3: {
                        String string = BufferHelper.getString(this.packetBuffer, j, i);
                        j += string.length();
                        if (!string.isEmpty() && string.equals(this.password)) {
                            this.authenticated = true;
                            this.respond(l, 2, "");
                            break;
                        }
                        this.authenticated = false;
                        this.fail();
                        break;
                    }
                    case 2: {
                        if (this.authenticated) {
                            String string2 = BufferHelper.getString(this.packetBuffer, j, i);
                            try {
                                this.respond(l, this.server.executeRconCommand(string2));
                            }
                            catch (Exception exception) {
                                this.respond(l, "Error executing: " + string2 + " (" + exception.getMessage() + ")");
                            }
                            continue block13;
                        }
                        this.fail();
                        break;
                    }
                    default: {
                        this.respond(l, String.format(Locale.ROOT, "Unknown request %s", Integer.toHexString(m)));
                    }
                }
                continue;
                break;
            }
            this.close();
            LOGGER.info("Thread {} shutting down", (Object)this.description);
            this.running = false;
            break block18;
            catch (IOException bufferedInputStream) {
                this.close();
                LOGGER.info("Thread {} shutting down", (Object)this.description);
                this.running = false;
                break block18;
            }
            catch (Exception exception2) {
                try {
                    LOGGER.error("Exception whilst parsing RCON input", (Throwable)exception2);
                    this.close();
                }
                catch (Throwable throwable) {
                    this.close();
                    LOGGER.info("Thread {} shutting down", (Object)this.description);
                    this.running = false;
                    throw throwable;
                }
                LOGGER.info("Thread {} shutting down", (Object)this.description);
                this.running = false;
            }
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
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to close socket", (Throwable)iOException);
        }
    }
}

