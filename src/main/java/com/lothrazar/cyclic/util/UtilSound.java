package com.lothrazar.cyclic.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.registries.ForgeRegistries;

public class UtilSound {

  public static void playSound(Level world, BlockPos pos, SoundEvent soundIn) {
    world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), soundIn, SoundSource.BLOCKS, 0.5F, 0.5F, false);
  }

  public static void playSound(Level world, BlockPos pos, SoundEvent soundIn, float volume) {
    world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), soundIn, SoundSource.BLOCKS, volume, 0.5F, false);
  }

  public static void playSound(Entity entityIn, SoundEvent soundIn) {
    playSound(entityIn, soundIn, 1.0F, 1.0F);
  }

  public static void playSound(Entity entityIn, SoundEvent soundIn, float volume) {
    playSound(entityIn, soundIn, volume, 1.0F);
  }

  public static void playSound(Entity entityIn, SoundEvent soundIn, float volume, float pitch) {
    if (entityIn != null && entityIn.level.isClientSide) {
      entityIn.playSound(soundIn, volume, pitch);
    }
  }

  public static void playSoundFromServer(ServerPlayer entityIn, BlockPos pos, SoundEvent soundIn) {
    if (soundIn == null || entityIn == null) {
      return;
    }
    entityIn.connection.send(new ClientboundSoundPacket(
        soundIn,
        SoundSource.BLOCKS,
        pos.getX(), pos.getY(), pos.getZ(),
        1.0f, 1.0f));
  }

  public static void playSoundFromServer(ServerPlayer entityIn, SoundEvent soundIn) {
    if (soundIn == null || entityIn == null) {
      return;
    }
    entityIn.connection.send(new ClientboundSoundPacket(
        soundIn,
        SoundSource.BLOCKS,
        entityIn.xOld, entityIn.yOld, entityIn.zOld,
        1.0f, 1.0f));
  }

  public static void playSoundFromServer(ServerLevel world, BlockPos pos, SoundEvent soundIn) {
    for (ServerPlayer sp : world.players()) {
      playSoundFromServer(sp, pos, soundIn);
    }
  }

  public static void playSoundFromServerById(ServerLevel world, BlockPos pos, String sid) {
    SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(sid));
    if (sound != null) {
      for (ServerPlayer sp : world.players()) {
        playSoundFromServer(sp, pos, sound);
      }
    }
  }

  public static void playSoundById(Player player, String sid) {
    //do the thing
    SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(sid));
    if (sound != null && player.level.isClientSide) {
      UtilSound.playSound(player, sound);
    }
  }
}
