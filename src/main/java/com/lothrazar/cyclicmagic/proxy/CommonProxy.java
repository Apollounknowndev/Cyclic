package com.lothrazar.cyclicmagic.proxy;
import java.awt.Color;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommonProxy {
  public void register() {
  }
  public World getClientWorld() {
    return null;
  }
  public BlockPos getBlockMouseoverExact(int max) {
    return null;
  }
  public BlockPos getBlockMouseoverOffset(int max) {
    return null;
  }
  public EnumFacing getSideMouseover(int max) {
    return null;
  }
  public void setClientPlayerData(NBTTagCompound tags) {
    //client side only
  }
  public void renderCube(BlockPos pos, Color color) {
  }
}
