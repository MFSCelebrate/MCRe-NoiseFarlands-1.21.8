/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class RealmsScreen
extends Screen {
    final static protected int field_33055 = 17;
    final static protected int field_33057 = 7;
    final static protected long MAX_FILE_SIZE = 0x140000000L;
    final static protected int DARK_GRAY = -11776948;
    final static protected int GRAY = -9671572;
    final static protected int GREEN = -8388737;
    final static protected int BLUE = -13408581;
    final static protected int PURPLE = -9670204;
    final static protected int field_39676 = 32;
    final static protected int field_54866 = 8;
    final static protected Identifier REALMS_LOGO_TEXTURE = Identifier.ofVanilla("textures/gui/title/realms.png");
    final static protected int REALMS_LOGO_WIDGET_WIDTH = 128;
    final static protected int REALMS_LOGO_WIDGET_HEIGHT = 34;
    final static protected int REALMS_LOGO_TEXTURE_WIDTH = 128;
    final static protected int REALMS_LOGO_TEXTURE_HEIGHT = 64;
    final private List<RealmsLabel> labels = Lists.newArrayList();

    public RealmsScreen(Text text) {
        super(text);
    }

    protected static int row(int index) {
        return 40 + index * 13;
    }

    protected RealmsLabel addLabel(RealmsLabel label) {
        this.labels.add(label);
        return this.addDrawable(label);
    }

    public Text narrateLabels() {
        return ScreenTexts.joinLines(this.labels.stream().map(RealmsLabel::getText).collect(Collectors.toList()));
    }

    protected static IconWidget createRealmsLogoIconWidget() {
        return IconWidget.create(128, 34, REALMS_LOGO_TEXTURE, 128, 64);
    }
}

