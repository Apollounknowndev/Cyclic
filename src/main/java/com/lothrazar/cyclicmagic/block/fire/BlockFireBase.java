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
package com.lothrazar.cyclicmagic.block.fire;

import java.util.List;
import java.util.Random;
import com.lothrazar.cyclicmagic.core.util.UtilChat;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFireBase extends BlockFire {

  public BlockFireBase() {
    super();
    this.setHardness(0.0F).setLightLevel(1.0F);
    this.enableStats = false;
    this.blockSoundType = SoundType.CLOTH;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
    tooltip.add(UtilChat.lang(this.getTranslationKey() + ".tooltip"));
  }

  @Override
  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    if (worldIn.getGameRules().getBoolean("doFireTick")) {
      if (!this.canPlaceBlockAt(worldIn, pos)) {
        worldIn.setBlockToAir(pos);
      }
      Block block = worldIn.getBlockState(pos.down()).getBlock();
      boolean isBlockBelowFireSource = block.isFireSource(worldIn, pos.down(), EnumFacing.UP);
      int intAge = state.getValue(AGE).intValue();
      if (!isBlockBelowFireSource && worldIn.isRaining() && this.canDie(worldIn, pos) && rand.nextFloat() < 0.2F + intAge * 0.03F) {
        worldIn.setBlockToAir(pos);
      }
      else {
        if (intAge < 15) {
          state = state.withProperty(AGE, Integer.valueOf(intAge + rand.nextInt(3) / 2));
          worldIn.setBlockState(pos, state, 4);
        }
        else if (!isBlockBelowFireSource && intAge == 15 && worldIn.rand.nextDouble() < 0.5) {
          //max age -> then start to burn out and expire 
          worldIn.setBlockToAir(pos);
        }
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + rand.nextInt(10));
        if (!isBlockBelowFireSource) {
          if (!this.canNeighborCatchFire(worldIn, pos)) {
            if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) || intAge > 3) {
              worldIn.setBlockToAir(pos);
            }
            return;
          }
          if (!this.canCatchFire(worldIn, pos.down(), EnumFacing.UP) && intAge == 15 && rand.nextInt(4) == 0) {
            worldIn.setBlockToAir(pos);
            return;
          }
        }
        boolean isHumid = worldIn.isBlockinHighHumidity(pos);
        int humidity = 0;
        if (isHumid) {
          humidity = -50;
        }
        this.tryCatchFire(worldIn, pos.east(), 300 + humidity, rand, intAge, EnumFacing.WEST);
        this.tryCatchFire(worldIn, pos.west(), 300 + humidity, rand, intAge, EnumFacing.EAST);
        this.tryCatchFire(worldIn, pos.down(), 250 + humidity, rand, intAge, EnumFacing.UP);
        this.tryCatchFire(worldIn, pos.up(), 250 + humidity, rand, intAge, EnumFacing.DOWN);
        this.tryCatchFire(worldIn, pos.north(), 300 + humidity, rand, intAge, EnumFacing.SOUTH);
        this.tryCatchFire(worldIn, pos.south(), 300 + humidity, rand, intAge, EnumFacing.NORTH);
        for (int k = -1; k <= 1; ++k) {
          for (int l = -1; l <= 1; ++l) {
            for (int i1 = -1; i1 <= 4; ++i1) {
              if (k != 0 || i1 != 0 || l != 0) {
                int j1 = 100;
                if (i1 > 1) {
                  j1 += (i1 - 1) * 100;
                }
                BlockPos blockpos = pos.add(k, i1, l);
                int k1 = this.getNeighborEncouragement(worldIn, blockpos);
                if (k1 > 0) {
                  int l1 = (k1 + 40 + worldIn.getDifficulty().getId() * 7) / (intAge + 30);
                  if (isHumid) {
                    l1 /= 2;
                  }
                  if (l1 > 0 && rand.nextInt(j1) <= l1 && (!worldIn.isRaining() || !this.canDie(worldIn, blockpos))) {
                    int i2 = intAge + rand.nextInt(5) / 4;
                    if (i2 > 15) {
                      i2 = 15;
                    }
                    worldIn.setBlockState(blockpos, state.withProperty(AGE, Integer.valueOf(i2)), 3);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private boolean canNeighborCatchFire(World worldIn, BlockPos pos) {
    for (EnumFacing enumfacing : EnumFacing.values()) {
      if (this.canCatchFire(worldIn, pos.offset(enumfacing), enumfacing.getOpposite())) {
        return true;
      }
    }
    return false;
  }

  private int getNeighborEncouragement(World worldIn, BlockPos pos) {
    if (!worldIn.isAirBlock(pos)) {
      return 0;
    }
    else {
      int i = 0;
      for (EnumFacing enumfacing : EnumFacing.values()) {
        i = Math.max(worldIn.getBlockState(pos.offset(enumfacing)).getBlock().getFireSpreadSpeed(worldIn, pos.offset(enumfacing), enumfacing.getOpposite()), i);
      }
      return i;
    }
  }

  private void tryCatchFire(World worldIn, BlockPos pos, int chance, Random random, int age, EnumFacing face) {
    int i = worldIn.getBlockState(pos).getBlock().getFlammability(worldIn, pos, face);
    if (random.nextInt(chance) < i && worldIn.isAirBlock(pos)) {//safe fire: only set fire if air
      IBlockState iblockstate = worldIn.getBlockState(pos);
      if (random.nextInt(age + 10) < 5 && !worldIn.isRainingAt(pos)) {
        int j = age + random.nextInt(5) / 4;
        if (j > 15) {
          j = 15;
        }
        worldIn.setBlockState(pos, this.getDefaultState().withProperty(AGE, Integer.valueOf(j)), 3);
      }
      if (iblockstate.getBlock() == Blocks.TNT) {
        Blocks.TNT.onPlayerDestroy(worldIn, pos, iblockstate.withProperty(BlockTNT.EXPLODE, Boolean.valueOf(true)));
      }
    }
  }
}
