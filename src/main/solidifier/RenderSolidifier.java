package com.lothrazar.cyclic.block.solidifier;

import com.lothrazar.cyclic.util.FluidHelpers;
import com.lothrazar.library.render.type.FluidTankRenderType;
import com.lothrazar.library.util.RenderBlockUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public class RenderSolidifier implements BlockEntityRenderer<TileSolidifier> {

  public RenderSolidifier(BlockEntityRendererProvider.Context d) {}

  @Override
  public void render(TileSolidifier tankHere, float v, PoseStack matrixStack,
      MultiBufferSource buffer, int light, int overlayLight) {
    IItemHandler itemHandler = tankHere.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).orElse(null);
    var level = tankHere.getLevel();
    if (itemHandler != null) {
      ItemStack stack = itemHandler.getStackInSlot(0);
      if (!stack.isEmpty()) {
        matrixStack.pushPose();
        //        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.translate(0.4, 0.65, 0.4);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, 0x111111, 0, matrixStack, buffer, level, light);
        matrixStack.popPose();
      }
      stack = itemHandler.getStackInSlot(1);
      if (!stack.isEmpty()) {
        matrixStack.pushPose();
        //        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.translate(0.5, 0.45, 0.5);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, 0x777777, 0, matrixStack, buffer, level, light);
        matrixStack.popPose();
      }
      stack = itemHandler.getStackInSlot(2);
      if (!stack.isEmpty()) {
        matrixStack.pushPose();
        matrixStack.translate(0.6, 0.15, 0.6);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, 0xBBBBBB, 0, matrixStack, buffer, level, light);
        matrixStack.popPose();
      }
    }
    IFluidHandler handler = tankHere.getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null);
    if (handler == null || handler.getFluidInTank(0) == null) {
      return;
    }
    FluidStack fluid = handler.getFluidInTank(0);
    if (fluid.isEmpty()) {
      return;
    }
    VertexConsumer vertexBuffer = buffer.getBuffer(FluidTankRenderType.RESIZABLE);
    matrixStack.pushPose();
    matrixStack.scale(1F, FluidHelpers.getScale(tankHere.tank), 1F);
    RenderBlockUtils.renderObject(FluidHelpers.getFluidModel(fluid, FluidHelpers.STAGES - 1),
        matrixStack, vertexBuffer, RenderBlockUtils.getColorARGB(fluid),
        RenderBlockUtils.calculateGlowLight(light, fluid));
    matrixStack.popPose();
  }
}
