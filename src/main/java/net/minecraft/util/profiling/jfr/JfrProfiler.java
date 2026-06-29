/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jdk.jfr.Configuration;
import jdk.jfr.Event;
import jdk.jfr.FlightRecorder;
import jdk.jfr.FlightRecorderListener;
import jdk.jfr.Recording;
import jdk.jfr.RecordingState;
import net.minecraft.SharedConstants;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.packet.PacketType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.function.Finishable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.path.PathUtil;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.util.profiling.jfr.InstanceType;
import net.minecraft.util.profiling.jfr.JfrListener;
import net.minecraft.util.profiling.jfr.event.ChunkGenerationEvent;
import net.minecraft.util.profiling.jfr.event.ChunkRegionReadEvent;
import net.minecraft.util.profiling.jfr.event.ChunkRegionWriteEvent;
import net.minecraft.util.profiling.jfr.event.NetworkSummaryEvent;
import net.minecraft.util.profiling.jfr.event.PacketReceivedEvent;
import net.minecraft.util.profiling.jfr.event.PacketSentEvent;
import net.minecraft.util.profiling.jfr.event.ServerTickTimeEvent;
import net.minecraft.util.profiling.jfr.event.StructureGenerationEvent;
import net.minecraft.util.profiling.jfr.event.WorldLoadFinishedEvent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.storage.ChunkCompressionFormat;
import net.minecraft.world.storage.StorageKey;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class JfrProfiler
implements FlightProfiler {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public String MINECRAFT = "Minecraft";
    final static public String WORLD_GENERATION = "World Generation";
    final static public String TICKING = "Ticking";
    final static public String NETWORK = "Network";
    final static public String STORAGE = "Storage";
    final static private List<Class<? extends Event>> EVENTS = List.of(ChunkGenerationEvent.class, ChunkRegionReadEvent.class, ChunkRegionWriteEvent.class, PacketReceivedEvent.class, PacketSentEvent.class, NetworkSummaryEvent.class, ServerTickTimeEvent.class, StructureGenerationEvent.class, WorldLoadFinishedEvent.class);
    final static private String CONFIG_PATH = "/flightrecorder-config.jfc";
    final static private DateTimeFormatter DATE_TIME_FORMAT = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd-HHmmss").toFormatter().withZone(ZoneId.systemDefault());
    final static private JfrProfiler INSTANCE = new JfrProfiler();
    @Nullable
    Recording currentRecording;
    private float tickTime;
    final private Map<String, NetworkSummaryEvent.Recorder> summaryRecorderByAddress = new ConcurrentHashMap<String, NetworkSummaryEvent.Recorder>();

    private JfrProfiler() {
        EVENTS.forEach(FlightRecorder::register);
        FlightRecorder.addPeriodicEvent(ServerTickTimeEvent.class, () -> new ServerTickTimeEvent(this.tickTime).commit());
        FlightRecorder.addPeriodicEvent(NetworkSummaryEvent.class, () -> {
            Iterator<NetworkSummaryEvent.Recorder> iterator = this.summaryRecorderByAddress.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().commit();
                iterator.remove();
            }
        });
    }

    public static JfrProfiler getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean start(InstanceType instanceType) {
        boolean bl;
        URL uRL = JfrProfiler.class.getResource(CONFIG_PATH);
        if (uRL == null) {
            LOGGER.warn("Could not find default flight recorder config at {}", (Object)CONFIG_PATH);
            return false;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uRL.openStream()));
        try {
            bl = this.start(bufferedReader, instanceType);
        }
        catch (Throwable throwable) {
            try {
                try {
                    bufferedReader.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to start flight recorder using configuration at {}", (Object)uRL, (Object)iOException);
                return false;
            }
        }
        bufferedReader.close();
        return bl;
    }

    @Override
    public Path stop() {
        if (this.currentRecording == null) {
            throw new IllegalStateException("Not currently profiling");
        }
        this.summaryRecorderByAddress.clear();
        Path path = this.currentRecording.getDestination();
        this.currentRecording.stop();
        return path;
    }

    @Override
    public boolean isProfiling() {
        return this.currentRecording != null;
    }

    @Override
    public boolean isAvailable() {
        return FlightRecorder.isAvailable();
    }

    private boolean start(Reader reader, InstanceType instanceType) {
        if (this.isProfiling()) {
            LOGGER.warn("Profiling already in progress");
            return false;
        }
        try {
            Configuration configuration = Configuration.create(reader);
            String string = DATE_TIME_FORMAT.format(Instant.now());
            this.currentRecording = Util.make(new Recording(configuration), recording -> {
                EVENTS.forEach(recording::enable);
                recording.setDumpOnExit(true);
                recording.setToDisk(true);
                recording.setName(String.format(Locale.ROOT, "%s-%s-%s", instanceType.getName(), SharedConstants.getGameVersion().name(), string));
            });
            Path path = Paths.get(String.format(Locale.ROOT, "debug/%s-%s.jfr", instanceType.getName(), string), new String[0]);
            PathUtil.createDirectories(path.getParent());
            this.currentRecording.setDestination(path);
            this.currentRecording.start();
            this.addListener();
        }
        catch (IOException | ParseException exception) {
            LOGGER.warn("Failed to start jfr profiling", (Throwable)exception);
            return false;
        }
        LOGGER.info("Started flight recorder profiling id({}):name({}) - will dump to {} on exit or stop command", new Object[]{this.currentRecording.getId(), this.currentRecording.getName(), this.currentRecording.getDestination()});
        return true;
    }

    private void addListener() {
        FlightRecorder.addListener(new FlightRecorderListener(){
            final JfrListener innerListener = new JfrListener(() -> {
                JfrProfiler.this.currentRecording = null;
            });

            @Override
            public void recordingStateChanged(Recording recording) {
                if (recording != JfrProfiler.this.currentRecording || recording.getState() != RecordingState.STOPPED) {
                    return;
                }
                this.innerListener.stop(recording.getDestination());
                FlightRecorder.removeListener(this);
            }
        });
    }

    @Override
    public void onTick(float tickTime) {
        if (ServerTickTimeEvent.TYPE.isEnabled()) {
            this.tickTime = tickTime;
        }
    }

    @Override
    public void onPacketReceived(NetworkPhase state, PacketType<?> type, SocketAddress remoteAddress, int bytes) {
        if (PacketReceivedEvent.TYPE.isEnabled()) {
            new PacketReceivedEvent(state.getId(), type.side().getName(), type.id().toString(), remoteAddress, bytes).commit();
        }
        if (NetworkSummaryEvent.TYPE.isEnabled()) {
            this.getOrCreateSummaryRecorder(remoteAddress).addReceivedPacket(bytes);
        }
    }

    @Override
    public void onPacketSent(NetworkPhase state, PacketType<?> type, SocketAddress remoteAddress, int bytes) {
        if (PacketSentEvent.TYPE.isEnabled()) {
            new PacketSentEvent(state.getId(), type.side().getName(), type.id().toString(), remoteAddress, bytes).commit();
        }
        if (NetworkSummaryEvent.TYPE.isEnabled()) {
            this.getOrCreateSummaryRecorder(remoteAddress).addSentPacket(bytes);
        }
    }

    private NetworkSummaryEvent.Recorder getOrCreateSummaryRecorder(SocketAddress address) {
        return this.summaryRecorderByAddress.computeIfAbsent(address.toString(), NetworkSummaryEvent.Recorder::new);
    }

    @Override
    public void onChunkRegionRead(StorageKey key, ChunkPos chunkPos, ChunkCompressionFormat format, int bytes) {
        if (ChunkRegionReadEvent.TYPE.isEnabled()) {
            new ChunkRegionReadEvent(key, chunkPos, format, bytes).commit();
        }
    }

    @Override
    public void onChunkRegionWrite(StorageKey key, ChunkPos chunkPos, ChunkCompressionFormat format, int bytes) {
        if (ChunkRegionWriteEvent.TYPE.isEnabled()) {
            new ChunkRegionWriteEvent(key, chunkPos, format, bytes).commit();
        }
    }

    @Override
    @Nullable
    public Finishable startWorldLoadProfiling() {
        if (!WorldLoadFinishedEvent.TYPE.isEnabled()) {
            return null;
        }
        WorldLoadFinishedEvent worldLoadFinishedEvent = new WorldLoadFinishedEvent();
        worldLoadFinishedEvent.begin();
        return success -> worldLoadFinishedEvent.commit();
    }

    @Override
    @Nullable
    public Finishable startChunkGenerationProfiling(ChunkPos chunkPos, RegistryKey<World> world, String targetStatus) {
        if (!ChunkGenerationEvent.TYPE.isEnabled()) {
            return null;
        }
        ChunkGenerationEvent chunkGenerationEvent = new ChunkGenerationEvent(chunkPos, world, targetStatus);
        chunkGenerationEvent.begin();
        return success -> chunkGenerationEvent.commit();
    }

    @Override
    @Nullable
    public Finishable startStructureGenerationProfiling(ChunkPos chunkPos, RegistryKey<World> world, RegistryEntry<Structure> structure) {
        if (!StructureGenerationEvent.TYPE.isEnabled()) {
            return null;
        }
        StructureGenerationEvent structureGenerationEvent = new StructureGenerationEvent(chunkPos, structure, world);
        structureGenerationEvent.begin();
        return success -> {
            structureGenerationEvent.success = success;
            structureGenerationEvent.commit();
        };
    }
}

