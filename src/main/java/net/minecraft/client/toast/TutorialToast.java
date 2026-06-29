/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.toast;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TutorialToast
implements Toast {
    final static private Identifier TEXTURE = Identifier.ofVanilla("toast/tutorial");
    final static public int PROGRESS_BAR_WIDTH = 154;
    final static public int PROGRESS_BAR_HEIGHT = 1;
    final static public int PROGRESS_BAR_X = 3;
    final static public int field_55091 = 4;
    final static private int field_55092 = 7;
    final static private int field_55093 = 3;
    final static private int field_55094 = 11;
    final static private int field_55095 = 30;
    final static private int field_55096 = 126;
    final private Type type;
    final private List<OrderedText> text;
    private Toast.Visibility visibility = Toast.Visibility.SHOW;
    private long lastTime;
    private float lastProgress;
    private float progress;
    final private boolean hasProgressBar;
    final private int displayDuration;

    public TutorialToast(TextRenderer textRenderer, Type type, Text title, @Nullable Text description, boolean hasProgressBar, int displayDuration) {
        this.type = type;
        this.text = new ArrayList<OrderedText>(2);
        this.text.addAll(textRenderer.wrapLines(title.copy().withColor(Colors.PURPLE), 126));
        if (description != null) {
            this.text.addAll(textRenderer.wrapLines(description, 126));
        }
        this.hasProgressBar = hasProgressBar;
        this.displayDuration = displayDuration;
    }

    public TutorialToast(TextRenderer textRenderer, Type type, Text title, @Nullable Text description, boolean hasProgressBar) {
        this(textRenderer, type, title, description, hasProgressBar, 0);
    }

    @Override
    public Toast.Visibility getVisibility() {
        return this.visibility;
    }

    @Override
    public void update(ToastManager manager, long time) {
        if (this.displayDuration > 0) {
            this.lastProgress = this.progress = Math.min((float)time / (float)this.displayDuration, 1.0f);
            this.lastTime = time;
            if (time > (long)this.displayDuration) {
                this.hide();
            }
        } else if (this.hasProgressBar) {
            this.lastProgress = MathHelper.clampedLerp(this.lastProgress, this.progress, (float)(time - this.lastTime) / 100.0f);
            this.lastTime = time;
        }
    }

    @Override
    public int getHeight() {
        return 7 + this.getTextHeight() + 3;
    }

    private int getTextHeight() {
        return Math.max(this.text.size(), 2) * 11;
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        int l;
        int i = this.getHeight();
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, this.getWidth(), i);
        this.type.drawIcon(context, 6, 6);
        int j = this.text.size() * 11;
        int k = 7 + (this.getTextHeight() - j) / 2;
        for (l = 0; l < this.text.size(); ++l) {
            context.drawText(textRenderer, this.text.get(l), 30, k + l * 11, -16777216, false);
        }
        if (this.hasProgressBar) {
            l = i - 4;
            context.fill(3, l, 157, l + 1, Colors.WHITE);
            int m = this.progress >= this.lastProgress ? -16755456 : -11206656;
            context.fill(3, l, (int)(3.0f + 154.0f * this.lastProgress), l + 1, m);
        }
    }

    public void hide() {
        this.visibility = Toast.Visibility.HIDE;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type> {
        final static public Type MOVEMENT_KEYS = new Type(Identifier.ofVanilla("toast/movement_keys"));
        final static public Type MOUSE = new Type(Identifier.ofVanilla("toast/mouse"));
        final static public Type TREE = new Type(Identifier.ofVanilla("toast/tree"));
        final static public Type RECIPE_BOOK = new Type(Identifier.ofVanilla("toast/recipe_book"));
        final static public Type WOODEN_PLANKS = new Type(Identifier.ofVanilla("toast/wooden_planks"));
        final static public Type SOCIAL_INTERACTIONS = new Type(Identifier.ofVanilla("toast/social_interactions"));
        final static public Type RIGHT_CLICK = new Type(Identifier.ofVanilla("toast/right_click"));
        final private Identifier texture;
        final static private Type[] field_2234;

        public static Type[] values() {
            return (Type[])field_2234.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(Identifier texture) {
            this.texture = texture;
        }

        public void drawIcon(DrawContext context, int x, int y) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, x, y, 20, 20);
        }

        private static Type[] method_36873() {
            return new Type[]{MOVEMENT_KEYS, MOUSE, TREE, RECIPE_BOOK, WOODEN_PLANKS, SOCIAL_INTERACTIONS, RIGHT_CLICK};
        }

        static {
            field_2234 = Type.method_36873();
        }
    }
}

