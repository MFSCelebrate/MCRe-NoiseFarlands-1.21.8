/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.fix.BlockPropertyRenameFix;

public class SimpleBlockPropertyRenameFix
extends BlockPropertyRenameFix {
    final private String targetId;
    final private String oldPropertyName;
    final private String newPropertyName;
    final private UnaryOperator<String> valueConverter;

    public SimpleBlockPropertyRenameFix(Schema outputSchema, String name, String targetId, String oldPropertyName, String newPropertyName, UnaryOperator<String> valueConverter) {
        super(outputSchema, name);
        this.targetId = targetId;
        this.oldPropertyName = oldPropertyName;
        this.newPropertyName = newPropertyName;
        this.valueConverter = valueConverter;
    }

    @Override
    protected boolean shouldFix(String id) {
        return id.equals(this.targetId);
    }

    @Override
    protected <T> Dynamic<T> fix(String id, Dynamic<T> properties) {
        return properties.renameAndFixField(this.oldPropertyName, this.newPropertyName, value -> value.createString((String)this.valueConverter.apply(value.asString(""))));
    }
}

