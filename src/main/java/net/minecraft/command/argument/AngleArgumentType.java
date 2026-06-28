/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class AngleArgumentType
implements ArgumentType<Angle> {
    final static private Collection<String> EXAMPLES = Arrays.asList("0", "~", "~-5");
    final static public SimpleCommandExceptionType INCOMPLETE_ANGLE_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("argument.angle.incomplete"));
    final static public SimpleCommandExceptionType INVALID_ANGLE_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("argument.angle.invalid"));

    public static AngleArgumentType angle() {
        return new AngleArgumentType();
    }

    public static float getAngle(CommandContext<ServerCommandSource> context, String name) {
        return ((Angle)context.getArgument(name, Angle.class)).getAngle((ServerCommandSource)context.getSource());
    }

    public Angle net_minecraft_command_argument_AngleArgumentType$Angle_parse(StringReader stringReader) throws CommandSyntaxException {
        float f;
        if (!stringReader.canRead()) {
            throw INCOMPLETE_ANGLE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        boolean bl = CoordinateArgument.isRelative(stringReader);
        float f2 = f = stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readFloat() : 0.0f;
        if (Float.isNaN(f) || Float.isInfinite(f)) {
            throw INVALID_ANGLE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        return new Angle(f, bl);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public Object java_lang_Object_parse(StringReader reader) throws CommandSyntaxException {
        return this.net_minecraft_command_argument_AngleArgumentType$Angle_parse(reader);
    }

    public static final class Angle {
        final private float angle;
        final private boolean relative;

        Angle(float angle, boolean relative) {
            this.angle = angle;
            this.relative = relative;
        }

        public float getAngle(ServerCommandSource source) {
            return MathHelper.wrapDegrees(this.relative ? this.angle + source.getRotation().y : this.angle);
        }
    }
}

