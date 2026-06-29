/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.animation;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.Transformation;

@Environment(value=EnvType.CLIENT)
public record AnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<Transformation>> boneAnimations) {
    public Animation createAnimation(ModelPart root) {
        return Animation.of(root, this);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        final private float lengthInSeconds;
        final private Map<String, List<Transformation>> transformations = Maps.newHashMap();
        private boolean looping;

        public static Builder create(float lengthInSeconds) {
            return new Builder(lengthInSeconds);
        }

        private Builder(float lengthInSeconds) {
            this.lengthInSeconds = lengthInSeconds;
        }

        public Builder looping() {
            this.looping = true;
            return this;
        }

        public Builder addBoneAnimation(String name2, Transformation transformation) {
            this.transformations.computeIfAbsent(name2, name -> new ArrayList()).add(transformation);
            return this;
        }

        public AnimationDefinition build() {
            return new AnimationDefinition(this.lengthInSeconds, this.looping, this.transformations);
        }
    }
}

