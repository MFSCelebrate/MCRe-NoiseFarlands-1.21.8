/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
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

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class GameProfileArgumentType
implements ArgumentType<GameProfileArgument> {
    final static private Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
    final static public SimpleCommandExceptionType UNKNOWN_PLAYER_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("argument.player.unknown"));

    public static Collection<GameProfile> getProfileArgument(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ((GameProfileArgument)context.getArgument(name, GameProfileArgument.class)).getNames((ServerCommandSource)context.getSource());
    }

    public static GameProfileArgumentType gameProfile() {
        return new GameProfileArgumentType();
    }

    public <S> GameProfileArgument net_minecraft_command_argument_GameProfileArgumentType$GameProfileArgument_parse(StringReader stringReader, S object) throws CommandSyntaxException {
        return GameProfileArgumentType.parse(stringReader, EntitySelectorReader.shouldAllowAtSelectors(object));
    }

    public GameProfileArgument net_minecraft_command_argument_GameProfileArgumentType$GameProfileArgument_parse(StringReader stringReader) throws CommandSyntaxException {
        return GameProfileArgumentType.parse(stringReader, true);
    }

    private static GameProfileArgument parse(StringReader reader, boolean allowAtSelectors) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '@') {
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(reader, allowAtSelectors);
            EntitySelector entitySelector = entitySelectorReader.read();
            if (entitySelector.includesNonPlayers()) {
                throw EntityArgumentType.PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION.createWithContext((ImmutableStringReader)reader);
            }
            return new SelectorBacked(entitySelector);
        }
        int i = reader.getCursor();
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }
        String string = reader.getString().substring(i, reader.getCursor());
        return source -> {
            Optional<GameProfile> optional = source.getServer().getUserCache().findByName(string);
            return Collections.singleton(optional.orElseThrow(() -> ((SimpleCommandExceptionType)UNKNOWN_PLAYER_EXCEPTION).create()));
        };
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
            return entitySelectorReader.listSuggestions(builder2, (SuggestionsBuilder builder) -> CommandSource.suggestMatching(commandSource.getPlayerNames(), builder));
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
        return this.net_minecraft_command_argument_GameProfileArgumentType$GameProfileArgument_parse(reader);
    }

    @FunctionalInterface
    public static interface GameProfileArgument {
        public Collection<GameProfile> getNames(ServerCommandSource var1) throws CommandSyntaxException;
    }

    public static class SelectorBacked
    implements GameProfileArgument {
        final private EntitySelector selector;

        public SelectorBacked(EntitySelector selector) {
            this.selector = selector;
        }

        @Override
        public Collection<GameProfile> getNames(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
            List<ServerPlayerEntity> list = this.selector.getPlayers(serverCommandSource);
            if (list.isEmpty()) {
                throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
            }
            ArrayList list2 = Lists.newArrayList();
            for (ServerPlayerEntity serverPlayerEntity : list) {
                list2.add(serverPlayerEntity.getGameProfile());
            }
            return list2;
        }
    }
}

