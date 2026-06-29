/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 */
package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class ScoreHolderArgumentType
implements ArgumentType<ScoreHolders> {
    final static public SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
        StringReader stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader, EntitySelectorReader.shouldAllowAtSelectors((ServerCommandSource)context.getSource()));
        try {
            entitySelectorReader.read();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return entitySelectorReader.listSuggestions(builder, builderx -> CommandSource.suggestMatching(((ServerCommandSource)context.getSource()).getPlayerNames(), builderx));
    };
    final static private Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "*", "@e");
    final static private SimpleCommandExceptionType EMPTY_SCORE_HOLDER_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("argument.scoreHolder.empty"));
    final boolean multiple;

    public ScoreHolderArgumentType(boolean multiple) {
        this.multiple = multiple;
    }

    public static ScoreHolder getScoreHolder(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ScoreHolderArgumentType.getScoreHolders(context, name).iterator().next();
    }

    public static Collection<ScoreHolder> getScoreHolders(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ScoreHolderArgumentType.getScoreHolders(context, name, Collections::emptyList);
    }

    public static Collection<ScoreHolder> getScoreboardScoreHolders(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ScoreHolderArgumentType.getScoreHolders(context, name, ((ServerCommandSource)context.getSource()).getServer().getScoreboard()::getKnownScoreHolders);
    }

    public static Collection<ScoreHolder> getScoreHolders(CommandContext<ServerCommandSource> context, String name, Supplier<Collection<ScoreHolder>> players) throws CommandSyntaxException {
        Collection<ScoreHolder> collection = ((ScoreHolders)context.getArgument(name, ScoreHolders.class)).getNames((ServerCommandSource)context.getSource(), players);
        if (collection.isEmpty()) {
            throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
        }
        return collection;
    }

    public static ScoreHolderArgumentType scoreHolder() {
        return new ScoreHolderArgumentType(false);
    }

    public static ScoreHolderArgumentType scoreHolders() {
        return new ScoreHolderArgumentType(true);
    }

    public ScoreHolders net_minecraft_command_argument_ScoreHolderArgumentType$ScoreHolders_parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader, true);
    }

    public <S> ScoreHolders net_minecraft_command_argument_ScoreHolderArgumentType$ScoreHolders_parse(StringReader stringReader, S object) throws CommandSyntaxException {
        return this.parse(stringReader, EntitySelectorReader.shouldAllowAtSelectors(object));
    }

    private ScoreHolders parse(StringReader reader, boolean allowAtSelectors) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '@') {
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(reader, allowAtSelectors);
            EntitySelector entitySelector = entitySelectorReader.read();
            if (!this.multiple && entitySelector.getLimit() > 1) {
                throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.createWithContext((ImmutableStringReader)reader);
            }
            return new SelectorScoreHolders(entitySelector);
        }
        int i = reader.getCursor();
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }
        String string = reader.getString().substring(i, reader.getCursor());
        if (string.equals("*")) {
            return (source, players) -> {
                Collection collection = (Collection)players.get();
                if (collection.isEmpty()) {
                    throw EMPTY_SCORE_HOLDER_EXCEPTION.create();
                }
                return collection;
            };
        }
        List<ScoreHolder> list = List.of(ScoreHolder.fromName(string));
        if (string.startsWith("#")) {
            return (source, players) -> list;
        }
        try {
            UUID uUID = UUID.fromString(string);
            return (source, holders) -> {
                MinecraftServer minecraftServer = source.getServer();
                Entity scoreHolder = null;
                ArrayList<Entity> list2 = null;
                for (ServerWorld serverWorld : minecraftServer.getWorlds()) {
                    Entity entity = serverWorld.net_minecraft_entity_Entity_getEntity(uUID);
                    if (entity == null) continue;
                    if (scoreHolder == null) {
                        scoreHolder = entity;
                        continue;
                    }
                    if (list2 == null) {
                        list2 = new ArrayList<Entity>();
                        list2.add(scoreHolder);
                    }
                    list2.add(entity);
                }
                if (list2 != null) {
                    return list2;
                }
                if (scoreHolder != null) {
                    return List.of(scoreHolder);
                }
                return list;
            };
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return (source, holders) -> {
                MinecraftServer minecraftServer = source.getServer();
                ServerPlayerEntity serverPlayerEntity = minecraftServer.net_minecraft_server_PlayerManager_getPlayerManager().getPlayer(string);
                if (serverPlayerEntity != null) {
                    return List.of(serverPlayerEntity);
                }
                return list;
            };
        }
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public Object java_lang_Object_parse(StringReader reader, Object source) throws CommandSyntaxException {
        return this.java_lang_Object_parse(reader, source);
    }

    public Object java_lang_Object_parse(StringReader reader) throws CommandSyntaxException {
        return this.net_minecraft_command_argument_ScoreHolderArgumentType$ScoreHolders_parse(reader);
    }

    @FunctionalInterface
    public static interface ScoreHolders {
        public Collection<ScoreHolder> getNames(ServerCommandSource var1, Supplier<Collection<ScoreHolder>> var2) throws CommandSyntaxException;
    }

    public static class SelectorScoreHolders
    implements ScoreHolders {
        final private EntitySelector selector;

        public SelectorScoreHolders(EntitySelector selector) {
            this.selector = selector;
        }

        @Override
        public Collection<ScoreHolder> getNames(ServerCommandSource serverCommandSource, Supplier<Collection<ScoreHolder>> supplier) throws CommandSyntaxException {
            List<? extends Entity> list = this.selector.getEntities(serverCommandSource);
            if (list.isEmpty()) {
                throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
            }
            return List.copyOf(list);
        }
    }

    public static class Serializer
    implements ArgumentSerializer<ScoreHolderArgumentType, Properties> {
        final static private byte MULTIPLE_FLAG = 1;

        @Override
        public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
            int i = 0;
            if (properties.multiple) {
                i |= 1;
            }
            packetByteBuf.net_minecraft_network_PacketByteBuf_writeByte(1);
        }

        @Override
        public Properties net_minecraft_command_argument_ScoreHolderArgumentType$Serializer$Properties_fromPacket(PacketByteBuf packetByteBuf) {
            byte b = packetByteBuf.readByte();
            boolean bl = (b & 1) != 0;
            return new Properties(bl);
        }

        @Override
        public void writeJson(Properties properties, JsonObject jsonObject) {
            jsonObject.addProperty("amount", properties.multiple ? "multiple" : "single");
        }

        @Override
        public Properties getArgumentTypeProperties(ScoreHolderArgumentType scoreHolderArgumentType) {
            return new Properties(scoreHolderArgumentType.multiple);
        }

        @Override
        public ArgumentSerializer.ArgumentTypeProperties net_minecraft_command_argument_serialize_ArgumentSerializer$ArgumentTypeProperties_fromPacket(PacketByteBuf buf) {
            return this.net_minecraft_command_argument_ScoreHolderArgumentType$Serializer$Properties_fromPacket(buf);
        }

        public final class Properties
        implements ArgumentSerializer.ArgumentTypeProperties<ScoreHolderArgumentType> {
            final boolean multiple;

            Properties(boolean multiple) {
                this.multiple = multiple;
            }

            @Override
            public ScoreHolderArgumentType net_minecraft_command_argument_ScoreHolderArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
                return new ScoreHolderArgumentType(this.multiple);
            }

            @Override
            public ArgumentSerializer<ScoreHolderArgumentType, ?> getSerializer() {
                return Serializer.this;
            }

            @Override
            public ArgumentType com_mojang_brigadier_arguments_ArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
                return this.net_minecraft_command_argument_ScoreHolderArgumentType_createType(commandRegistryAccess);
            }
        }
    }
}

