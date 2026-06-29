/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public final class ProjectionType
extends Enum<ProjectionType> {
    final static public ProjectionType PERSPECTIVE = new ProjectionType(VertexSorter.BY_DISTANCE, (matrix, f) -> matrix.scale(1.0f - f / 4096.0f));
    final static public ProjectionType ORTHOGRAPHIC = new ProjectionType(VertexSorter.BY_Z, (matrix, f) -> matrix.translate(0.0f, 0.0f, f / 512.0f));
    final private VertexSorter vertexSorter;
    final private Applier applier;
    final static private ProjectionType[] field_54957;

    public static ProjectionType[] values() {
        return (ProjectionType[])field_54957.clone();
    }

    public static ProjectionType valueOf(String string) {
        return Enum.valueOf(ProjectionType.class, string);
    }

    private ProjectionType(VertexSorter vertexSorter, Applier applier) {
        this.vertexSorter = vertexSorter;
        this.applier = applier;
    }

    public VertexSorter getVertexSorter() {
        return this.vertexSorter;
    }

    public void apply(Matrix4f matrix, float f) {
        this.applier.apply(matrix, f);
    }

    private static ProjectionType[] method_65047() {
        return new ProjectionType[]{PERSPECTIVE, ORTHOGRAPHIC};
    }

    static {
        field_54957 = ProjectionType.method_65047();
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface Applier {
        public void apply(Matrix4f var1, float var2);
    }
}

