/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.sensor;

import java.util.function.Supplier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.ArmadilloScareDetectedSensor;
import net.minecraft.entity.ai.brain.sensor.AxolotlAttackablesSensor;
import net.minecraft.entity.ai.brain.sensor.BreezeAttackablesSensor;
import net.minecraft.entity.ai.brain.sensor.DummySensor;
import net.minecraft.entity.ai.brain.sensor.FrogAttackablesSensor;
import net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor;
import net.minecraft.entity.ai.brain.sensor.HoglinSpecificSensor;
import net.minecraft.entity.ai.brain.sensor.HurtBySensor;
import net.minecraft.entity.ai.brain.sensor.IsInWaterSensor;
import net.minecraft.entity.ai.brain.sensor.NearestBedSensor;
import net.minecraft.entity.ai.brain.sensor.NearestFollowableFriendlyMobSensor;
import net.minecraft.entity.ai.brain.sensor.NearestItemsSensor;
import net.minecraft.entity.ai.brain.sensor.NearestLivingEntitiesSensor;
import net.minecraft.entity.ai.brain.sensor.NearestPlayersSensor;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleAdultSensor;
import net.minecraft.entity.ai.brain.sensor.PiglinBruteSpecificSensor;
import net.minecraft.entity.ai.brain.sensor.PiglinSpecificSensor;
import net.minecraft.entity.ai.brain.sensor.SecondaryPointsOfInterestSensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.TemptationsSensor;
import net.minecraft.entity.ai.brain.sensor.VillagerBabiesSensor;
import net.minecraft.entity.ai.brain.sensor.VillagerHostilesSensor;
import net.minecraft.entity.ai.brain.sensor.WardenAttackablesSensor;
import net.minecraft.entity.passive.ArmadilloBrain;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.AxolotlBrain;
import net.minecraft.entity.passive.CamelBrain;
import net.minecraft.entity.passive.FrogBrain;
import net.minecraft.entity.passive.GoatBrain;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.passive.SnifferBrain;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SensorType<U extends Sensor<?>> {
    final static public SensorType<DummySensor> DUMMY = SensorType.register("dummy", DummySensor::new);
    final static public SensorType<NearestItemsSensor> NEAREST_ITEMS = SensorType.register("nearest_items", NearestItemsSensor::new);
    final static public SensorType<NearestLivingEntitiesSensor<LivingEntity>> NEAREST_LIVING_ENTITIES = SensorType.register("nearest_living_entities", NearestLivingEntitiesSensor::new);
    final static public SensorType<NearestPlayersSensor> NEAREST_PLAYERS = SensorType.register("nearest_players", NearestPlayersSensor::new);
    final static public SensorType<NearestBedSensor> NEAREST_BED = SensorType.register("nearest_bed", NearestBedSensor::new);
    final static public SensorType<HurtBySensor> HURT_BY = SensorType.register("hurt_by", HurtBySensor::new);
    final static public SensorType<VillagerHostilesSensor> VILLAGER_HOSTILES = SensorType.register("villager_hostiles", VillagerHostilesSensor::new);
    final static public SensorType<VillagerBabiesSensor> VILLAGER_BABIES = SensorType.register("villager_babies", VillagerBabiesSensor::new);
    final static public SensorType<SecondaryPointsOfInterestSensor> SECONDARY_POIS = SensorType.register("secondary_pois", SecondaryPointsOfInterestSensor::new);
    final static public SensorType<GolemLastSeenSensor> GOLEM_DETECTED = SensorType.register("golem_detected", GolemLastSeenSensor::new);
    final static public SensorType<ArmadilloScareDetectedSensor<ArmadilloEntity>> ARMADILLO_SCARE_DETECTED = SensorType.register("armadillo_scare_detected", () -> new ArmadilloScareDetectedSensor<ArmadilloEntity>(5, ArmadilloEntity::isEntityThreatening, ArmadilloEntity::canRollUp, MemoryModuleType.DANGER_DETECTED_RECENTLY, 80));
    final static public SensorType<PiglinSpecificSensor> PIGLIN_SPECIFIC_SENSOR = SensorType.register("piglin_specific_sensor", PiglinSpecificSensor::new);
    final static public SensorType<PiglinBruteSpecificSensor> PIGLIN_BRUTE_SPECIFIC_SENSOR = SensorType.register("piglin_brute_specific_sensor", PiglinBruteSpecificSensor::new);
    final static public SensorType<HoglinSpecificSensor> HOGLIN_SPECIFIC_SENSOR = SensorType.register("hoglin_specific_sensor", HoglinSpecificSensor::new);
    final static public SensorType<NearestVisibleAdultSensor> NEAREST_ADULT = SensorType.register("nearest_adult", NearestVisibleAdultSensor::new);
    final static public SensorType<NearestVisibleAdultSensor> NEAREST_ADULT_ANY_TYPE = SensorType.register("nearest_adult_any_type", NearestFollowableFriendlyMobSensor::new);
    final static public SensorType<AxolotlAttackablesSensor> AXOLOTL_ATTACKABLES = SensorType.register("axolotl_attackables", AxolotlAttackablesSensor::new);
    final static public SensorType<TemptationsSensor> AXOLOTL_TEMPTATIONS = SensorType.register("axolotl_temptations", () -> new TemptationsSensor(AxolotlBrain.getTemptItemPredicate()));
    final static public SensorType<TemptationsSensor> GOAT_TEMPTATIONS = SensorType.register("goat_temptations", () -> new TemptationsSensor(GoatBrain.getTemptItemPredicate()));
    final static public SensorType<TemptationsSensor> FROG_TEMPTATIONS = SensorType.register("frog_temptations", () -> new TemptationsSensor(FrogBrain.getTemptItemPredicate()));
    final static public SensorType<TemptationsSensor> CAMEL_TEMPTATIONS = SensorType.register("camel_temptations", () -> new TemptationsSensor(CamelBrain.getTemptItemPredicate()));
    final static public SensorType<TemptationsSensor> ARMADILLO_TEMPTATIONS = SensorType.register("armadillo_temptations", () -> new TemptationsSensor(ArmadilloBrain.getTemptItemPredicate()));
    final static public SensorType<TemptationsSensor> HAPPY_GHAST_TEMPTATIONS = SensorType.register("happy_ghast_temptations", () -> new TemptationsSensor(HappyGhastEntity.FOOD_PREDICATE));
    final static public SensorType<FrogAttackablesSensor> FROG_ATTACKABLES = SensorType.register("frog_attackables", FrogAttackablesSensor::new);
    final static public SensorType<IsInWaterSensor> IS_IN_WATER = SensorType.register("is_in_water", IsInWaterSensor::new);
    final static public SensorType<WardenAttackablesSensor> WARDEN_ENTITY_SENSOR = SensorType.register("warden_entity_sensor", WardenAttackablesSensor::new);
    final static public SensorType<TemptationsSensor> SNIFFER_TEMPTATIONS = SensorType.register("sniffer_temptations", () -> new TemptationsSensor(SnifferBrain.getTemptItemPredicate()));
    final static public SensorType<BreezeAttackablesSensor> BREEZE_ATTACK_ENTITY_SENSOR = SensorType.register("breeze_attack_entity_sensor", BreezeAttackablesSensor::new);
    final private Supplier<U> factory;

    public SensorType(Supplier<U> factory) {
        this.factory = factory;
    }

    public U create() {
        return (U)((Sensor)this.factory.get());
    }

    private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> factory) {
        return Registry.register(Registries.SENSOR_TYPE, Identifier.ofVanilla(id), new SensorType<U>(factory));
    }
}

