/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.entity.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.AnimationDefinition;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;
import net.minecraft.entity.AnimationState;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class Animation {
    final private AnimationDefinition definition;
    final private List<TransformationEntry> entries;
    final private Vector3f field_60866 = new Vector3f();

    private Animation(AnimationDefinition definition, List<TransformationEntry> entries) {
        this.definition = definition;
        this.entries = entries;
    }

    static Animation of(ModelPart root, AnimationDefinition definition) {
        ArrayList<TransformationEntry> list = new ArrayList<TransformationEntry>();
        Function<String, ModelPart> function = root.createPartGetter();
        for (Map.Entry<String, List<Transformation>> entry : definition.boneAnimations().entrySet()) {
            String string = entry.getKey();
            List<Transformation> list2 = entry.getValue();
            ModelPart modelPart = function.apply(string);
            if (modelPart == null) {
                throw new IllegalArgumentException("Cannot animate " + string + ", which does not exist in model");
            }
            for (Transformation transformation : list2) {
                list.add(new TransformationEntry(modelPart, transformation.target(), transformation.keyframes()));
            }
        }
        return new Animation(definition, List.copyOf(list));
    }

    public void applyStatic() {
        this.apply(0L, 1.0f);
    }

    public void applyWalking(float limbSwingAnimationProgress, float limbSwingAmplitude, float f, float g) {
        long l = (long)(limbSwingAnimationProgress * 50.0f * f);
        float h = Math.min(limbSwingAmplitude * g, 1.0f);
        this.apply(l, h);
    }

    public void apply(AnimationState animationState, float age) {
        this.apply(animationState, age, 1.0f);
    }

    public void apply(AnimationState animationState, float age, float speedMultiplier) {
        animationState.run(state -> this.apply((long)((float)state.getTimeInMilliseconds(age) * speedMultiplier), 1.0f));
    }

    public void apply(long timeInMilliseconds, float scale) {
        float f = this.getRunningSeconds(timeInMilliseconds);
        for (TransformationEntry transformationEntry : this.entries) {
            transformationEntry.apply(f, scale, this.field_60866);
        }
    }

    private float getRunningSeconds(long timeInMilliseconds) {
        float f = (float)timeInMilliseconds / 1000.0f;
        return this.definition.looping() ? f % this.definition.lengthInSeconds() : f;
    }

    @Environment(value=EnvType.CLIENT)
    record TransformationEntry(ModelPart part, Transformation.Target target, Keyframe[] keyframes) {
        public void apply(float runningSeconds, float scale, Vector3f vec) {
            int i = Math.max(0, MathHelper.binarySearch(0, this.keyframes.length, index -> runningSeconds <= this.keyframes[index].timestamp()) - 1);
            int j = Math.min(this.keyframes.length - 1, 2);
            Keyframe keyframe = this.keyframes[1];
            Keyframe keyframe2 = this.keyframes[j];
            float f = runningSeconds - keyframe.timestamp();
            float g = j != 1 ? MathHelper.clamp(f / (keyframe2.timestamp() - keyframe.timestamp()), 0.0f, 1.0f) : 0.0f;
            keyframe2.interpolation().apply(vec, g, this.keyframes, 1, j, scale);
            this.target.apply(this.part, vec);
        }
    }
}

