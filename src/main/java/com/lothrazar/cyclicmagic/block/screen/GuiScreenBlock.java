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
package com.lothrazar.cyclicmagic.block.screen;

import java.io.IOException;
import org.lwjgl.input.Keyboard;
import com.lothrazar.cyclicmagic.ModCyclic;
import com.lothrazar.cyclicmagic.block.base.ITileTextbox;
import com.lothrazar.cyclicmagic.block.screen.TileEntityScreen.Fields;
import com.lothrazar.cyclicmagic.data.Const;
import com.lothrazar.cyclicmagic.data.Const.ScreenSize;
import com.lothrazar.cyclicmagic.gui.GuiSliderInteger;
import com.lothrazar.cyclicmagic.gui.GuiTextFieldMulti;
import com.lothrazar.cyclicmagic.gui.base.GuiBaseContainer;
import com.lothrazar.cyclicmagic.gui.button.ButtonTileEntityField;
import com.lothrazar.cyclicmagic.net.PacketTileSetField;
import com.lothrazar.cyclicmagic.net.PacketTileTextbox;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiScreenBlock extends GuiBaseContainer {

  private GuiTextFieldMulti txtInput;
  TileEntityScreen screen;
  private ButtonTileEntityField btnToggle;

  public GuiScreenBlock(InventoryPlayer inventoryPlayer, TileEntityScreen tileEntity) {
    super(new ContainerScreen(inventoryPlayer, tileEntity), tileEntity);
    screen = tileEntity;
    screenSize = ScreenSize.STANDARDPLAIN;
  }

  @Override
  public void initGui() {
    super.initGui();
    Keyboard.enableRepeatEvents(true);
    int id = 1;
    int width = 124;
    int xCenter = (xSize / 2 - width / 2);
    int x = this.guiLeft + 26;
    txtInput = new GuiTextFieldMulti(id, this.fontRenderer, xCenter, Const.PAD / 2, width, 12 * 6);
    txtInput.setMaxStringLength(ScreenTESR.MAX_TOTAL);
    txtInput.setText(screen.getText());
    txtInput.setFocused(true);
    txtInput.setCursorPosition(tile.getField(Fields.CURSORPOS.ordinal()));
    int h = 12;
    id++;
    int y = this.guiTop + txtInput.height + Const.PAD;
    GuiSliderInteger sliderX = new GuiSliderInteger(tile, id, x, y, width, h, 0, 255, Fields.RED.ordinal(), false);
    sliderX.setTooltip("screen.red");
    this.addButton(sliderX);
    id++;
    y += h + 1;
    GuiSliderInteger sliderG = new GuiSliderInteger(tile, id, x, y, width, h, 0, 255, Fields.GREEN.ordinal(), false);
    sliderG.setTooltip("screen.green");
    this.addButton(sliderG);
    id++;
    y += h + 1;
    GuiSliderInteger sliderB = new GuiSliderInteger(tile, id, x, y, width, h, 0, 255, Fields.BLUE.ordinal(), false);
    sliderB.setTooltip("screen.blue");
    this.addButton(sliderB);
    id++;
    y += h + 1;
    GuiSliderInteger sliderPadding = new GuiSliderInteger(tile, id, x, y, width, h, 0, 60, Fields.PADDING.ordinal(), false);
    sliderPadding.setTooltip("screen.padding");
    this.addButton(sliderPadding);
    //text box of course
    id++;
    btnToggle = new ButtonTileEntityField(id++,
        this.guiLeft + 4,
        this.guiTop + Const.PAD / 2, this.tile.getPos(), Fields.JUSTIFICATION.ordinal(), 1);
    btnToggle.setTooltip("screen.justification");
    btnToggle.width = 20;
    this.addButton(btnToggle);
  }

  @Override
  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
    if (txtInput != null) {
      tile.setField(Fields.CURSORPOS.ordinal(), this.txtInput.getCursorPosition());
      ModCyclic.network.sendToServer(new PacketTileSetField(tile.getPos(), Fields.CURSORPOS.ordinal(), this.txtInput.getCursorPosition()));
    }
  }

  @SideOnly(Side.CLIENT)
  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    if (txtInput != null) {
      txtInput.drawTextBox();
      txtInput.setTextColor(screen.getColor());
    }
    btnToggle.setTextureIndex(8 + tile.getField(Fields.JUSTIFICATION.ordinal()));
  }

  // http://www.minecraftforge.net/forum/index.php?topic=22378.0
  // below is all the stuff that makes the text box NOT broken
  @Override
  public void updateScreen() {
    super.updateScreen();
    if (txtInput != null) {
      txtInput.updateCursorCounter();
    }
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode) == false) {
      super.keyTyped(typedChar, keyCode);
    }
    if (txtInput != null && txtInput.isFocused()) {
      txtInput.textboxKeyTyped(typedChar, keyCode);
      ((ITileTextbox) tile).setText(txtInput.getText());
      ModCyclic.network.sendToServer(new PacketTileTextbox(txtInput.getText(), tile.getPos()));
    }
  }

  @Override
  protected void mouseClicked(int x, int y, int btn) throws IOException {
    super.mouseClicked(x, y, btn);// x/y pos is 33/30
    if (txtInput != null) {
      txtInput.mouseClicked(x, y, btn);
      txtInput.setFocused(true);
    }
  }
  // ok end of textbox fixing stuff
}
