/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.server.dedicated;

import com.mojang.logging.LogUtils;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Properties;
import net.minecraft.SharedConstants;
import net.minecraft.util.Urls;
import org.slf4j.Logger;

public class EulaReader {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private Path eulaFile;
    final private boolean eulaAgreedTo;

    public EulaReader(Path eulaFile) {
        this.eulaFile = eulaFile;
        this.eulaAgreedTo = SharedConstants.isDevelopment || this.checkEulaAgreement();
    }

    private boolean checkEulaAgreement() {
        boolean bl;
        block8: {
            InputStream inputStream = Files.newInputStream(this.eulaFile, new OpenOption[0]);
            try {
                Properties properties = new Properties();
                properties.load(inputStream);
                bl = Boolean.parseBoolean(properties.getProperty("eula", "false"));
                if (inputStream == null) break block8;
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
                }
                catch (Exception exception) {
                    LOGGER.warn("Failed to load {}", (Object)this.eulaFile);
                    this.createEulaFile();
                    return false;
                }
            }
            inputStream.close();
        }
        return bl;
    }

    public boolean isEulaAgreedTo() {
        return this.eulaAgreedTo;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void createEulaFile() {
        if (SharedConstants.isDevelopment) {
            return;
        }
        try {
            try (OutputStream outputStream = Files.newOutputStream(this.eulaFile, new OpenOption[0]);){
                Properties properties = new Properties();
                properties.setProperty("eula", "false");
                properties.store(outputStream, "By changing the setting below to TRUE you are indicating your agreement to our EULA (" + String.valueOf(Urls.EULA) + ").");
                if (outputStream == null) return;
            }
            return;
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to save {}", (Object)this.eulaFile, (Object)exception);
        }
    }
}

