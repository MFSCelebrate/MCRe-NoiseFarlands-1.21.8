/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import org.jetbrains.annotations.Nullable;

public interface Portal {
    default public int getPortalDelay(ServerWorld world, Entity entity) {
        return 0;
    }

    @Nullable
    public TeleportTarget createTeleportTarget(ServerWorld var1, Entity var2, BlockPos var3);

    default public Effect getPortalEffect() {
        return Effect.NONE;
    }

    public static final class Effect
    extends Enum<Effect> {
        final static public Effect CONFUSION = new Effect();
        final static public Effect NONE = new Effect();
        final static private Effect[] field_52063;

        public static Effect[] values() {
            return (Effect[])field_52063.clone();
        }

        public static Effect valueOf(String string) {
            return Enum.valueOf(Effect.class, string);
        }

        private static Effect[] method_60779() {
            return new Effect[]{CONFUSION, NONE};
        }

        static {
            field_52063 = Effect.method_60779();
        }
    }
}

