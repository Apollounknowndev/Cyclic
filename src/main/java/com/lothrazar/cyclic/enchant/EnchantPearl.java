package com.lothrazar.cyclic.enchant;

import com.lothrazar.cyclic.base.EnchantBase;
import com.lothrazar.cyclic.util.UtilEntity;
import com.lothrazar.cyclic.util.UtilSound;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class EnchantPearl extends EnchantBase {

  public EnchantPearl(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
    super(rarityIn, typeIn, slots);
    MinecraftForge.EVENT_BUS.register(this);
  }

  private static final int COOLDOWN = 6 * 20;
  private static final float VELOCITY = 1.5F; //Same as EnderPearlItem
  private static final float INNACCURACY = 1F; //Same as EnderPearlItem
  public static BooleanValue CFG;
  public static final String ID = "ender";

  @Override
  public boolean isEnabled() {
    return CFG.get();
  }

  @Override
  public int getMaxLevel() {
    return 3;
  }

  @Override
  public boolean canEnchant(ItemStack stack) {
    return stack.getItem() instanceof SwordItem;
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return this.canEnchant(stack);
  }

  @SubscribeEvent
  public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
    Level world = event.getWorld();
    if (!world.isClientSide && event.getResult() != Result.DENY) {
      int level = EnchantmentHelper.getItemEnchantmentLevel(this, event.getItemStack());
      if (level > 0) {
        int adjustedCooldown = COOLDOWN / level;
        Player player = event.getPlayer();
        if (player.getCooldowns().isOnCooldown(event.getItemStack().getItem())) {
          return;
        }
        ThrownEnderpearl pearl = new ThrownEnderpearl(world, player);
        Vec3 lookVector = player.getLookAngle();
        pearl.shoot(lookVector.x(), lookVector.y(), lookVector.z(), VELOCITY, INNACCURACY);
        UtilEntity.setCooldownItem(player, event.getItemStack().getItem(), adjustedCooldown);
        UtilSound.playSound(player, SoundEvents.ENDER_PEARL_THROW, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
        world.addFreshEntity(pearl);
        //block propogation of event 
        event.setResult(Result.DENY);
        event.setCanceled(true);
      }
    }
  }
}
