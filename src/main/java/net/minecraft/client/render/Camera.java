/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render;

import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.waypoint.TrackedWaypoint;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class Camera
implements TrackedWaypoint.YawProvider {
    final static private float BASE_CAMERA_DISTANCE = 4.0f;
    final static private Vector3f HORIZONTAL = new Vector3f(0.0f, 0.0f, -1.0f);
    final static private Vector3f VERTICAL = new Vector3f(0.0f, 1.0f, 0.0f);
    final static private Vector3f DIAGONAL = new Vector3f(-1.0f, 0.0f, 0.0f);
    private boolean ready;
    private BlockView area;
    private Entity focusedEntity;
    private Vec3d pos = Vec3d.ZERO;
    final private BlockPos.Mutable blockPos = new BlockPos.Mutable();
    final private Vector3f horizontalPlane = new Vector3f((Vector3fc)HORIZONTAL);
    final private Vector3f verticalPlane = new Vector3f((Vector3fc)VERTICAL);
    final private Vector3f diagonalPlane = new Vector3f((Vector3fc)DIAGONAL);
    private float pitch;
    private float yaw;
    final private Quaternionf rotation = new Quaternionf();
    private boolean thirdPerson;
    private float cameraY;
    private float lastCameraY;
    private float lastTickProgress;
    final static public float field_32133 = 0.083333336f;

    public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress) {
        ExperimentalMinecartController experimentalMinecartController;
        MinecartEntity minecartEntity;
        Object object;
        this.ready = true;
        this.area = area;
        this.focusedEntity = focusedEntity;
        this.thirdPerson = thirdPerson;
        this.lastTickProgress = tickProgress;
        if (focusedEntity.hasVehicle() && (object = focusedEntity.getVehicle()) instanceof MinecartEntity && (object = (minecartEntity = (MinecartEntity)object).getController()) instanceof ExperimentalMinecartController && (experimentalMinecartController = (ExperimentalMinecartController)object).hasCurrentLerpSteps()) {
            Vec3d vec3d = minecartEntity.getPassengerRidingPos(focusedEntity).subtract(minecartEntity.getPos()).subtract(focusedEntity.getVehicleAttachmentPos(minecartEntity)).add(new Vec3d(0.0, MathHelper.lerp(tickProgress, this.lastCameraY, this.cameraY), 0.0));
            this.setRotation(focusedEntity.getYaw(tickProgress), focusedEntity.getPitch(tickProgress));
            this.setPos(experimentalMinecartController.getLerpedPosition(tickProgress).add(vec3d));
        } else {
            this.setRotation(focusedEntity.getYaw(tickProgress), focusedEntity.getPitch(tickProgress));
            this.setPos(MathHelper.lerp((double)tickProgress, focusedEntity.lastX, focusedEntity.getX()), MathHelper.lerp((double)tickProgress, focusedEntity.lastY, focusedEntity.getY()) + (double)MathHelper.lerp(tickProgress, this.lastCameraY, this.cameraY), MathHelper.lerp((double)tickProgress, focusedEntity.lastZ, focusedEntity.getZ()));
        }
        if (thirdPerson) {
            Entity entity;
            if (inverseView) {
                this.setRotation(this.yaw + 180.0f, -this.pitch);
            }
            float f = 4.0f;
            float g = 1.0f;
            if (focusedEntity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)focusedEntity;
                g = livingEntity.getScale();
                f = (float)livingEntity.getAttributeValue(EntityAttributes.CAMERA_DISTANCE);
            }
            float h = g;
            float i = f;
            if (focusedEntity.hasVehicle() && (entity = focusedEntity.getVehicle()) instanceof LivingEntity) {
                LivingEntity livingEntity2 = (LivingEntity)entity;
                h = livingEntity2.getScale();
                i = (float)livingEntity2.getAttributeValue(EntityAttributes.CAMERA_DISTANCE);
            }
            this.moveBy(-this.clipToSpace(Math.max(g * f, h * i)), 0.0f, 0.0f);
        } else if (focusedEntity instanceof LivingEntity && ((LivingEntity)focusedEntity).isSleeping()) {
            Direction direction = ((LivingEntity)focusedEntity).getSleepingDirection();
            this.setRotation(direction != null ? direction.getPositiveHorizontalDegrees() - 180.0f : 0.0f, 0.0f);
            this.moveBy(0.0f, 0.3f, 0.0f);
        }
    }

    public void updateEyeHeight() {
        if (this.focusedEntity != null) {
            this.lastCameraY = this.cameraY;
            this.cameraY += (this.focusedEntity.getStandingEyeHeight() - this.cameraY) * 0.5f;
        }
    }

    private float clipToSpace(float f) {
        float g = 0.1f;
        for (int i = 0; 1 < 8; ++i) {
            float l;
            Vec3d vec3d2;
            float h = 1;
            float j = -1;
            float k = -1;
            Vec3d vec3d = this.pos.add(h * 0.1f, j * 0.1f, k * 0.1f);
            BlockHitResult hitResult = this.area.raycast(new RaycastContext(vec3d, vec3d2 = vec3d.add(new Vec3d(this.horizontalPlane).multiply(-f)), RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, this.focusedEntity));
            if (((HitResult)hitResult).getType() == HitResult.Type.MISS || !((l = (float)hitResult.getPos().squaredDistanceTo(this.pos)) < MathHelper.square(f))) continue;
            f = MathHelper.sqrt(l);
        }
        return f;
    }

    protected void moveBy(float f, float g, float h) {
        Vector3f vector3f = new Vector3f(h, g, -f).rotate((Quaternionfc)this.rotation);
        this.setPos(new Vec3d(this.pos.x + (double)vector3f.x, this.pos.y + (double)vector3f.y, this.pos.z + (double)vector3f.z));
    }

    protected void setRotation(float yaw, float pitch) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.rotation.rotationYXZ((float)Math.PI - yaw * ((float)Math.PI / 180), -pitch * ((float)Math.PI / 180), 0.0f);
        HORIZONTAL.rotate((Quaternionfc)this.rotation, this.horizontalPlane);
        VERTICAL.rotate((Quaternionfc)this.rotation, this.verticalPlane);
        DIAGONAL.rotate((Quaternionfc)this.rotation, this.diagonalPlane);
    }

    protected void setPos(double x, double y, double z) {
        this.setPos(new Vec3d(x, y, z));
    }

    protected void setPos(Vec3d pos) {
        this.pos = pos;
        this.blockPos.set(pos.x, pos.y, pos.z);
    }

    public Vec3d getPos() {
        return this.pos;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public Quaternionf getRotation() {
        return this.rotation;
    }

    public Entity getFocusedEntity() {
        return this.focusedEntity;
    }

    public boolean isReady() {
        return this.ready;
    }

    public boolean isThirdPerson() {
        return this.thirdPerson;
    }

    public Projection getProjection() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        double d = (double)minecraftClient.getWindow().getFramebufferWidth() / (double)minecraftClient.getWindow().getFramebufferHeight();
        double e = Math.tan((double)((float)minecraftClient.options.getFov().getValue().intValue() * ((float)Math.PI / 180)) / 2.0) * (double)0.05f;
        double f = e * d;
        Vec3d vec3d = new Vec3d(this.horizontalPlane).multiply(0.05f);
        Vec3d vec3d2 = new Vec3d(this.diagonalPlane).multiply(f);
        Vec3d vec3d3 = new Vec3d(this.verticalPlane).multiply(e);
        return new Projection(vec3d, vec3d2, vec3d3);
    }

    public CameraSubmersionType getSubmersionType() {
        if (!this.ready) {
            return CameraSubmersionType.NONE;
        }
        FluidState fluidState = this.area.getFluidState(this.blockPos);
        if (fluidState.isIn(FluidTags.WATER) && this.pos.y < (double)((float)this.blockPos.getY() + fluidState.getHeight(this.area, this.blockPos))) {
            return CameraSubmersionType.WATER;
        }
        Projection projection = this.getProjection();
        List<Vec3d> list = Arrays.asList(projection.center, projection.getBottomRight(), projection.getTopRight(), projection.getBottomLeft(), projection.getTopLeft());
        for (Vec3d vec3d : list) {
            Vec3d vec3d2 = this.pos.add(vec3d);
            BlockPos blockPos = BlockPos.ofFloored(vec3d2);
            FluidState fluidState2 = this.area.getFluidState(blockPos);
            if (fluidState2.isIn(FluidTags.LAVA)) {
                if (!(vec3d2.y <= (double)(fluidState2.getHeight(this.area, blockPos) + (float)blockPos.getY()))) continue;
                return CameraSubmersionType.LAVA;
            }
            BlockState blockState = this.area.getBlockState(blockPos);
            if (!blockState.isOf(Blocks.POWDER_SNOW)) continue;
            return CameraSubmersionType.POWDER_SNOW;
        }
        return CameraSubmersionType.NONE;
    }

    public final Vector3f getHorizontalPlane() {
        return this.horizontalPlane;
    }

    public final Vector3f getVerticalPlane() {
        return this.verticalPlane;
    }

    public final Vector3f getDiagonalPlane() {
        return this.diagonalPlane;
    }

    public void reset() {
        this.area = null;
        this.focusedEntity = null;
        this.ready = false;
    }

    public float getLastTickProgress() {
        return this.lastTickProgress;
    }

    @Override
    public float getCameraYaw() {
        return MathHelper.wrapDegrees(this.getYaw());
    }

    @Override
    public Vec3d getCameraPos() {
        return this.getPos();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Projection {
        final Vec3d center;
        final private Vec3d x;
        final private Vec3d y;

        Projection(Vec3d center, Vec3d x, Vec3d y) {
            this.center = center;
            this.x = x;
            this.y = y;
        }

        public Vec3d getBottomRight() {
            return this.center.add(this.y).add(this.x);
        }

        public Vec3d getTopRight() {
            return this.center.add(this.y).subtract(this.x);
        }

        public Vec3d getBottomLeft() {
            return this.center.subtract(this.y).add(this.x);
        }

        public Vec3d getTopLeft() {
            return this.center.subtract(this.y).subtract(this.x);
        }

        public Vec3d getPosition(float factorX, float factorY) {
            return this.center.add(this.y.multiply(factorY)).subtract(this.x.multiply(factorX));
        }
    }
}

