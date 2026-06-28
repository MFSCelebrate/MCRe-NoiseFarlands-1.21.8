/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.world;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public record ChunkTicketType(long expiryTicks, boolean persist, Use use) {
    final static public long NO_EXPIRATION = 0L;
    final static public ChunkTicketType START = ChunkTicketType.register("start", 0L, false, Use.LOADING_AND_SIMULATION);
    final static public ChunkTicketType DRAGON = ChunkTicketType.register("dragon", 0L, false, Use.LOADING_AND_SIMULATION);
    final static public ChunkTicketType PLAYER_LOADING = ChunkTicketType.register("player_loading", 0L, false, Use.LOADING);
    final static public ChunkTicketType PLAYER_SIMULATION = ChunkTicketType.register("player_simulation", 0L, false, Use.SIMULATION);
    final static public ChunkTicketType FORCED = ChunkTicketType.register("forced", 0L, true, Use.LOADING_AND_SIMULATION);
    final static public ChunkTicketType PORTAL = ChunkTicketType.register("portal", 300L, true, Use.LOADING_AND_SIMULATION);
    final static public ChunkTicketType ENDER_PEARL = ChunkTicketType.register("ender_pearl", 40L, false, Use.LOADING_AND_SIMULATION);
    final static public ChunkTicketType UNKNOWN = ChunkTicketType.register("unknown", 1L, false, Use.LOADING);

    private static ChunkTicketType register(String id, long expiryTicks, boolean persist, Use use) {
        return Registry.register(Registries.TICKET_TYPE, id, new ChunkTicketType(expiryTicks, persist, use));
    }

    public boolean isForLoading() {
        return this.use == Use.LOADING || this.use == Use.LOADING_AND_SIMULATION;
    }

    public boolean isForSimulation() {
        return this.use == Use.SIMULATION || this.use == Use.LOADING_AND_SIMULATION;
    }

    public boolean canExpire() {
        return this.expiryTicks != 0L;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ChunkTicketType.class, "timeout;persist;use", "expiryTicks", "persist", "use"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ChunkTicketType.class, "timeout;persist;use", "expiryTicks", "persist", "use"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ChunkTicketType.class, "timeout;persist;use", "expiryTicks", "persist", "use"}, this, object);
    }

    public static final class Use
    extends Enum<Use> {
        final static public Use LOADING = new Use();
        final static public Use SIMULATION = new Use();
        final static public Use LOADING_AND_SIMULATION = new Use();
        final static private Use[] field_55604;

        public static Use[] values() {
            return (Use[])field_55604.clone();
        }

        public static Use valueOf(String string) {
            return Enum.valueOf(Use.class, string);
        }

        private static Use[] method_66029() {
            return new Use[]{LOADING, SIMULATION, LOADING_AND_SIMULATION};
        }

        static {
            field_55604 = Use.method_66029();
        }
    }
}

