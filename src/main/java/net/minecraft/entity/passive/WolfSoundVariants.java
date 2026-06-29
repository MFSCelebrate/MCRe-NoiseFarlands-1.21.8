/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.passive.WolfSoundVariant;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class WolfSoundVariants {
    final static public RegistryKey<WolfSoundVariant> CLASSIC = WolfSoundVariants.of(Type.CLASSIC);
    final static public RegistryKey<WolfSoundVariant> PUGLIN = WolfSoundVariants.of(Type.PUGLIN);
    final static public RegistryKey<WolfSoundVariant> SAD = WolfSoundVariants.of(Type.SAD);
    final static public RegistryKey<WolfSoundVariant> ANGRY = WolfSoundVariants.of(Type.ANGRY);
    final static public RegistryKey<WolfSoundVariant> GRUMPY = WolfSoundVariants.of(Type.GRUMPY);
    final static public RegistryKey<WolfSoundVariant> BIG = WolfSoundVariants.of(Type.BIG);
    final static public RegistryKey<WolfSoundVariant> CUTE = WolfSoundVariants.of(Type.CUTE);

    private static RegistryKey<WolfSoundVariant> of(Type type) {
        return RegistryKey.of(RegistryKeys.WOLF_SOUND_VARIANT, Identifier.ofVanilla(type.getId()));
    }

    public static void bootstrap(Registerable<WolfSoundVariant> registry) {
        WolfSoundVariants.register(registry, CLASSIC, Type.CLASSIC);
        WolfSoundVariants.register(registry, PUGLIN, Type.PUGLIN);
        WolfSoundVariants.register(registry, SAD, Type.SAD);
        WolfSoundVariants.register(registry, ANGRY, Type.ANGRY);
        WolfSoundVariants.register(registry, GRUMPY, Type.GRUMPY);
        WolfSoundVariants.register(registry, BIG, Type.BIG);
        WolfSoundVariants.register(registry, CUTE, Type.CUTE);
    }

    private static void register(Registerable<WolfSoundVariant> registry, RegistryKey<WolfSoundVariant> key, Type type) {
        registry.register(key, SoundEvents.WOLF_SOUNDS.get((Object)type));
    }

    public static RegistryEntry<WolfSoundVariant> select(DynamicRegistryManager registries, Random random) {
        return registries.net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.WOLF_SOUND_VARIANT).getRandom(random).orElseThrow();
    }

    public static final class Type
    extends Enum<Type> {
        final static public Type CLASSIC = new Type("classic", "");
        final static public Type PUGLIN = new Type("puglin", "_puglin");
        final static public Type SAD = new Type("sad", "_sad");
        final static public Type ANGRY = new Type("angry", "_angry");
        final static public Type GRUMPY = new Type("grumpy", "_grumpy");
        final static public Type BIG = new Type("big", "_big");
        final static public Type CUTE = new Type("cute", "_cute");
        final private String id;
        final private String suffix;
        final static private Type[] field_57096;

        public static Type[] values() {
            return (Type[])field_57096.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(String id, String suffix) {
            this.id = id;
            this.suffix = suffix;
        }

        public String getId() {
            return this.id;
        }

        public String getSoundEventSuffix() {
            return this.suffix;
        }

        private static Type[] method_68141() {
            return new Type[]{CLASSIC, PUGLIN, SAD, ANGRY, GRUMPY, BIG, CUTE};
        }

        static {
            field_57096 = Type.method_68141();
        }
    }
}

