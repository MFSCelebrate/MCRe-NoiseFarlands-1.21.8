/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.network;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.authlib.GameProfile;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.message.MessageVerifier;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerListEntry {
    final private GameProfile profile;
    final private java.util.function.Supplier<SkinTextures> texturesSupplier;
    private GameMode gameMode = GameMode.DEFAULT;
    private int latency;
    @Nullable
    private Text displayName;
    private boolean showHat = true;
    @Nullable
    private PublicPlayerSession session;
    private MessageVerifier messageVerifier;
    private int listOrder;

    public PlayerListEntry(GameProfile profile, boolean secureChatEnforced) {
        this.profile = profile;
        this.messageVerifier = PlayerListEntry.getInitialVerifier(secureChatEnforced);
        Supplier supplier = Suppliers.memoize(() -> PlayerListEntry.texturesSupplier(profile));
        this.texturesSupplier = () -> PlayerListEntry.method_52807((java.util.function.Supplier)supplier);
    }

    private static java.util.function.Supplier<SkinTextures> texturesSupplier(GameProfile profile) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        PlayerSkinProvider playerSkinProvider = minecraftClient.getSkinProvider();
        CompletableFuture<Optional<SkinTextures>> completableFuture = playerSkinProvider.fetchSkinTextures(profile);
        boolean bl = !minecraftClient.uuidEquals(profile.getId());
        SkinTextures skinTextures = DefaultSkinHelper.getSkinTextures(profile);
        return () -> {
            SkinTextures skinTextures2 = completableFuture.getNow(Optional.empty()).orElse(skinTextures);
            if (bl && !skinTextures2.secure()) {
                return skinTextures;
            }
            return skinTextures2;
        };
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    @Nullable
    public PublicPlayerSession getSession() {
        return this.session;
    }

    public MessageVerifier getMessageVerifier() {
        return this.messageVerifier;
    }

    public boolean hasPublicKey() {
        return this.session != null;
    }

    protected void setSession(PublicPlayerSession session) {
        this.session = session;
        this.messageVerifier = session.createVerifier(PlayerPublicKey.EXPIRATION_GRACE_PERIOD);
    }

    protected void resetSession(boolean secureChatEnforced) {
        this.session = null;
        this.messageVerifier = PlayerListEntry.getInitialVerifier(secureChatEnforced);
    }

    private static MessageVerifier getInitialVerifier(boolean secureChatEnforced) {
        return secureChatEnforced ? MessageVerifier.UNVERIFIED : MessageVerifier.NO_SIGNATURE;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    protected void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public int getLatency() {
        return this.latency;
    }

    protected void setLatency(int latency) {
        this.latency = latency;
    }

    public SkinTextures getSkinTextures() {
        return this.texturesSupplier.get();
    }

    @Nullable
    public Team getScoreboardTeam() {
        return MinecraftClient.getInstance().world.net_minecraft_scoreboard_Scoreboard_getScoreboard().getScoreHolderTeam(this.getProfile().getName());
    }

    public void setDisplayName(@Nullable Text displayName) {
        this.displayName = displayName;
    }

    @Nullable
    public Text getDisplayName() {
        return this.displayName;
    }

    public void setShowHat(boolean showHat) {
        this.showHat = showHat;
    }

    public boolean shouldShowHat() {
        return this.showHat;
    }

    public void setListOrder(int listOrder) {
        this.listOrder = listOrder;
    }

    public int getListOrder() {
        return this.listOrder;
    }

    private static SkinTextures method_71647(SkinTextures skinTextures) {
        return skinTextures;
    }

    private static SkinTextures method_52807(java.util.function.Supplier supplier) {
        return (SkinTextures)((java.util.function.Supplier)supplier.get()).get();
    }
}

