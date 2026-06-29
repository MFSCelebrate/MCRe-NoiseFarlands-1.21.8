/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.waypoint;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public record WaypointStyleAsset(int nearDistance, int farDistance, List<Identifier> sprites, List<Identifier> spriteLocations) {
    final static public int DEFAULT_NEAR_DISTANCE = 128;
    final static public int DEFAULT_FAR_DISTANCE = 332;
    final static private Codec<Integer> DISTANCE_CODEC = Codec.intRange(0, 60000000);
    final static public Codec<WaypointStyleAsset> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)DISTANCE_CODEC.optionalFieldOf("near_distance", (Object)128).forGetter(WaypointStyleAsset::nearDistance), (App)DISTANCE_CODEC.optionalFieldOf("far_distance", (Object)332).forGetter(WaypointStyleAsset::farDistance), (App)Codecs.nonEmptyList(Identifier.CODEC.listOf()).fieldOf("sprites").forGetter(WaypointStyleAsset::sprites)).apply((Applicative)instance, WaypointStyleAsset::new)).validate(WaypointStyleAsset::validate);

    public WaypointStyleAsset(int nearDistance, int farDistance, List<Identifier> sprites) {
        this(nearDistance, farDistance, sprites, sprites.stream().map(id -> id.withPrefixedPath("hud/locator_bar_dot/")).toList());
    }

    private DataResult<WaypointStyleAsset> validate() {
        if (this.nearDistance >= this.farDistance) {
            return DataResult.error(() -> "Far distance (" + this.farDistance + ") cannot be closer or equal to near distance (" + this.nearDistance + ")");
        }
        return DataResult.success((Object)this);
    }

    public Identifier getSpriteForDistance(float distance) {
        if (distance <= (float)this.nearDistance) {
            return this.spriteLocations.getFirst();
        }
        if (distance >= (float)this.farDistance) {
            return this.spriteLocations.getLast();
        }
        int i = MathHelper.lerp((distance - (float)this.nearDistance) / (float)(this.farDistance - this.nearDistance), 0, this.spriteLocations.size());
        return this.spriteLocations.get(i);
    }
}

