package com.lothrazar.cyclicmagic.module;
import java.util.ArrayList;
import com.lothrazar.cyclicmagic.item.ItemPotionCustom;
import com.lothrazar.cyclicmagic.potion.PotionBase;
import com.lothrazar.cyclicmagic.potion.PotionEnder;
import com.lothrazar.cyclicmagic.potion.PotionMagnet;
import com.lothrazar.cyclicmagic.potion.PotionSlowfall;
import com.lothrazar.cyclicmagic.potion.PotionSnow;
import com.lothrazar.cyclicmagic.potion.PotionWaterwalk;
import com.lothrazar.cyclicmagic.registry.ItemRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.Const.Potions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionModule extends BaseEventModule {
  public static enum PotionType {
    NORMAL, POWERED, LONG//, SPLASH, LINGER // todo: these last two
  }
  public static PotionBase slowfallEffect;
  public static PotionBase magnetEffect;
  public static PotionBase enderEffect;
  public static PotionBase waterwalkEffect;
  public static PotionBase snowEffect;
  public static final ItemPotionCustom potion_viscous = new ItemPotionCustom(false);
  public static ItemPotionCustom potion_snow;
  public static ItemPotionCustom potion_ender;
  public static ItemPotionCustom potion_ender_long;
  public static ItemPotionCustom potion_magnet;
  public static ItemPotionCustom potion_magnet_long;
  public static ItemPotionCustom potion_waterwalk;
  public static ItemPotionCustom potion_waterwalk_long;
  public static ItemPotionCustom potion_slowfall;
  public static ItemPotionCustom potion_slowfall_long;
  public static ItemPotionCustom potion_levitation;
  public static ItemPotionCustom potion_levitation_long;
  public static ItemPotionCustom potion_luck;
  public static ItemPotionCustom potion_luck_long;
  //  public static final ItemPotionCustom potion_glowing = new ItemPotionCustom(MobEffects.GLOWING, 60*3);
  //  public static final ItemPotionCustom potion_glowing_long = new ItemPotionCustom(MobEffects.GLOWING, 60*8);
  public static ItemPotionCustom potion_resistance;
  public static ItemPotionCustom potion_resistance_strong;
  public static ItemPotionCustom potion_resistance_long;
  public static ItemPotionCustom potion_boost;
  public static ItemPotionCustom potion_boost_long;
  public static ItemPotionCustom potion_haste;
  public static ItemPotionCustom potion_haste_strong;
  public static ItemPotionCustom potion_haste_long;
  public boolean cancelPotionInventoryShift;
  private boolean enableMagnet;
  private boolean enableWaterwalk;
  private boolean enableSlowfall;
  private boolean enableSnow;
  private boolean enableEnder;
  private boolean enableHaste;
  private boolean enableResist;
  private boolean enableLuck;
  private boolean enableLevit;
  private boolean enableHBoost;
  final static int SHORT = 60 + 30;
  final static int NORMAL = 60 * 3;
  final static int LONG = 60 * 8;
  private ArrayList<PotionBase> potionEffects = new ArrayList<PotionBase>();
  private ItemPotionCustom potion_snow_long;
  private void registerPotionEffect(PotionBase effect){

    GameRegistry.register(effect, effect.getIcon());
    potionEffects.add(effect);
  }
  @Override
  public void onInit() {
    MinecraftForge.EVENT_BUS.register(this);
    // http://www.minecraftforge.net/forum/index.php?topic=11024.0
    // ??? http://www.minecraftforge.net/forum/index.php?topic=12358.0
    //CORE/BASE POTION
    ItemStack awkward = BrewingRecipeRegistry.getOutput(new ItemStack(Items.POTIONITEM), new ItemStack(Items.NETHER_WART));
    ItemRegistry.addItem(potion_viscous, "potion_viscous");
    BrewingRecipeRegistry.addRecipe(
        awkward,
        new ItemStack(Items.DYE, 1, EnumDyeColor.BROWN.getDyeDamage()),
        new ItemStack(potion_viscous));
    if (enableEnder) {
      enderEffect = new PotionEnder("ender", true, 0);
      registerPotionEffect(enderEffect);
      potion_ender = new ItemPotionCustom(true,enderEffect, NORMAL, Potions.I,"item.potion_ender.tooltip");
      ItemRegistry.addItem(potion_ender, "potion_ender");
   
      potion_ender_long = new ItemPotionCustom(true,enderEffect, LONG, Potions.I,"item.potion_ender.tooltip");
      ItemRegistry.addItem(potion_ender_long, "potion_ender_long");
       
      addBrewingRecipe(
          potion_viscous,
          Items.ENDER_EYE,
          potion_ender);
      addBrewingRecipe(
          potion_ender,
          Items.REDSTONE,
          potion_ender_long);
    }
    if (enableMagnet) {
      magnetEffect = new PotionMagnet("magnet", true, 0);
      registerPotionEffect(magnetEffect);
      potion_magnet = new ItemPotionCustom(false,magnetEffect, NORMAL, Potions.I);
      potion_magnet_long = new ItemPotionCustom(false,magnetEffect, LONG, Potions.I);
      ItemRegistry.addItem(potion_magnet, "potion_magnet");
      ItemRegistry.addItem(potion_magnet_long, "potion_magnet_long");
      BrewingRecipeRegistry.addRecipe(
          new ItemStack(potion_viscous),
          new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
          new ItemStack(potion_magnet));
      addBrewingRecipe(
          potion_magnet,
          Items.REDSTONE,
          potion_magnet_long);
    }
    if (enableWaterwalk) {
      waterwalkEffect = new PotionWaterwalk("waterwalk", true, 0);
      registerPotionEffect(waterwalkEffect);
      potion_waterwalk = new ItemPotionCustom(false,waterwalkEffect, NORMAL, Potions.I);
      potion_waterwalk_long = new ItemPotionCustom(false,waterwalkEffect, LONG, Potions.I);
      ItemRegistry.addItem(potion_waterwalk, "potion_waterwalk");
      ItemRegistry.addItem(potion_waterwalk_long, "potion_waterwalk_long");
      addBrewingRecipe(
          potion_viscous,
          Items.PRISMARINE_CRYSTALS,
          potion_waterwalk);
      addBrewingRecipe(
          potion_waterwalk,
          Items.REDSTONE,
          potion_waterwalk_long);
    }
    if (enableSlowfall) {
      slowfallEffect = new PotionSlowfall("slowfall", true, 0);
      registerPotionEffect(slowfallEffect);
      potion_slowfall = new ItemPotionCustom(true,slowfallEffect, NORMAL, Potions.I);
      potion_slowfall_long = new ItemPotionCustom(true,slowfallEffect, LONG, Potions.I);
      ItemRegistry.addItem(potion_slowfall, "potion_slowfall");
      ItemRegistry.addItem(potion_slowfall_long, "potion_slowfall_long");
      BrewingRecipeRegistry.addRecipe(
          new ItemStack(potion_viscous),
          new ItemStack(Items.FISH, 1, ItemFishFood.FishType.CLOWNFISH.getMetadata()),
          new ItemStack(potion_slowfall));
      BrewingRecipeRegistry.addRecipe(
          new ItemStack(potion_slowfall),
          new ItemStack(Items.REDSTONE),
          new ItemStack(potion_slowfall_long));
    }
    if (enableSnow) {
      snowEffect = new PotionSnow("snow", true, 0);
      registerPotionEffect(snowEffect);
      potion_snow = new ItemPotionCustom(true, snowEffect, NORMAL, Potions.I, "item.potion_snow.tooltip");
      ItemRegistry.addItem(potion_snow, "potion_snow");
      BrewingRecipeRegistry.addRecipe(
          new ItemStack(potion_viscous),
          new ItemStack(Blocks.ICE),
          new ItemStack(potion_snow));
      potion_snow_long = new ItemPotionCustom(true, snowEffect, LONG, Potions.I, "item.potion_snow.tooltip");
      ItemRegistry.addItem(potion_snow_long, "potion_snow_long");
      BrewingRecipeRegistry.addRecipe(
          new ItemStack(potion_snow),
          new ItemStack(Items.REDSTONE),
          new ItemStack(potion_snow_long));
    }
    if (enableHBoost) {
      potion_boost = new ItemPotionCustom(true, MobEffects.HEALTH_BOOST,NORMAL, Const.Potions.V);
      potion_boost_long = new ItemPotionCustom(true, MobEffects.HEALTH_BOOST, LONG, Const.Potions.V);
      ItemRegistry.addItem(potion_boost, "potion_boost");
      ItemRegistry.addItem(potion_boost_long, "potion_boost_long");
      addBrewingRecipe(
          potion_viscous,
          Items.GOLDEN_APPLE,
          potion_boost);
      addBrewingRecipe(
          potion_boost,
          Items.REDSTONE,
          potion_boost_long);
    }
    if (enableResist) {
      potion_resistance = new ItemPotionCustom(true, MobEffects.RESISTANCE, NORMAL);
      potion_resistance_strong = new ItemPotionCustom(true, MobEffects.RESISTANCE, SHORT, Const.Potions.II);
      potion_resistance_long = new ItemPotionCustom(true, MobEffects.RESISTANCE, LONG);
      ItemRegistry.addItem(potion_resistance, "potion_resistance");
      ItemRegistry.addItem(potion_resistance_long, "potion_resistance_long");
      ItemRegistry.addItem(potion_resistance_strong, "potion_resistance_strong");
      addBrewingRecipe(
          potion_viscous,
          Items.DIAMOND,
          potion_resistance);
      addBrewingRecipe(
          potion_resistance,
          Items.REDSTONE,
          potion_resistance_long);
      addBrewingRecipe(
          potion_resistance,
          Items.GLOWSTONE_DUST,
          potion_resistance_strong);
    }
    if (enableHaste) {
      potion_haste = new ItemPotionCustom(false, MobEffects.HASTE, 60 * 3);
      potion_haste_strong = new ItemPotionCustom(false, MobEffects.HASTE, 90, Const.Potions.II);
      potion_haste_long = new ItemPotionCustom(false, MobEffects.HASTE, LONG);
      ItemRegistry.addItem(potion_haste, "potion_haste");
      ItemRegistry.addItem(potion_haste_long, "potion_haste_long");
      ItemRegistry.addItem(potion_haste_strong, "potion_haste_strong");
      addBrewingRecipe(
          potion_viscous,
          Items.EMERALD,
          potion_haste);
      addBrewingRecipe(
          potion_haste,
          Items.REDSTONE,
          potion_haste_long);
      addBrewingRecipe(
          potion_haste,
          Items.GLOWSTONE_DUST,
          potion_haste_strong);
    }
    if (enableLuck) {
      potion_luck = new ItemPotionCustom(true, MobEffects.LUCK, NORMAL);
      potion_luck_long = new ItemPotionCustom(true, MobEffects.LUCK, LONG);
      ItemRegistry.addItem(potion_luck, "potion_luck");
      ItemRegistry.addItem(potion_luck_long, "potion_luck_long");
      BrewingRecipeRegistry.addRecipe(
          new ItemStack(potion_viscous),
          new ItemStack(Items.SLIME_BALL),
          new ItemStack(potion_luck));
      addBrewingRecipe(
          potion_luck,
          Items.REDSTONE,
          potion_luck_long);
    }
    if (enableLevit) {
      potion_levitation = new ItemPotionCustom(true, MobEffects.LEVITATION,NORMAL);
      potion_levitation_long = new ItemPotionCustom(true, MobEffects.LEVITATION, LONG);
      ItemRegistry.addItem(potion_levitation, "potion_levitation");
      ItemRegistry.addItem(potion_levitation_long, "potion_levitation_long");
      BrewingRecipeRegistry.addRecipe(
          new ItemStack(potion_viscous),
          new ItemStack(Items.CHORUS_FRUIT),
          new ItemStack(potion_levitation));
      BrewingRecipeRegistry.addRecipe(
          new ItemStack(potion_levitation),
          new ItemStack(Items.REDSTONE),
          new ItemStack(potion_levitation_long));
    }
  }
  private static void addBrewingRecipe(Item input, Item ingredient, Item output) {
    BrewingRecipeRegistry.addRecipe(
        new ItemStack(input),
        new ItemStack(ingredient),
        new ItemStack(output));
  }
  @SubscribeEvent
  public void onEntityUpdate(LivingUpdateEvent event) {
    EntityLivingBase entity = event.getEntityLiving();
    if (entity == null) { return; }
    for(PotionBase effect : this.potionEffects){
      if (effect != null && entity.isPotionActive(effect)) {
        effect.tick(entity);
      }
    }
  }
  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onPotionShiftEvent(GuiScreenEvent.PotionShiftEvent event) {
    event.setCanceled(cancelPotionInventoryShift);
  }
  @Override
  public void syncConfig(Configuration config) {
    String category = Const.ConfigCategory.inventory;
    cancelPotionInventoryShift = config.getBoolean("Potion Inventory Shift", category, true,
        "When true, this blocks the potions moving the inventory over");
    category = Const.ConfigCategory.content;
    enableMagnet = config.getBoolean("PotionMagnet", category, true, Const.ConfigCategory.contentDefaultText);
    enableWaterwalk = config.getBoolean("PotionWaterwalk", category, true, Const.ConfigCategory.contentDefaultText);
    enableSlowfall = config.getBoolean("PotionSlowfall", category, true, Const.ConfigCategory.contentDefaultText);
    enableSnow = config.getBoolean("PotionSnow", category, true, Const.ConfigCategory.contentDefaultText);
    enableEnder = config.getBoolean("PotionEnder", category, true, Const.ConfigCategory.contentDefaultText);
    enableHaste = config.getBoolean("Potionhaste", category, true, Const.ConfigCategory.contentDefaultText);
    enableResist = config.getBoolean("PotionResistance", category, true, Const.ConfigCategory.contentDefaultText);
    enableLuck = config.getBoolean("PotionLuck", category, true, Const.ConfigCategory.contentDefaultText);
    enableLevit = config.getBoolean("PotionLevitation", category, true, Const.ConfigCategory.contentDefaultText);
    enableHBoost = config.getBoolean("PotionHealthBoost", category, true, Const.ConfigCategory.contentDefaultText);
  }
}
