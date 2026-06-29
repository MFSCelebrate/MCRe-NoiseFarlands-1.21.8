/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.BackupPromptScreen;
import net.minecraft.client.gui.screen.world.OptimizeWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.nbt.NbtCrashException;
import net.minecraft.nbt.NbtException;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.path.PathUtil;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class EditWorldScreen
extends Screen {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private Text ENTER_NAME_TEXT = Text.translatable("selectWorld.enterName").formatted(Formatting.GRAY);
    final static private Text RESET_ICON_TEXT = Text.translatable("selectWorld.edit.resetIcon");
    final static private Text OPEN_FOLDER_TEXT = Text.translatable("selectWorld.edit.openFolder");
    final static private Text BACKUP_TEXT = Text.translatable("selectWorld.edit.backup");
    final static private Text BACKUP_FOLDER_TEXT = Text.translatable("selectWorld.edit.backupFolder");
    final static private Text OPTIMIZE_TEXT = Text.translatable("selectWorld.edit.optimize");
    final static private Text CONFIRM_TITLE_TEXT = Text.translatable("optimizeWorld.confirm.title");
    final static private Text CONFIRM_DESCRIPTION_TEXT = Text.translatable("optimizeWorld.confirm.description");
    final static private Text CONFIRM_PROCEED_TEXT = Text.translatable("optimizeWorld.confirm.proceed");
    final static private Text SAVE_TEXT = Text.translatable("selectWorld.edit.save");
    final static private int field_46893 = 200;
    final static private int field_46894 = 4;
    final static private int field_46895 = 98;
    final private DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical().spacing(5);
    final private BooleanConsumer callback;
    final private LevelStorage.Session storageSession;
    final private TextFieldWidget nameFieldWidget;

    public static EditWorldScreen create(MinecraftClient client, LevelStorage.Session session, BooleanConsumer callback) throws IOException {
        LevelSummary levelSummary = session.getLevelSummary(session.readLevelProperties());
        return new EditWorldScreen(client, session, levelSummary.getDisplayName(), callback);
    }

    private EditWorldScreen(MinecraftClient client, LevelStorage.Session session, String levelName, BooleanConsumer callback) {
        super(Text.translatable("selectWorld.edit.title"));
        this.callback = callback;
        this.storageSession = session;
        TextRenderer textRenderer = client.textRenderer;
        this.layout.add(new EmptyWidget(200, 20));
        this.layout.add(new TextWidget(ENTER_NAME_TEXT, textRenderer));
        this.nameFieldWidget = this.layout.add(new TextFieldWidget(textRenderer, 200, 20, ENTER_NAME_TEXT));
        this.nameFieldWidget.setText(levelName);
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(4);
        ButtonWidget buttonWidget = directionalLayoutWidget.add(ButtonWidget.builder(SAVE_TEXT, button -> this.commit(this.nameFieldWidget.getText())).width(98).build());
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).width(98).build());
        this.nameFieldWidget.setChangedListener(name -> {
            buttonWidget.active = !StringHelper.isBlank(name);
        });
        this.layout.add(ButtonWidget.builder(EditWorldScreen.RESET_ICON_TEXT, button -> EditWorldScreen.method_54608(session, button)).width(200).build()).active = session.getIconFile().filter(path -> Files.isRegularFile(path, new LinkOption[0])).isPresent();
        this.layout.add(ButtonWidget.builder(OPEN_FOLDER_TEXT, button -> Util.getOperatingSystem().open(session.getDirectory(WorldSavePath.ROOT))).width(200).build());
        this.layout.add(ButtonWidget.builder(BACKUP_TEXT, button -> {
            boolean bl = EditWorldScreen.backupLevel(session);
            this.callback.accept(!bl);
        }).width(200).build());
        this.layout.add(ButtonWidget.builder(BACKUP_FOLDER_TEXT, button -> {
            LevelStorage levelStorage = client.getLevelStorage();
            Path path = levelStorage.getBackupsDirectory();
            try {
                PathUtil.createDirectories(path);
            }
            catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
            Util.getOperatingSystem().open(path);
        }).width(200).build());
        this.layout.add(ButtonWidget.builder(OPTIMIZE_TEXT, button -> client.setScreen(new BackupPromptScreen(() -> client.setScreen(this), (backup, eraseCache) -> {
            if (backup) {
                EditWorldScreen.backupLevel(session);
            }
            client.setScreen(OptimizeWorldScreen.create(client, this.callback, client.getDataFixer(), session, eraseCache));
        }, CONFIRM_TITLE_TEXT, CONFIRM_DESCRIPTION_TEXT, CONFIRM_PROCEED_TEXT, true))).width(200).build());
        this.layout.add(new EmptyWidget(200, 20));
        this.layout.add(directionalLayoutWidget);
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.nameFieldWidget);
    }

    @Override
    protected void init() {
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
    }

    @Override
    public void close() {
        this.callback.accept(false);
    }

    private void commit(String levelName) {
        try {
            this.storageSession.save(levelName);
        }
        catch (IOException | NbtCrashException | NbtException exception) {
            LOGGER.error("Failed to access world '{}'", (Object)this.storageSession.getDirectoryName(), (Object)exception);
            SystemToast.addWorldAccessFailureToast(this.client, this.storageSession.getDirectoryName());
        }
        this.callback.accept(true);
    }

    public static boolean backupLevel(LevelStorage.Session storageSession) {
        long l = 0L;
        IOException iOException = null;
        try {
            l = storageSession.createBackup();
        }
        catch (IOException iOException2) {
            iOException = iOException2;
        }
        if (iOException != null) {
            MutableText text = Text.translatable("selectWorld.edit.backupFailed");
            MutableText text2 = Text.literal(iOException.getMessage());
            MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.WORLD_BACKUP, text, text2));
            return false;
        }
        MutableText text = Text.translatable("selectWorld.edit.backupCreated", storageSession.getDirectoryName());
        MutableText text2 = Text.translatable("selectWorld.edit.backupSize", MathHelper.ceil((double)l / 1048576.0));
        MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.WORLD_BACKUP, text, text2));
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, Colors.WHITE);
    }

    private static void method_54608(LevelStorage.Session session, ButtonWidget buttonWidget) {
        session.getIconFile().ifPresent(path -> FileUtils.deleteQuietly((File)path.toFile()));
        buttonWidget.active = false;
    }
}

