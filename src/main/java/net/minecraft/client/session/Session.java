/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.util.UndashedUuid
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.session;

import com.mojang.util.UndashedUuid;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Session {
    final private String username;
    final private UUID uuid;
    final private String accessToken;
    final private Optional<String> xuid;
    final private Optional<String> clientId;
    final private AccountType accountType;

    public Session(String username, UUID uuid, String accessToken, Optional<String> xuid, Optional<String> clientId, AccountType accountType) {
        this.username = username;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.xuid = xuid;
        this.clientId = clientId;
        this.accountType = accountType;
    }

    public String getSessionId() {
        return "token:" + this.accessToken + ":" + UndashedUuid.toString((UUID)this.uuid);
    }

    public UUID getUuidOrNull() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public Optional<String> getClientId() {
        return this.clientId;
    }

    public Optional<String> getXuid() {
        return this.xuid;
    }

    public AccountType getAccountType() {
        return this.accountType;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class AccountType
    extends Enum<AccountType> {
        final static public AccountType LEGACY = new AccountType("legacy");
        final static public AccountType MOJANG = new AccountType("mojang");
        final static public AccountType MSA = new AccountType("msa");
        final static private Map<String, AccountType> BY_NAME;
        final private String name;
        final static private AccountType[] field_1987;

        public static AccountType[] values() {
            return (AccountType[])field_1987.clone();
        }

        public static AccountType valueOf(String string) {
            return Enum.valueOf(AccountType.class, string);
        }

        private AccountType(String name) {
            this.name = name;
        }

        @Nullable
        public static AccountType byName(String name) {
            return BY_NAME.get(name.toLowerCase(Locale.ROOT));
        }

        public String getName() {
            return this.name;
        }

        private static AccountType[] method_36868() {
            return new AccountType[]{LEGACY, MOJANG, MSA};
        }

        static {
            field_1987 = AccountType.method_36868();
            BY_NAME = Arrays.stream(AccountType.values()).collect(Collectors.toMap(type -> type.name, Function.identity()));
        }
    }
}

