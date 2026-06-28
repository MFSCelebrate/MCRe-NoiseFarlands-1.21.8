/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

public final class NetworkSide
extends Enum<NetworkSide> {
    final static public NetworkSide SERVERBOUND = new NetworkSide("serverbound");
    final static public NetworkSide CLIENTBOUND = new NetworkSide("clientbound");
    final private String name;
    final static private NetworkSide[] field_11940;

    public static NetworkSide[] values() {
        return (NetworkSide[])field_11940.clone();
    }

    public static NetworkSide valueOf(String string) {
        return Enum.valueOf(NetworkSide.class, string);
    }

    private NetworkSide(String name) {
        this.name = name;
    }

    public NetworkSide getOpposite() {
        return this == CLIENTBOUND ? SERVERBOUND : CLIENTBOUND;
    }

    public String getName() {
        return this.name;
    }

    private static NetworkSide[] method_36947() {
        return new NetworkSide[]{SERVERBOUND, CLIENTBOUND};
    }

    static {
        field_11940 = NetworkSide.method_36947();
    }
}

