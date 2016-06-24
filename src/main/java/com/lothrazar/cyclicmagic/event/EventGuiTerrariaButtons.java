package com.lothrazar.cyclicmagic.event;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.lothrazar.cyclicmagic.IHasConfig;
import com.lothrazar.cyclicmagic.gui.button.ButtonTerrariaDepositAll;
import com.lothrazar.cyclicmagic.gui.button.ButtonTerrariaLootAll;
import com.lothrazar.cyclicmagic.gui.button.ButtonTerrariaQuickStack;
import com.lothrazar.cyclicmagic.gui.button.ButtonTerrariaRestock;
import com.lothrazar.cyclicmagic.gui.player.GuiPlayerExtended;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventGuiTerrariaButtons implements IHasConfig {
  private String position;
  public static boolean restockLeaveOne; //referenced by the PacketRestock
  public static final String posLeft = "topleft";
  public static final String posRight = "topright";
  public static final String posBottom = "bottomleft";
  public static final String posAlign = "align";
  public static final int padding = 4;
  public static final int BTNWIDTH = 20;
  private List<String> blacklistGuis;
  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onGuiPostInit(InitGuiEvent.Post event) {
    GuiScreen gui = event.getGui();
    if (gui == null) { return; } // probably doesn't ever happen
    // all containers by default
    // but with a blacklist in config
    String self = gui.getClass().getName();
    int button_id = 256;
    // config for different locations - left right bottom top
    int x = 0, y = 0, yDelta = 24, xDelta = 0;
    // not GuiContainerCreative
    if (gui instanceof GuiContainer &&
        !(gui instanceof GuiInventory) &&
        !(gui instanceof GuiPlayerExtended) &&
        blacklistGuis.contains(self) == false) {
      GuiContainer guiInv = (GuiContainer) gui;
      // align to different area depending on config
      if (position.equalsIgnoreCase(posLeft)) {
        x = padding;
        y = padding;
        // we are moving top to bottom, so
        xDelta = 0;
        yDelta = Const.btnHeight + padding;
      }
      else if (position.equalsIgnoreCase(posRight)) {
        x = Minecraft.getMinecraft().displayWidth / 2 - BTNWIDTH - padding;
        y = padding;
        // we are moving top to bottom, so
        xDelta = 0;
        yDelta = Const.btnHeight + padding;
      }
      else if (position.equalsIgnoreCase(posBottom)) {
        // test bottom
        x = padding;
        y = Minecraft.getMinecraft().displayHeight / 2 - padding - Const.btnHeight;
        xDelta = BTNWIDTH + padding;
        yDelta = 0;
      }
      else if (position.equalsIgnoreCase(posAlign) || true) {
        //				x = gui.height;
        //				y = gui.width;
        int guiLeft = ReflectionHelper.getPrivateValue(GuiContainer.class, guiInv, "i", "field_147003_i", "guiLeft");
        int guiTop = ReflectionHelper.getPrivateValue(GuiContainer.class, guiInv, "r", "field_147009_r", "guiTop");
        x = guiLeft;
        y = guiTop;
        // we are moving top to bottom, so
        xDelta = 0;
        yDelta = Const.btnHeight + padding;
      }
      event.getButtonList().add(new ButtonTerrariaLootAll(button_id++, x, y));
      x += xDelta;
      y += yDelta;
      event.getButtonList().add(new ButtonTerrariaDepositAll(button_id++, x, y));
      x += xDelta;
      y += yDelta;
      event.getButtonList().add(new ButtonTerrariaQuickStack(button_id++, x, y));
      x += xDelta;
      y += yDelta;
      event.getButtonList().add(new ButtonTerrariaRestock(button_id++, x, y));
    }
  }
  @Override
  public void syncConfig(Configuration config) {
    String category = Const.ConfigCategory.inventory;
    config.setCategoryComment(category, "Terraria-inspired inventory helper buttons");
    List<String> valid = new ArrayList<String>();
    valid.add(posLeft);
    valid.add(posRight);
    valid.add(posBottom);
    restockLeaveOne = config.getBoolean("Restock Leave One", category, false, "By default (false) the Restock feature will empty your chests if possible.  If you change this to true, then using Restock will leave one behind of each item stack");
    position = config.getString("Button Location", category, posBottom, "Location of the extra inventory buttons, valid entries are: " + String.join(",", valid));
    if (valid.contains(position) == false) {
      position = posRight;// default
    }
    category = Const.ConfigCategory.inventoryModpack;
    config.addCustomCategoryComment(category, "Here you can blacklist any "
        + "container, vanilla or modded. Mostly for creating modpacks, if some "
        + "containers shouldnt have these buttons showing up.");
    // the default
    String blacklistDefault = "net.minecraft.client.gui.GuiMerchant,"
        + "net.minecraft.client.gui.inventory.GuiBrewingStand,"
        + "net.minecraft.client.gui.inventory.GuiBeacon,"
        + "net.minecraft.client.gui.inventory.GuiCrafting,"
        + "net.minecraft.client.gui.inventory.GuiFurnace,"
        + "net.minecraft.client.gui.inventory.GuiScreenHorseInventory,"
        + "net.minecraft.client.gui.inventory.GuiContainerCreative";
    String csv = config.getString("Blacklist Container CSV", category, blacklistDefault, "FOR MODPACK DEVS: These containers are blocked from getting the buttons.  By default, anything that extends 'GuiContainer' will get the buttons.  ");
    // blacklistGuis = new ArrayList<String>();
    blacklistGuis = (List<String>) Arrays.asList(csv.split(","));
    if (blacklistGuis == null) {
      blacklistGuis = new ArrayList<String>();// just being extra safe
    }
  }
}
