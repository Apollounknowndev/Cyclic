package com.lothrazar.cyclic.net;

import java.util.function.Supplier;
import com.lothrazar.cyclic.capabilities.ClientDataManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * Sync Player and Chunk capabilities to client
 *
 */
public class PacketSyncManaToClient extends PacketBaseCyclic {

  private int playerMana; // TODO : playerData object
  private int chunkMana; // TODO: chunkData object

  public PacketSyncManaToClient(int playerMana, int chunkMana) {
    this.playerMana = playerMana;
    this.chunkMana = chunkMana;
  }
  //  public PacketSyncManaToClient() {}

  public static void handle(PacketSyncManaToClient message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      // .println("client sync message spam");
      ClientDataManager.set(message.playerMana, message.chunkMana);
    });
    message.done(ctx);
  }

  public static PacketSyncManaToClient decode(FriendlyByteBuf buf) {
    return new PacketSyncManaToClient(buf.readInt(), buf.readInt());
  }

  public static void encode(PacketSyncManaToClient msg, FriendlyByteBuf buf) {
    buf.writeInt(msg.playerMana);
    buf.writeInt(msg.chunkMana);
  }
}
