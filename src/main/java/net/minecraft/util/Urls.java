/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.util.UndashedUuid
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import com.mojang.util.UndashedUuid;
import java.net.URI;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public class Urls {
    final static public URI GDPR = URI.create("https://aka.ms/MinecraftGDPR");
    final static public URI EULA = URI.create("https://aka.ms/MinecraftEULA");
    final static public URI PRIVACY_STATEMENT = URI.create("http://go.microsoft.com/fwlink/?LinkId=521839");
    final static public URI JAVA_ATTRIBUTION = URI.create("https://aka.ms/MinecraftJavaAttribution");
    final static public URI JAVA_LICENSES = URI.create("https://aka.ms/MinecraftJavaLicenses");
    final static public URI BUY_JAVA = URI.create("https://aka.ms/BuyMinecraftJava");
    final static public URI JAVA_ACCOUNT_SETTINGS = URI.create("https://aka.ms/JavaAccountSettings");
    final static public URI SNAPSHOT_FEEDBACK = URI.create("https://aka.ms/snapshotfeedback?ref=game");
    final static public URI JAVA_FEEDBACK = URI.create("https://aka.ms/javafeedback?ref=game");
    final static public URI SNAPSHOT_BUGS = URI.create("https://aka.ms/snapshotbugs?ref=game");
    final static public URI MINECRAFT_SUPPORT = URI.create("https://aka.ms/Minecraft-Support");
    final static public URI JAVA_ACCESSIBILITY = URI.create("https://aka.ms/MinecraftJavaAccessibility");
    final static public URI ABOUT_JAVA_REPORTING = URI.create("https://aka.ms/aboutjavareporting");
    final static public URI JAVA_MODERATION = URI.create("https://aka.ms/mcjavamoderation");
    final static public URI JAVA_BLOCKING = URI.create("https://aka.ms/javablocking");
    final static public URI MINECRAFT_SYMLINKS = URI.create("https://aka.ms/MinecraftSymLinks");
    final static public URI JAVA_REALMS_TRIAL = URI.create("https://aka.ms/startjavarealmstrial");
    final static public URI BUY_JAVA_REALMS = URI.create("https://aka.ms/BuyJavaRealms");
    final static public URI REALMS_TERMS = URI.create("https://aka.ms/MinecraftRealmsTerms");
    final static public URI REALMS_CONTENT_CREATOR = URI.create("https://aka.ms/MinecraftRealmsContentCreator");
    final static public String EXTEND_JAVA_REALMS = "https://aka.ms/ExtendJavaRealms";
    final static public String INTENTIONAL_GAME_DESIGN_ISSUE_ID = "MCPE-28723";
    final static public URI INTENTIONAL_GAME_DESIGN_ISSUE = URI.create("https://bugs.mojang.com/browse/MCPE-28723");

    public static String getExtendJavaRealmsUrl(@Nullable String subscriptionId, UUID uuid, boolean trial) {
        if (subscriptionId == null) {
            return EXTEND_JAVA_REALMS;
        }
        return Urls.getExtendJavaRealmsUrl(subscriptionId, uuid) + "&ref=" + (trial ? "expiredTrial" : "expiredRealm");
    }

    public static String getExtendJavaRealmsUrl(@Nullable String subscriptionId, UUID uuid) {
        if (subscriptionId == null) {
            return EXTEND_JAVA_REALMS;
        }
        return "https://aka.ms/ExtendJavaRealms?subscriptionId=" + subscriptionId + "&profileId=" + UndashedUuid.toString((UUID)uuid);
    }
}

