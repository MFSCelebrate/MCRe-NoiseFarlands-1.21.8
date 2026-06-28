/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command.argument;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class EntityArgumentType
implements ArgumentType<EntitySelector> {
    final static private Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
    final static public SimpleCommandExceptionType TOO_MANY_ENTITIES_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("argument.entity.toomany"));
    final static public SimpleCommandExceptionType TOO_MANY_PLAYERS_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("argument.player.toomany"));
    final static public SimpleCommandExceptionType PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("argument.player.entities"));
    final static public SimpleCommandExceptionType ENTITY_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("argument.entity.notfound.entity"));
    final static public SimpleCommandExceptionType PLAYER_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("argument.entity.notfound.player"));
    final static public SimpleCommandExceptionType NOT_ALLOWED_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("argument.entity.selector.not_allowed"));
    final boolean singleTarget;
    final boolean playersOnly;

    protected EntityArgumentType(boolean singleTarget, boolean playersOnly) {
        this.singleTarget = singleTarget;
        this.playersOnly = playersOnly;
    }

    public static EntityArgumentType entity() {
        return new EntityArgumentType(true, false);
    }

    public static Entity getEntity(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ((EntitySelector)context.getArgument(name, EntitySelector.class)).getEntity((ServerCommandSource)context.getSource());
    }

    public static EntityArgumentType entities() {
        return new EntityArgumentType(false, false);
    }

    public static Collection<? extends Entity> getEntities(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        Collection<? extends Entity> collection = EntityArgumentType.getOptionalEntities(context, name);
        if (collection.isEmpty()) {
            throw ENTITY_NOT_FOUND_EXCEPTION.create();
        }
        return collection;
    }

    public static Collection<? extends Entity> getOptionalEntities(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ((EntitySelector)context.getArgument(name, EntitySelector.class)).getEntities((ServerCommandSource)context.getSource());
    }

    public static Collection<ServerPlayerEntity> getOptionalPlayers(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ((EntitySelector)context.getArgument(name, EntitySelector.class)).getPlayers((ServerCommandSource)context.getSource());
    }

    public static EntityArgumentType player() {
        return new EntityArgumentType(true, true);
    }

    public static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ((EntitySelector)context.getArgument(name, EntitySelector.class)).getPlayer((ServerCommandSource)context.getSource());
    }

    public static EntityArgumentType players() {
        return new EntityArgumentType(false, true);
    }

    public static Collection<ServerPlayerEntity> getPlayers(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        List<ServerPlayerEntity> list = ((EntitySelector)context.getArgument(name, EntitySelector.class)).getPlayers((ServerCommandSource)context.getSource());
        if (list.isEmpty()) {
            throw PLAYER_NOT_FOUND_EXCEPTION.create();
        }
        return list;
    }

    public EntitySelector net_minecraft_command_EntitySelector_parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader, true);
    }

    public <S> EntitySelector net_minecraft_command_EntitySelector_parse(StringReader stringReader, S object) throws CommandSyntaxException {
        return this.parse(stringReader, EntitySelectorReader.shouldAllowAtSelectors(object));
    }

    private EntitySelector parse(StringReader reader, boolean allowAtSelectors) throws CommandSyntaxException {
        boolean i = false;
        EntitySelectorReader entitySelectorReader = new EntitySelectorReader(reader, allowAtSelectors);
        EntitySelector entitySelector = entitySelectorReader.read();
        if (entitySelector.getLimit() > 1 && this.singleTarget) {
            if (this.playersOnly) {
                reader.setCursor(0);
                throw TOO_MANY_PLAYERS_EXCEPTION.createWithContext((ImmutableStringReader)reader);
            }
            reader.setCursor(0);
            throw TOO_MANY_ENTITIES_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        if (entitySelector.includesNonPlayers() && this.playersOnly && !entitySelector.isSenderOnly()) {
            reader.setCursor(0);
            throw PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        return entitySelector;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder2) {
        Object object = context.getSource();
        if (object instanceof CommandSource) {
            CommandSource commandSource = (CommandSource)object;
            StringReader stringReader = new StringReader(builder2.getInput());
            stringReader.setCursor(builder2.getStart());
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader, EntitySelectorReader.shouldAllowAtSelectors(commandSource));
            try {
                entitySelectorReader.read();
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
            return entitySelectorReader.listSuggestions(builder2, (SuggestionsBuilder builder) -> {
                Collection<String> collection = commandSource.getPlayerNames();
                Collection<String> iterable = this.playersOnly ? collection : Iterables.concat(collection, commandSource.getEntitySuggestions());
                CommandSource.suggestMatching(iterable, builder);
            });
        }
        return Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public Object java_lang_Object_parse(StringReader reader, Object source) throws CommandSyntaxException {
        return this.java_lang_Object_parse(reader, source);
    }

    public Object java_lang_Object_parse(StringReader reader) throws CommandSyntaxException {
        return this.net_minecraft_command_EntitySelector_parse(reader);
    }

    public static class Serializer
    implements ArgumentSerializer<EntityArgumentType, Properties> {
        final static private byte SINGLE_FLAG = 1;
        final static private byte PLAYERS_ONLY_FLAG = 2;

        @Override
        public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
            int i = 0;
            if (properties.single) {
                i |= 1;
            }
            if (properties.playersOnly) {
                i |= 2;
            }
            packetByteBuf.net_minecraft_network_PacketByteBuf_writeByte(1);
        }

        @Override
        public Properties net_minecraft_command_argument_EntityArgumentType$Serializer$Properties_fromPacket(PacketByteBuf packetByteBuf) {
            byte b = packetByteBuf.readByte();
            return new Properties((b & 1) != 0, (b & 2) != 0);
        }

        @Override
        public void writeJson(Properties properties, JsonObject jsonObject) {
            jsonObject.addProperty("amount", properties.single ? "single" : "multiple");
            jsonObject.addProperty("type", properties.playersOnly ? "players" : "entities");
        }

        @Override
        public Properties getArgumentTypeProperties(EntityArgumentType entityArgumentType) {
            return new Properties(entityArgumentType.singleTarget, entityArgumentType.playersOnly);
        }

        @Override
        public ArgumentSerializer.ArgumentTypeProperties net_minecraft_command_argument_serialize_ArgumentSerializer$ArgumentTypeProperties_fromPacket(PacketByteBuf buf) {
            return this.net_minecraft_command_argument_EntityArgumentType$Serializer$Properties_fromPacket(buf);
        }

        public final class Properties
        implements ArgumentSerializer.ArgumentTypeProperties<EntityArgumentType> {
            final boolean single;
            final boolean playersOnly;

            Properties(boolean single, boolean playersOnly) {
                this.single = single;
                this.playersOnly = playersOnly;
            }

            @Override
            public EntityArgumentType net_minecraft_command_argument_EntityArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
                return new EntityArgumentType(this.single, this.playersOnly);
            }

            @Override
            public ArgumentSerializer<EntityArgumentType, ?> getSerializer() {
                return Serializer.this;
            }

            @Override
            public ArgumentType com_mojang_brigadier_arguments_ArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
                return this.net_minecraft_command_argument_EntityArgumentType_createType(commandRegistryAccess);
            }
        }
    }
}

