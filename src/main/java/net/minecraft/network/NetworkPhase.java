/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

public final class NetworkPhase
extends Enum<NetworkPhase> {
    final static public NetworkPhase HANDSHAKING = new NetworkPhase("handshake");
    final static public NetworkPhase PLAY = new NetworkPhase("play");
    final static public NetworkPhase STATUS = new NetworkPhase("status");
    final static public NetworkPhase LOGIN = new NetworkPhase("login");
    final static public NetworkPhase CONFIGURATION = new NetworkPhase("configuration");
    final private String id;
    final static private NetworkPhase[] field_11694;

    public static NetworkPhase[] values() {
        return (NetworkPhase[])field_11694.clone();
    }

    public static NetworkPhase valueOf(String string) {
        return Enum.valueOf(NetworkPhase.class, string);
    }

    private NetworkPhase(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    private static NetworkPhase[] method_36943() {
        return new NetworkPhase[]{HANDSHAKING, PLAY, STATUS, LOGIN, CONFIGURATION};
    }

    static {
        field_11694 = NetworkPhase.method_36943();
    }
}

