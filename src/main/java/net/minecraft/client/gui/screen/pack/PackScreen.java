/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.hash.Hashing
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.pack;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.gui.screen.world.SymlinkWarningScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackOpener;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.path.SymlinkEntry;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PackScreen
extends Screen {
    final static Logger LOGGER = LogUtils.getLogger();
    final static private Text AVAILABLE_TITLE = Text.translatable("pack.available.title");
    final static private Text SELECTED_TITLE = Text.translatable("pack.selected.title");
    final static private Text OPEN_FOLDER = Text.translatable("pack.openFolder");
    final static private int field_32395 = 200;
    final static private Text DROP_INFO = Text.translatable("pack.dropInfo").formatted(Formatting.GRAY);
    final static private Text FOLDER_INFO = Text.translatable("pack.folderInfo");
    final static private int field_32396 = 20;
    final static private Identifier UNKNOWN_PACK = Identifier.ofVanilla("textures/misc/unknown_pack.png");
    final private ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    final private ResourcePackOrganizer organizer;
    @Nullable
    private DirectoryWatcher directoryWatcher;
    private long refreshTimeout;
    private PackListWidget availablePackList;
    private PackListWidget selectedPackList;
    final private Path file;
    private ButtonWidget doneButton;
    final private Map<String, Identifier> iconTextures = Maps.newHashMap();

    public PackScreen(ResourcePackManager resourcePackManager, Consumer<ResourcePackManager> applier, Path file, Text title) {
        super(title);
        this.organizer = new ResourcePackOrganizer(this::updatePackLists, this::getPackIconTexture, resourcePackManager, applier);
        this.file = file;
        this.directoryWatcher = DirectoryWatcher.create(file);
    }

    @Override
    public void close() {
        this.organizer.apply();
        this.closeDirectoryWatcher();
    }

    private void closeDirectoryWatcher() {
        if (this.directoryWatcher != null) {
            try {
                this.directoryWatcher.close();
                this.directoryWatcher = null;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    protected void init() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addHeader(DirectionalLayoutWidget.vertical().spacing(5));
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add(new TextWidget(this.getTitle(), this.textRenderer));
        directionalLayoutWidget.add(new TextWidget(DROP_INFO, this.textRenderer));
        this.availablePackList = this.addDrawableChild(new PackListWidget(this.client, this, 200, this.height - 66, AVAILABLE_TITLE));
        this.selectedPackList = this.addDrawableChild(new PackListWidget(this.client, this, 200, this.height - 66, SELECTED_TITLE));
        DirectionalLayoutWidget directionalLayoutWidget2 = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget2.add(ButtonWidget.builder(OPEN_FOLDER, button -> Util.getOperatingSystem().open(this.file)).tooltip(Tooltip.of(FOLDER_INFO)).build());
        this.doneButton = directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).build());
        this.refresh();
        this.layout.forEachChild(element -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(element);
        });
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        this.availablePackList.position(200, this.layout);
        this.availablePackList.setX(this.width / 2 - 15 - 200);
        this.selectedPackList.position(200, this.layout);
        this.selectedPackList.setX(this.width / 2 + 15);
    }

    @Override
    public void tick() {
        if (this.directoryWatcher != null) {
            try {
                if (this.directoryWatcher.pollForChange()) {
                    this.refreshTimeout = 20L;
                }
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to poll for directory {} changes, stopping", (Object)this.file);
                this.closeDirectoryWatcher();
            }
        }
        if (this.refreshTimeout > 0L && --this.refreshTimeout == 0L) {
            this.refresh();
        }
    }

    private void updatePackLists() {
        this.updatePackList(this.selectedPackList, this.organizer.getEnabledPacks());
        this.updatePackList(this.availablePackList, this.organizer.getDisabledPacks());
        this.doneButton.active = !this.selectedPackList.children().isEmpty();
    }

    private void updatePackList(PackListWidget widget, Stream<ResourcePackOrganizer.Pack> packs) {
        widget.children().clear();
        PackListWidget.ResourcePackEntry resourcePackEntry = (PackListWidget.ResourcePackEntry)widget.getSelectedOrNull();
        String string = resourcePackEntry == null ? "" : resourcePackEntry.getName();
        widget.setSelected(null);
        packs.forEach(pack -> {
            PackListWidget.ResourcePackEntry resourcePackEntry = new PackListWidget.ResourcePackEntry(this.client, widget, (ResourcePackOrganizer.Pack)pack);
            widget.children().add(resourcePackEntry);
            if (pack.getName().equals(string)) {
                widget.setSelected(resourcePackEntry);
            }
        });
    }

    public void switchFocusedList(PackListWidget listWidget) {
        PackListWidget packListWidget = this.selectedPackList == listWidget ? this.availablePackList : this.selectedPackList;
        this.switchFocus(GuiNavigationPath.of(packListWidget.getFirst(), new ParentElement[]{packListWidget, this}));
    }

    public void clearSelection() {
        this.selectedPackList.setSelected(null);
        this.availablePackList.setSelected(null);
    }

    private void refresh() {
        this.organizer.refresh();
        this.updatePackLists();
        this.refreshTimeout = 0L;
        this.iconTextures.clear();
    }

    protected static void copyPacks(MinecraftClient client, List<Path> srcPaths, Path destPath) {
        MutableBoolean mutableBoolean = new MutableBoolean();
        srcPaths.forEach(src -> {
            try {
                try (Stream<Path> stream = Files.walk(src, new FileVisitOption[0]);){
                    stream.forEach(toCopy -> {
                        try {
                            Util.relativeCopy(src.getParent(), destPath, toCopy);
                        }
                        catch (IOException iOException) {
                            LOGGER.warn("Failed to copy datapack file  from {} to {}", new Object[]{toCopy, destPath, iOException});
                            mutableBoolean.setTrue();
                        }
                    });
                    if (stream == null) return;
                }
                return;
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to copy datapack file from {} to {}", src, (Object)destPath);
                mutableBoolean.setTrue();
            }
        });
        if (mutableBoolean.isTrue()) {
            SystemToast.addPackCopyFailure(client, destPath.toString());
        }
    }

    @Override
    public void onFilesDropped(List<Path> paths) {
        String string = PackScreen.streamFileNames(paths).collect(Collectors.joining(", "));
        this.client.setScreen(new ConfirmScreen(confirmed -> {
            if (confirmed) {
                ArrayList<Path> list2 = new ArrayList<Path>(paths.size());
                HashSet<Path> set = new HashSet<Path>(paths);
                ResourcePackOpener<Path> resourcePackOpener = new ResourcePackOpener<Path>(this, this.client.getSymlinkFinder()){

                    @Override
                    protected Path java_nio_file_Path_openZip(Path path) {
                        return path;
                    }

                    @Override
                    protected Path java_nio_file_Path_openDirectory(Path path) {
                        return path;
                    }

                    @Override
                    protected Object java_lang_Object_openDirectory(Path path) throws IOException {
                        return this.java_nio_file_Path_openDirectory(path);
                    }

                    @Override
                    protected Object java_lang_Object_openZip(Path path) throws IOException {
                        return this.java_nio_file_Path_openZip(path);
                    }
                };
                ArrayList<SymlinkEntry> list3 = new ArrayList<SymlinkEntry>();
                for (Path path : paths) {
                    try {
                        Path path2 = (Path)resourcePackOpener.open(path, list3);
                        if (path2 == null) {
                            LOGGER.warn("Path {} does not seem like pack", (Object)path);
                            continue;
                        }
                        list2.add(path2);
                        set.remove(path2);
                    }
                    catch (IOException iOException) {
                        LOGGER.warn("Failed to check {} for packs", (Object)path, (Object)iOException);
                    }
                }
                if (!list3.isEmpty()) {
                    this.client.setScreen(SymlinkWarningScreen.pack(() -> this.client.setScreen(this)));
                    return;
                }
                if (!list2.isEmpty()) {
                    PackScreen.copyPacks(this.client, list2, this.file);
                    this.refresh();
                }
                if (!set.isEmpty()) {
                    String string = PackScreen.streamFileNames(set).collect(Collectors.joining(", "));
                    this.client.setScreen(new NoticeScreen(() -> this.client.setScreen(this), Text.translatable("pack.dropRejected.title"), Text.translatable("pack.dropRejected.message", string)));
                    return;
                }
            }
            this.client.setScreen(this);
        }, Text.translatable("pack.dropConfirm"), Text.literal(string)));
    }

    private static Stream<String> streamFileNames(Collection<Path> paths) {
        return paths.stream().map(Path::getFileName).map(Path::toString);
    }

    /*
     * Loose catch block
     */
    private Identifier loadPackIcon(TextureManager textureManager, ResourcePackProfile resourcePackProfile) {
        Identifier identifier;
        ResourcePack resourcePack;
        block19: {
            InputSupplier<InputStream> inputSupplier;
            block18: {
                resourcePack = resourcePackProfile.createResourcePack();
                inputSupplier = resourcePack.openRoot("pack.png");
                if (inputSupplier != null) break block18;
                Identifier identifier2 = UNKNOWN_PACK;
                if (resourcePack != null) {
                    resourcePack.close();
                }
                return identifier2;
            }
            String string = resourcePackProfile.getId();
            Identifier identifier3 = Identifier.ofVanilla("pack/" + Util.replaceInvalidChars(string, Identifier::isPathCharacterValid) + "/" + String.valueOf(Hashing.sha1().hashUnencodedChars((CharSequence)string)) + "/icon");
            InputStream inputStream = inputSupplier.get();
            try {
                NativeImage nativeImage = NativeImage.read(inputStream);
                textureManager.registerTexture(identifier3, new NativeImageBackedTexture(identifier3::toString, nativeImage));
                identifier = identifier3;
                if (inputStream == null) break block19;
            }
            catch (Throwable throwable) {
                try {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                    {
                        catch (Throwable throwable3) {
                            if (resourcePack != null) {
                                try {
                                    resourcePack.close();
                                }
                                catch (Throwable throwable4) {
                                    throwable3.addSuppressed(throwable4);
                                }
                            }
                            throw throwable3;
                        }
                    }
                }
                catch (Exception exception) {
                    LOGGER.warn("Failed to load icon from pack {}", (Object)resourcePackProfile.getId(), (Object)exception);
                    return UNKNOWN_PACK;
                }
            }
            inputStream.close();
        }
        if (resourcePack != null) {
            resourcePack.close();
        }
        return identifier;
    }

    private Identifier getPackIconTexture(ResourcePackProfile resourcePackProfile) {
        return this.iconTextures.computeIfAbsent(resourcePackProfile.getId(), profileName -> this.loadPackIcon(this.client.getTextureManager(), resourcePackProfile));
    }

    @Environment(value=EnvType.CLIENT)
    static class DirectoryWatcher
    implements AutoCloseable {
        final private WatchService watchService;
        final private Path path;

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public DirectoryWatcher(Path path) throws IOException {
            this.path = path;
            this.watchService = path.getFileSystem().newWatchService();
            try {
                this.watchDirectory(path);
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);){
                    for (Path path2 : directoryStream) {
                        if (!Files.isDirectory(path2, LinkOption.NOFOLLOW_LINKS)) continue;
                        this.watchDirectory(path2);
                    }
                    if (directoryStream == null) return;
                }
                return;
            }
            catch (Exception exception) {
                this.watchService.close();
                throw exception;
            }
        }

        @Nullable
        public static DirectoryWatcher create(Path path) {
            try {
                return new DirectoryWatcher(path);
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to initialize pack directory {} monitoring", (Object)path, (Object)iOException);
                return null;
            }
        }

        private void watchDirectory(Path path) throws IOException {
            path.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        }

        public boolean pollForChange() throws IOException {
            WatchKey watchKey;
            boolean bl = false;
            while ((watchKey = this.watchService.poll()) != null) {
                List<WatchEvent<?>> list = watchKey.pollEvents();
                for (WatchEvent<?> watchEvent : list) {
                    Path path;
                    bl = true;
                    if (watchKey.watchable() != this.path || watchEvent.kind() != StandardWatchEventKinds.ENTRY_CREATE || !Files.isDirectory(path = this.path.resolve((Path)watchEvent.context()), LinkOption.NOFOLLOW_LINKS)) continue;
                    this.watchDirectory(path);
                }
                watchKey.reset();
            }
            return bl;
        }

        @Override
        public void close() throws IOException {
            this.watchService.close();
        }
    }
}

