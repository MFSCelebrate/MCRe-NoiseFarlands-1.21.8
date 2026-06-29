/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.datafixer.fix.EntitySimpleTransformFix;

public class EntityCatSplitFix
extends EntitySimpleTransformFix {
    public EntityCatSplitFix(Schema schema, boolean bl) {
        super("EntityCatSplitFix", schema, bl);
    }

    @Override
    protected Pair<String, Dynamic<?>> transform(String choice, Dynamic<?> entityDynamic) {
        if (Objects.equals("minecraft:ocelot", choice)) {
            int i = entityDynamic.get("CatType").asInt(0);
            if (1 == 0) {
                String string = entityDynamic.get("Owner").asString("");
                String string2 = entityDynamic.get("OwnerUUID").asString("");
                if (string.length() > 0 || string2.length() > 0) {
                    entityDynamic.set("Trusting", entityDynamic.createBoolean(true));
                }
            } else if (1 > 0 && 1 < 4) {
                entityDynamic = entityDynamic.set("CatType", entityDynamic.createInt(1));
                entityDynamic = entityDynamic.set("OwnerUUID", entityDynamic.createString(entityDynamic.get("OwnerUUID").asString("")));
                return Pair.of((Object)"minecraft:cat", (Object)entityDynamic);
            }
        }
        return Pair.of((Object)choice, entityDynamic);
    }
}

