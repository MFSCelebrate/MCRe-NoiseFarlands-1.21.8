/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.option;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class KeyBinding
implements Comparable<KeyBinding> {
    final static private Map<String, KeyBinding> KEYS_BY_ID = Maps.newHashMap();
    final static private Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS = Maps.newHashMap();
    final static private Set<String> KEY_CATEGORIES = Sets.newHashSet();
    final static public String MOVEMENT_CATEGORY = "key.categories.movement";
    final static public String MISC_CATEGORY = "key.categories.misc";
    final static public String MULTIPLAYER_CATEGORY = "key.categories.multiplayer";
    final static public String GAMEPLAY_CATEGORY = "key.categories.gameplay";
    final static public String INVENTORY_CATEGORY = "key.categories.inventory";
    final static public String UI_CATEGORY = "key.categories.ui";
    final static public String CREATIVE_CATEGORY = "key.categories.creative";
    final static private Map<String, Integer> CATEGORY_ORDER_MAP = Util.make(Maps.newHashMap(), map -> {
        map.put(MOVEMENT_CATEGORY, 1);
        map.put(GAMEPLAY_CATEGORY, 2);
        map.put(INVENTORY_CATEGORY, 3);
        map.put(CREATIVE_CATEGORY, 4);
        map.put(MULTIPLAYER_CATEGORY, 5);
        map.put(UI_CATEGORY, 6);
        map.put(MISC_CATEGORY, 7);
    });
    final private String translationKey;
    final private InputUtil.Key defaultKey;
    final private String category;
    private InputUtil.Key boundKey;
    private boolean pressed;
    private int timesPressed;

    public static void onKeyPressed(InputUtil.Key key) {
        KeyBinding keyBinding = KEY_TO_BINDINGS.get(key);
        if (keyBinding != null) {
            ++keyBinding.timesPressed;
        }
    }

    public static void setKeyPressed(InputUtil.Key key, boolean pressed) {
        KeyBinding keyBinding = KEY_TO_BINDINGS.get(key);
        if (keyBinding != null) {
            keyBinding.setPressed(pressed);
        }
    }

    public static void updatePressedStates() {
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            if (keyBinding.boundKey.getCategory() != InputUtil.Type.KEYSYM || keyBinding.boundKey.getCode() == InputUtil.UNKNOWN_KEY.getCode()) continue;
            keyBinding.setPressed(InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyBinding.boundKey.getCode()));
        }
    }

    public static void unpressAll() {
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            keyBinding.reset();
        }
    }

    public static void untoggleStickyKeys() {
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            if (!(keyBinding instanceof StickyKeyBinding)) continue;
            StickyKeyBinding stickyKeyBinding = (StickyKeyBinding)keyBinding;
            stickyKeyBinding.untoggle();
        }
    }

    public static void updateKeysByCode() {
        KEY_TO_BINDINGS.clear();
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            KEY_TO_BINDINGS.put(keyBinding.boundKey, keyBinding);
        }
    }

    public KeyBinding(String translationKey, int code, String category) {
        this(translationKey, InputUtil.Type.KEYSYM, code, category);
    }

    public KeyBinding(String translationKey, InputUtil.Type type, int code, String category) {
        this.translationKey = translationKey;
        this.defaultKey = this.boundKey = type.createFromCode(code);
        this.category = category;
        KEYS_BY_ID.put(translationKey, this);
        KEY_TO_BINDINGS.put(this.boundKey, this);
        KEY_CATEGORIES.add(category);
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public String getCategory() {
        return this.category;
    }

    public boolean wasPressed() {
        if (this.timesPressed == 0) {
            return false;
        }
        --this.timesPressed;
        return true;
    }

    private void reset() {
        this.timesPressed = 0;
        this.setPressed(false);
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public InputUtil.Key getDefaultKey() {
        return this.defaultKey;
    }

    public void setBoundKey(InputUtil.Key boundKey) {
        this.boundKey = boundKey;
    }

    @Override
    public int compareTo(KeyBinding keyBinding) {
        if (this.category.equals(keyBinding.category)) {
            return I18n.translate(this.translationKey, new Object[0]).compareTo(I18n.translate(keyBinding.translationKey, new Object[0]));
        }
        return CATEGORY_ORDER_MAP.get(this.category).compareTo(CATEGORY_ORDER_MAP.get(keyBinding.category));
    }

    public static Supplier<Text> getLocalizedName(String id) {
        KeyBinding keyBinding = KEYS_BY_ID.get(id);
        if (keyBinding == null) {
            return () -> Text.translatable(id);
        }
        return keyBinding::getBoundKeyLocalizedText;
    }

    public boolean equals(KeyBinding other) {
        return this.boundKey.equals(other.boundKey);
    }

    public boolean isUnbound() {
        return this.boundKey.equals(InputUtil.UNKNOWN_KEY);
    }

    public boolean matchesKey(int keyCode, int scanCode) {
        if (keyCode == InputUtil.UNKNOWN_KEY.getCode()) {
            return this.boundKey.getCategory() == InputUtil.Type.SCANCODE && this.boundKey.getCode() == scanCode;
        }
        return this.boundKey.getCategory() == InputUtil.Type.KEYSYM && this.boundKey.getCode() == keyCode;
    }

    public boolean matchesMouse(int code) {
        return this.boundKey.getCategory() == InputUtil.Type.MOUSE && this.boundKey.getCode() == code;
    }

    public Text getBoundKeyLocalizedText() {
        return this.boundKey.getLocalizedText();
    }

    public boolean isDefault() {
        return this.boundKey.equals(this.defaultKey);
    }

    public String getBoundKeyTranslationKey() {
        return this.boundKey.getTranslationKey();
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    @Nullable
    public static KeyBinding byId(String id) {
        return KEYS_BY_ID.get(id);
    }

    @Override
    public int compareTo(Object other) {
        return this.compareTo((KeyBinding)other);
    }
}

