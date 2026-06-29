/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.glfw.GLFW
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.GameModeSwitcherScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.util.Clipboard;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.c2s.play.ChangeGameModeC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.VersionCommand;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.WinNativeModuleUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class Keyboard {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public int DEBUG_CRASH_TIME = 10000;
    final private MinecraftClient client;
    final private Clipboard clipboard = new Clipboard();
    private long debugCrashStartTime = -1L;
    private long debugCrashLastLogTime = -1L;
    private long debugCrashElapsedTime = -1L;
    private boolean switchF3State;

    public Keyboard(MinecraftClient client) {
        this.client = client;
    }

    private boolean processDebugKeys(int key) {
        switch (key) {
            case 69: {
                this.client.debugChunkInfo = !this.client.debugChunkInfo;
                this.debugFormattedLog("SectionPath: {0}", this.client.debugChunkInfo ? "shown" : "hidden");
                return true;
            }
            case 76: {
                this.client.chunkCullingEnabled = !this.client.chunkCullingEnabled;
                this.debugFormattedLog("SmartCull: {0}", this.client.chunkCullingEnabled ? "enabled" : "disabled");
                return true;
            }
            case 79: {
                boolean bl = this.client.debugRenderer.toggleShowOctree();
                this.debugFormattedLog("Frustum culling Octree: {0}", bl ? "enabled" : "disabled");
                return true;
            }
            case 70: {
                boolean bl2 = FogRenderer.toggleFog();
                this.debugFormattedLog("Fog: {0}", bl2 ? "enabled" : "disabled");
                return true;
            }
            case 85: {
                if (Screen.hasShiftDown()) {
                    this.client.worldRenderer.killFrustum();
                    this.debugFormattedLog("Killed frustum", new Object[0]);
                } else {
                    this.client.worldRenderer.captureFrustum();
                    this.debugFormattedLog("Captured frustum", new Object[0]);
                }
                return true;
            }
            case 86: {
                this.client.debugChunkOcclusion = !this.client.debugChunkOcclusion;
                this.debugFormattedLog("SectionVisibility: {0}", this.client.debugChunkOcclusion ? "enabled" : "disabled");
                return true;
            }
            case 87: {
                this.client.wireFrame = !this.client.wireFrame;
                this.debugFormattedLog("WireFrame: {0}", this.client.wireFrame ? "enabled" : "disabled");
                return true;
            }
        }
        return false;
    }

    private void sendMessage(Text message) {
        this.client.inGameHud.getChatHud().addMessage(message);
        this.client.getNarratorManager().narrateSystemMessage(message);
    }

    private static Text getDebugMessage(Formatting formatting, Text message) {
        return Text.empty().append(Text.translatable("debug.prefix").formatted(formatting, Formatting.BOLD)).append(ScreenTexts.SPACE).append(message);
    }

    private void debugError(Text message) {
        this.sendMessage(Keyboard.getDebugMessage(Formatting.RED, message));
    }

    private void debugLog(Text text) {
        this.sendMessage(Keyboard.getDebugMessage(Formatting.YELLOW, text));
    }

    private void debugLog(String key) {
        this.debugLog(Text.translatable(key));
    }

    private void debugFormattedLog(String pattern, Object ... args) {
        this.debugLog(Text.literal(MessageFormat.format(pattern, args)));
    }

    private boolean processF3(int key) {
        if (this.debugCrashStartTime > 0L && this.debugCrashStartTime < Util.getMeasuringTimeMs() - 100L) {
            return true;
        }
        switch (key) {
            case 65: {
                this.client.worldRenderer.reload();
                this.debugLog("debug.reload_chunks.message");
                return true;
            }
            case 66: {
                boolean bl = !this.client.getEntityRenderDispatcher().shouldRenderHitboxes();
                this.client.getEntityRenderDispatcher().setRenderHitboxes(bl);
                this.debugLog(bl ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
                return true;
            }
            case 68: {
                if (this.client.inGameHud != null) {
                    this.client.inGameHud.getChatHud().clear(false);
                }
                return true;
            }
            case 71: {
                boolean bl2 = this.client.debugRenderer.toggleShowChunkBorder();
                this.debugLog(bl2 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
                return true;
            }
            case 72: {
                this.client.options.advancedItemTooltips = !this.client.options.advancedItemTooltips;
                this.debugLog(this.client.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
                this.client.options.write();
                return true;
            }
            case 73: {
                if (!this.client.player.hasReducedDebugInfo()) {
                    this.copyLookAt(this.client.player.hasPermissionLevel(2), !Screen.hasShiftDown());
                }
                return true;
            }
            case 78: {
                if (!this.client.player.hasPermissionLevel(2)) {
                    this.debugLog("debug.creative_spectator.error");
                } else if (!this.client.player.isSpectator()) {
                    this.client.player.networkHandler.sendPacket(new ChangeGameModeC2SPacket(GameMode.SPECTATOR));
                } else {
                    GameMode gameMode = (GameMode)MoreObjects.firstNonNull((Object)this.client.interactionManager.getPreviousGameMode(), (Object)GameMode.CREATIVE);
                    this.client.player.networkHandler.sendPacket(new ChangeGameModeC2SPacket(gameMode));
                }
                return true;
            }
            case 293: {
                if (!this.client.player.hasPermissionLevel(2)) {
                    this.debugLog("debug.gamemodes.error");
                } else {
                    this.client.setScreen(new GameModeSwitcherScreen());
                }
                return true;
            }
            case 80: {
                this.client.options.pauseOnLostFocus = !this.client.options.pauseOnLostFocus;
                this.client.options.write();
                this.debugLog(this.client.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
                return true;
            }
            case 81: {
                this.debugLog("debug.help.message");
                this.sendMessage(Text.translatable("debug.reload_chunks.help"));
                this.sendMessage(Text.translatable("debug.show_hitboxes.help"));
                this.sendMessage(Text.translatable("debug.copy_location.help"));
                this.sendMessage(Text.translatable("debug.clear_chat.help"));
                this.sendMessage(Text.translatable("debug.chunk_boundaries.help"));
                this.sendMessage(Text.translatable("debug.advanced_tooltips.help"));
                this.sendMessage(Text.translatable("debug.inspect.help"));
                this.sendMessage(Text.translatable("debug.profiling.help"));
                this.sendMessage(Text.translatable("debug.creative_spectator.help"));
                this.sendMessage(Text.translatable("debug.pause_focus.help"));
                this.sendMessage(Text.translatable("debug.help.help"));
                this.sendMessage(Text.translatable("debug.dump_dynamic_textures.help"));
                this.sendMessage(Text.translatable("debug.reload_resourcepacks.help"));
                this.sendMessage(Text.translatable("debug.version.help"));
                this.sendMessage(Text.translatable("debug.pause.help"));
                this.sendMessage(Text.translatable("debug.gamemodes.help"));
                return true;
            }
            case 83: {
                Path path = this.client.runDirectory.toPath().toAbsolutePath();
                Path path2 = TextureUtil.getDebugTexturePath(path);
                this.client.getTextureManager().dumpDynamicTextures(path2);
                MutableText text = Text.literal(path.relativize(path2).toString()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent.OpenFile(path2)));
                this.debugLog(Text.translatable("debug.dump_dynamic_textures", text));
                return true;
            }
            case 84: {
                this.debugLog("debug.reload_resourcepacks.message");
                this.client.reloadResources();
                return true;
            }
            case 76: {
                if (this.client.toggleDebugProfiler(this::debugLog)) {
                    this.debugLog(Text.translatable("debug.profiling.start", 10));
                }
                return true;
            }
            case 67: {
                if (this.client.player.hasReducedDebugInfo()) {
                    return false;
                }
                ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
                if (clientPlayNetworkHandler == null) {
                    return false;
                }
                this.debugLog("debug.copy_location.message");
                this.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", this.client.player.net_minecraft_world_World_getWorld().getRegistryKey().getValue(), this.client.player.getX(), this.client.player.getY(), this.client.player.getZ(), Float.valueOf(this.client.player.getYaw()), Float.valueOf(this.client.player.getPitch())));
                return true;
            }
            case 86: {
                this.debugLog("debug.version.header");
                VersionCommand.acceptInfo(this::sendMessage);
                return true;
            }
            case 49: {
                this.client.getDebugHud().toggleRenderingChart();
                return true;
            }
            case 50: {
                this.client.getDebugHud().toggleRenderingAndTickCharts();
                return true;
            }
            case 51: {
                this.client.getDebugHud().togglePacketSizeAndPingCharts();
                return true;
            }
        }
        return false;
    }

    private void copyLookAt(boolean hasQueryPermission, boolean queryServer) {
        HitResult hitResult = this.client.crosshairTarget;
        if (hitResult == null) {
            return;
        }
        switch (hitResult.getType()) {
            case BLOCK: {
                BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
                World world = this.client.player.net_minecraft_world_World_getWorld();
                BlockState blockState = world.getBlockState(blockPos);
                if (hasQueryPermission) {
                    if (queryServer) {
                        this.client.player.networkHandler.getDataQueryHandler().queryBlockNbt(blockPos, nbt -> {
                            this.copyBlock(blockState, blockPos, (NbtCompound)nbt);
                            this.debugLog("debug.inspect.server.block");
                        });
                        break;
                    }
                    BlockEntity blockEntity = world.getBlockEntity(blockPos);
                    NbtCompound nbtCompound = blockEntity != null ? blockEntity.createNbt(world.getRegistryManager()) : null;
                    this.copyBlock(blockState, blockPos, nbtCompound);
                    this.debugLog("debug.inspect.client.block");
                    break;
                }
                this.copyBlock(blockState, blockPos, null);
                this.debugLog("debug.inspect.client.block");
                break;
            }
            case ENTITY: {
                Entity entity = ((EntityHitResult)hitResult).getEntity();
                Identifier identifier = Registries.ENTITY_TYPE.getId(entity.getType());
                if (hasQueryPermission) {
                    if (queryServer) {
                        this.client.player.networkHandler.getDataQueryHandler().queryEntityNbt(entity.getId(), nbt -> {
                            this.copyEntity(identifier, entity.getPos(), (NbtCompound)nbt);
                            this.debugLog("debug.inspect.server.entity");
                        });
                        break;
                    }
                    try (ErrorReporter.Logging logging = new ErrorReporter.Logging(entity.getErrorReporterContext(), LOGGER);){
                        NbtWriteView nbtWriteView = NbtWriteView.create(logging, entity.getRegistryManager());
                        entity.writeData(nbtWriteView);
                        this.copyEntity(identifier, entity.getPos(), nbtWriteView.getNbt());
                    }
                    this.debugLog("debug.inspect.client.entity");
                    break;
                }
                this.copyEntity(identifier, entity.getPos(), null);
                this.debugLog("debug.inspect.client.entity");
                break;
            }
        }
    }

    private void copyBlock(BlockState state, BlockPos pos, @Nullable NbtCompound nbt) {
        StringBuilder stringBuilder = new StringBuilder(BlockArgumentParser.stringifyBlockState(state));
        if (nbt != null) {
            stringBuilder.append(nbt);
        }
        String string = String.format(Locale.ROOT, "/setblock %d %d %d %s", pos.getX(), pos.getY(), pos.getZ(), stringBuilder);
        this.setClipboard(string);
    }

    private void copyEntity(Identifier id, Vec3d pos, @Nullable NbtCompound nbt) {
        String string2;
        if (nbt != null) {
            nbt.remove("UUID");
            nbt.remove("Pos");
            String string = NbtHelper.toPrettyPrintedText(nbt).getString();
            string2 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", id, pos.x, pos.y, pos.z, string);
        } else {
            string2 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", id, pos.x, pos.y, pos.z);
        }
        this.setClipboard(string2);
    }

    public void onKey(long window, int key, int scancode, int action, int modifiers) {
        GameMenuScreen gameMenuScreen;
        Screen screen;
        boolean bl4;
        Screen screen2;
        if (window != this.client.getWindow().getHandle()) {
            return;
        }
        this.client.getInactivityFpsLimiter().onInput();
        boolean bl = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_F3);
        if (this.debugCrashStartTime > 0L) {
            if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_C) || !bl) {
                this.debugCrashStartTime = -1L;
            }
        } else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_C) && bl) {
            this.switchF3State = true;
            this.debugCrashStartTime = Util.getMeasuringTimeMs();
            this.debugCrashLastLogTime = Util.getMeasuringTimeMs();
            this.debugCrashElapsedTime = 0L;
        }
        if ((screen2 = this.client.currentScreen) != null) {
            switch (key) {
                case 262: 
                case 263: 
                case 264: 
                case 265: {
                    this.client.setNavigationType(GuiNavigationType.KEYBOARD_ARROW);
                    break;
                }
                case 258: {
                    this.client.setNavigationType(GuiNavigationType.KEYBOARD_TAB);
                }
            }
        }
        if (!(action != 1 || this.client.currentScreen instanceof KeybindsScreen && ((KeybindsScreen)screen2).lastKeyCodeUpdateTime > Util.getMeasuringTimeMs() - 20L)) {
            if (this.client.options.fullscreenKey.matchesKey(key, scancode)) {
                this.client.getWindow().toggleFullscreen();
                boolean bl2 = this.client.getWindow().isFullscreen();
                this.client.options.getFullscreen().setValue(bl2);
                this.client.options.write();
                Screen screen3 = this.client.currentScreen;
                if (screen3 instanceof VideoOptionsScreen) {
                    VideoOptionsScreen videoOptionsScreen = (VideoOptionsScreen)screen3;
                    videoOptionsScreen.updateFullscreenButtonValue(bl2);
                }
                return;
            }
            if (this.client.options.screenshotKey.matchesKey(key, scancode)) {
                if (Screen.hasControlDown()) {
                    // empty if block
                }
                ScreenshotRecorder.saveScreenshot(this.client.runDirectory, this.client.getFramebuffer(), message -> this.client.execute(() -> this.sendMessage((Text)message)));
                return;
            }
        }
        if (action != 0) {
            boolean bl2;
            boolean bl3 = bl2 = screen2 == null || !(screen2.getFocused() instanceof TextFieldWidget) || !((TextFieldWidget)screen2.getFocused()).isActive();
            if (bl2) {
                if (Screen.hasControlDown() && key == GLFW.GLFW_KEY_B && this.client.getNarratorManager().isActive() && this.client.options.getNarratorHotkey().getValue().booleanValue()) {
                    boolean bl32 = this.client.options.getNarrator().getValue() == NarratorMode.OFF;
                    this.client.options.getNarrator().setValue(NarratorMode.byId(this.client.options.getNarrator().getValue().getId() + 1));
                    this.client.options.write();
                    if (screen2 != null) {
                        screen2.refreshNarrator(bl32);
                    }
                }
                ClientPlayerEntity bl32 = this.client.player;
            }
        }
        if (screen2 != null) {
            try {
                if (action == 1 || action == 2) {
                    screen2.applyKeyPressNarratorDelay();
                    if (screen2.keyPressed(key, scancode, modifiers)) {
                        return;
                    }
                } else if (action == 0 && screen2.keyReleased(key, scancode, modifiers)) {
                    return;
                }
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "keyPressed event handler");
                screen2.addCrashReportSection(crashReport);
                CrashReportSection crashReportSection = crashReport.addElement("Key");
                crashReportSection.add("Key", key);
                crashReportSection.add("Scancode", scancode);
                crashReportSection.add("Mods", modifiers);
                throw new CrashException(crashReport);
            }
        }
        InputUtil.Key key2 = InputUtil.fromKeyCode(key, scancode);
        boolean bl3 = this.client.currentScreen == null;
        boolean bl5 = bl4 = bl3 || (screen = this.client.currentScreen) instanceof GameMenuScreen && !(gameMenuScreen = (GameMenuScreen)screen).shouldShowMenu();
        if (action == 0) {
            KeyBinding.setKeyPressed(key2, false);
            if (bl4 && key == GLFW.GLFW_KEY_F3) {
                if (this.switchF3State) {
                    this.switchF3State = false;
                } else {
                    this.client.getDebugHud().toggleDebugHud();
                }
            }
            return;
        }
        boolean bl52 = false;
        if (bl4) {
            if (key == GLFW.GLFW_KEY_F4 && this.client.gameRenderer != null) {
                this.client.gameRenderer.togglePostProcessorEnabled();
            }
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                this.client.openGameMenu(bl);
                bl52 |= bl;
            }
            this.switchF3State |= (bl52 |= bl && this.processF3(key));
            if (key == GLFW.GLFW_KEY_F1) {
                boolean bl6 = this.client.options.hudHidden = !this.client.options.hudHidden;
            }
            if (this.client.getDebugHud().shouldShowRenderingChart() && !bl && key >= GLFW.GLFW_KEY_0 && key <= GLFW.GLFW_KEY_9) {
                this.client.getDebugHud().getPieChart().select(key - GLFW.GLFW_KEY_0);
            }
        }
        if (bl3) {
            if (bl52) {
                KeyBinding.setKeyPressed(key2, false);
            } else {
                KeyBinding.setKeyPressed(key2, true);
                KeyBinding.onKeyPressed(key2);
            }
        }
    }

    private void onChar(long window, int codePoint, int modifiers) {
        if (window != this.client.getWindow().getHandle()) {
            return;
        }
        Screen screen = this.client.currentScreen;
        if (screen == null || this.client.getOverlay() != null) {
            return;
        }
        try {
            if (Character.isBmpCodePoint(codePoint)) {
                screen.charTyped((char)codePoint, modifiers);
            } else if (Character.isValidCodePoint(codePoint)) {
                screen.charTyped(Character.highSurrogate(codePoint), modifiers);
                screen.charTyped(Character.lowSurrogate(codePoint), modifiers);
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "charTyped event handler");
            screen.addCrashReportSection(crashReport);
            CrashReportSection crashReportSection = crashReport.addElement("Key");
            crashReportSection.add("Codepoint", codePoint);
            crashReportSection.add("Mods", modifiers);
            throw new CrashException(crashReport);
        }
    }

    public void setup(long window2) {
        InputUtil.setKeyboardCallbacks(window2, (window, key, scancode, action, modifiers) -> this.client.execute(() -> this.onKey(window, key, scancode, action, modifiers)), (window, codePoint, modifiers) -> this.client.execute(() -> this.onChar(window, codePoint, modifiers)));
    }

    public String getClipboard() {
        return this.clipboard.getClipboard(this.client.getWindow().getHandle(), (error, description) -> {
            if (error != 65545) {
                this.client.getWindow().logGlError(error, description);
            }
        });
    }

    public void setClipboard(String clipboard) {
        if (!clipboard.isEmpty()) {
            this.clipboard.setClipboard(this.client.getWindow().getHandle(), clipboard);
        }
    }

    public void pollDebugCrash() {
        if (this.debugCrashStartTime > 0L) {
            long l = Util.getMeasuringTimeMs();
            long m = 10000L - (l - this.debugCrashStartTime);
            long n = l - this.debugCrashLastLogTime;
            if (m < 0L) {
                if (Screen.hasControlDown()) {
                    GlfwUtil.makeJvmCrash();
                }
                String string = "Manually triggered debug crash";
                CrashReport crashReport = new CrashReport("Manually triggered debug crash", new Throwable("Manually triggered debug crash"));
                CrashReportSection crashReportSection = crashReport.addElement("Manual crash details");
                WinNativeModuleUtil.addDetailTo(crashReportSection);
                throw new CrashException(crashReport);
            }
            if (n >= 1000L) {
                if (this.debugCrashElapsedTime == 0L) {
                    this.debugLog("debug.crash.message");
                } else {
                    this.debugError(Text.translatable("debug.crash.warning", MathHelper.ceil((float)m / 1000.0f)));
                }
                this.debugCrashLastLogTime = l;
                ++this.debugCrashElapsedTime;
            }
        }
    }
}

