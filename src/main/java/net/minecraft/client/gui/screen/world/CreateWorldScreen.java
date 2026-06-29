/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.glfw.GLFW
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.ExperimentalWarningScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.CreateWorldCallback;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.screen.world.ExperimentsScreen;
import net.minecraft.client.gui.screen.world.InitialWorldOptions;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.gui.screen.world.WorldCreationSettings;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.screen.world.WorldScreenOptionGrid;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.world.GeneratorOptionsFactory;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.path.PathUtil;
import net.minecraft.util.path.SymlinkFinder;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.FlatLevelGeneratorPresets;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class CreateWorldScreen
extends Screen {
    final static private int field_42165 = 1;
    final static private int field_42166 = 210;
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private String TEMP_DIR_PREFIX = "mcworld-";
    final static Text GAME_MODE_TEXT = Text.translatable("selectWorld.gameMode");
    final static Text ENTER_NAME_TEXT = Text.translatable("selectWorld.enterName");
    final static Text EXPERIMENTS_TEXT = Text.translatable("selectWorld.experiments");
    final static Text ALLOW_COMMANDS_INFO_TEXT = Text.translatable("selectWorld.allowCommands.info");
    final static private Text PREPARING_TEXT = Text.translatable("createWorld.preparing");
    final static private int field_42170 = 10;
    final static private int field_42171 = 8;
    final static public Identifier TAB_HEADER_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/tab_header_background.png");
    final private ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    final WorldCreator worldCreator;
    final private TabManager tabManager = new TabManager(element -> {
        ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(element);
    }, child -> this.remove((Element)child));
    private boolean recreated;
    final private SymlinkFinder symlinkFinder;
    final private CreateWorldCallback callback;
    @Nullable
    final private Screen parent;
    @Nullable
    private Path dataPackTempDir;
    @Nullable
    private ResourcePackManager packManager;
    @Nullable
    private TabNavigationWidget tabNavigation;

    public static void show(MinecraftClient client, @Nullable Screen parent) {
        CreateWorldScreen.show(client, parent, (screen, combinedDynamicRegistries, levelProperties, dataPackTempDir) -> screen.startServer(combinedDynamicRegistries, levelProperties));
    }

    public static void show(MinecraftClient client, @Nullable Screen parent, CreateWorldCallback callback) {
        GeneratorOptionsFactory generatorOptionsFactory = (dataPackContents, dynamicRegistries, settings) -> new GeneratorOptionsHolder(settings.worldGenSettings(), dynamicRegistries, dataPackContents, settings.dataConfiguration());
        Function<SaveLoading.LoadContextSupplierContext, WorldGenSettings> function = context -> new WorldGenSettings(GeneratorOptions.createRandom(), WorldPresets.createDemoOptions(context.worldGenRegistryManager()));
        CreateWorldScreen.show(client, parent, function, generatorOptionsFactory, WorldPresets.DEFAULT, callback);
    }

    public static void showTestWorld(MinecraftClient client, @Nullable Screen parent) {
        GeneratorOptionsFactory generatorOptionsFactory = (dataPackContents, dynamicRegistries, settings) -> new GeneratorOptionsHolder(settings.worldGenSettings().generatorOptions(), settings.worldGenSettings().dimensionOptionsRegistryHolder(), dynamicRegistries, dataPackContents, settings.dataConfiguration(), new InitialWorldOptions(WorldCreator.Mode.CREATIVE, Set.of(GameRules.DO_DAYLIGHT_CYCLE, GameRules.DO_WEATHER_CYCLE, GameRules.DO_MOB_SPAWNING), FlatLevelGeneratorPresets.REDSTONE_READY));
        Function<SaveLoading.LoadContextSupplierContext, WorldGenSettings> function = context -> new WorldGenSettings(GeneratorOptions.createTestWorld(), WorldPresets.createTestOptions(context.worldGenRegistryManager()));
        CreateWorldScreen.show(client, parent, function, generatorOptionsFactory, WorldPresets.FLAT, (screen, combinedDynamicRegistries, levelProperties, dataPackTempDir) -> screen.startServer(combinedDynamicRegistries, levelProperties));
    }

    private static void show(MinecraftClient client, @Nullable Screen parent, Function<SaveLoading.LoadContextSupplierContext, WorldGenSettings> settingsSupplier, GeneratorOptionsFactory generatorOptionsFactory, RegistryKey<WorldPreset> presetKey, CreateWorldCallback callback) {
        CreateWorldScreen.showMessage(client, PREPARING_TEXT);
        ResourcePackManager resourcePackManager = new ResourcePackManager(new VanillaDataPackProvider(client.getSymlinkFinder()));
        DataConfiguration dataConfiguration = SharedConstants.isDevelopment ? new DataConfiguration(new DataPackSettings(List.of("vanilla", "tests"), List.of()), FeatureFlags.DEFAULT_ENABLED_FEATURES) : DataConfiguration.SAFE_MODE;
        SaveLoading.ServerConfig serverConfig = CreateWorldScreen.createServerConfig(resourcePackManager, dataConfiguration);
        CompletableFuture<GeneratorOptionsHolder> completableFuture = SaveLoading.load(serverConfig, context -> new SaveLoading.LoadContext<WorldCreationSettings>(new WorldCreationSettings((WorldGenSettings)settingsSupplier.apply(context), context.dataConfiguration()), context.dimensionsRegistryManager()), (resourceManager, dataPackContents, dynamicRegistries, settings) -> {
            resourceManager.close();
            return generatorOptionsFactory.apply(dataPackContents, dynamicRegistries, (WorldCreationSettings)settings);
        }, Util.getMainWorkerExecutor(), client);
        client.runTasks(completableFuture::isDone);
        client.setScreen(new CreateWorldScreen(client, parent, completableFuture.join(), Optional.of(presetKey), OptionalLong.empty(), callback));
    }

    public static CreateWorldScreen create(MinecraftClient client, @Nullable Screen parent, LevelInfo levelInfo, GeneratorOptionsHolder generatorOptionsHolder, @Nullable Path dataPackTempDir2) {
        CreateWorldScreen createWorldScreen = new CreateWorldScreen(client, parent, generatorOptionsHolder, WorldPresets.getWorldPreset(generatorOptionsHolder.selectedDimensions()), OptionalLong.of(generatorOptionsHolder.generatorOptions().getSeed()), (screen, combinedDynamicRegistries, levelProperties, dataPackTempDir) -> screen.startServer(combinedDynamicRegistries, levelProperties));
        createWorldScreen.recreated = true;
        createWorldScreen.worldCreator.setWorldName(levelInfo.getLevelName());
        createWorldScreen.worldCreator.setCheatsEnabled(levelInfo.areCommandsAllowed());
        createWorldScreen.worldCreator.setDifficulty(levelInfo.getDifficulty());
        createWorldScreen.worldCreator.getGameRules().setAllValues(levelInfo.getGameRules(), null);
        if (levelInfo.isHardcore()) {
            createWorldScreen.worldCreator.setGameMode(WorldCreator.Mode.HARDCORE);
        } else if (levelInfo.getGameMode().isSurvivalLike()) {
            createWorldScreen.worldCreator.setGameMode(WorldCreator.Mode.SURVIVAL);
        } else if (levelInfo.getGameMode().isCreative()) {
            createWorldScreen.worldCreator.setGameMode(WorldCreator.Mode.CREATIVE);
        }
        createWorldScreen.dataPackTempDir = dataPackTempDir2;
        return createWorldScreen;
    }

    private CreateWorldScreen(MinecraftClient client, @Nullable Screen parent, GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> defaultWorldType, OptionalLong seed, CreateWorldCallback callback) {
        super(Text.translatable("selectWorld.create"));
        this.parent = parent;
        this.symlinkFinder = client.getSymlinkFinder();
        this.callback = callback;
        this.worldCreator = new WorldCreator(client.getLevelStorage().getSavesDirectory(), generatorOptionsHolder, defaultWorldType, seed);
    }

    public WorldCreator getWorldCreator() {
        return this.worldCreator;
    }

    @Override
    protected void init() {
        this.tabNavigation = TabNavigationWidget.builder(this.tabManager, this.width).tabs(new GameTab(), new WorldTab(), new MoreTab()).build();
        this.addDrawableChild(this.tabNavigation);
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget.add(ButtonWidget.builder(Text.translatable("selectWorld.create"), button -> this.createLevel()).build());
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.onCloseScreen()).build());
        this.layout.forEachChild(child -> {
            child.setNavigationOrder(1);
            this.addDrawableChild(child);
        });
        this.tabNavigation.selectTab(0, false);
        this.worldCreator.update();
        this.refreshWidgetPositions();
    }

    @Override
    protected void setInitialFocus() {
    }

    @Override
    public void refreshWidgetPositions() {
        if (this.tabNavigation == null) {
            return;
        }
        this.tabNavigation.setWidth(this.width);
        this.tabNavigation.init();
        int i = this.tabNavigation.getNavigationFocus().getBottom();
        ScreenRect screenRect = new ScreenRect(0, i, this.width, this.height - this.layout.getFooterHeight() - i);
        this.tabManager.setTabArea(screenRect);
        this.layout.setHeaderHeight(i);
        this.layout.refreshPositions();
    }

    private static void showMessage(MinecraftClient client, Text text) {
        client.setScreenAndRender(new MessageScreen(text));
    }

    private void createLevel() {
        GeneratorOptionsHolder generatorOptionsHolder = this.worldCreator.getGeneratorOptionsHolder();
        DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = generatorOptionsHolder.selectedDimensions().toConfig(generatorOptionsHolder.dimensionOptionsRegistry());
        CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries = generatorOptionsHolder.combinedDynamicRegistries().with(ServerDynamicRegistryType.DIMENSIONS, dimensionsConfig.toDynamicRegistryManager());
        Lifecycle lifecycle = FeatureFlags.isNotVanilla(generatorOptionsHolder.dataConfiguration().enabledFeatures()) ? Lifecycle.experimental() : Lifecycle.stable();
        Lifecycle lifecycle2 = combinedDynamicRegistries.getCombinedRegistryManager().getLifecycle();
        Lifecycle lifecycle3 = lifecycle2.add(lifecycle);
        boolean bl = !this.recreated && lifecycle2 == Lifecycle.stable();
        LevelInfo levelInfo = this.createLevelInfo(dimensionsConfig.specialWorldProperty() == LevelProperties.SpecialProperty.DEBUG);
        LevelProperties levelProperties = new LevelProperties(levelInfo, this.worldCreator.getGeneratorOptionsHolder().generatorOptions(), dimensionsConfig.specialWorldProperty(), lifecycle3);
        IntegratedServerLoader.tryLoad(this.client, this, lifecycle3, () -> this.createAndClearTempDir(combinedDynamicRegistries, levelProperties), bl);
    }

    private void createAndClearTempDir(CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, LevelProperties levelProperties) {
        boolean bl = this.callback.create(this, combinedDynamicRegistries, levelProperties, this.dataPackTempDir);
        this.clearDataPackTempDir();
        if (!bl) {
            this.onCloseScreen();
        }
    }

    private boolean startServer(CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, SaveProperties saveProperties) {
        String string = this.worldCreator.getWorldDirectoryName();
        GeneratorOptionsHolder generatorOptionsHolder = this.worldCreator.getGeneratorOptionsHolder();
        CreateWorldScreen.showMessage(this.client, PREPARING_TEXT);
        Optional<LevelStorage.Session> optional = CreateWorldScreen.createSession(this.client, string, this.dataPackTempDir);
        if (optional.isEmpty()) {
            SystemToast.addPackCopyFailure(this.client, string);
            return false;
        }
        this.client.createIntegratedServerLoader().startNewWorld(optional.get(), generatorOptionsHolder.dataPackContents(), combinedDynamicRegistries, saveProperties);
        return true;
    }

    private LevelInfo createLevelInfo(boolean debugWorld) {
        String string = this.worldCreator.getWorldName().trim();
        if (debugWorld) {
            GameRules gameRules = new GameRules(DataConfiguration.SAFE_MODE.enabledFeatures());
            gameRules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
            return new LevelInfo(string, GameMode.SPECTATOR, false, Difficulty.PEACEFUL, true, gameRules, DataConfiguration.SAFE_MODE);
        }
        return new LevelInfo(string, this.worldCreator.getGameMode().defaultGameMode, this.worldCreator.isHardcore(), this.worldCreator.getDifficulty(), this.worldCreator.areCheatsEnabled(), this.worldCreator.getGameRules(), this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.tabNavigation.trySwitchTabsWithKey(keyCode)) {
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.createLevel();
            return true;
        }
        return false;
    }

    @Override
    public void close() {
        this.onCloseScreen();
    }

    public void onCloseScreen() {
        this.client.setScreen(this.parent);
        this.clearDataPackTempDir();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR_TEXTURE, 0, this.height - this.layout.getFooterHeight() - 2, 0.0f, 0.0f, this.width, 2, 32, 2);
    }

    @Override
    protected void renderDarkening(DrawContext context) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TAB_HEADER_BACKGROUND_TEXTURE, 0, 0, 0.0f, 0.0f, this.width, this.layout.getHeaderHeight(), 16, 16);
        this.renderDarkening(context, 0, this.layout.getHeaderHeight(), this.width, this.height);
    }

    @Nullable
    private Path getOrCreateDataPackTempDir() {
        if (this.dataPackTempDir == null) {
            try {
                this.dataPackTempDir = Files.createTempDirectory(TEMP_DIR_PREFIX, new FileAttribute[0]);
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to create temporary dir", (Throwable)iOException);
                SystemToast.addPackCopyFailure(this.client, this.worldCreator.getWorldDirectoryName());
                this.onCloseScreen();
            }
        }
        return this.dataPackTempDir;
    }

    void openExperimentsScreen(DataConfiguration dataConfiguration) {
        Pair<Path, ResourcePackManager> pair = this.getScannedPack(dataConfiguration);
        if (pair != null) {
            this.client.setScreen(new ExperimentsScreen(this, (ResourcePackManager)pair.getSecond(), resourcePackManager -> this.applyDataPacks((ResourcePackManager)resourcePackManager, false, this::openExperimentsScreen)));
        }
    }

    void openPackScreen(DataConfiguration dataConfiguration) {
        Pair<Path, ResourcePackManager> pair = this.getScannedPack(dataConfiguration);
        if (pair != null) {
            this.client.setScreen(new PackScreen((ResourcePackManager)pair.getSecond(), resourcePackManager -> this.applyDataPacks((ResourcePackManager)resourcePackManager, true, this::openPackScreen), (Path)pair.getFirst(), Text.translatable("dataPack.title")));
        }
    }

    private void applyDataPacks(ResourcePackManager dataPackManager, boolean fromPackScreen, Consumer<DataConfiguration> configurationSetter) {
        List list2;
        ImmutableList list = ImmutableList.copyOf(dataPackManager.getEnabledIds());
        DataConfiguration dataConfiguration = new DataConfiguration(new DataPackSettings((List<String>)list, list2 = (List)dataPackManager.getIds().stream().filter(arg_0 -> CreateWorldScreen.method_29983((List)list, arg_0)).collect(ImmutableList.toImmutableList())), this.worldCreator.getGeneratorOptionsHolder().dataConfiguration().enabledFeatures());
        if (this.worldCreator.updateDataConfiguration(dataConfiguration)) {
            this.client.setScreen(this);
            return;
        }
        FeatureSet featureSet = dataPackManager.getRequestedFeatures();
        if (FeatureFlags.isNotVanilla(featureSet) && fromPackScreen) {
            this.client.setScreen(new ExperimentalWarningScreen(dataPackManager.getEnabledProfiles(), confirmed -> {
                if (confirmed) {
                    this.validateDataPacks(dataPackManager, dataConfiguration, configurationSetter);
                } else {
                    configurationSetter.accept(this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
                }
            }));
        } else {
            this.validateDataPacks(dataPackManager, dataConfiguration, configurationSetter);
        }
    }

    private void validateDataPacks(ResourcePackManager dataPackManager, DataConfiguration dataConfiguration, Consumer<DataConfiguration> configurationSetter) {
        this.client.setScreenAndRender(new MessageScreen(Text.translatable("dataPack.validation.working")));
        SaveLoading.ServerConfig serverConfig = CreateWorldScreen.createServerConfig(dataPackManager, dataConfiguration);
        ((CompletableFuture)((CompletableFuture)SaveLoading.load(serverConfig, context -> {
            if (context.worldGenRegistryManager().net_minecraft_registry_RegistryEntryLookup_getOrThrow(RegistryKeys.WORLD_PRESET).streamEntries().findAny().isEmpty()) {
                throw new IllegalStateException("Needs at least one world preset to continue");
            }
            if (context.worldGenRegistryManager().net_minecraft_registry_RegistryEntryLookup_getOrThrow(RegistryKeys.BIOME).streamEntries().findAny().isEmpty()) {
                throw new IllegalStateException("Needs at least one biome continue");
            }
            GeneratorOptionsHolder generatorOptionsHolder = this.worldCreator.getGeneratorOptionsHolder();
            RegistryOps dynamicOps = generatorOptionsHolder.getCombinedRegistryManager().getOps(JsonOps.INSTANCE);
            DataResult dataResult = WorldGenSettings.encode(dynamicOps, generatorOptionsHolder.generatorOptions(), generatorOptionsHolder.selectedDimensions()).setLifecycle(Lifecycle.stable());
            RegistryOps dynamicOps2 = context.worldGenRegistryManager().getOps(JsonOps.INSTANCE);
            WorldGenSettings worldGenSettings = (WorldGenSettings)dataResult.flatMap(json -> WorldGenSettings.CODEC.parse(dynamicOps2, json)).getOrThrow(error -> new IllegalStateException("Error parsing worldgen settings after loading data packs: " + error));
            return new SaveLoading.LoadContext<WorldCreationSettings>(new WorldCreationSettings(worldGenSettings, context.dataConfiguration()), context.dimensionsRegistryManager());
        }, (resourceManager, dataPackContents, combinedDynamicRegistries, context) -> {
            resourceManager.close();
            return new GeneratorOptionsHolder(context.worldGenSettings(), combinedDynamicRegistries, dataPackContents, context.dataConfiguration());
        }, Util.getMainWorkerExecutor(), this.client).thenApply(generatorOptionsHolder -> {
            generatorOptionsHolder.initializeIndexedFeaturesLists();
            return generatorOptionsHolder;
        })).thenAcceptAsync(this.worldCreator::setGeneratorOptionsHolder, (Executor)this.client)).handleAsync((void_, throwable) -> {
            if (throwable != null) {
                LOGGER.warn("Failed to validate datapack", throwable);
                this.client.setScreen(new ConfirmScreen(confirmed -> {
                    if (confirmed) {
                        configurationSetter.accept(this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
                    } else {
                        configurationSetter.accept(DataConfiguration.SAFE_MODE);
                    }
                }, Text.translatable("dataPack.validation.failed"), ScreenTexts.EMPTY, Text.translatable("dataPack.validation.back"), Text.translatable("dataPack.validation.reset")));
            } else {
                this.client.setScreen(this);
            }
            return null;
        }, (Executor)this.client);
    }

    private static SaveLoading.ServerConfig createServerConfig(ResourcePackManager dataPackManager, DataConfiguration dataConfiguration) {
        SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(dataPackManager, dataConfiguration, false, true);
        return new SaveLoading.ServerConfig(dataPacks, CommandManager.RegistrationEnvironment.INTEGRATED, 2);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    private void clearDataPackTempDir() {
        block7: {
            if (this.dataPackTempDir != null && Files.exists(this.dataPackTempDir, new LinkOption[0])) {
                try (Stream<Path> stream = Files.walk(this.dataPackTempDir, new FileVisitOption[0]);){
                    stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                        try {
                            Files.delete(path);
                        }
                        catch (IOException iOException) {
                            LOGGER.warn("Failed to remove temporary file {}", path, (Object)iOException);
                        }
                    });
                    if (stream == null) break block7;
                }
                catch (IOException iOException) {
                    LOGGER.warn("Failed to list temporary dir {}", (Object)this.dataPackTempDir);
                }
            }
        }
        this.dataPackTempDir = null;
    }

    private static void copyDataPack(Path srcFolder, Path destFolder, Path dataPackFile) {
        try {
            Util.relativeCopy(srcFolder, destFolder, dataPackFile);
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to copy datapack file from {} to {}", (Object)dataPackFile, (Object)destFolder);
            throw new UncheckedIOException(iOException);
        }
    }

    /*
     * WARNING - bad return control flow
     */
    private static Optional<LevelStorage.Session> createSession(MinecraftClient client, String worldDirectoryName, @Nullable Path dataPackTempDir) {
        Optional<LevelStorage.Session> optional;
        block12: {
            LevelStorage.Session session;
            block11: {
                session = client.getLevelStorage().createSessionWithoutSymlinkCheck(worldDirectoryName);
                if (dataPackTempDir != null) break block11;
                return Optional.of(session);
            }
            Stream<Path> stream = Files.walk(dataPackTempDir, new FileVisitOption[0]);
            try {
                Path path2 = session.getDirectory(WorldSavePath.DATAPACKS);
                PathUtil.createDirectories(path2);
                stream.filter(path -> !path.equals(dataPackTempDir)).forEach(path -> CreateWorldScreen.copyDataPack(dataPackTempDir, path2, path));
                optional = Optional.of(session);
                if (stream == null) break block12;
            }
            catch (Throwable throwable) {
                try {
                    try {
                        if (stream != null) {
                            try {
                                stream.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException | UncheckedIOException exception) {
                        LOGGER.warn("Failed to copy datapacks to world {}", (Object)worldDirectoryName, (Object)exception);
                        session.close();
                    }
                }
                catch (IOException | UncheckedIOException exception2) {
                    LOGGER.warn("Failed to create access for {}", (Object)worldDirectoryName, (Object)exception2);
                }
            }
            stream.close();
        }
        return optional;
        return Optional.empty();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Nullable
    public static Path copyDataPack(Path srcFolder, MinecraftClient client) {
        MutableObject mutableObject = new MutableObject();
        try {
            try (Stream<Path> stream = Files.walk(srcFolder, new FileVisitOption[0]);){
                stream.filter(dataPackFile -> !dataPackFile.equals(srcFolder)).forEach(dataPackFile -> {
                    Path path2 = (Path)mutableObject.getValue();
                    if (path2 == null) {
                        try {
                            path2 = Files.createTempDirectory(TEMP_DIR_PREFIX, new FileAttribute[0]);
                        }
                        catch (IOException iOException) {
                            LOGGER.warn("Failed to create temporary dir");
                            throw new UncheckedIOException(iOException);
                        }
                        mutableObject.setValue((Object)path2);
                    }
                    CreateWorldScreen.copyDataPack(srcFolder, path2, dataPackFile);
                });
                if (stream == null) return (Path)mutableObject.getValue();
            }
            return (Path)mutableObject.getValue();
        }
        catch (IOException | UncheckedIOException exception) {
            LOGGER.warn("Failed to copy datapacks from world {}", (Object)srcFolder, (Object)exception);
            SystemToast.addPackCopyFailure(client, srcFolder.toString());
            return null;
        }
    }

    @Nullable
    private Pair<Path, ResourcePackManager> getScannedPack(DataConfiguration dataConfiguration) {
        Path path = this.getOrCreateDataPackTempDir();
        if (path != null) {
            if (this.packManager == null) {
                this.packManager = VanillaDataPackProvider.createManager(path, this.symlinkFinder);
                this.packManager.scanPacks();
            }
            this.packManager.setEnabledProfiles(dataConfiguration.dataPacks().getEnabled());
            return Pair.of((Object)path, (Object)this.packManager);
        }
        return null;
    }

    private static boolean method_29983(List list, String name) {
        return !list.contains(name);
    }

    @Environment(value=EnvType.CLIENT)
    class GameTab
    extends GridScreenTab {
        final static private Text GAME_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.game.title");
        final static private Text ALLOW_COMMANDS_TEXT = Text.translatable("selectWorld.allowCommands");
        final private TextFieldWidget worldNameField;

        GameTab() {
            super(GAME_TAB_TITLE_TEXT);
            GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);
            Positioner positioner = adder.copyPositioner();
            this.worldNameField = new TextFieldWidget(CreateWorldScreen.this.textRenderer, 208, 20, Text.translatable("selectWorld.enterName"));
            this.worldNameField.setText(CreateWorldScreen.this.worldCreator.getWorldName());
            this.worldNameField.setChangedListener(CreateWorldScreen.this.worldCreator::setWorldName);
            CreateWorldScreen.this.worldCreator.addListener(creator -> this.worldNameField.setTooltip(Tooltip.of(Text.translatable("selectWorld.targetFolder", Text.literal(creator.getWorldDirectoryName()).formatted(Formatting.ITALIC)))));
            CreateWorldScreen.this.setInitialFocus(this.worldNameField);
            adder.add(LayoutWidgets.createLabeledWidget(CreateWorldScreen.this.textRenderer, this.worldNameField, ENTER_NAME_TEXT), adder.copyPositioner().alignHorizontalCenter());
            CyclingButtonWidget<WorldCreator.Mode> cyclingButtonWidget = adder.add(CyclingButtonWidget.builder(value -> value.name).values((WorldCreator.Mode[])new WorldCreator.Mode[]{WorldCreator.Mode.SURVIVAL, WorldCreator.Mode.HARDCORE, WorldCreator.Mode.CREATIVE}).build(0, 0, 210, 20, GAME_MODE_TEXT, (button, value) -> CreateWorldScreen.this.worldCreator.setGameMode((WorldCreator.Mode)((Object)value))), positioner);
            CreateWorldScreen.this.worldCreator.addListener(creator -> {
                cyclingButtonWidget.setValue(creator.getGameMode());
                cyclingButtonWidget.active = !creator.isDebug();
                cyclingButtonWidget.setTooltip(Tooltip.of(creator.getGameMode().getInfo()));
            });
            CyclingButtonWidget<Difficulty> cyclingButtonWidget2 = adder.add(CyclingButtonWidget.builder(Difficulty::getTranslatableName).values((Difficulty[])Difficulty.values()).build(0, 0, 210, 20, Text.translatable("options.difficulty"), (button, value) -> CreateWorldScreen.this.worldCreator.setDifficulty((Difficulty)value)), positioner);
            CreateWorldScreen.this.worldCreator.addListener(creator -> {
                cyclingButtonWidget2.setValue(CreateWorldScreen.this.worldCreator.getDifficulty());
                cyclingButtonWidget.active = !CreateWorldScreen.this.worldCreator.isHardcore();
                cyclingButtonWidget2.setTooltip(Tooltip.of(CreateWorldScreen.this.worldCreator.getDifficulty().getInfo()));
            });
            CyclingButtonWidget<Boolean> cyclingButtonWidget3 = adder.add(CyclingButtonWidget.onOffBuilder().tooltip(value -> Tooltip.of(ALLOW_COMMANDS_INFO_TEXT)).build(0, 0, 210, 20, ALLOW_COMMANDS_TEXT, (button, value) -> CreateWorldScreen.this.worldCreator.setCheatsEnabled((boolean)value)));
            CreateWorldScreen.this.worldCreator.addListener(creator -> {
                cyclingButtonWidget3.setValue(CreateWorldScreen.this.worldCreator.areCheatsEnabled());
                cyclingButtonWidget.active = !CreateWorldScreen.this.worldCreator.isDebug() && !CreateWorldScreen.this.worldCreator.isHardcore();
            });
            if (!SharedConstants.getGameVersion().stable()) {
                adder.add(ButtonWidget.builder(EXPERIMENTS_TEXT, button -> CreateWorldScreen.this.openExperimentsScreen(CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder().dataConfiguration())).width(210).build());
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class WorldTab
    extends GridScreenTab {
        final static private Text WORLD_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.world.title");
        final static private Text AMPLIFIED_GENERATOR_INFO_TEXT = Text.translatable("generator.minecraft.amplified.info");
        final static private Text MAP_FEATURES_TEXT = Text.translatable("selectWorld.mapFeatures");
        final static private Text MAP_FEATURES_INFO_TEXT = Text.translatable("selectWorld.mapFeatures.info");
        final static private Text BONUS_ITEMS_TEXT = Text.translatable("selectWorld.bonusItems");
        final static private Text ENTER_SEED_TEXT = Text.translatable("selectWorld.enterSeed");
        final static Text SEED_INFO_TEXT = Text.translatable("selectWorld.seedInfo").formatted(Formatting.DARK_GRAY);
        final static private int field_42190 = 310;
        final private TextFieldWidget seedField;
        final private ButtonWidget customizeButton;

        WorldTab() {
            super(WORLD_TAB_TITLE_TEXT);
            GridWidget.Adder adder = this.grid.setColumnSpacing(10).setRowSpacing(8).createAdder(2);
            CyclingButtonWidget<WorldCreator.WorldType> cyclingButtonWidget = adder.add(CyclingButtonWidget.builder(WorldCreator.WorldType::getName).values(this.getWorldTypes()).narration(WorldTab::getWorldTypeNarrationMessage).build(0, 0, 150, 20, Text.translatable("selectWorld.mapType"), (button, worldType) -> CreateWorldScreen.this.worldCreator.setWorldType((WorldCreator.WorldType)worldType)));
            cyclingButtonWidget.setValue(CreateWorldScreen.this.worldCreator.getWorldType());
            CreateWorldScreen.this.worldCreator.addListener(creator -> {
                WorldCreator.WorldType worldType = creator.getWorldType();
                cyclingButtonWidget.setValue(worldType);
                if (worldType.isAmplified()) {
                    cyclingButtonWidget.setTooltip(Tooltip.of(AMPLIFIED_GENERATOR_INFO_TEXT));
                } else {
                    cyclingButtonWidget.setTooltip(null);
                }
                cyclingButtonWidget.active = CreateWorldScreen.this.worldCreator.getWorldType().preset() != null;
            });
            this.customizeButton = adder.add(ButtonWidget.builder(Text.translatable("selectWorld.customizeType"), button -> this.openCustomizeScreen()).build());
            CreateWorldScreen.this.worldCreator.addListener(creator -> {
                this.customizeButton.active = !creator.isDebug() && creator.getLevelScreenProvider() != null;
            });
            this.seedField = new TextFieldWidget(this, CreateWorldScreen.this.textRenderer, 308, 20, Text.translatable("selectWorld.enterSeed")){

                @Override
                protected MutableText getNarrationMessage() {
                    return super.getNarrationMessage().append(ScreenTexts.SENTENCE_SEPARATOR).append(SEED_INFO_TEXT);
                }
            };
            this.seedField.setPlaceholder(SEED_INFO_TEXT);
            this.seedField.setText(CreateWorldScreen.this.worldCreator.getSeed());
            this.seedField.setChangedListener(seed -> CreateWorldScreen.this.worldCreator.setSeed(this.seedField.getText()));
            adder.add(LayoutWidgets.createLabeledWidget(CreateWorldScreen.this.textRenderer, this.seedField, ENTER_SEED_TEXT), 2);
            WorldScreenOptionGrid.Builder builder = WorldScreenOptionGrid.builder(310);
            builder.add(MAP_FEATURES_TEXT, CreateWorldScreen.this.worldCreator::shouldGenerateStructures, CreateWorldScreen.this.worldCreator::setGenerateStructures).toggleable(() -> !CreateWorldScreen.this.worldCreator.isDebug()).tooltip(MAP_FEATURES_INFO_TEXT);
            builder.add(BONUS_ITEMS_TEXT, CreateWorldScreen.this.worldCreator::isBonusChestEnabled, CreateWorldScreen.this.worldCreator::setBonusChestEnabled).toggleable(() -> !CreateWorldScreen.this.worldCreator.isHardcore() && !CreateWorldScreen.this.worldCreator.isDebug());
            WorldScreenOptionGrid worldScreenOptionGrid = builder.build();
            adder.add(worldScreenOptionGrid.getLayout(), 2);
            CreateWorldScreen.this.worldCreator.addListener(creator -> worldScreenOptionGrid.refresh());
        }

        private void openCustomizeScreen() {
            LevelScreenProvider levelScreenProvider = CreateWorldScreen.this.worldCreator.getLevelScreenProvider();
            if (levelScreenProvider != null) {
                CreateWorldScreen.this.client.setScreen(levelScreenProvider.createEditScreen(CreateWorldScreen.this, CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder()));
            }
        }

        private CyclingButtonWidget.Values<WorldCreator.WorldType> getWorldTypes() {
            return new CyclingButtonWidget.Values<WorldCreator.WorldType>(){

                @Override
                public List<WorldCreator.WorldType> getCurrent() {
                    return CyclingButtonWidget.HAS_ALT_DOWN.getAsBoolean() ? CreateWorldScreen.this.worldCreator.getExtendedWorldTypes() : CreateWorldScreen.this.worldCreator.getNormalWorldTypes();
                }

                @Override
                public List<WorldCreator.WorldType> getDefaults() {
                    return CreateWorldScreen.this.worldCreator.getNormalWorldTypes();
                }
            };
        }

        private static MutableText getWorldTypeNarrationMessage(CyclingButtonWidget<WorldCreator.WorldType> worldTypeButton) {
            if (worldTypeButton.getValue().isAmplified()) {
                return ScreenTexts.joinSentences(worldTypeButton.getGenericNarrationMessage(), AMPLIFIED_GENERATOR_INFO_TEXT);
            }
            return worldTypeButton.getGenericNarrationMessage();
        }
    }

    @Environment(value=EnvType.CLIENT)
    class MoreTab
    extends GridScreenTab {
        final static private Text MORE_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.more.title");
        final static private Text GAME_RULES_TEXT = Text.translatable("selectWorld.gameRules");
        final static private Text DATA_PACKS_TEXT = Text.translatable("selectWorld.dataPacks");

        MoreTab() {
            super(MORE_TAB_TITLE_TEXT);
            GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);
            adder.add(ButtonWidget.builder(GAME_RULES_TEXT, button -> this.openGameRulesScreen()).width(210).build());
            adder.add(ButtonWidget.builder(EXPERIMENTS_TEXT, button -> CreateWorldScreen.this.openExperimentsScreen(CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder().dataConfiguration())).width(210).build());
            adder.add(ButtonWidget.builder(DATA_PACKS_TEXT, button -> CreateWorldScreen.this.openPackScreen(CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder().dataConfiguration())).width(210).build());
        }

        private void openGameRulesScreen() {
            CreateWorldScreen.this.client.setScreen(new EditGameRulesScreen(CreateWorldScreen.this.worldCreator.getGameRules().copy(CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder().dataConfiguration().enabledFeatures()), gameRules -> {
                CreateWorldScreen.this.client.setScreen(CreateWorldScreen.this);
                gameRules.ifPresent(CreateWorldScreen.this.worldCreator::setGameRules);
            }));
        }
    }
}

