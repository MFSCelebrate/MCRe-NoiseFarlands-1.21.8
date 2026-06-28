/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.minecraft.BanDetails
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.minecraft.UserApiService
 *  com.mojang.authlib.minecraft.UserApiService$UserFlag
 *  com.mojang.authlib.minecraft.UserApiService$UserProperties
 *  com.mojang.authlib.yggdrasil.ProfileActionType
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  com.mojang.authlib.yggdrasil.ServicesKeyType
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.jtracy.DiscontinuousFrame
 *  com.mojang.jtracy.TracyClient
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2BooleanFunction
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.FileUtils
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileActionType;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixer;
import com.mojang.jtracy.DiscontinuousFrame;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandle;
import java.lang.management.ManagementFactory;
import java.lang.runtime.ObjectMethods;
import java.lang.runtime.SwitchBootstraps;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.ClientWatchdog;
import net.minecraft.client.Keyboard;
import net.minecraft.client.Mouse;
import net.minecraft.client.QuickPlay;
import net.minecraft.client.QuickPlayLogger;
import net.minecraft.client.RunArgs;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FreeTypeUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlTimer;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.WindowFramebuffer;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.debug.PieChart;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.OutOfMemoryScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.client.option.InactivityFpsLimiter;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.resource.DefaultClientResourcePackProvider;
import net.minecraft.client.resource.DryFoliageColormapResourceSupplier;
import net.minecraft.client.resource.FoliageColormapResourceSupplier;
import net.minecraft.client.resource.GrassColormapResourceSupplier;
import net.minecraft.client.resource.PeriodicNotificationManager;
import net.minecraft.client.resource.ResourceReloadLogger;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.client.resource.waypoint.WaypointStyleAssetManager;
import net.minecraft.client.session.Bans;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.client.session.telemetry.GameLoadTimeEvent;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.session.telemetry.TelemetryManager;
import net.minecraft.client.sound.MusicInstance;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.GuiAtlasManager;
import net.minecraft.client.texture.MapDecorationsAtlasManager;
import net.minecraft.client.texture.MapTextureManager;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.util.ClientSamplerSource;
import net.minecraft.client.util.CommandHistoryManager;
import net.minecraft.client.util.GlException;
import net.minecraft.client.util.Icons;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.WindowProvider;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.datafixer.Schemas;
import net.minecraft.dialog.Dialogs;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.DialogTags;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.QueueingWorldGenerationProgressListener;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressTracker;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.MusicType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.test.TestManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.KeybindTranslations;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ModStatus;
import net.minecraft.util.Nullables;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.TickDurationMonitor;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.Urls;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.ZipCompressor;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashMemoryReserve;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ReportType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.path.PathUtil;
import net.minecraft.util.path.SymlinkFinder;
import net.minecraft.util.profiler.DebugRecorder;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.DummyRecorder;
import net.minecraft.util.profiler.EmptyProfileResult;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.RecordDumper;
import net.minecraft.util.profiler.Recorder;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.util.profiler.TickTimeTracker;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.tick.TickManager;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MinecraftClient
extends ReentrantThreadExecutor<Runnable>
implements WindowEventHandler {
    static MinecraftClient instance;
    final static private Logger LOGGER;
    final static public boolean IS_SYSTEM_MAC;
    final static private int field_32145 = 10;
    final static public Identifier DEFAULT_FONT_ID;
    final static public Identifier UNICODE_FONT_ID;
    final static public Identifier ALT_TEXT_RENDERER_ID;
    final static private Identifier REGIONAL_COMPLIANCIES_ID;
    final static private CompletableFuture<Unit> COMPLETED_UNIT_FUTURE;
    final static private Text SOCIAL_INTERACTIONS_NOT_AVAILABLE;
    final static private Text SAVING_LEVEL_TEXT;
    final static public String GL_ERROR_DIALOGUE = "Please make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).";
    final private long UNIVERSE = Double.doubleToLongBits(Math.PI);
    final private Path resourcePackDir;
    final private CompletableFuture<com.mojang.authlib.yggdrasil.ProfileResult> gameProfileFuture;
    final private TextureManager textureManager;
    final private ShaderLoader shaderLoader;
    final private DataFixer dataFixer;
    final private WindowProvider windowProvider;
    final private Window window;
    final private RenderTickCounter.Dynamic renderTickCounter = new RenderTickCounter.Dynamic(20.0f, 0L, this::getTargetMillisPerTick);
    final private BufferBuilderStorage bufferBuilders;
    final public WorldRenderer worldRenderer;
    final private EntityRenderDispatcher entityRenderDispatcher;
    final private ItemModelManager itemModelManager;
    final private ItemRenderer itemRenderer;
    final private MapRenderer mapRenderer;
    final public ParticleManager particleManager;
    final private Session session;
    final public TextRenderer textRenderer;
    final public TextRenderer advanceValidatingTextRenderer;
    final public GameRenderer gameRenderer;
    final public DebugRenderer debugRenderer;
    final private AtomicReference<WorldGenerationProgressTracker> worldGenProgressTracker = new AtomicReference();
    final public InGameHud inGameHud;
    final public GameOptions options;
    final private HotbarStorage creativeHotbarStorage;
    final public Mouse mouse;
    final public Keyboard keyboard;
    private GuiNavigationType navigationType = GuiNavigationType.NONE;
    final public File runDirectory;
    final private String gameVersion;
    final private String versionType;
    final private Proxy networkProxy;
    final private LevelStorage levelStorage;
    final private boolean isDemo;
    final private boolean multiplayerEnabled;
    final private boolean onlineChatEnabled;
    final private ReloadableResourceManagerImpl resourceManager;
    final private DefaultResourcePack defaultResourcePack;
    final private ServerResourcePackLoader serverResourcePackLoader;
    final private ResourcePackManager resourcePackManager;
    final private LanguageManager languageManager;
    final private BlockColors blockColors;
    final private Framebuffer framebuffer;
    @Nullable
    final private TracyFrameCapturer tracyFrameCapturer;
    final private SoundManager soundManager;
    final private MusicTracker musicTracker;
    final private FontManager fontManager;
    final private SplashTextResourceSupplier splashTextLoader;
    final private VideoWarningManager videoWarningManager;
    final private PeriodicNotificationManager regionalComplianciesManager = new PeriodicNotificationManager(REGIONAL_COMPLIANCIES_ID, (Object2BooleanFunction<String>)((Object2BooleanFunction)MinecraftClient::isCountrySetTo));
    final private YggdrasilAuthenticationService authenticationService;
    final private MinecraftSessionService sessionService;
    final private UserApiService userApiService;
    final private CompletableFuture<UserApiService.UserProperties> userPropertiesFuture;
    final private PlayerSkinProvider skinProvider;
    final private BakedModelManager bakedModelManager;
    final private BlockRenderManager blockRenderManager;
    final private PaintingManager paintingManager;
    final private MapTextureManager mapTextureManager;
    final private MapDecorationsAtlasManager mapDecorationsAtlasManager;
    final private GuiAtlasManager guiAtlasManager;
    final private WaypointStyleAssetManager waypointStyleAssetManager;
    final private ToastManager toastManager;
    final private TutorialManager tutorialManager;
    final private SocialInteractionsManager socialInteractionsManager;
    final private BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    final private TelemetryManager telemetryManager;
    final private ProfileKeys profileKeys;
    final private RealmsPeriodicCheckers realmsPeriodicCheckers;
    final private QuickPlayLogger quickPlayLogger;
    @Nullable
    public ClientPlayerInteractionManager interactionManager;
    @Nullable
    public ClientWorld world;
    @Nullable
    public ClientPlayerEntity player;
    @Nullable
    private IntegratedServer server;
    @Nullable
    private ClientConnection integratedServerConnection;
    private boolean integratedServerRunning;
    @Nullable
    public Entity cameraEntity;
    @Nullable
    public Entity targetedEntity;
    @Nullable
    public HitResult crosshairTarget;
    private int itemUseCooldown;
    public int attackCooldown;
    private volatile boolean paused;
    private long lastMetricsSampleTime = Util.getMeasuringTimeNano();
    private long nextDebugInfoUpdateTime;
    private int fpsCounter;
    public boolean skipGameRender;
    @Nullable
    public Screen currentScreen;
    @Nullable
    private Overlay overlay;
    private boolean disconnecting;
    Thread thread;
    private volatile boolean running;
    @Nullable
    private Supplier<CrashReport> crashReportSupplier;
    static private int currentFps;
    public String fpsDebugString = "";
    private long renderTime;
    final private InactivityFpsLimiter inactivityFpsLimiter;
    public boolean wireFrame;
    public boolean debugChunkInfo;
    public boolean debugChunkOcclusion;
    public boolean chunkCullingEnabled = true;
    private boolean windowFocused;
    final private Queue<Runnable> renderTaskQueue = Queues.newConcurrentLinkedQueue();
    @Nullable
    private CompletableFuture<Void> resourceReloadFuture;
    @Nullable
    private TutorialToast socialInteractionsToast;
    private int trackingTick;
    final private TickTimeTracker tickTimeTracker;
    private Recorder recorder = DummyRecorder.INSTANCE;
    final private ResourceReloadLogger resourceReloadLogger = new ResourceReloadLogger();
    private long metricsSampleDuration;
    private double gpuUtilizationPercentage;
    @Nullable
    private GlTimer.Query currentGlTimerQuery;
    final private NarratorManager narratorManager;
    final private MessageHandler messageHandler;
    private AbuseReportContext abuseReportContext;
    final private CommandHistoryManager commandHistoryManager;
    final private SymlinkFinder symlinkFinder;
    private boolean finishedLoading;
    final private long startTime;
    private long uptimeInTicks;

    public MinecraftClient(final RunArgs args) {
        super("Client");
        instance = this;
        this.startTime = System.currentTimeMillis();
        this.runDirectory = args.directories.runDir;
        File file = args.directories.assetDir;
        this.resourcePackDir = args.directories.resourcePackDir.toPath();
        this.gameVersion = args.game.version;
        this.versionType = args.game.versionType;
        Path path = this.runDirectory.toPath();
        this.symlinkFinder = LevelStorage.createSymlinkFinder(path.resolve("allowed_symlinks.txt"));
        DefaultClientResourcePackProvider defaultClientResourcePackProvider = new DefaultClientResourcePackProvider(args.directories.getAssetDir(), this.symlinkFinder);
        this.serverResourcePackLoader = new ServerResourcePackLoader(this, path.resolve("downloads"), args.network);
        FileResourcePackProvider resourcePackProvider = new FileResourcePackProvider(this.resourcePackDir, ResourceType.CLIENT_RESOURCES, ResourcePackSource.NONE, this.symlinkFinder);
        this.resourcePackManager = new ResourcePackManager(defaultClientResourcePackProvider, this.serverResourcePackLoader.getPassthroughPackProvider(), resourcePackProvider);
        this.defaultResourcePack = defaultClientResourcePackProvider.getResourcePack();
        this.networkProxy = args.network.netProxy;
        this.authenticationService = new YggdrasilAuthenticationService(this.networkProxy);
        this.sessionService = this.authenticationService.createMinecraftSessionService();
        this.session = args.network.session;
        this.gameProfileFuture = CompletableFuture.supplyAsync(() -> this.sessionService.fetchProfile(this.session.getUuidOrNull(), true), Util.getDownloadWorkerExecutor());
        this.userApiService = this.createUserApiService(this.authenticationService, args);
        this.userPropertiesFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return this.userApiService.fetchProperties();
            }
            catch (AuthenticationException authenticationException) {
                LOGGER.error("Failed to fetch user properties", (Throwable)authenticationException);
                return UserApiService.OFFLINE_PROPERTIES;
            }
        }, Util.getDownloadWorkerExecutor());
        LOGGER.info("Setting user: {}", (Object)this.session.getUsername());
        LOGGER.debug("(Session ID is {})", (Object)this.session.getSessionId());
        this.isDemo = args.game.demo;
        this.multiplayerEnabled = !args.game.multiplayerDisabled;
        this.onlineChatEnabled = !args.game.onlineChatDisabled;
        this.server = null;
        KeybindTranslations.setFactory(KeyBinding::getLocalizedName);
        this.dataFixer = Schemas.getFixer();
        this.thread = Thread.currentThread();
        this.options = new GameOptions(this, this.runDirectory);
        this.toastManager = new ToastManager(this, this.options);
        boolean bl = this.options.startedCleanly;
        this.options.startedCleanly = false;
        this.options.write();
        this.running = true;
        this.tutorialManager = new TutorialManager(this, this.options);
        this.creativeHotbarStorage = new HotbarStorage(path, this.dataFixer);
        LOGGER.info("Backend library: {}", (Object)RenderSystem.getBackendDescription());
        WindowSettings windowSettings = args.windowSettings;
        if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
            windowSettings = args.windowSettings.withDimensions(this.options.overrideWidth, this.options.overrideHeight);
        }
        if (!bl) {
            windowSettings = windowSettings.withFullscreen(false);
            this.options.fullscreenResolution = null;
            LOGGER.warn("Detected unexpected shutdown during last game startup: resetting fullscreen mode");
        }
        Util.nanoTimeSupplier = RenderSystem.initBackendSystem();
        this.windowProvider = new WindowProvider(this);
        this.window = this.windowProvider.createWindow(windowSettings, this.options.fullscreenResolution, this.getWindowTitle());
        this.onWindowFocusChanged(true);
        this.window.setCloseCallback(new Runnable(){
            private boolean closed;

            @Override
            public void run() {
                if (!this.closed) {
                    this.closed = true;
                    ClientWatchdog.shutdownClient(args.directories.runDir, MinecraftClient.this.thread.threadId());
                }
            }
        });
        GameLoadTimeEvent.INSTANCE.stopTimer(TelemetryEventProperty.LOAD_TIME_PRE_WINDOW_MS);
        try {
            this.window.setIcon(this.defaultResourcePack, SharedConstants.getGameVersion().stable() ? Icons.RELEASE : Icons.SNAPSHOT);
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't set icon", (Throwable)iOException);
        }
        this.mouse = new Mouse(this);
        this.mouse.setup(this.window.getHandle());
        this.keyboard = new Keyboard(this);
        this.keyboard.setup(this.window.getHandle());
        RenderSystem.initRenderer(this.window.getHandle(), this.options.glDebugVerbosity, false, (id, type) -> this.getShaderLoader().getSource((Identifier)id, (ShaderType)((Object)type)), args.game.renderDebugLabels);
        LOGGER.info("Using optional rendering extensions: {}", (Object)String.join((CharSequence)", ", RenderSystem.getDevice().getEnabledExtensions()));
        this.framebuffer = new WindowFramebuffer(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.CLIENT_RESOURCES);
        this.resourcePackManager.scanPacks();
        this.options.addResourcePackProfilesToManager(this.resourcePackManager);
        this.languageManager = new LanguageManager(this.options.language, translationStorage -> {
            if (this.player != null) {
                this.player.networkHandler.refreshSearchManager();
            }
        });
        this.resourceManager.registerReloader(this.languageManager);
        this.textureManager = new TextureManager(this.resourceManager);
        this.resourceManager.registerReloader(this.textureManager);
        this.shaderLoader = new ShaderLoader(this.textureManager, this::onShaderResourceReloadFailure);
        this.resourceManager.registerReloader(this.shaderLoader);
        this.skinProvider = new PlayerSkinProvider(file.toPath().resolve("skins"), this.sessionService, this);
        this.levelStorage = new LevelStorage(path.resolve("saves"), path.resolve("backups"), this.symlinkFinder, this.dataFixer);
        this.commandHistoryManager = new CommandHistoryManager(path);
        this.musicTracker = new MusicTracker(this);
        this.soundManager = new SoundManager(this.options, this.musicTracker);
        this.resourceManager.registerReloader(this.soundManager);
        this.splashTextLoader = new SplashTextResourceSupplier(this.session);
        this.resourceManager.registerReloader(this.splashTextLoader);
        this.fontManager = new FontManager(this.textureManager);
        this.textRenderer = this.fontManager.createTextRenderer();
        this.advanceValidatingTextRenderer = this.fontManager.createAdvanceValidatingTextRenderer();
        this.resourceManager.registerReloader(this.fontManager);
        this.onFontOptionsChanged();
        this.resourceManager.registerReloader(new GrassColormapResourceSupplier());
        this.resourceManager.registerReloader(new FoliageColormapResourceSupplier());
        this.resourceManager.registerReloader(new DryFoliageColormapResourceSupplier());
        this.window.setPhase("Startup");
        RenderSystem.setupDefaultState();
        this.window.setPhase("Post startup");
        this.blockColors = BlockColors.create();
        this.bakedModelManager = new BakedModelManager(this.textureManager, this.blockColors, this.options.getMipmapLevels().getValue());
        this.resourceManager.registerReloader(this.bakedModelManager);
        EquipmentModelLoader equipmentModelLoader = new EquipmentModelLoader();
        this.resourceManager.registerReloader(equipmentModelLoader);
        this.itemModelManager = new ItemModelManager(this.bakedModelManager);
        this.itemRenderer = new ItemRenderer(this.itemModelManager);
        this.mapTextureManager = new MapTextureManager(this.textureManager);
        this.mapDecorationsAtlasManager = new MapDecorationsAtlasManager(this.textureManager);
        this.resourceManager.registerReloader(this.mapDecorationsAtlasManager);
        this.mapRenderer = new MapRenderer(this.mapDecorationsAtlasManager, this.mapTextureManager);
        try {
            int i = Runtime.getRuntime().availableProcessors();
            Tessellator.initialize();
            this.bufferBuilders = new BufferBuilderStorage(i);
        }
        catch (OutOfMemoryError outOfMemoryError) {
            TinyFileDialogs.tinyfd_messageBox((CharSequence)"Minecraft", (CharSequence)("Oh no! The game was unable to allocate memory off-heap while trying to start. You may try to free some memory by closing other applications on your computer, check that your system meets the minimum requirements, and try again. If the problem persists, please visit: " + String.valueOf(Urls.MINECRAFT_SUPPORT)), (CharSequence)"ok", (CharSequence)"error", (boolean)true);
            throw new GlException("Unable to allocate render buffers", outOfMemoryError);
        }
        this.socialInteractionsManager = new SocialInteractionsManager(this, this.userApiService);
        this.blockRenderManager = new BlockRenderManager(this.bakedModelManager.getBlockModels(), this.bakedModelManager.getBlockEntityModelsSupplier(), this.blockColors);
        this.resourceManager.registerReloader(this.blockRenderManager);
        this.entityRenderDispatcher = new EntityRenderDispatcher(this, this.textureManager, this.itemModelManager, this.itemRenderer, this.mapRenderer, this.blockRenderManager, this.textRenderer, this.options, this.bakedModelManager.getEntityModelsSupplier(), equipmentModelLoader);
        this.resourceManager.registerReloader(this.entityRenderDispatcher);
        this.blockEntityRenderDispatcher = new BlockEntityRenderDispatcher(this.textRenderer, this.bakedModelManager.getEntityModelsSupplier(), this.blockRenderManager, this.itemModelManager, this.itemRenderer, this.entityRenderDispatcher);
        this.resourceManager.registerReloader(this.blockEntityRenderDispatcher);
        this.particleManager = new ParticleManager(this.world, this.textureManager);
        this.resourceManager.registerReloader(this.particleManager);
        this.paintingManager = new PaintingManager(this.textureManager);
        this.resourceManager.registerReloader(this.paintingManager);
        this.guiAtlasManager = new GuiAtlasManager(this.textureManager);
        this.resourceManager.registerReloader(this.guiAtlasManager);
        this.waypointStyleAssetManager = new WaypointStyleAssetManager();
        this.resourceManager.registerReloader(this.waypointStyleAssetManager);
        this.gameRenderer = new GameRenderer(this, this.entityRenderDispatcher.getHeldItemRenderer(), this.bufferBuilders);
        this.worldRenderer = new WorldRenderer(this, this.entityRenderDispatcher, this.blockEntityRenderDispatcher, this.bufferBuilders);
        this.resourceManager.registerReloader(this.worldRenderer);
        this.resourceManager.registerReloader(this.worldRenderer.getCloudRenderer());
        this.videoWarningManager = new VideoWarningManager();
        this.resourceManager.registerReloader(this.videoWarningManager);
        this.resourceManager.registerReloader(this.regionalComplianciesManager);
        this.inGameHud = new InGameHud(this);
        this.debugRenderer = new DebugRenderer(this);
        RealmsClient realmsClient = RealmsClient.createRealmsClient(this);
        this.realmsPeriodicCheckers = new RealmsPeriodicCheckers(realmsClient);
        RenderSystem.setErrorCallback(this::handleGlErrorByDisableVsync);
        if (this.framebuffer.textureWidth != this.window.getFramebufferWidth() || this.framebuffer.textureHeight != this.window.getFramebufferHeight()) {
            StringBuilder stringBuilder = new StringBuilder("Recovering from unsupported resolution (" + this.window.getFramebufferWidth() + "x" + this.window.getFramebufferHeight() + ").\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).");
            try {
                GpuDevice gpuDevice = RenderSystem.getDevice();
                List<String> list = gpuDevice.getLastDebugMessages();
                if (!list.isEmpty()) {
                    stringBuilder.append("\n\nReported GL debug messages:\n").append(String.join((CharSequence)"\n", list));
                }
            }
            catch (Throwable gpuDevice) {
                // empty catch block
            }
            this.window.setWindowedSize(this.framebuffer.textureWidth, this.framebuffer.textureHeight);
            TinyFileDialogs.tinyfd_messageBox((CharSequence)"Minecraft", (CharSequence)stringBuilder.toString(), (CharSequence)"ok", (CharSequence)"error", (boolean)false);
        } else if (this.options.getFullscreen().getValue().booleanValue() && !this.window.isFullscreen()) {
            if (bl) {
                this.window.toggleFullscreen();
                this.options.getFullscreen().setValue(this.window.isFullscreen());
            } else {
                this.options.getFullscreen().setValue(false);
            }
        }
        this.window.setVsync(this.options.getEnableVsync().getValue());
        this.window.setRawMouseMotion(this.options.getRawMouseInput().getValue());
        this.window.logOnGlError();
        this.onResolutionChanged();
        this.gameRenderer.preloadPrograms(this.defaultResourcePack.getFactory());
        this.telemetryManager = new TelemetryManager(this, this.userApiService, this.session);
        this.profileKeys = ProfileKeys.create(this.userApiService, this.session, path);
        this.narratorManager = new NarratorManager(this);
        this.narratorManager.checkNarratorLibrary(this.options.getNarrator().getValue() != NarratorMode.OFF);
        this.messageHandler = new MessageHandler(this);
        this.messageHandler.setChatDelay(this.options.getChatDelay().getValue());
        this.abuseReportContext = AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), this.userApiService);
        TitleScreen.registerTextures(this.textureManager);
        SplashOverlay.init(this.textureManager);
        this.gameRenderer.getRotatingPanoramaRenderer().registerTextures(this.textureManager);
        this.setScreen(new MessageScreen(Text.translatable("gui.loadingMinecraft")));
        List<ResourcePack> list2 = this.resourcePackManager.createResourcePacks();
        this.resourceReloadLogger.reload(ResourceReloadLogger.ReloadReason.INITIAL, list2);
        ResourceReload resourceReload = this.resourceManager.reload(Util.getMainWorkerExecutor().named("resourceLoad"), this, COMPLETED_UNIT_FUTURE, list2);
        GameLoadTimeEvent.INSTANCE.startTimer(TelemetryEventProperty.LOAD_TIME_LOADING_OVERLAY_MS);
        LoadingContext loadingContext = new LoadingContext(realmsClient, args.quickPlay);
        this.setOverlay(new SplashOverlay(this, resourceReload, error -> Util.ifPresentOrElse(error, throwable -> this.handleResourceReloadException((Throwable)throwable, loadingContext), () -> {
            if (SharedConstants.isDevelopment) {
                this.checkGameData();
            }
            this.resourceReloadLogger.finish();
            this.onFinishedLoading(loadingContext);
        }), false));
        this.quickPlayLogger = QuickPlayLogger.create(args.quickPlay.logPath());
        this.inactivityFpsLimiter = new InactivityFpsLimiter(this.options, this);
        this.tickTimeTracker = new TickTimeTracker(Util.nanoTimeSupplier, () -> this.trackingTick, this.inactivityFpsLimiter::shouldDisableProfilerTimeout);
        this.tracyFrameCapturer = TracyClient.isAvailable() && args.game.tracyEnabled ? new TracyFrameCapturer() : null;
    }

    private void onFinishedLoading(@Nullable LoadingContext loadingContext) {
        if (!this.finishedLoading) {
            this.finishedLoading = true;
            this.collectLoadTimes(loadingContext);
        }
    }

    private void collectLoadTimes(@Nullable LoadingContext loadingContext) {
        Runnable runnable = this.onInitFinished(loadingContext);
        GameLoadTimeEvent.INSTANCE.stopTimer(TelemetryEventProperty.LOAD_TIME_LOADING_OVERLAY_MS);
        GameLoadTimeEvent.INSTANCE.stopTimer(TelemetryEventProperty.LOAD_TIME_TOTAL_TIME_MS);
        GameLoadTimeEvent.INSTANCE.send(this.telemetryManager.getSender());
        runnable.run();
        this.options.startedCleanly = true;
        this.options.write();
    }

    public boolean isFinishedLoading() {
        return this.finishedLoading;
    }

    private Runnable onInitFinished(@Nullable LoadingContext loadingContext) {
        ArrayList<Function<Runnable, Screen>> list = new ArrayList<Function<Runnable, Screen>>();
        boolean bl = this.createInitScreens(list);
        Runnable runnable = () -> {
            if (loadingContext != null && loadingContext.quickPlayData.isEnabled()) {
                QuickPlay.startQuickPlay(this, loadingContext.quickPlayData.variant(), loadingContext.realmsClient());
            } else {
                this.setScreen(new TitleScreen(true, new LogoDrawer(bl)));
            }
        };
        for (Function function : Lists.reverse(list)) {
            Screen screen = (Screen)function.apply(runnable);
            runnable = () -> this.setScreen(screen);
        }
        return runnable;
    }

    private boolean createInitScreens(List<Function<Runnable, Screen>> list) {
        com.mojang.authlib.yggdrasil.ProfileResult profileResult;
        boolean bl = false;
        if (this.options.onboardAccessibility) {
            list.add(onClose -> new AccessibilityOnboardingScreen(this.options, (Runnable)onClose));
            bl = true;
        }
        BanDetails banDetails = this.getMultiplayerBanDetails();
        if (banDetails != null) {
            list.add(onClose -> Bans.createBanScreen(confirmed -> {
                if (confirmed) {
                    Util.getOperatingSystem().open(Urls.JAVA_MODERATION);
                }
                onClose.run();
            }, banDetails));
        }
        if ((profileResult = this.gameProfileFuture.join()) != null) {
            GameProfile gameProfile = profileResult.profile();
            Set set = profileResult.actions();
            if (set.contains(ProfileActionType.FORCED_NAME_CHANGE)) {
                list.add(onClose -> Bans.createUsernameBanScreen(gameProfile.getName(), onClose));
            }
            if (set.contains(ProfileActionType.USING_BANNED_SKIN)) {
                list.add(Bans::createSkinBanScreen);
            }
        }
        return bl;
    }

    private static boolean isCountrySetTo(Object country) {
        try {
            return Locale.getDefault().getISO3Country().equals(country);
        }
        catch (MissingResourceException missingResourceException) {
            return false;
        }
    }

    public void updateWindowTitle() {
        this.window.setTitle(this.getWindowTitle());
    }

    private String getWindowTitle() {
        StringBuilder stringBuilder = new StringBuilder("Minecraft");
        if (MinecraftClient.getModStatus().isModded()) {
            stringBuilder.append("*");
        }
        stringBuilder.append(" ");
        stringBuilder.append(SharedConstants.getGameVersion().name());
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            stringBuilder.append(" - ");
            ServerInfo serverInfo = this.getCurrentServerEntry();
            if (this.server != null && !this.server.isRemote()) {
                stringBuilder.append(I18n.translate("title.singleplayer", new Object[0]));
            } else if (serverInfo != null && serverInfo.isRealm()) {
                stringBuilder.append(I18n.translate("title.multiplayer.realms", new Object[0]));
            } else if (this.server != null || serverInfo != null && serverInfo.isLocal()) {
                stringBuilder.append(I18n.translate("title.multiplayer.lan", new Object[0]));
            } else {
                stringBuilder.append(I18n.translate("title.multiplayer.other", new Object[0]));
            }
        }
        return stringBuilder.toString();
    }

    private UserApiService createUserApiService(YggdrasilAuthenticationService authService, RunArgs runArgs) {
        if (runArgs.network.session.getAccountType() != Session.AccountType.MSA) {
            return UserApiService.OFFLINE;
        }
        return authService.createUserApiService(runArgs.network.session.getAccessToken());
    }

    public static ModStatus getModStatus() {
        return ModStatus.check("vanilla", ClientBrandRetriever::getClientModName, "Client", MinecraftClient.class);
    }

    private void handleResourceReloadException(Throwable throwable, @Nullable LoadingContext loadingContext) {
        if (this.resourcePackManager.getEnabledIds().size() > 1) {
            this.onResourceReloadFailure(throwable, null, loadingContext);
        } else {
            Util.throwUnchecked(throwable);
        }
    }

    public void onResourceReloadFailure(Throwable exception, @Nullable Text resourceName, @Nullable LoadingContext loadingContext) {
        LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", exception);
        this.resourceReloadLogger.recover(exception);
        this.serverResourcePackLoader.onReloadFailure();
        this.resourcePackManager.setEnabledProfiles(Collections.emptyList());
        this.options.resourcePacks.clear();
        this.options.incompatibleResourcePacks.clear();
        this.options.write();
        this.reloadResources(true, loadingContext).thenRunAsync(() -> this.showResourceReloadFailureToast(resourceName), this);
    }

    private void onForcedResourceReloadFailure() {
        this.setOverlay(null);
        if (this.world != null) {
            this.world.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
            this.disconnectWithProgressScreen();
        }
        this.setScreen(new TitleScreen());
        this.showResourceReloadFailureToast(null);
    }

    private void showResourceReloadFailureToast(@Nullable Text description) {
        ToastManager toastManager = this.getToastManager();
        SystemToast.show(toastManager, SystemToast.Type.PACK_LOAD_FAILURE, Text.translatable("resourcePack.load_fail"), description);
    }

    public void onShaderResourceReloadFailure(Exception exception) {
        if (!this.resourcePackManager.hasOptionalProfilesEnabled()) {
            if (this.resourcePackManager.getEnabledIds().size() <= 1) {
                LOGGER.error(LogUtils.FATAL_MARKER, exception.getMessage(), (Throwable)exception);
                this.printCrashReport(new CrashReport(exception.getMessage(), exception));
            } else {
                this.send(this::onForcedResourceReloadFailure);
            }
            return;
        }
        this.onResourceReloadFailure(exception, Text.translatable("resourcePack.runtime_failure"), null);
    }

    public void run() {
        this.thread = Thread.currentThread();
        if (Runtime.getRuntime().availableProcessors() > 4) {
            this.thread.setPriority(10);
        }
        DiscontinuousFrame discontinuousFrame = TracyClient.createDiscontinuousFrame((String)"Client Tick");
        try {
            boolean bl = false;
            while (this.running) {
                this.printCrashReport();
                try {
                    TickDurationMonitor tickDurationMonitor = TickDurationMonitor.create("Renderer");
                    boolean bl2 = this.getDebugHud().shouldShowRenderingChart();
                    Profilers.Scoped scoped = Profilers.using(this.startMonitor(bl2, tickDurationMonitor));
                    try {
                        this.recorder.startTick();
                        discontinuousFrame.start();
                        this.render(!bl);
                        discontinuousFrame.end();
                        this.recorder.endTick();
                        if (scoped != null) {
                            scoped.close();
                        }
                    }
                    catch (Throwable throwable) {
                        if (scoped != null) {
                            try {
                                scoped.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    this.endMonitor(bl2, tickDurationMonitor);
                }
                catch (OutOfMemoryError outOfMemoryError) {
                    if (bl) {
                        throw outOfMemoryError;
                    }
                    this.cleanUpAfterCrash();
                    this.setScreen(new OutOfMemoryScreen());
                    System.gc();
                    LOGGER.error(LogUtils.FATAL_MARKER, "Out of memory", (Throwable)outOfMemoryError);
                    bl = true;
                }
            }
        }
        catch (CrashException crashException) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Reported exception thrown!", (Throwable)crashException);
            this.printCrashReport(crashException.getReport());
        }
        catch (Throwable throwable) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Unreported exception thrown!", throwable);
            this.printCrashReport(new CrashReport("Unexpected error", throwable));
        }
    }

    void onFontOptionsChanged() {
        this.fontManager.setActiveFilters(this.options);
    }

    private void handleGlErrorByDisableVsync(int error, long description) {
        this.options.getEnableVsync().setValue(false);
        this.options.write();
    }

    public Framebuffer getFramebuffer() {
        return this.framebuffer;
    }

    public String getGameVersion() {
        return this.gameVersion;
    }

    public String getVersionType() {
        return this.versionType;
    }

    public void setCrashReportSupplierAndAddDetails(CrashReport crashReport) {
        this.crashReportSupplier = () -> this.addDetailsToCrashReport(crashReport);
    }

    public void setCrashReportSupplier(CrashReport crashReport) {
        this.crashReportSupplier = () -> crashReport;
    }

    private void printCrashReport() {
        if (this.crashReportSupplier != null) {
            MinecraftClient.printCrashReport(this, this.runDirectory, this.crashReportSupplier.get());
        }
    }

    public void printCrashReport(CrashReport crashReport) {
        CrashMemoryReserve.releaseMemory();
        CrashReport crashReport2 = this.addDetailsToCrashReport(crashReport);
        this.cleanUpAfterCrash();
        MinecraftClient.printCrashReport(this, this.runDirectory, crashReport2);
    }

    public static int saveCrashReport(File runDir, CrashReport crashReport) {
        Path path = runDir.toPath().resolve("crash-reports");
        Path path2 = path.resolve("crash-" + Util.getFormattedCurrentTime() + "-client.txt");
        Bootstrap.println(crashReport.asString(ReportType.MINECRAFT_CRASH_REPORT));
        if (crashReport.getFile() != null) {
            Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + String.valueOf(crashReport.getFile().toAbsolutePath()));
            return -1;
        }
        if (crashReport.writeToFile(path2, ReportType.MINECRAFT_CRASH_REPORT)) {
            Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + String.valueOf(path2.toAbsolutePath()));
            return -1;
        }
        Bootstrap.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
        return -2;
    }

    public static void printCrashReport(@Nullable MinecraftClient client, File runDirectory, CrashReport crashReport) {
        int i = MinecraftClient.saveCrashReport(runDirectory, crashReport);
        if (client != null) {
            client.soundManager.stopAbruptly();
        }
        System.exit(1);
    }

    public boolean forcesUnicodeFont() {
        return this.options.getForceUnicodeFont().getValue();
    }

    public CompletableFuture<Void> reloadResources() {
        return this.reloadResources(false, null);
    }

    private CompletableFuture<Void> reloadResources(boolean force, @Nullable LoadingContext loadingContext) {
        if (this.resourceReloadFuture != null) {
            return this.resourceReloadFuture;
        }
        CompletableFuture<Void> completableFuture = new CompletableFuture<Void>();
        if (!force && this.overlay instanceof SplashOverlay) {
            this.resourceReloadFuture = completableFuture;
            return completableFuture;
        }
        this.resourcePackManager.scanPacks();
        List<ResourcePack> list = this.resourcePackManager.createResourcePacks();
        if (!force) {
            this.resourceReloadLogger.reload(ResourceReloadLogger.ReloadReason.MANUAL, list);
        }
        this.setOverlay(new SplashOverlay(this, this.resourceManager.reload(Util.getMainWorkerExecutor().named("resourceLoad"), this, COMPLETED_UNIT_FUTURE, list), error -> Util.ifPresentOrElse(error, throwable -> {
            if (force) {
                this.serverResourcePackLoader.onForcedReloadFailure();
                this.onForcedResourceReloadFailure();
            } else {
                this.handleResourceReloadException((Throwable)throwable, loadingContext);
            }
        }, () -> {
            this.worldRenderer.reload();
            this.resourceReloadLogger.finish();
            this.serverResourcePackLoader.onReloadSuccess();
            completableFuture.complete(null);
            this.onFinishedLoading(loadingContext);
        }), !force));
        return completableFuture;
    }

    private void checkGameData() {
        boolean bl = false;
        BlockModels blockModels = this.getBlockRenderManager().getModels();
        BlockStateModel blockStateModel = blockModels.getModelManager().getMissingModel();
        for (Block block : Registries.BLOCK) {
            for (BlockState blockState : block.getStateManager().getStates()) {
                BlockStateModel blockStateModel2;
                if (blockState.getRenderType() != BlockRenderType.MODEL || (blockStateModel2 = blockModels.getModel(blockState)) != blockStateModel) continue;
                LOGGER.debug("Missing model for: {}", (Object)blockState);
                bl = true;
            }
        }
        Sprite sprite = blockStateModel.particleSprite();
        for (Block block2 : Registries.BLOCK) {
            for (BlockState blockState2 : block2.getStateManager().getStates()) {
                Sprite sprite2 = blockModels.getModelParticleSprite(blockState2);
                if (blockState2.isAir() || sprite2 != sprite) continue;
                LOGGER.debug("Missing particle icon for: {}", (Object)blockState2);
            }
        }
        Registries.ITEM.streamEntries().forEach(item -> {
            Item item2 = (Item)item.value();
            String string = item2.getTranslationKey();
            String string2 = Text.translatable(string).getString();
            if (string2.toLowerCase(Locale.ROOT).equals(item2.getTranslationKey())) {
                LOGGER.debug("Missing translation for: {} {} {}", new Object[]{item.registryKey().getValue(), string, item2});
            }
        });
        bl |= HandledScreens.isMissingScreens();
        if (bl |= EntityRenderers.isMissingRendererFactories()) {
            throw new IllegalStateException("Your game data is foobar, fix the errors above!");
        }
    }

    public LevelStorage getLevelStorage() {
        return this.levelStorage;
    }

    private void openChatScreen(String text) {
        ChatRestriction chatRestriction = this.getChatRestriction();
        if (!chatRestriction.allowsChat(this.isInSingleplayer())) {
            if (this.inGameHud.shouldShowChatDisabledScreen()) {
                this.inGameHud.setCanShowChatDisabledScreen(false);
                this.setScreen(new ConfirmLinkScreen(confirmed -> {
                    if (confirmed) {
                        Util.getOperatingSystem().open(Urls.JAVA_ACCOUNT_SETTINGS);
                    }
                    this.setScreen(null);
                }, ChatRestriction.MORE_INFO_TEXT, Urls.JAVA_ACCOUNT_SETTINGS, true));
            } else {
                Text text2 = chatRestriction.getDescription();
                this.inGameHud.setOverlayMessage(text2, false);
                this.narratorManager.narrateSystemImmediately(text2);
                this.inGameHud.setCanShowChatDisabledScreen(chatRestriction == ChatRestriction.DISABLED_BY_PROFILE);
            }
        } else {
            this.setScreen(new ChatScreen(text));
        }
    }

    public void setScreen(@Nullable Screen screen) {
        if (SharedConstants.isDevelopment && Thread.currentThread() != this.thread) {
            LOGGER.error("setScreen called from non-game thread");
        }
        if (this.currentScreen != null) {
            this.currentScreen.removed();
        } else {
            this.setNavigationType(GuiNavigationType.NONE);
        }
        if (screen == null && this.disconnecting) {
            throw new IllegalStateException("Trying to return to in-game GUI during disconnection");
        }
        if (screen == null && this.world == null) {
            screen = new TitleScreen();
        } else if (screen == null && this.player.isDead()) {
            if (this.player.showsDeathScreen()) {
                screen = new DeathScreen(null, this.world.net_minecraft_client_world_ClientWorld$Properties_getLevelProperties().isHardcore());
            } else {
                this.player.requestRespawn();
            }
        }
        this.currentScreen = screen;
        if (this.currentScreen != null) {
            this.currentScreen.onDisplayed();
        }
        if (screen != null) {
            this.mouse.unlockCursor();
            KeyBinding.unpressAll();
            screen.init(this, this.window.getScaledWidth(), this.window.getScaledHeight());
            this.skipGameRender = false;
        } else {
            this.soundManager.resumeAll();
            this.mouse.lockCursor();
        }
        this.updateWindowTitle();
    }

    public void setOverlay(@Nullable Overlay overlay) {
        this.overlay = overlay;
    }

    public void stop() {
        try {
            LOGGER.info("Stopping!");
            try {
                this.narratorManager.destroy();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                if (this.world != null) {
                    this.world.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
                }
                this.disconnectWithProgressScreen();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (this.currentScreen != null) {
                this.currentScreen.removed();
            }
            this.close();
        }
        catch (Throwable throwable) {
            Util.nanoTimeSupplier = System::nanoTime;
            if (this.crashReportSupplier == null) {
                System.exit(0);
            }
            throw throwable;
        }
        Util.nanoTimeSupplier = System::nanoTime;
        if (this.crashReportSupplier == null) {
            System.exit(0);
        }
    }

    @Override
    public void close() {
        if (this.currentGlTimerQuery != null) {
            this.currentGlTimerQuery.close();
        }
        try {
            this.telemetryManager.close();
            this.regionalComplianciesManager.close();
            this.bakedModelManager.close();
            this.fontManager.close();
            this.gameRenderer.close();
            this.shaderLoader.close();
            this.worldRenderer.close();
            this.soundManager.close();
            this.particleManager.clearAtlas();
            this.paintingManager.close();
            this.mapDecorationsAtlasManager.close();
            this.guiAtlasManager.close();
            this.mapTextureManager.close();
            this.textureManager.close();
            this.resourceManager.close();
            if (this.tracyFrameCapturer != null) {
                this.tracyFrameCapturer.close();
            }
            FreeTypeUtil.release();
            Util.shutdownExecutors();
            RenderSystem.getDevice().close();
            this.windowProvider.close();
            this.window.close();
        }
        catch (Throwable throwable) {
            try {
                LOGGER.error("Shutdown failure!", throwable);
                throw throwable;
            }
            catch (Throwable throwable2) {
                this.windowProvider.close();
                this.window.close();
                throw throwable2;
            }
        }
    }

    private void render(boolean tick) {
        boolean bl;
        Runnable runnable;
        this.window.setPhase("Pre render");
        if (this.window.shouldClose()) {
            this.scheduleStop();
        }
        if (this.resourceReloadFuture != null && !(this.overlay instanceof SplashOverlay)) {
            CompletableFuture<Void> completableFuture = this.resourceReloadFuture;
            this.resourceReloadFuture = null;
            this.reloadResources().thenRun(() -> completableFuture.complete(null));
        }
        while ((runnable = this.renderTaskQueue.poll()) != null) {
            runnable.run();
        }
        int i = this.renderTickCounter.beginRenderTick(Util.getMeasuringTimeMs(), tick);
        Profiler profiler = Profilers.get();
        if (tick) {
            profiler.push("scheduledExecutables");
            this.runTasks();
            profiler.pop();
            profiler.push("tick");
            for (int j = 0; j < Math.min(10, 1); ++j) {
                profiler.visit("clientTick");
                this.tick();
            }
            profiler.pop();
        }
        this.window.setPhase("Render");
        profiler.push("gpuAsync");
        RenderSystem.executePendingTasks();
        profiler.swap("sound");
        this.soundManager.updateListenerPosition(this.gameRenderer.getCamera());
        profiler.swap("toasts");
        this.toastManager.update();
        profiler.swap("render");
        long l = Util.getMeasuringTimeNano();
        if (this.getDebugHud().shouldShowDebugHud() || this.recorder.isActive()) {
            boolean bl2 = bl = this.currentGlTimerQuery == null || this.currentGlTimerQuery.isResultAvailable();
            if (bl) {
                GlTimer.getInstance().ifPresent(GlTimer::beginProfile);
            }
        } else {
            bl = false;
            this.gpuUtilizationPercentage = 0.0;
        }
        Framebuffer framebuffer = this.getFramebuffer();
        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(framebuffer.getColorAttachment(), 0, framebuffer.getDepthAttachment(), 1.0);
        profiler.push("mouse");
        this.mouse.tick();
        profiler.pop();
        if (!this.skipGameRender) {
            profiler.swap("gameRenderer");
            this.gameRenderer.render(this.renderTickCounter, tick);
            profiler.pop();
        }
        profiler.push("blit");
        if (!this.window.hasZeroWidthOrHeight()) {
            framebuffer.blitToScreen();
        }
        this.renderTime = Util.getMeasuringTimeNano() - l;
        if (bl) {
            GlTimer.getInstance().ifPresent(glTimer -> {
                this.currentGlTimerQuery = glTimer.endProfile();
            });
        }
        profiler.swap("updateDisplay");
        if (this.tracyFrameCapturer != null) {
            this.tracyFrameCapturer.upload();
            this.tracyFrameCapturer.capture(framebuffer);
        }
        this.window.swapBuffers(this.tracyFrameCapturer);
        int k = this.inactivityFpsLimiter.update();
        if (k < 260) {
            RenderSystem.limitDisplayFPS(k);
        }
        profiler.swap("yield");
        Thread.yield();
        profiler.pop();
        this.window.setPhase("Post render");
        ++this.fpsCounter;
        boolean bl2 = this.paused;
        boolean bl3 = this.paused = this.isIntegratedServerRunning() && (this.currentScreen != null && this.currentScreen.shouldPause() || this.overlay != null && this.overlay.pausesGame()) && !this.server.isRemote();
        if (!bl2 && this.paused) {
            this.soundManager.pauseAllExcept(SoundCategory.MUSIC, SoundCategory.UI);
        }
        this.renderTickCounter.tick(this.paused);
        this.renderTickCounter.setTickFrozen(!this.shouldTick());
        long m = Util.getMeasuringTimeNano();
        long n = m - this.lastMetricsSampleTime;
        if (bl) {
            this.metricsSampleDuration = n;
        }
        this.getDebugHud().pushToFrameLog(n);
        this.lastMetricsSampleTime = m;
        profiler.push("fpsUpdate");
        if (this.currentGlTimerQuery != null && this.currentGlTimerQuery.isResultAvailable()) {
            this.gpuUtilizationPercentage = (double)this.currentGlTimerQuery.queryResult() * 100.0 / (double)this.metricsSampleDuration;
        }
        while (Util.getMeasuringTimeMs() >= this.nextDebugInfoUpdateTime + 1000L) {
            Object string = this.gpuUtilizationPercentage > 0.0 ? " GPU: " + (this.gpuUtilizationPercentage > 100.0 ? String.valueOf(Formatting.RED) + "100%" : Math.round(this.gpuUtilizationPercentage) + "%") : "";
            currentFps = this.fpsCounter;
            this.fpsDebugString = String.format(Locale.ROOT, "%d fps T: %s%s%s%s B: %d%s", currentFps, k == 260 ? "inf" : Integer.valueOf(k), this.options.getEnableVsync().getValue() != false ? " vsync " : " ", this.options.getGraphicsMode().getValue(), this.options.getCloudRenderMode().getValue() == CloudRenderMode.OFF ? "" : (this.options.getCloudRenderMode().getValue() == CloudRenderMode.FAST ? " fast-clouds" : " fancy-clouds"), this.options.getBiomeBlendRadius().getValue(), string);
            this.nextDebugInfoUpdateTime += 1000L;
            this.fpsCounter = 0;
        }
        profiler.pop();
    }

    private Profiler startMonitor(boolean active, @Nullable TickDurationMonitor monitor) {
        Profiler profiler;
        if (!active) {
            this.tickTimeTracker.disable();
            if (!this.recorder.isActive() && monitor == null) {
                return DummyProfiler.INSTANCE;
            }
        }
        if (active) {
            if (!this.tickTimeTracker.isActive()) {
                this.trackingTick = 0;
                this.tickTimeTracker.enable();
            }
            ++this.trackingTick;
            profiler = this.tickTimeTracker.getProfiler();
        } else {
            profiler = DummyProfiler.INSTANCE;
        }
        if (this.recorder.isActive()) {
            profiler = Profiler.union(profiler, this.recorder.getProfiler());
        }
        return TickDurationMonitor.tickProfiler(profiler, monitor);
    }

    private void endMonitor(boolean active, @Nullable TickDurationMonitor monitor) {
        if (monitor != null) {
            monitor.endTick();
        }
        PieChart pieChart = this.getDebugHud().getPieChart();
        if (active) {
            pieChart.setProfileResult(this.tickTimeTracker.getResult());
        } else {
            pieChart.setProfileResult(null);
        }
    }

    @Override
    public void onResolutionChanged() {
        int i = this.window.calculateScaleFactor(this.options.getGuiScale().getValue(), this.forcesUnicodeFont());
        this.window.setScaleFactor(i);
        if (this.currentScreen != null) {
            this.currentScreen.resize(this, this.window.getScaledWidth(), this.window.getScaledHeight());
        }
        Framebuffer framebuffer = this.getFramebuffer();
        framebuffer.resize(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.gameRenderer.onResized(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.mouse.onResolutionChanged();
    }

    @Override
    public void onCursorEnterChanged() {
        this.mouse.setResolutionChanged();
    }

    public int getCurrentFps() {
        return currentFps;
    }

    public long getRenderTime() {
        return this.renderTime;
    }

    private void cleanUpAfterCrash() {
        CrashMemoryReserve.releaseMemory();
        try {
            if (this.integratedServerRunning && this.server != null) {
                this.server.stop(true);
            }
            this.disconnectWithSavingScreen();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        System.gc();
    }

    public boolean toggleDebugProfiler(Consumer<Text> chatMessageSender) {
        Consumer<Path> consumer4;
        if (this.recorder.isActive()) {
            this.stopRecorder();
            return false;
        }
        Consumer<ProfileResult> consumer = result -> {
            if (result == EmptyProfileResult.INSTANCE) {
                return;
            }
            int i = result.getTickSpan();
            double d = (double)result.getTimeSpan() / (double)TimeHelper.SECOND_IN_NANOS;
            this.execute(() -> chatMessageSender.accept(Text.translatable("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", d), 1, String.format(Locale.ROOT, "%.2f", 1.0 / d))));
        };
        Consumer<Path> consumer2 = path -> {
            MutableText text = Text.literal(path.toString()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent.OpenFile(path.getParent())));
            this.execute(() -> chatMessageSender.accept(Text.translatable("debug.profiling.stop", text)));
        };
        SystemDetails systemDetails = MinecraftClient.addSystemDetailsToCrashReport(new SystemDetails(), this, this.languageManager, this.gameVersion, this.options);
        Consumer<List> consumer3 = files -> {
            Path path = this.saveProfilingResult(systemDetails, (List<Path>)files);
            consumer2.accept(path);
        };
        if (this.server == null) {
            consumer4 = path -> consumer3.accept((List)ImmutableList.of((Object)path));
        } else {
            this.server.addSystemDetails(systemDetails);
            CompletableFuture completableFuture = new CompletableFuture();
            CompletableFuture completableFuture2 = new CompletableFuture();
            CompletableFuture.allOf(completableFuture, completableFuture2).thenRunAsync(() -> consumer3.accept((List)ImmutableList.of((Object)((Path)completableFuture.join()), (Object)((Path)completableFuture2.join()))), Util.getIoWorkerExecutor());
            this.server.setupRecorder(result -> {}, completableFuture2::complete);
            consumer4 = completableFuture::complete;
        }
        this.recorder = DebugRecorder.of(new ClientSamplerSource(Util.nanoTimeSupplier, this.worldRenderer), Util.nanoTimeSupplier, Util.getIoWorkerExecutor(), new RecordDumper("client"), result -> {
            this.recorder = DummyRecorder.INSTANCE;
            consumer.accept((ProfileResult)result);
        }, consumer4);
        return true;
    }

    private void stopRecorder() {
        this.recorder.stop();
        if (this.server != null) {
            this.server.stopRecorder();
        }
    }

    private void forceStopRecorder() {
        this.recorder.forceStop();
        if (this.server != null) {
            this.server.forceStopRecorder();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Path saveProfilingResult(SystemDetails details, List<Path> files) {
        Path path;
        ServerInfo serverInfo;
        String string = this.isInSingleplayer() ? this.getServer().getSaveProperties().getLevelName() : ((serverInfo = this.getCurrentServerEntry()) != null ? serverInfo.name : "unknown");
        try {
            String string2 = String.format(Locale.ROOT, "%s-%s-%s", Util.getFormattedCurrentTime(), string, SharedConstants.getGameVersion().id());
            String string3 = PathUtil.getNextUniqueName(RecordDumper.DEBUG_PROFILING_DIRECTORY, string2, ".zip");
            path = RecordDumper.DEBUG_PROFILING_DIRECTORY.resolve(string3);
        }
        catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
        try (ZipCompressor zipCompressor = new ZipCompressor(path);){
            zipCompressor.write(Paths.get("system.txt", new String[0]), details.collect());
            zipCompressor.write(Paths.get("client", new String[0]).resolve(this.options.getOptionsFile().getName()), this.options.collectProfiledOptions());
            files.forEach(zipCompressor::copyAll);
        }
        catch (Throwable throwable) {
            for (Path path3 : files) {
                try {
                    FileUtils.forceDelete((File)path3.toFile());
                }
                catch (IOException iOException3) {
                    LOGGER.warn("Failed to delete temporary profiling result {}", (Object)path3, (Object)iOException3);
                }
            }
            throw throwable;
        }
        for (Path path2 : files) {
            try {
                FileUtils.forceDelete((File)path2.toFile());
            }
            catch (IOException iOException2) {
                LOGGER.warn("Failed to delete temporary profiling result {}", (Object)path2, (Object)iOException2);
            }
        }
        return path;
    }

    public void scheduleStop() {
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void openGameMenu(boolean pauseOnly) {
        boolean bl;
        if (this.currentScreen != null) {
            return;
        }
        boolean bl2 = bl = this.isIntegratedServerRunning() && !this.server.isRemote();
        if (bl) {
            this.setScreen(new GameMenuScreen(!pauseOnly));
        } else {
            this.setScreen(new GameMenuScreen(true));
        }
    }

    private void handleBlockBreaking(boolean breaking) {
        if (!breaking) {
            this.attackCooldown = 0;
        }
        if (this.attackCooldown > 0 || this.player.isUsingItem()) {
            return;
        }
        if (breaking && this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            Direction direction;
            BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!this.world.getBlockState(blockPos).isAir() && this.interactionManager.updateBlockBreakingProgress(blockPos, direction = blockHitResult.getSide())) {
                this.particleManager.addBlockBreakingParticles(blockPos, direction);
                this.player.swingHand(Hand.MAIN_HAND);
            }
            return;
        }
        this.interactionManager.cancelBlockBreaking();
    }

    private boolean doAttack() {
        if (this.attackCooldown > 0) {
            return false;
        }
        if (this.crosshairTarget == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.interactionManager.hasLimitedAttackSpeed()) {
                this.attackCooldown = 10;
            }
            return false;
        }
        if (this.player.isRiding()) {
            return false;
        }
        ItemStack itemStack = this.player.getStackInHand(Hand.MAIN_HAND);
        if (!itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
            return false;
        }
        boolean bl = false;
        switch (this.crosshairTarget.getType()) {
            case ENTITY: {
                this.interactionManager.attackEntity(this.player, ((EntityHitResult)this.crosshairTarget).getEntity());
                break;
            }
            case BLOCK: {
                BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (!this.world.getBlockState(blockPos).isAir()) {
                    this.interactionManager.attackBlock(blockPos, blockHitResult.getSide());
                    if (!this.world.getBlockState(blockPos).isAir()) break;
                    bl = true;
                    break;
                }
            }
            case MISS: {
                if (this.interactionManager.hasLimitedAttackSpeed()) {
                    this.attackCooldown = 10;
                }
                this.player.resetLastAttackedTicks();
            }
        }
        this.player.swingHand(Hand.MAIN_HAND);
        return bl;
    }

    private void doItemUse() {
        if (this.interactionManager.isBreakingBlock()) {
            return;
        }
        this.itemUseCooldown = 4;
        if (this.player.isRiding()) {
            return;
        }
        if (this.crosshairTarget == null) {
            LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
        }
        for (Hand hand : Hand.values()) {
            ActionResult actionResult3;
            ItemStack itemStack = this.player.getStackInHand(hand);
            if (!itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
                return;
            }
            if (this.crosshairTarget != null) {
                switch (this.crosshairTarget.getType()) {
                    case ENTITY: {
                        EntityHitResult entityHitResult = (EntityHitResult)this.crosshairTarget;
                        Entity entity = entityHitResult.getEntity();
                        if (!this.world.getWorldBorder().contains(entity.getBlockPos())) {
                            return;
                        }
                        ActionResult actionResult = this.interactionManager.interactEntityAtLocation(this.player, entity, entityHitResult, hand);
                        if (!actionResult.isAccepted()) {
                            actionResult = this.interactionManager.interactEntity(this.player, entity, hand);
                        }
                        if (!(actionResult instanceof ActionResult.Success)) break;
                        ActionResult.Success success = (ActionResult.Success)actionResult;
                        if (success.swingSource() == ActionResult.SwingSource.CLIENT) {
                            this.player.swingHand(hand);
                        }
                        return;
                    }
                    case BLOCK: {
                        BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                        int i = itemStack.getCount();
                        ActionResult actionResult2 = this.interactionManager.interactBlock(this.player, hand, blockHitResult);
                        if (actionResult2 instanceof ActionResult.Success) {
                            ActionResult.Success success2 = (ActionResult.Success)actionResult2;
                            if (success2.swingSource() == ActionResult.SwingSource.CLIENT) {
                                this.player.swingHand(hand);
                                if (!itemStack.isEmpty() && (itemStack.getCount() != i || this.player.isInCreativeMode())) {
                                    this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                                }
                            }
                            return;
                        }
                        if (!(actionResult2 instanceof ActionResult.Fail)) break;
                        return;
                    }
                }
            }
            if (itemStack.isEmpty() || !((actionResult3 = this.interactionManager.interactItem(this.player, hand)) instanceof ActionResult.Success)) continue;
            ActionResult.Success success3 = (ActionResult.Success)actionResult3;
            if (success3.swingSource() == ActionResult.SwingSource.CLIENT) {
                this.player.swingHand(hand);
            }
            this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
            return;
        }
    }

    public MusicTracker getMusicTracker() {
        return this.musicTracker;
    }

    public void tick() {
        CrashReport crashReport;
        ++this.uptimeInTicks;
        if (this.world != null && !this.paused) {
            this.world.getTickManager().step();
        }
        if (this.itemUseCooldown > 0) {
            --this.itemUseCooldown;
        }
        Profiler profiler = Profilers.get();
        profiler.push("gui");
        this.messageHandler.processDelayedMessages();
        this.inGameHud.tick(this.paused);
        profiler.pop();
        this.gameRenderer.updateCrosshairTarget(1.0f);
        this.tutorialManager.tick(this.world, this.crosshairTarget);
        profiler.push("gameMode");
        if (!this.paused && this.world != null) {
            this.interactionManager.tick();
        }
        profiler.swap("textures");
        if (this.shouldTick()) {
            this.textureManager.tick();
        }
        if (this.currentScreen == null && this.player != null) {
            if (this.player.isDead() && !(this.currentScreen instanceof DeathScreen)) {
                this.setScreen(null);
            } else if (this.player.isSleeping() && this.world != null) {
                this.setScreen(new SleepingChatScreen());
            }
        } else {
            Screen screen = this.currentScreen;
            if (screen instanceof SleepingChatScreen) {
                SleepingChatScreen sleepingChatScreen = (SleepingChatScreen)screen;
                if (!this.player.isSleeping()) {
                    sleepingChatScreen.closeChatIfEmpty();
                }
            }
        }
        if (this.currentScreen != null) {
            this.attackCooldown = 10000;
        }
        if (this.currentScreen != null) {
            try {
                this.currentScreen.tick();
            }
            catch (Throwable throwable) {
                crashReport = CrashReport.create(throwable, "Ticking screen");
                this.currentScreen.addCrashReportSection(crashReport);
                throw new CrashException(crashReport);
            }
        }
        if (!this.getDebugHud().shouldShowDebugHud()) {
            this.inGameHud.resetDebugHudChunk();
        }
        if (this.overlay == null && this.currentScreen == null) {
            profiler.swap("Keybindings");
            this.handleInputEvents();
            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }
        }
        if (this.world != null) {
            profiler.swap("gameRenderer");
            if (!this.paused) {
                this.gameRenderer.tick();
            }
            profiler.swap("levelRenderer");
            if (!this.paused) {
                this.worldRenderer.tick();
            }
            profiler.swap("level");
            if (!this.paused) {
                this.world.tickEntities();
            }
        } else if (this.gameRenderer.getPostProcessorId() != null) {
            this.gameRenderer.clearPostProcessor();
        }
        this.musicTracker.tick();
        this.soundManager.tick(this.paused);
        if (this.world != null) {
            ClientPlayNetworkHandler clientPlayNetworkHandler;
            if (!this.paused) {
                if (!this.options.joinedFirstServer && this.isConnectedToServer()) {
                    MutableText text = Text.translatable("tutorial.socialInteractions.title");
                    MutableText text2 = Text.translatable("tutorial.socialInteractions.description", TutorialManager.keyToText("socialInteractions"));
                    this.socialInteractionsToast = new TutorialToast(this.textRenderer, TutorialToast.Type.SOCIAL_INTERACTIONS, text, text2, true, 8000);
                    this.toastManager.add(this.socialInteractionsToast);
                    this.options.joinedFirstServer = true;
                    this.options.write();
                }
                this.tutorialManager.tick();
                try {
                    this.world.tick(() -> true);
                }
                catch (Throwable throwable) {
                    crashReport = CrashReport.create(throwable, "Exception in world tick");
                    if (this.world == null) {
                        CrashReportSection crashReportSection = crashReport.addElement("Affected level");
                        crashReportSection.add("Problem", "Level is null!");
                    } else {
                        this.world.addDetailsToCrashReport(crashReport);
                    }
                    throw new CrashException(crashReport);
                }
            }
            profiler.swap("animateTick");
            if (!this.paused && this.shouldTick()) {
                this.world.doRandomBlockDisplayTicks(this.player.getBlockX(), this.player.getBlockY(), this.player.getBlockZ());
            }
            profiler.swap("particles");
            if (!this.paused && this.shouldTick()) {
                this.particleManager.tick();
            }
            if ((clientPlayNetworkHandler = this.getNetworkHandler()) != null && !this.paused) {
                clientPlayNetworkHandler.sendPacket(ClientTickEndC2SPacket.INSTANCE);
            }
        } else if (this.integratedServerConnection != null) {
            profiler.swap("pendingConnection");
            this.integratedServerConnection.tick();
        }
        profiler.swap("keyboard");
        this.keyboard.pollDebugCrash();
        profiler.pop();
    }

    private boolean shouldTick() {
        return this.world == null || this.world.getTickManager().shouldTick();
    }

    private boolean isConnectedToServer() {
        return !this.integratedServerRunning || this.server != null && this.server.isRemote();
    }

    private void handleInputEvents() {
        while (this.options.togglePerspectiveKey.wasPressed()) {
            Perspective perspective = this.options.getPerspective();
            this.options.setPerspective(this.options.getPerspective().next());
            if (perspective.isFirstPerson() != this.options.getPerspective().isFirstPerson()) {
                this.gameRenderer.onCameraEntitySet(this.options.getPerspective().isFirstPerson() ? this.getCameraEntity() : null);
            }
            this.worldRenderer.scheduleTerrainUpdate();
        }
        while (this.options.smoothCameraKey.wasPressed()) {
            this.options.smoothCameraEnabled = !this.options.smoothCameraEnabled;
        }
        for (int i = 0; i < 9; ++i) {
            boolean bl = this.options.saveToolbarActivatorKey.isPressed();
            boolean bl2 = this.options.loadToolbarActivatorKey.isPressed();
            if (!this.options.hotbarKeys[i].wasPressed()) continue;
            if (this.player.isSpectator()) {
                this.inGameHud.getSpectatorHud().selectSlot(i);
                continue;
            }
            if (this.player.isInCreativeMode() && this.currentScreen == null && (bl2 || bl)) {
                CreativeInventoryScreen.onHotbarKeyPress(this, i, bl2, bl);
                continue;
            }
            this.player.getInventory().setSelectedSlot(i);
        }
        while (this.options.socialInteractionsKey.wasPressed()) {
            if (!this.isConnectedToServer()) {
                this.player.sendMessage(SOCIAL_INTERACTIONS_NOT_AVAILABLE, true);
                this.narratorManager.narrateSystemImmediately(SOCIAL_INTERACTIONS_NOT_AVAILABLE);
                continue;
            }
            if (this.socialInteractionsToast != null) {
                this.socialInteractionsToast.hide();
                this.socialInteractionsToast = null;
            }
            this.setScreen(new SocialInteractionsScreen());
        }
        while (this.options.inventoryKey.wasPressed()) {
            if (this.interactionManager.hasRidingInventory()) {
                this.player.openRidingInventory();
                continue;
            }
            this.tutorialManager.onInventoryOpened();
            this.setScreen(new InventoryScreen(this.player));
        }
        while (this.options.advancementsKey.wasPressed()) {
            this.setScreen(new AdvancementsScreen(this.player.networkHandler.getAdvancementHandler()));
        }
        while (this.options.quickActionsKey.wasPressed()) {
            this.getQuickActionsDialog().ifPresent(dialog -> this.player.networkHandler.showDialog((RegistryEntry<Dialog>)dialog, this.currentScreen));
        }
        while (this.options.swapHandsKey.wasPressed()) {
            if (this.player.isSpectator()) continue;
            this.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
        }
        while (this.options.dropKey.wasPressed()) {
            if (this.player.isSpectator() || !this.player.dropSelectedItem(Screen.hasControlDown())) continue;
            this.player.swingHand(Hand.MAIN_HAND);
        }
        while (this.options.chatKey.wasPressed()) {
            this.openChatScreen("");
        }
        if (this.currentScreen == null && this.overlay == null && this.options.commandKey.wasPressed()) {
            this.openChatScreen("/");
        }
        boolean bl3 = false;
        if (this.player.isUsingItem()) {
            if (!this.options.useKey.isPressed()) {
                this.interactionManager.stopUsingItem(this.player);
            }
            while (this.options.attackKey.wasPressed()) {
            }
            while (this.options.useKey.wasPressed()) {
            }
            while (this.options.pickItemKey.wasPressed()) {
            }
        } else {
            while (this.options.attackKey.wasPressed()) {
                bl3 |= this.doAttack();
            }
            while (this.options.useKey.wasPressed()) {
                this.doItemUse();
            }
            while (this.options.pickItemKey.wasPressed()) {
                this.doItemPick();
            }
        }
        if (this.options.useKey.isPressed() && this.itemUseCooldown == 0 && !this.player.isUsingItem()) {
            this.doItemUse();
        }
        this.handleBlockBreaking(this.currentScreen == null && !bl3 && this.options.attackKey.isPressed() && this.mouse.isCursorLocked());
    }

    private Optional<RegistryEntry<Dialog>> getQuickActionsDialog() {
        RegistryWrapper.Impl registry = this.player.networkHandler.getRegistryManager().net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.DIALOG);
        return registry.getOptional(DialogTags.QUICK_ACTIONS).flatMap(arg_0 -> MinecraftClient.method_72097((Registry)registry, arg_0));
    }

    public TelemetryManager getTelemetryManager() {
        return this.telemetryManager;
    }

    public double getGpuUtilizationPercentage() {
        return this.gpuUtilizationPercentage;
    }

    public ProfileKeys getProfileKeys() {
        return this.profileKeys;
    }

    public IntegratedServerLoader createIntegratedServerLoader() {
        return new IntegratedServerLoader(this, this.levelStorage);
    }

    public void startIntegratedServer(LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, boolean newWorld) {
        this.disconnectWithProgressScreen();
        this.worldGenProgressTracker.set(null);
        Instant instant = Instant.now();
        try {
            session.backupLevelDataFile(saveLoader.combinedDynamicRegistries().getCombinedRegistryManager(), saveLoader.saveProperties());
            ApiServices apiServices = ApiServices.create(this.authenticationService, this.runDirectory);
            apiServices.userCache().setExecutor(this);
            SkullBlockEntity.setServices(apiServices, this);
            UserCache.setUseRemote(false);
            this.server = MinecraftServer.startServer(thread -> new IntegratedServer((Thread)thread, this, session, dataPackManager, saveLoader, apiServices, spawnChunkRadius -> {
                WorldGenerationProgressTracker worldGenerationProgressTracker = WorldGenerationProgressTracker.create(spawnChunkRadius + 0);
                this.worldGenProgressTracker.set(worldGenerationProgressTracker);
                return QueueingWorldGenerationProgressListener.create(worldGenerationProgressTracker, this.renderTaskQueue::add);
            }));
            this.integratedServerRunning = true;
            this.ensureAbuseReportContext(ReporterEnvironment.ofIntegratedServer());
            this.quickPlayLogger.setWorld(QuickPlayLogger.WorldType.SINGLEPLAYER, session.getDirectoryName(), saveLoader.saveProperties().getLevelName());
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Starting integrated server");
            CrashReportSection crashReportSection = crashReport.addElement("Starting integrated server");
            crashReportSection.add("Level ID", session.getDirectoryName());
            crashReportSection.add("Level Name", () -> saveLoader.saveProperties().getLevelName());
            throw new CrashException(crashReport);
        }
        while (this.worldGenProgressTracker.get() == null) {
            Thread.yield();
        }
        LevelLoadingScreen levelLoadingScreen = new LevelLoadingScreen(this.worldGenProgressTracker.get());
        Profiler profiler = Profilers.get();
        this.setScreen(levelLoadingScreen);
        profiler.push("waitForServer");
        while (!this.server.isLoading() || this.overlay != null) {
            levelLoadingScreen.tick();
            this.render(false);
            try {
                Thread.sleep(16L);
            }
            catch (InterruptedException crashReportSection) {
                // empty catch block
            }
            this.printCrashReport();
        }
        TestManager.INSTANCE.startTicking();
        profiler.pop();
        Duration duration = Duration.between(instant, Instant.now());
        SocketAddress socketAddress = this.server.getNetworkIo().bindLocal();
        ClientConnection clientConnection = ClientConnection.connectLocal(socketAddress);
        clientConnection.connect(socketAddress.toString(), 0, new ClientLoginNetworkHandler(clientConnection, this, null, null, newWorld, duration, status -> {}, null));
        clientConnection.send(new LoginHelloC2SPacket(this.getSession().getUsername(), this.getSession().getUuidOrNull()));
        this.integratedServerConnection = clientConnection;
    }

    public void joinWorld(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason) {
        this.reset(new DownloadingTerrainScreen(() -> false, worldEntryReason));
        this.world = world;
        this.setWorld(world);
        if (!this.integratedServerRunning) {
            ApiServices apiServices = ApiServices.create(this.authenticationService, this.runDirectory);
            apiServices.userCache().setExecutor(this);
            SkullBlockEntity.setServices(apiServices, this);
            UserCache.setUseRemote(false);
        }
    }

    public void disconnectWithSavingScreen() {
        this.disconnect(new MessageScreen(SAVING_LEVEL_TEXT), false);
    }

    public void disconnectWithProgressScreen() {
        this.disconnect(new ProgressScreen(true), false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void disconnect(Screen disconnectionScreen, boolean transferring) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            this.cancelTasks();
            clientPlayNetworkHandler.unloadWorld();
            if (!transferring) {
                this.onDisconnected();
            }
        }
        this.socialInteractionsManager.unloadBlockList();
        if (this.recorder.isActive()) {
            this.forceStopRecorder();
        }
        IntegratedServer integratedServer = this.server;
        this.server = null;
        this.gameRenderer.reset();
        this.interactionManager = null;
        this.narratorManager.clear();
        this.disconnecting = true;
        try {
            this.reset(disconnectionScreen);
            if (this.world != null) {
                if (integratedServer != null) {
                    Profiler profiler = Profilers.get();
                    profiler.push("waitForServer");
                    while (!integratedServer.isStopping()) {
                        this.render(false);
                    }
                    profiler.pop();
                }
                this.inGameHud.clear();
                this.integratedServerRunning = false;
            }
            this.world = null;
            this.setWorld(null);
            this.player = null;
            this.disconnecting = false;
        }
        catch (Throwable throwable) {
            this.disconnecting = false;
            throw throwable;
        }
        SkullBlockEntity.clearServices();
    }

    public void onDisconnected() {
        this.serverResourcePackLoader.clear();
        this.runTasks();
    }

    public void enterReconfiguration(Screen reconfigurationScreen) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.clearWorld();
        }
        if (this.recorder.isActive()) {
            this.forceStopRecorder();
        }
        this.gameRenderer.reset();
        this.interactionManager = null;
        this.narratorManager.clear();
        this.disconnecting = true;
        try {
            this.reset(reconfigurationScreen);
            this.inGameHud.clear();
            this.world = null;
            this.setWorld(null);
            this.player = null;
            this.disconnecting = false;
        }
        catch (Throwable throwable) {
            this.disconnecting = false;
            throw throwable;
        }
        SkullBlockEntity.clearServices();
    }

    private void reset(Screen resettingScreen) {
        Profiler profiler = Profilers.get();
        profiler.push("forcedTick");
        this.soundManager.stopAll();
        this.cameraEntity = null;
        this.integratedServerConnection = null;
        this.setScreen(resettingScreen);
        this.render(false);
        profiler.pop();
    }

    public void setScreenAndRender(Screen screen) {
        ScopedProfiler scopedProfiler = Profilers.get().scoped("forcedTick");
        try {
            this.setScreen(screen);
            this.render(false);
            if (scopedProfiler != null) {
                scopedProfiler.close();
            }
        }
        catch (Throwable throwable) {
            if (scopedProfiler != null) {
                try {
                    scopedProfiler.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
    }

    private void setWorld(@Nullable ClientWorld world) {
        this.worldRenderer.setWorld(world);
        this.particleManager.setWorld(world);
        this.blockEntityRenderDispatcher.setWorld(world);
        this.gameRenderer.setWorld(world);
        this.updateWindowTitle();
    }

    private UserApiService.UserProperties getUserProperties() {
        return this.userPropertiesFuture.join();
    }

    public boolean isOptionalTelemetryEnabled() {
        return this.isOptionalTelemetryEnabledByApi() && this.options.getTelemetryOptInExtra().getValue() != false;
    }

    public boolean isOptionalTelemetryEnabledByApi() {
        return this.isTelemetryEnabledByApi() && this.getUserProperties().flag(UserApiService.UserFlag.OPTIONAL_TELEMETRY_AVAILABLE);
    }

    public boolean isTelemetryEnabledByApi() {
        if (SharedConstants.isDevelopment) {
            return false;
        }
        return this.getUserProperties().flag(UserApiService.UserFlag.TELEMETRY_ENABLED);
    }

    public boolean isMultiplayerEnabled() {
        return this.multiplayerEnabled && this.getUserProperties().flag(UserApiService.UserFlag.SERVERS_ALLOWED) && this.getMultiplayerBanDetails() == null && !this.isUsernameBanned();
    }

    public boolean isRealmsEnabled() {
        return this.getUserProperties().flag(UserApiService.UserFlag.REALMS_ALLOWED) && this.getMultiplayerBanDetails() == null;
    }

    @Nullable
    public BanDetails getMultiplayerBanDetails() {
        return (BanDetails)this.getUserProperties().bannedScopes().get("MULTIPLAYER");
    }

    public boolean isUsernameBanned() {
        com.mojang.authlib.yggdrasil.ProfileResult profileResult = this.gameProfileFuture.getNow(null);
        return profileResult != null && profileResult.actions().contains(ProfileActionType.FORCED_NAME_CHANGE);
    }

    public boolean shouldBlockMessages(UUID sender) {
        if (!this.getChatRestriction().allowsChat(false)) {
            return (this.player == null || !sender.equals(this.player.getUuid())) && !sender.equals(Util.NIL_UUID);
        }
        return this.socialInteractionsManager.isPlayerMuted(sender);
    }

    public ChatRestriction getChatRestriction() {
        if (this.options.getChatVisibility().getValue() == ChatVisibility.HIDDEN) {
            return ChatRestriction.DISABLED_BY_OPTIONS;
        }
        if (!this.onlineChatEnabled) {
            return ChatRestriction.DISABLED_BY_LAUNCHER;
        }
        if (!this.getUserProperties().flag(UserApiService.UserFlag.CHAT_ALLOWED)) {
            return ChatRestriction.DISABLED_BY_PROFILE;
        }
        return ChatRestriction.ENABLED;
    }

    public final boolean isDemo() {
        return this.isDemo;
    }

    @Nullable
    public ClientPlayNetworkHandler getNetworkHandler() {
        return this.player == null ? null : this.player.networkHandler;
    }

    public static boolean isHudEnabled() {
        return !MinecraftClient.instance.options.hudHidden;
    }

    public static boolean isFancyGraphicsOrBetter() {
        return MinecraftClient.instance.options.getGraphicsMode().getValue().getId() >= GraphicsMode.FANCY.getId();
    }

    public static boolean isFabulousGraphicsOrBetter() {
        return !MinecraftClient.instance.gameRenderer.isRenderingPanorama() && MinecraftClient.instance.options.getGraphicsMode().getValue().getId() >= GraphicsMode.FABULOUS.getId();
    }

    public static boolean isAmbientOcclusionEnabled() {
        return MinecraftClient.instance.options.getAo().getValue();
    }

    private void doItemPick() {
        if (this.crosshairTarget == null || this.crosshairTarget.getType() == HitResult.Type.MISS) {
            return;
        }
        boolean bl = Screen.hasControlDown();
        HitResult hitResult = this.crosshairTarget;
        Objects.requireNonNull(hitResult);
        HitResult hitResult2 = hitResult;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{BlockHitResult.class, EntityHitResult.class}, (Object)hitResult2, n)) {
            case 0: {
                BlockHitResult blockHitResult = (BlockHitResult)hitResult2;
                this.interactionManager.pickItemFromBlock(blockHitResult.getBlockPos(), bl);
                break;
            }
            case 1: {
                EntityHitResult entityHitResult = (EntityHitResult)hitResult2;
                this.interactionManager.pickItemFromEntity(entityHitResult.getEntity(), bl);
                break;
            }
        }
    }

    public CrashReport addDetailsToCrashReport(CrashReport report) {
        SystemDetails systemDetails = report.getSystemDetailsSection();
        try {
            MinecraftClient.addSystemDetailsToCrashReport(systemDetails, this, this.languageManager, this.gameVersion, this.options);
            this.addUptimesToCrashReport(report.addElement("Uptime"));
            if (this.world != null) {
                this.world.addDetailsToCrashReport(report);
            }
            if (this.server != null) {
                this.server.addSystemDetails(systemDetails);
            }
            this.resourceReloadLogger.addReloadSection(report);
        }
        catch (Throwable throwable) {
            LOGGER.error("Failed to collect details", throwable);
        }
        return report;
    }

    public static void addSystemDetailsToCrashReport(@Nullable MinecraftClient client, @Nullable LanguageManager languageManager, String version, @Nullable GameOptions options, CrashReport report) {
        SystemDetails systemDetails = report.getSystemDetailsSection();
        MinecraftClient.addSystemDetailsToCrashReport(systemDetails, client, languageManager, version, options);
    }

    private static String formatSeconds(double seconds) {
        return String.format(Locale.ROOT, "%.3fs", seconds);
    }

    private void addUptimesToCrashReport(CrashReportSection section) {
        section.add("JVM uptime", () -> MinecraftClient.formatSeconds((double)ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0));
        section.add("Wall uptime", () -> MinecraftClient.formatSeconds((double)(System.currentTimeMillis() - this.startTime) / 1000.0));
        section.add("High-res time", () -> MinecraftClient.formatSeconds((double)Util.getMeasuringTimeMs() / 1000.0));
        section.add("Client ticks", () -> String.format(Locale.ROOT, "%d ticks / %.3fs", this.uptimeInTicks, (double)this.uptimeInTicks / 20.0));
    }

    private static SystemDetails addSystemDetailsToCrashReport(SystemDetails systemDetails, @Nullable MinecraftClient client, @Nullable LanguageManager languageManager, String version, @Nullable GameOptions options) {
        systemDetails.addSection("Launched Version", () -> version);
        String string = MinecraftClient.getLauncherBrand();
        if (string != null) {
            systemDetails.addSection("Launcher name", string);
        }
        systemDetails.addSection("Backend library", RenderSystem::getBackendDescription);
        systemDetails.addSection("Backend API", RenderSystem::getApiDescription);
        systemDetails.addSection("Window size", () -> client != null ? minecraftClient.window.getFramebufferWidth() + "x" + minecraftClient.window.getFramebufferHeight() : "<not initialized>");
        systemDetails.addSection("GFLW Platform", Window::getGlfwPlatform);
        systemDetails.addSection("Render Extensions", () -> String.join((CharSequence)", ", RenderSystem.getDevice().getEnabledExtensions()));
        systemDetails.addSection("GL debug messages", () -> {
            GpuDevice gpuDevice = RenderSystem.tryGetDevice();
            if (gpuDevice == null) {
                return "<no renderer available>";
            }
            if (gpuDevice.isDebuggingEnabled()) {
                return String.join((CharSequence)"\n", gpuDevice.getLastDebugMessages());
            }
            return "<debugging unavailable>";
        });
        systemDetails.addSection("Is Modded", () -> MinecraftClient.getModStatus().getMessage());
        systemDetails.addSection("Universe", () -> client != null ? Long.toHexString(minecraftClient.UNIVERSE) : "404");
        systemDetails.addSection("Type", "Client (map_client.txt)");
        if (options != null) {
            String string2;
            if (client != null && (string2 = client.getVideoWarningManager().getWarningsAsString()) != null) {
                systemDetails.addSection("GPU Warnings", string2);
            }
            systemDetails.addSection("Graphics mode", options.getGraphicsMode().getValue().toString());
            systemDetails.addSection("Render Distance", options.getClampedViewDistance() + "/" + String.valueOf(options.getViewDistance().getValue()) + " chunks");
        }
        if (client != null) {
            systemDetails.addSection("Resource Packs", () -> ResourcePackManager.listPacks(client.getResourcePackManager().getEnabledProfiles()));
        }
        if (languageManager != null) {
            systemDetails.addSection("Current Language", () -> languageManager.getLanguage());
        }
        systemDetails.addSection("Locale", String.valueOf(Locale.getDefault()));
        systemDetails.addSection("System encoding", () -> System.getProperty("sun.jnu.encoding", "<not set>"));
        systemDetails.addSection("File encoding", () -> System.getProperty("file.encoding", "<not set>"));
        systemDetails.addSection("CPU", GLX::_getCpuInfo);
        return systemDetails;
    }

    public static MinecraftClient getInstance() {
        return instance;
    }

    public CompletableFuture<Void> reloadResourcesConcurrently() {
        return this.submit(this::reloadResources).thenCompose(future -> future);
    }

    public void ensureAbuseReportContext(ReporterEnvironment environment) {
        if (!this.abuseReportContext.environmentEquals(environment)) {
            this.abuseReportContext = AbuseReportContext.create(environment, this.userApiService);
        }
    }

    @Nullable
    public ServerInfo getCurrentServerEntry() {
        return Nullables.map(this.getNetworkHandler(), ClientPlayNetworkHandler::getServerInfo);
    }

    public boolean isInSingleplayer() {
        return this.integratedServerRunning;
    }

    public boolean isIntegratedServerRunning() {
        return this.integratedServerRunning && this.server != null;
    }

    @Nullable
    public IntegratedServer getServer() {
        return this.server;
    }

    public boolean isConnectedToLocalServer() {
        IntegratedServer integratedServer = this.getServer();
        return integratedServer != null && !integratedServer.isRemote();
    }

    public boolean uuidEquals(UUID uuid) {
        return uuid.equals(this.getSession().getUuidOrNull());
    }

    public Session getSession() {
        return this.session;
    }

    public GameProfile getGameProfile() {
        com.mojang.authlib.yggdrasil.ProfileResult profileResult = this.gameProfileFuture.join();
        if (profileResult != null) {
            return profileResult.profile();
        }
        return new GameProfile(this.session.getUuidOrNull(), this.session.getUsername());
    }

    public Proxy getNetworkProxy() {
        return this.networkProxy;
    }

    public TextureManager getTextureManager() {
        return this.textureManager;
    }

    public ShaderLoader getShaderLoader() {
        return this.shaderLoader;
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public ResourcePackManager getResourcePackManager() {
        return this.resourcePackManager;
    }

    public DefaultResourcePack getDefaultResourcePack() {
        return this.defaultResourcePack;
    }

    public ServerResourcePackLoader getServerResourcePackProvider() {
        return this.serverResourcePackLoader;
    }

    public Path getResourcePackDir() {
        return this.resourcePackDir;
    }

    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    public Function<Identifier, Sprite> getSpriteAtlas(Identifier id) {
        return this.bakedModelManager.getAtlas(id)::getSprite;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public VideoWarningManager getVideoWarningManager() {
        return this.videoWarningManager;
    }

    public SoundManager getSoundManager() {
        return this.soundManager;
    }

    public MusicInstance getMusicInstance() {
        MusicSound musicSound = Nullables.map(this.currentScreen, Screen::getMusic);
        if (musicSound != null) {
            return new MusicInstance(musicSound);
        }
        if (this.player != null) {
            World world = this.player.net_minecraft_world_World_getWorld();
            if (world.getRegistryKey() == World.END) {
                if (this.inGameHud.getBossBarHud().shouldPlayDragonMusic()) {
                    return new MusicInstance(MusicType.DRAGON);
                }
                return new MusicInstance(MusicType.END);
            }
            RegistryEntry<Biome> registryEntry = world.getBiome(this.player.getBlockPos());
            Biome biome = registryEntry.value();
            float f = biome.getMusicVolume();
            Optional<Pool<MusicSound>> optional = biome.getMusic();
            if (optional.isPresent()) {
                Optional<MusicSound> optional2 = optional.get().getOrEmpty(world.random);
                return new MusicInstance(optional2.orElse(null), f);
            }
            if (this.musicTracker.isPlayingType(MusicType.UNDERWATER) || this.player.isSubmergedInWater() && registryEntry.isIn(BiomeTags.PLAYS_UNDERWATER_MUSIC)) {
                return new MusicInstance(MusicType.UNDERWATER, f);
            }
            if (world.getRegistryKey() != World.NETHER && this.player.getAbilities().creativeMode && this.player.getAbilities().allowFlying) {
                return new MusicInstance(MusicType.CREATIVE, f);
            }
            return new MusicInstance(MusicType.GAME, f);
        }
        return new MusicInstance(MusicType.MENU);
    }

    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }

    public PlayerSkinProvider getSkinProvider() {
        return this.skinProvider;
    }

    @Nullable
    public Entity getCameraEntity() {
        return this.cameraEntity;
    }

    public void setCameraEntity(Entity entity) {
        this.cameraEntity = entity;
        this.gameRenderer.onCameraEntitySet(entity);
    }

    public boolean hasOutline(Entity entity) {
        return entity.isGlowing() || this.player != null && this.player.isSpectator() && this.options.spectatorOutlinesKey.isPressed() && entity.getType() == EntityType.PLAYER;
    }

    @Override
    protected Thread getThread() {
        return this.thread;
    }

    @Override
    public Runnable createTask(Runnable runnable) {
        return runnable;
    }

    @Override
    protected boolean canExecute(Runnable task) {
        return true;
    }

    public BlockRenderManager getBlockRenderManager() {
        return this.blockRenderManager;
    }

    public EntityRenderDispatcher getEntityRenderDispatcher() {
        return this.entityRenderDispatcher;
    }

    public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
        return this.blockEntityRenderDispatcher;
    }

    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    public MapRenderer getMapRenderer() {
        return this.mapRenderer;
    }

    public DataFixer getDataFixer() {
        return this.dataFixer;
    }

    public RenderTickCounter getRenderTickCounter() {
        return this.renderTickCounter;
    }

    public BlockColors getBlockColors() {
        return this.blockColors;
    }

    public boolean hasReducedDebugInfo() {
        return this.player != null && this.player.hasReducedDebugInfo() || this.options.getReducedDebugInfo().getValue() != false;
    }

    public ToastManager getToastManager() {
        return this.toastManager;
    }

    public TutorialManager getTutorialManager() {
        return this.tutorialManager;
    }

    public boolean isWindowFocused() {
        return this.windowFocused;
    }

    public HotbarStorage getCreativeHotbarStorage() {
        return this.creativeHotbarStorage;
    }

    public BakedModelManager getBakedModelManager() {
        return this.bakedModelManager;
    }

    public PaintingManager getPaintingManager() {
        return this.paintingManager;
    }

    public MapTextureManager getMapTextureManager() {
        return this.mapTextureManager;
    }

    public MapDecorationsAtlasManager getMapDecorationsAtlasManager() {
        return this.mapDecorationsAtlasManager;
    }

    public GuiAtlasManager getGuiAtlasManager() {
        return this.guiAtlasManager;
    }

    public WaypointStyleAssetManager getWaypointStyleAssetManager() {
        return this.waypointStyleAssetManager;
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        this.windowFocused = focused;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Text takePanorama(File directory) {
        MutableText mutableText;
        int i = 4;
        int j = 4096;
        int k = 4096;
        int l = this.window.getFramebufferWidth();
        int m = this.window.getFramebufferHeight();
        Framebuffer framebuffer = this.getFramebuffer();
        float f = this.player.getPitch();
        float g = this.player.getYaw();
        float h = this.player.lastPitch;
        float n = this.player.lastYaw;
        this.gameRenderer.setBlockOutlineEnabled(false);
        try {
            this.gameRenderer.setRenderingPanorama(true);
            this.window.setFramebufferWidth(4096);
            this.window.setFramebufferHeight(4096);
            framebuffer.resize(4096, 4096);
            for (int o = 0; o < 6; ++o) {
                switch (o) {
                    case 0: {
                        this.player.setYaw(g);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 1: {
                        this.player.setYaw((g + 90.0f) % 360.0f);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 2: {
                        this.player.setYaw((g + 180.0f) % 360.0f);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 3: {
                        this.player.setYaw((g - 90.0f) % 360.0f);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 4: {
                        this.player.setYaw(g);
                        this.player.setPitch(-90.0f);
                        break;
                    }
                    default: {
                        this.player.setYaw(g);
                        this.player.setPitch(90.0f);
                    }
                }
                this.player.lastYaw = this.player.getYaw();
                this.player.lastPitch = this.player.getPitch();
                this.gameRenderer.renderWorld(RenderTickCounter.ONE);
                try {
                    Thread.sleep(10L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                ScreenshotRecorder.saveScreenshot(directory, "panorama_" + o + ".png", framebuffer, 4, message -> {});
            }
            MutableText text = Text.literal(directory.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent.OpenFile(directory.getAbsoluteFile())));
            mutableText = Text.translatable("screenshot.success", text);
        }
        catch (Exception exception) {
            MutableText mutableText2;
            try {
                LOGGER.error("Couldn't save image", (Throwable)exception);
                mutableText2 = Text.translatable("screenshot.failure", exception.getMessage());
            }
            catch (Throwable throwable) {
                this.player.setPitch(f);
                this.player.setYaw(g);
                this.player.lastPitch = h;
                this.player.lastYaw = n;
                this.gameRenderer.setBlockOutlineEnabled(true);
                this.window.setFramebufferWidth(l);
                this.window.setFramebufferHeight(m);
                framebuffer.resize(l, m);
                this.gameRenderer.setRenderingPanorama(false);
                throw throwable;
            }
            this.player.setPitch(f);
            this.player.setYaw(g);
            this.player.lastPitch = h;
            this.player.lastYaw = n;
            this.gameRenderer.setBlockOutlineEnabled(true);
            this.window.setFramebufferWidth(l);
            this.window.setFramebufferHeight(m);
            framebuffer.resize(l, m);
            this.gameRenderer.setRenderingPanorama(false);
            return mutableText2;
        }
        this.player.setPitch(f);
        this.player.setYaw(g);
        this.player.lastPitch = h;
        this.player.lastYaw = n;
        this.gameRenderer.setBlockOutlineEnabled(true);
        this.window.setFramebufferWidth(l);
        this.window.setFramebufferHeight(m);
        framebuffer.resize(l, m);
        this.gameRenderer.setRenderingPanorama(false);
        return mutableText;
    }

    @Nullable
    public WorldGenerationProgressTracker getWorldGenerationProgressTracker() {
        return this.worldGenProgressTracker.get();
    }

    public SplashTextResourceSupplier getSplashTextLoader() {
        return this.splashTextLoader;
    }

    @Nullable
    public Overlay getOverlay() {
        return this.overlay;
    }

    public SocialInteractionsManager getSocialInteractionsManager() {
        return this.socialInteractionsManager;
    }

    public Window getWindow() {
        return this.window;
    }

    public InactivityFpsLimiter getInactivityFpsLimiter() {
        return this.inactivityFpsLimiter;
    }

    public DebugHud getDebugHud() {
        return this.inGameHud.getDebugHud();
    }

    public BufferBuilderStorage getBufferBuilders() {
        return this.bufferBuilders;
    }

    public void setMipmapLevels(int mipmapLevels) {
        this.bakedModelManager.setMipmapLevels(mipmapLevels);
    }

    public LoadedEntityModels getLoadedEntityModels() {
        return this.bakedModelManager.getEntityModelsSupplier().get();
    }

    public boolean shouldFilterText() {
        return this.getUserProperties().flag(UserApiService.UserFlag.PROFANITY_FILTER_ENABLED);
    }

    public void loadBlockList() {
        this.socialInteractionsManager.loadBlockList();
        this.getProfileKeys().fetchKeyPair();
    }

    @Nullable
    public SignatureVerifier getServicesSignatureVerifier() {
        return SignatureVerifier.create(this.authenticationService.getServicesKeySet(), ServicesKeyType.PROFILE_KEY);
    }

    public boolean providesProfileKeys() {
        return !this.authenticationService.getServicesKeySet().keys(ServicesKeyType.PROFILE_KEY).isEmpty();
    }

    public GuiNavigationType getNavigationType() {
        return this.navigationType;
    }

    public void setNavigationType(GuiNavigationType navigationType) {
        this.navigationType = navigationType;
    }

    public NarratorManager getNarratorManager() {
        return this.narratorManager;
    }

    public MessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    public AbuseReportContext getAbuseReportContext() {
        return this.abuseReportContext;
    }

    public RealmsPeriodicCheckers getRealmsPeriodicCheckers() {
        return this.realmsPeriodicCheckers;
    }

    public QuickPlayLogger getQuickPlayLogger() {
        return this.quickPlayLogger;
    }

    public CommandHistoryManager getCommandHistoryManager() {
        return this.commandHistoryManager;
    }

    public SymlinkFinder getSymlinkFinder() {
        return this.symlinkFinder;
    }

    private float getTargetMillisPerTick(float millis) {
        TickManager tickManager;
        if (this.world != null && (tickManager = this.world.getTickManager()).shouldTick()) {
            return Math.max(millis, tickManager.getMillisPerTick());
        }
        return millis;
    }

    public ItemModelManager getItemModelManager() {
        return this.itemModelManager;
    }

    @Nullable
    public static String getLauncherBrand() {
        return System.getProperty("minecraft.launcher.brand");
    }

    private static Optional method_72097(Registry registry, RegistryEntryList.Named quickActionsDialogs) {
        if (quickActionsDialogs.size() == 0) {
            return Optional.empty();
        }
        if (quickActionsDialogs.size() == 1) {
            return Optional.of(quickActionsDialogs.get(0));
        }
        return registry.getOptional(Dialogs.QUICK_ACTIONS);
    }

    static {
        LOGGER = LogUtils.getLogger();
        IS_SYSTEM_MAC = Util.getOperatingSystem() == Util.OperatingSystem.OSX;
        DEFAULT_FONT_ID = Identifier.ofVanilla("default");
        UNICODE_FONT_ID = Identifier.ofVanilla("uniform");
        ALT_TEXT_RENDERER_ID = Identifier.ofVanilla("alt");
        REGIONAL_COMPLIANCIES_ID = Identifier.ofVanilla("regional_compliancies.json");
        COMPLETED_UNIT_FUTURE = CompletableFuture.completedFuture(Unit.INSTANCE);
        SOCIAL_INTERACTIONS_NOT_AVAILABLE = Text.translatable("multiplayer.socialInteractions.not_available");
        SAVING_LEVEL_TEXT = Text.translatable("menu.savingLevel");
    }

    @Environment(value=EnvType.CLIENT)
    static final class LoadingContext
    extends Record {
        final private RealmsClient realmsClient;
        final RunArgs.QuickPlay quickPlayData;

        LoadingContext(RealmsClient realmsClient, RunArgs.QuickPlay quickPlay) {
            this.realmsClient = realmsClient;
            this.quickPlayData = quickPlay;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{LoadingContext.class, "realmsClient;quickPlayData", "realmsClient", "quickPlayData"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LoadingContext.class, "realmsClient;quickPlayData", "realmsClient", "quickPlayData"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LoadingContext.class, "realmsClient;quickPlayData", "realmsClient", "quickPlayData"}, this, object);
        }

        public RealmsClient realmsClient() {
            return this.realmsClient;
        }

        public RunArgs.QuickPlay quickPlayData() {
            return this.quickPlayData;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract sealed class ChatRestriction
    extends Enum<ChatRestriction> {
        final static public ChatRestriction ENABLED = new ChatRestriction(ScreenTexts.EMPTY){

            @Override
            public boolean allowsChat(boolean singlePlayer) {
                return true;
            }
        };
        final static public ChatRestriction DISABLED_BY_OPTIONS = new ChatRestriction(Text.translatable("chat.disabled.options").formatted(Formatting.RED)){

            @Override
            public boolean allowsChat(boolean singlePlayer) {
                return false;
            }
        };
        final static public ChatRestriction DISABLED_BY_LAUNCHER = new ChatRestriction(Text.translatable("chat.disabled.launcher").formatted(Formatting.RED)){

            @Override
            public boolean allowsChat(boolean singlePlayer) {
                return singlePlayer;
            }
        };
        final static public ChatRestriction DISABLED_BY_PROFILE = new ChatRestriction(Text.translatable("chat.disabled.profile", Text.keybind(MinecraftClient.instance.options.chatKey.getTranslationKey())).formatted(Formatting.RED)){

            @Override
            public boolean allowsChat(boolean singlePlayer) {
                return singlePlayer;
            }
        };
        final static Text MORE_INFO_TEXT;
        final private Text description;
        final static private ChatRestriction[] field_28945;

        public static ChatRestriction[] values() {
            return (ChatRestriction[])field_28945.clone();
        }

        public static ChatRestriction valueOf(String string) {
            return Enum.valueOf(ChatRestriction.class, string);
        }

        ChatRestriction(Text description) {
            this.description = description;
        }

        public Text getDescription() {
            return this.description;
        }

        public abstract boolean allowsChat(boolean var1);

        private static ChatRestriction[] method_36862() {
            return new ChatRestriction[]{ENABLED, DISABLED_BY_OPTIONS, DISABLED_BY_LAUNCHER, DISABLED_BY_PROFILE};
        }

        static {
            field_28945 = ChatRestriction.method_36862();
            MORE_INFO_TEXT = Text.translatable("chat.disabled.profile.moreInfo");
        }
    }
}

