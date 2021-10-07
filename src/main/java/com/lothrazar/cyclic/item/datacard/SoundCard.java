package com.lothrazar.cyclic.item.datacard;

import com.lothrazar.cyclic.base.ItemBase;
import com.lothrazar.cyclic.util.UtilSound;
import java.util.List;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.item.Item.Properties;

public class SoundCard extends ItemBase {

  public static final String SOUND_ID = "sound_id";

  public SoundCard(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    //    BlockPos pos = context.getPos();
    //    World world = context.getWorld();
    Player player = context.getPlayer();
    if (player.getCooldowns().isOnCooldown(this)) {
      return InteractionResult.PASS;
    }
    ItemStack stack = context.getItemInHand();
    if (stack.hasTag() && stack.getTag().contains(SOUND_ID)) {
      //assume sound is valid
      player.getCooldowns().addCooldown(this, 10);
      player.swing(context.getHand());
      //actually play it
      String sid = stack.getTag().getString(SOUND_ID);
      UtilSound.playSoundById(player, sid);
    }
    return InteractionResult.PASS;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    super.appendHoverText(stack, worldIn, tooltip, flagIn);
    if (stack.hasTag() && stack.getTag().contains(SOUND_ID)) {
      tooltip.add(new TextComponent(stack.getTag().getString(SOUND_ID)).withStyle(ChatFormatting.GOLD));
    }
  }

  public static void saveSound(ItemStack stack, String soundId) {
    if (stack.hasTag() && (soundId == null || soundId.isEmpty())) {
      stack.getTag().remove(SOUND_ID);
    }
    else {
      stack.getOrCreateTag().putString(SOUND_ID, soundId);
    }
  }
}
