/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.level.storage;

import java.nio.file.Path;
import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.SaveVersionInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class LevelSummary
implements Comparable<LevelSummary> {
    final static public Text SELECT_WORLD_TEXT = Text.translatable("selectWorld.select");
    final private LevelInfo levelInfo;
    final private SaveVersionInfo versionInfo;
    final private String name;
    final private boolean requiresConversion;
    final private boolean locked;
    final private boolean experimental;
    final private Path iconPath;
    @Nullable
    private Text details;

    public LevelSummary(LevelInfo levelInfo, SaveVersionInfo versionInfo, String name, boolean requiresConversion, boolean locked, boolean experimental, Path iconPath) {
        this.levelInfo = levelInfo;
        this.versionInfo = versionInfo;
        this.name = name;
        this.locked = locked;
        this.experimental = experimental;
        this.iconPath = iconPath;
        this.requiresConversion = requiresConversion;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return StringUtils.isEmpty((CharSequence)this.levelInfo.getLevelName()) ? this.name : this.levelInfo.getLevelName();
    }

    public Path getIconPath() {
        return this.iconPath;
    }

    public boolean requiresConversion() {
        return this.requiresConversion;
    }

    public boolean isExperimental() {
        return this.experimental;
    }

    public long getLastPlayed() {
        return this.versionInfo.getLastPlayed();
    }

    @Override
    public int compareTo(LevelSummary levelSummary) {
        if (this.getLastPlayed() < levelSummary.getLastPlayed()) {
            return 1;
        }
        if (this.getLastPlayed() > levelSummary.getLastPlayed()) {
            return -1;
        }
        return this.name.compareTo(levelSummary.name);
    }

    public LevelInfo getLevelInfo() {
        return this.levelInfo;
    }

    public GameMode getGameMode() {
        return this.levelInfo.getGameMode();
    }

    public boolean isHardcore() {
        return this.levelInfo.isHardcore();
    }

    public boolean hasCheats() {
        return this.levelInfo.areCommandsAllowed();
    }

    public MutableText getVersion() {
        if (StringHelper.isEmpty(this.versionInfo.getVersionName())) {
            return Text.translatable("selectWorld.versionUnknown");
        }
        return Text.literal(this.versionInfo.getVersionName());
    }

    public SaveVersionInfo getVersionInfo() {
        return this.versionInfo;
    }

    public boolean shouldPromptBackup() {
        return this.getConversionWarning().promptsBackup();
    }

    public boolean wouldBeDowngraded() {
        return this.getConversionWarning() == ConversionWarning.DOWNGRADE;
    }

    public ConversionWarning getConversionWarning() {
        GameVersion gameVersion = SharedConstants.getGameVersion();
        int i = gameVersion.dataVersion().id();
        int j = this.versionInfo.getVersion().id();
        if (!gameVersion.stable() && j < i) {
            return ConversionWarning.UPGRADE_TO_SNAPSHOT;
        }
        if (j > i) {
            return ConversionWarning.DOWNGRADE;
        }
        return ConversionWarning.NONE;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public boolean isUnavailable() {
        if (this.isLocked() || this.requiresConversion()) {
            return true;
        }
        return !this.isVersionAvailable();
    }

    public boolean isVersionAvailable() {
        return SharedConstants.getGameVersion().dataVersion().isAvailableTo(this.versionInfo.getVersion());
    }

    public Text getDetails() {
        if (this.details == null) {
            this.details = this.createDetails();
        }
        return this.details;
    }

    private Text createDetails() {
        MutableText mutableText;
        if (this.isLocked()) {
            return Text.translatable("selectWorld.locked").formatted(Formatting.RED);
        }
        if (this.requiresConversion()) {
            return Text.translatable("selectWorld.conversion").formatted(Formatting.RED);
        }
        if (!this.isVersionAvailable()) {
            return Text.translatable("selectWorld.incompatible.info", this.getVersion()).formatted(Formatting.RED);
        }
        MutableText mutableText2 = mutableText = this.isHardcore() ? Text.empty().append(Text.translatable("gameMode.hardcore").withColor(Colors.RED)) : Text.translatable("gameMode." + this.getGameMode().getId());
        if (this.hasCheats()) {
            mutableText.append(", ").append(Text.translatable("selectWorld.commands"));
        }
        if (this.isExperimental()) {
            mutableText.append(", ").append(Text.translatable("selectWorld.experimental").formatted(Formatting.YELLOW));
        }
        MutableText mutableText22 = this.getVersion();
        MutableText mutableText3 = Text.literal(", ").append(Text.translatable("selectWorld.version")).append(ScreenTexts.SPACE);
        if (this.shouldPromptBackup()) {
            mutableText3.append(mutableText22.formatted(this.wouldBeDowngraded() ? Formatting.RED : Formatting.ITALIC));
        } else {
            mutableText3.append(mutableText22);
        }
        mutableText.append(mutableText3);
        return mutableText;
    }

    public Text getSelectWorldText() {
        return SELECT_WORLD_TEXT;
    }

    public boolean isSelectable() {
        return !this.isUnavailable();
    }

    public boolean isImmediatelyLoadable() {
        return !this.requiresConversion() && !this.isLocked();
    }

    public boolean isEditable() {
        return !this.isUnavailable();
    }

    public boolean isRecreatable() {
        return !this.isUnavailable();
    }

    public boolean isDeletable() {
        return true;
    }

    @Override
    public int compareTo(Object other) {
        return this.compareTo((LevelSummary)other);
    }

    public static final class ConversionWarning
    extends Enum<ConversionWarning> {
        final static public ConversionWarning NONE = new ConversionWarning(false, false, "");
        final static public ConversionWarning DOWNGRADE = new ConversionWarning(true, true, "downgrade");
        final static public ConversionWarning UPGRADE_TO_SNAPSHOT = new ConversionWarning(true, false, "snapshot");
        final private boolean backup;
        final private boolean dangerous;
        final private String translationKeySuffix;
        final static private ConversionWarning[] field_28443;

        public static ConversionWarning[] values() {
            return (ConversionWarning[])field_28443.clone();
        }

        public static ConversionWarning valueOf(String string) {
            return Enum.valueOf(ConversionWarning.class, string);
        }

        private ConversionWarning(boolean backup, boolean dangerous, String translationKeySuffix) {
            this.backup = backup;
            this.dangerous = dangerous;
            this.translationKeySuffix = translationKeySuffix;
        }

        public boolean promptsBackup() {
            return this.backup;
        }

        public boolean isDangerous() {
            return this.dangerous;
        }

        public String getTranslationKeySuffix() {
            return this.translationKeySuffix;
        }

        private static ConversionWarning[] method_36792() {
            return new ConversionWarning[]{NONE, DOWNGRADE, UPGRADE_TO_SNAPSHOT};
        }

        static {
            field_28443 = ConversionWarning.method_36792();
        }
    }

    public static class RecoveryWarning
    extends LevelSummary {
        final static private Text WARNING_TEXT = Text.translatable("recover_world.warning").styled(style -> style.withColor(Colors.RED));
        final static private Text BUTTON_TEXT = Text.translatable("recover_world.button");
        final private long lastPlayed;

        public RecoveryWarning(String name, Path iconPath, long lastPlayed) {
            super(null, null, name, false, false, false, iconPath);
            this.lastPlayed = lastPlayed;
        }

        @Override
        public String getDisplayName() {
            return this.getName();
        }

        @Override
        public Text getDetails() {
            return WARNING_TEXT;
        }

        @Override
        public long getLastPlayed() {
            return this.lastPlayed;
        }

        @Override
        public boolean isUnavailable() {
            return false;
        }

        @Override
        public Text getSelectWorldText() {
            return BUTTON_TEXT;
        }

        @Override
        public boolean isSelectable() {
            return true;
        }

        @Override
        public boolean isImmediatelyLoadable() {
            return false;
        }

        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public boolean isRecreatable() {
            return false;
        }

        @Override
        public int compareTo(Object object) {
            return super.compareTo((LevelSummary)object);
        }
    }

    public static class SymlinkLevelSummary
    extends LevelSummary {
        final static private Text MORE_INFO_TEXT = Text.translatable("symlink_warning.more_info");
        final static private Text TITLE_TEXT = Text.translatable("symlink_warning.title").withColor(Colors.RED);

        public SymlinkLevelSummary(String name, Path iconPath) {
            super(null, null, name, false, false, false, iconPath);
        }

        @Override
        public String getDisplayName() {
            return this.getName();
        }

        @Override
        public Text getDetails() {
            return TITLE_TEXT;
        }

        @Override
        public long getLastPlayed() {
            return -1L;
        }

        @Override
        public boolean isUnavailable() {
            return false;
        }

        @Override
        public Text getSelectWorldText() {
            return MORE_INFO_TEXT;
        }

        @Override
        public boolean isSelectable() {
            return true;
        }

        @Override
        public boolean isImmediatelyLoadable() {
            return false;
        }

        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public boolean isRecreatable() {
            return false;
        }

        @Override
        public int compareTo(Object object) {
            return super.compareTo((LevelSummary)object);
        }
    }
}

