/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model.json;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.MultipartModelCondition;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record SimpleMultipartModelSelector(Map<String, Terms> tests) implements MultipartModelCondition
{
    final static Logger LOGGER = LogUtils.getLogger();
    final static public Codec<SimpleMultipartModelSelector> CODEC = Codecs.nonEmptyMap(Codec.unboundedMap((Codec)Codec.STRING, Terms.VALUE_CODEC)).xmap(SimpleMultipartModelSelector::new, SimpleMultipartModelSelector::tests);

    @Override
    public <O, S extends State<O, S>> Predicate<S> instantiate(StateManager<O, S> stateManager) {
        ArrayList list = new ArrayList(this.tests.size());
        this.tests.forEach((property, terms) -> list.add(SimpleMultipartModelSelector.init(stateManager, property, terms)));
        return Util.allOf(list);
    }

    private static <O, S extends State<O, S>> Predicate<S> init(StateManager<O, S> stateManager, String property, Terms terms) {
        Property<?> property2 = stateManager.getProperty(property);
        if (property2 == null) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Unknown property '%s' on '%s'", property, stateManager.getOwner()));
        }
        return terms.instantiate(stateManager.getOwner(), property2);
    }

    @Environment(value=EnvType.CLIENT)
    public record Terms(List<Term> entries) {
        final static private char DELIMITER = '|';
        final static private Joiner JOINER = Joiner.on((char)'|');
        final static private Splitter SPLITTER = Splitter.on((char)'|');
        final static private Codec<String> CODEC = Codec.either((Codec)Codec.INT, (Codec)Codec.BOOL).flatComapMap(either -> (String)either.map(String::valueOf, String::valueOf), string -> DataResult.error(() -> "This codec can't be used for encoding"));
        final static public Codec<Terms> VALUE_CODEC = Codec.withAlternative((Codec)Codec.STRING, CODEC).comapFlatMap(Terms::tryParse, Terms::toString);

        public Terms {
            if (entries.isEmpty()) {
                throw new IllegalArgumentException("Empty value for property");
            }
        }

        public static DataResult<Terms> tryParse(String terms) {
            List<Term> list = SPLITTER.splitToStream((CharSequence)terms).map(Term::parse).toList();
            if (list.isEmpty()) {
                return DataResult.error(() -> "Empty value for property");
            }
            for (Term term : list) {
                if (!term.value.isEmpty()) continue;
                return DataResult.error(() -> "Empty term in value '" + terms + "'");
            }
            return DataResult.success((Object)new Terms(list));
        }

        @Override
        public String toString() {
            return JOINER.join(this.entries);
        }

        public <O, S extends State<O, S>, T extends Comparable<T>> Predicate<S> instantiate(O object, Property<T> property) {
            ArrayList list2;
            boolean bl;
            Predicate predicate = Util.anyOf(Lists.transform(this.entries, term -> this.instantiate(object, property, (Term)term)));
            ArrayList list = new ArrayList(property.getValues());
            int i = list.size();
            list.removeIf(predicate.negate());
            int j = list.size();
            if (j == 0) {
                LOGGER.warn("Condition {} for property {} on {} is always false", new Object[]{this, property.getName(), object});
                return state -> false;
            }
            int k = i - j;
            if (k == 0) {
                LOGGER.warn("Condition {} for property {} on {} is always true", new Object[]{this, property.getName(), object});
                return state -> true;
            }
            if (j <= k) {
                bl = false;
                list2 = list;
            } else {
                bl = true;
                ArrayList<T> list3 = new ArrayList<T>(property.getValues());
                list3.removeIf(predicate);
                list2 = list3;
            }
            if (list2.size() == 1) {
                Comparable comparable = (Comparable)list2.getFirst();
                return state -> {
                    Object comparable2 = state.get(property);
                    return comparable.equals(comparable2) ^ bl;
                };
            }
            return state -> {
                Object comparable = state.get(property);
                return list2.contains(comparable) ^ bl;
            };
        }

        private <T extends Comparable<T>> T parseValue(Object object, Property<T> property, String value) {
            Optional<T> optional = property.parse(value);
            if (optional.isEmpty()) {
                throw new RuntimeException(String.format(Locale.ROOT, "Unknown value '%s' for property '%s' on '%s' in '%s'", value, property, object, this));
            }
            return (T)((Comparable)optional.get());
        }

        private <T extends Comparable<T>> Predicate<T> instantiate(Object object, Property<T> property, Term term) {
            Object comparable = this.parseValue(object, property, term.value);
            if (term.negated) {
                return value -> !value.equals(comparable);
            }
            return value -> value.equals(comparable);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Term
    extends Record {
        final String value;
        final boolean negated;
        final static private String NEGATED_PREFIX = "!";

        public Term(String value, boolean negated) {
            if (value.isEmpty()) {
                throw new IllegalArgumentException("Empty term");
            }
            this.value = value;
            this.negated = negated;
        }

        public static Term parse(String value) {
            if (value.startsWith(NEGATED_PREFIX)) {
                return new Term(value.substring(1), true);
            }
            return new Term(value, false);
        }

        @Override
        public String toString() {
            return this.negated ? NEGATED_PREFIX + this.value : this.value;
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Term.class, "value;negated", "value", "negated"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Term.class, "value;negated", "value", "negated"}, this, object);
        }

        public String value() {
            return this.value;
        }

        public boolean negated() {
            return this.negated;
        }
    }
}

