package com.lothrazar.cyclic.block.forester;

import com.lothrazar.cyclic.config.ClientConfigCyclic;
import com.lothrazar.cyclic.util.UtilRender;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;

public class RenderForester extends BlockEntityRenderer<TileForester> {

  public RenderForester(BlockEntityRenderDispatcher d) {
    super(d);
  }

  @Override
  public void render(TileForester te, float v, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int partialTicks, int destroyStage) {
    // ok
    if (te.getField(TileForester.Fields.RENDER.ordinal()) == 1) {
      UtilRender.renderOutline(te.getBlockPos(), te.getShapeHollow(), matrixStack, 0.7F, ClientConfigCyclic.getColor(te));
    }
  }
}
