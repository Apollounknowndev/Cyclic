package com.lothrazar.cyclic.block.eye;

import com.lothrazar.cyclic.base.TileEntityBase;
import com.lothrazar.cyclic.registry.TileRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class TileEye extends TileEntityBase implements TickableBlockEntity {

  public static IntValue RANGE;
  public static IntValue FREQUENCY;

  public TileEye() {
    super(TileRegistry.eye_redstone);
  }

  @Override
  public void load(BlockState bs, CompoundTag tag) {
    super.load(bs, tag);
  }

  @Override
  public CompoundTag save(CompoundTag tag) {
    return super.save(tag);
  }

  @Override
  public void tick() {
    if (level.isClientSide) {
      return;
    }
    timer--;
    if (timer > 0) {
      return;
    }
    timer = FREQUENCY.get();
    //
    boolean playerFound = getLookingPlayer(RANGE.get(), false) != null;
    this.setLitProperty(playerFound);
  }

  @Override
  public void setField(int field, int value) {}

  @Override
  public int getField(int field) {
    return 0;
  }
}
