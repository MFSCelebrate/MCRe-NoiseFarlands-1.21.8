/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.exception.upload;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.exception.RealmsUploadException;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class CancelledRealmsUploadException
extends RealmsUploadException {
    final static private Text STATUS_TEXT = Text.translatable("mco.upload.cancelled");

    @Override
    public Text getStatus() {
        return STATUS_TEXT;
    }
}

