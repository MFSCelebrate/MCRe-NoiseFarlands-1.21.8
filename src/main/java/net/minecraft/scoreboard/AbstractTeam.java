/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.scoreboard;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTeam {
    public boolean isEqual(@Nullable AbstractTeam team) {
        if (team == null) {
            return false;
        }
        return this == team;
    }

    public abstract String getName();

    public abstract MutableText decorateName(Text var1);

    public abstract boolean shouldShowFriendlyInvisibles();

    public abstract boolean isFriendlyFireAllowed();

    public abstract VisibilityRule getNameTagVisibilityRule();

    public abstract Formatting getColor();

    public abstract Collection<String> getPlayerList();

    public abstract VisibilityRule getDeathMessageVisibilityRule();

    public abstract CollisionRule getCollisionRule();

    public static final class CollisionRule
    extends Enum<CollisionRule>
    implements StringIdentifiable {
        final static public CollisionRule ALWAYS = new CollisionRule("always", 0);
        final static public CollisionRule NEVER = new CollisionRule("never", 1);
        final static public CollisionRule PUSH_OTHER_TEAMS = new CollisionRule("pushOtherTeams", 2);
        final static public CollisionRule PUSH_OWN_TEAM = new CollisionRule("pushOwnTeam", 3);
        final static public Codec<CollisionRule> CODEC;
        final static private IntFunction<CollisionRule> INDEX_MAPPER;
        final static public PacketCodec<ByteBuf, CollisionRule> PACKET_CODEC;
        final public String name;
        final public int index;
        final static private CollisionRule[] field_1439;

        public static CollisionRule[] values() {
            return (CollisionRule[])field_1439.clone();
        }

        public static CollisionRule valueOf(String string) {
            return Enum.valueOf(CollisionRule.class, string);
        }

        private CollisionRule(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public Text getDisplayName() {
            return Text.translatable("team.collision." + this.name);
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static CollisionRule[] method_36797() {
            return new CollisionRule[]{ALWAYS, NEVER, PUSH_OTHER_TEAMS, PUSH_OWN_TEAM};
        }

        static {
            field_1439 = CollisionRule.method_36797();
            CODEC = StringIdentifiable.createCodec(CollisionRule::values);
            INDEX_MAPPER = ValueLists.createIndexToValueFunction(collisionRule -> collisionRule.index, CollisionRule.values(), ValueLists.OutOfBoundsHandling.ZERO);
            PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, collisionRule -> collisionRule.index);
        }
    }

    public static final class VisibilityRule
    extends Enum<VisibilityRule>
    implements StringIdentifiable {
        final static public VisibilityRule ALWAYS = new VisibilityRule("always", 0);
        final static public VisibilityRule NEVER = new VisibilityRule("never", 1);
        final static public VisibilityRule HIDE_FOR_OTHER_TEAMS = new VisibilityRule("hideForOtherTeams", 2);
        final static public VisibilityRule HIDE_FOR_OWN_TEAM = new VisibilityRule("hideForOwnTeam", 3);
        final static public Codec<VisibilityRule> CODEC;
        final static private IntFunction<VisibilityRule> INDEX_MAPPER;
        final static public PacketCodec<ByteBuf, VisibilityRule> PACKET_CODEC;
        final public String name;
        final public int index;
        final static private VisibilityRule[] field_1448;

        public static VisibilityRule[] values() {
            return (VisibilityRule[])field_1448.clone();
        }

        public static VisibilityRule valueOf(String string) {
            return Enum.valueOf(VisibilityRule.class, string);
        }

        private VisibilityRule(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public Text getDisplayName() {
            return Text.translatable("team.visibility." + this.name);
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static VisibilityRule[] method_36798() {
            return new VisibilityRule[]{ALWAYS, NEVER, HIDE_FOR_OTHER_TEAMS, HIDE_FOR_OWN_TEAM};
        }

        static {
            field_1448 = VisibilityRule.method_36798();
            CODEC = StringIdentifiable.createCodec(VisibilityRule::values);
            INDEX_MAPPER = ValueLists.createIndexToValueFunction(visibilityRule -> visibilityRule.index, VisibilityRule.values(), ValueLists.OutOfBoundsHandling.ZERO);
            PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, visibilityRule -> visibilityRule.index);
        }
    }
}

