/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AnyBlockUseCriterion;
import net.minecraft.advancement.criterion.BeeNestDestroyedCriterion;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.advancement.criterion.BrewedPotionCriterion;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.ChanneledLightningCriterion;
import net.minecraft.advancement.criterion.ConstructBeaconCriterion;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CuredZombieVillagerCriterion;
import net.minecraft.advancement.criterion.DefaultBlockUseCriterion;
import net.minecraft.advancement.criterion.EffectsChangedCriterion;
import net.minecraft.advancement.criterion.EnchantedItemCriterion;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.advancement.criterion.EntityHurtPlayerCriterion;
import net.minecraft.advancement.criterion.FallAfterExplosionCriterion;
import net.minecraft.advancement.criterion.FilledBucketCriterion;
import net.minecraft.advancement.criterion.FishingRodHookedCriterion;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.advancement.criterion.ItemDurabilityChangedCriterion;
import net.minecraft.advancement.criterion.KilledByArrowCriterion;
import net.minecraft.advancement.criterion.LevitationCriterion;
import net.minecraft.advancement.criterion.LightningStrikeCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.PlayerGeneratesContainerLootCriterion;
import net.minecraft.advancement.criterion.PlayerHurtEntityCriterion;
import net.minecraft.advancement.criterion.PlayerInteractedWithEntityCriterion;
import net.minecraft.advancement.criterion.RecipeCraftedCriterion;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.advancement.criterion.ShotCrossbowCriterion;
import net.minecraft.advancement.criterion.SlideDownBlockCriterion;
import net.minecraft.advancement.criterion.StartedRidingCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.TameAnimalCriterion;
import net.minecraft.advancement.criterion.TargetHitCriterion;
import net.minecraft.advancement.criterion.ThrownItemPickedUpByEntityCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.advancement.criterion.TravelCriterion;
import net.minecraft.advancement.criterion.UsedEnderEyeCriterion;
import net.minecraft.advancement.criterion.UsedTotemCriterion;
import net.minecraft.advancement.criterion.UsingItemCriterion;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Criteria {
    final static public Codec<Criterion<?>> CODEC = Registries.CRITERION.getCodec();
    final static public ImpossibleCriterion IMPOSSIBLE = Criteria.register("impossible", new ImpossibleCriterion());
    final static public OnKilledCriterion PLAYER_KILLED_ENTITY = Criteria.register("player_killed_entity", new OnKilledCriterion());
    final static public OnKilledCriterion ENTITY_KILLED_PLAYER = Criteria.register("entity_killed_player", new OnKilledCriterion());
    final static public EnterBlockCriterion ENTER_BLOCK = Criteria.register("enter_block", new EnterBlockCriterion());
    final static public InventoryChangedCriterion INVENTORY_CHANGED = Criteria.register("inventory_changed", new InventoryChangedCriterion());
    final static public RecipeUnlockedCriterion RECIPE_UNLOCKED = Criteria.register("recipe_unlocked", new RecipeUnlockedCriterion());
    final static public PlayerHurtEntityCriterion PLAYER_HURT_ENTITY = Criteria.register("player_hurt_entity", new PlayerHurtEntityCriterion());
    final static public EntityHurtPlayerCriterion ENTITY_HURT_PLAYER = Criteria.register("entity_hurt_player", new EntityHurtPlayerCriterion());
    final static public EnchantedItemCriterion ENCHANTED_ITEM = Criteria.register("enchanted_item", new EnchantedItemCriterion());
    final static public FilledBucketCriterion FILLED_BUCKET = Criteria.register("filled_bucket", new FilledBucketCriterion());
    final static public BrewedPotionCriterion BREWED_POTION = Criteria.register("brewed_potion", new BrewedPotionCriterion());
    final static public ConstructBeaconCriterion CONSTRUCT_BEACON = Criteria.register("construct_beacon", new ConstructBeaconCriterion());
    final static public UsedEnderEyeCriterion USED_ENDER_EYE = Criteria.register("used_ender_eye", new UsedEnderEyeCriterion());
    final static public SummonedEntityCriterion SUMMONED_ENTITY = Criteria.register("summoned_entity", new SummonedEntityCriterion());
    final static public BredAnimalsCriterion BRED_ANIMALS = Criteria.register("bred_animals", new BredAnimalsCriterion());
    final static public TickCriterion LOCATION = Criteria.register("location", new TickCriterion());
    final static public TickCriterion SLEPT_IN_BED = Criteria.register("slept_in_bed", new TickCriterion());
    final static public CuredZombieVillagerCriterion CURED_ZOMBIE_VILLAGER = Criteria.register("cured_zombie_villager", new CuredZombieVillagerCriterion());
    final static public VillagerTradeCriterion VILLAGER_TRADE = Criteria.register("villager_trade", new VillagerTradeCriterion());
    final static public ItemDurabilityChangedCriterion ITEM_DURABILITY_CHANGED = Criteria.register("item_durability_changed", new ItemDurabilityChangedCriterion());
    final static public LevitationCriterion LEVITATION = Criteria.register("levitation", new LevitationCriterion());
    final static public ChangedDimensionCriterion CHANGED_DIMENSION = Criteria.register("changed_dimension", new ChangedDimensionCriterion());
    final static public TickCriterion TICK = Criteria.register("tick", new TickCriterion());
    final static public TameAnimalCriterion TAME_ANIMAL = Criteria.register("tame_animal", new TameAnimalCriterion());
    final static public ItemCriterion PLACED_BLOCK = Criteria.register("placed_block", new ItemCriterion());
    final static public ConsumeItemCriterion CONSUME_ITEM = Criteria.register("consume_item", new ConsumeItemCriterion());
    final static public EffectsChangedCriterion EFFECTS_CHANGED = Criteria.register("effects_changed", new EffectsChangedCriterion());
    final static public UsedTotemCriterion USED_TOTEM = Criteria.register("used_totem", new UsedTotemCriterion());
    final static public TravelCriterion NETHER_TRAVEL = Criteria.register("nether_travel", new TravelCriterion());
    final static public FishingRodHookedCriterion FISHING_ROD_HOOKED = Criteria.register("fishing_rod_hooked", new FishingRodHookedCriterion());
    final static public ChanneledLightningCriterion CHANNELED_LIGHTNING = Criteria.register("channeled_lightning", new ChanneledLightningCriterion());
    final static public ShotCrossbowCriterion SHOT_CROSSBOW = Criteria.register("shot_crossbow", new ShotCrossbowCriterion());
    final static public KilledByArrowCriterion KILLED_BY_ARROW = Criteria.register("killed_by_arrow", new KilledByArrowCriterion());
    final static public TickCriterion HERO_OF_THE_VILLAGE = Criteria.register("hero_of_the_village", new TickCriterion());
    final static public TickCriterion VOLUNTARY_EXILE = Criteria.register("voluntary_exile", new TickCriterion());
    final static public SlideDownBlockCriterion SLIDE_DOWN_BLOCK = Criteria.register("slide_down_block", new SlideDownBlockCriterion());
    final static public BeeNestDestroyedCriterion BEE_NEST_DESTROYED = Criteria.register("bee_nest_destroyed", new BeeNestDestroyedCriterion());
    final static public TargetHitCriterion TARGET_HIT = Criteria.register("target_hit", new TargetHitCriterion());
    final static public ItemCriterion ITEM_USED_ON_BLOCK = Criteria.register("item_used_on_block", new ItemCriterion());
    final static public DefaultBlockUseCriterion DEFAULT_BLOCK_USE = Criteria.register("default_block_use", new DefaultBlockUseCriterion());
    final static public AnyBlockUseCriterion ANY_BLOCK_USE = Criteria.register("any_block_use", new AnyBlockUseCriterion());
    final static public PlayerGeneratesContainerLootCriterion PLAYER_GENERATES_CONTAINER_LOOT = Criteria.register("player_generates_container_loot", new PlayerGeneratesContainerLootCriterion());
    final static public ThrownItemPickedUpByEntityCriterion THROWN_ITEM_PICKED_UP_BY_ENTITY = Criteria.register("thrown_item_picked_up_by_entity", new ThrownItemPickedUpByEntityCriterion());
    final static public ThrownItemPickedUpByEntityCriterion THROWN_ITEM_PICKED_UP_BY_PLAYER = Criteria.register("thrown_item_picked_up_by_player", new ThrownItemPickedUpByEntityCriterion());
    final static public PlayerInteractedWithEntityCriterion PLAYER_INTERACTED_WITH_ENTITY = Criteria.register("player_interacted_with_entity", new PlayerInteractedWithEntityCriterion());
    final static public PlayerInteractedWithEntityCriterion PLAYER_SHEARED_EQUIPMENT = Criteria.register("player_sheared_equipment", new PlayerInteractedWithEntityCriterion());
    final static public StartedRidingCriterion STARTED_RIDING = Criteria.register("started_riding", new StartedRidingCriterion());
    final static public LightningStrikeCriterion LIGHTNING_STRIKE = Criteria.register("lightning_strike", new LightningStrikeCriterion());
    final static public UsingItemCriterion USING_ITEM = Criteria.register("using_item", new UsingItemCriterion());
    final static public TravelCriterion FALL_FROM_HEIGHT = Criteria.register("fall_from_height", new TravelCriterion());
    final static public TravelCriterion RIDE_ENTITY_IN_LAVA = Criteria.register("ride_entity_in_lava", new TravelCriterion());
    final static public OnKilledCriterion KILL_MOB_NEAR_SCULK_CATALYST = Criteria.register("kill_mob_near_sculk_catalyst", new OnKilledCriterion());
    final static public ItemCriterion ALLAY_DROP_ITEM_ON_BLOCK = Criteria.register("allay_drop_item_on_block", new ItemCriterion());
    final static public TickCriterion AVOID_VIBRATION = Criteria.register("avoid_vibration", new TickCriterion());
    final static public RecipeCraftedCriterion RECIPE_CRAFTED = Criteria.register("recipe_crafted", new RecipeCraftedCriterion());
    final static public RecipeCraftedCriterion CRAFTER_RECIPE_CRAFTED = Criteria.register("crafter_recipe_crafted", new RecipeCraftedCriterion());
    final static public FallAfterExplosionCriterion FALL_AFTER_EXPLOSION = Criteria.register("fall_after_explosion", new FallAfterExplosionCriterion());

    public static <T extends Criterion<?>> T register(String id, T criterion) {
        return (T)Registry.register(Registries.CRITERION, id, criterion);
    }

    public static Criterion<?> getDefault(Registry<Criterion<?>> registry) {
        return IMPOSSIBLE;
    }
}

