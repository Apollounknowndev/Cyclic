/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (C) 2014-2018 Sam Bassett (aka Lothrazar)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.lothrazar.cyclicmagic.component.wandtorch;

import com.lothrazar.cyclicmagic.IHasRecipe;
import com.lothrazar.cyclicmagic.item.base.BaseTool;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.util.UtilSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemTorchThrower extends BaseTool implements IHasRecipe {

  private static final float VELOCITY_DEFAULT = 1.5F;
  private static final float INACCURACY_DEFAULT = 1.0F;
  private static final float PITCHOFFSET = 0.0F;//copied from BaseItemProjectile
  private static final int COOLDOWN = 8;//ticks

  public ItemTorchThrower() {
    super(256);//at 64 it reparied 21->37
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    ItemStack stack = player.getHeldItem(hand);
    if (world.isRemote == false) {
      EntityTorchBolt thing = new EntityTorchBolt(world, player);
      thing.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, PITCHOFFSET, VELOCITY_DEFAULT, INACCURACY_DEFAULT);
      world.spawnEntity(thing);
    }
    UtilSound.playSound(player, player.getPosition(), SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS);
    player.getCooldownTracker().setCooldown(this, COOLDOWN);
    super.onUse(stack, player, world, hand);
    return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
  }

  @Override
  public IRecipe addRecipe() {
    RecipeRegistry.addShapedRecipe(new ItemStack(this),
        " gc",
        " cg",
        "l  ",
        'g', "ingotGold",
        'c', "blockCoal",
        'l', "logWood");
    return null;
  }

  @Override
  public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {//176 to 240 as an example repair
    ItemStack mat = new ItemStack(Blocks.COAL_BLOCK);
    if (!mat.isEmpty() && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) {
      return true;
    }
    return super.getIsRepairable(toRepair, repair);
  }
}
