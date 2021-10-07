package com.lothrazar.cyclic.block.bedrock;

import com.lothrazar.cyclic.base.BlockBase;
import com.lothrazar.cyclic.util.UtilParticle;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class UnbreakablePoweredBlock extends BlockBase {

  public static final BooleanProperty BREAKABLE = UnbreakableBlock.BREAKABLE;

  public UnbreakablePoweredBlock(Properties properties) {
    super(properties.strength(50.0F, 1200.0F));
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
    return new UnbreakablePoweredTile();
  }

  public static void setBreakable(Level world, BlockPos pos, boolean isBreakable) {
    BlockState state = world.getBlockState(pos);
    boolean oldBreakable = state.getValue(BREAKABLE);
    if (oldBreakable != isBreakable) {
      world.setBlockAndUpdate(pos, state.setValue(BREAKABLE, isBreakable));
      if (world.isClientSide) {
        UtilParticle.spawnParticle(world, DustParticleOptions.REDSTONE, pos, 5);
      }
    }
  }

  @Override
  @SuppressWarnings("deprecation")
  public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
    return (state.hasProperty(BREAKABLE) && !state.getValue(BREAKABLE)) ? 0.0F : super.getDestroyProgress(state, player, worldIn, pos);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(BREAKABLE);
  }
}
