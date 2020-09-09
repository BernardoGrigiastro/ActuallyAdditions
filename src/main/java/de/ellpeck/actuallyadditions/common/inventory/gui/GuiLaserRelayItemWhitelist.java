package de.ellpeck.actuallyadditions.common.inventory.gui;

import java.util.ArrayList;
import java.util.List;

import de.ellpeck.actuallyadditions.common.ActuallyAdditions;
import de.ellpeck.actuallyadditions.common.inventory.ContainerLaserRelayItemWhitelist;
import de.ellpeck.actuallyadditions.common.inventory.gui.GuiInputter.SmallerButton;
import de.ellpeck.actuallyadditions.common.network.PacketHandlerHelper;
import de.ellpeck.actuallyadditions.common.tile.TileEntityBase;
import de.ellpeck.actuallyadditions.common.tile.TileEntityLaserRelayItemWhitelist;
import de.ellpeck.actuallyadditions.common.util.AssetUtil;
import de.ellpeck.actuallyadditions.common.util.StringUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLaserRelayItemWhitelist extends GuiWtfMojang {

    private static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_laser_relay_item_whitelist");
    private final TileEntityLaserRelayItemWhitelist tile;

    private FilterSettingsGui leftFilter;
    private FilterSettingsGui rightFilter;

    private GuiButton buttonSmartWhitelistLeft;
    private GuiButton buttonSmartWhitelistRight;

    public GuiLaserRelayItemWhitelist(InventoryPlayer inventory, TileEntityBase tile) {
        super(new ContainerLaserRelayItemWhitelist(inventory, tile));
        this.tile = (TileEntityLaserRelayItemWhitelist) tile;
        this.xSize = 176;
        this.ySize = 93 + 86;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        this.leftFilter.update();
        this.rightFilter.update();
    }

    @Override
    public void initGui() {
        super.initGui();

        this.leftFilter = new FilterSettingsGui(this.tile.leftFilter, this.guiLeft + 3, this.guiTop + 6, this.buttonList);
        this.rightFilter = new FilterSettingsGui(this.tile.rightFilter, this.guiLeft + 157, this.guiTop + 6, this.buttonList);

        this.buttonSmartWhitelistLeft = new SmallerButton(2, this.guiLeft + 3, this.guiTop + 79, "S");
        this.buttonSmartWhitelistRight = new SmallerButton(3, this.guiLeft + 157, this.guiTop + 79, "S");
        this.buttonList.add(this.buttonSmartWhitelistLeft);
        this.buttonList.add(this.buttonSmartWhitelistRight);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        PacketHandlerHelper.sendButtonPacket(this.tile, button.id);
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawScreen(x, y, f);

        if (this.buttonSmartWhitelistLeft.isMouseOver() || this.buttonSmartWhitelistRight.isMouseOver()) {
            List<String> list = new ArrayList<>();
            list.add(TextFormatting.BOLD + StringUtil.localize("info." + ActuallyAdditions.MODID + ".gui.smart"));
            list.addAll(this.fontRenderer.listFormattedStringToWidth(StringUtil.localize("info." + ActuallyAdditions.MODID + ".gui.smartInfo"), 200));
            this.drawHoveringText(list, x, y);
        }

        this.leftFilter.drawHover(x, y);
        this.rightFilter.drawHover(x, y);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        AssetUtil.displayNameString(this.fontRenderer, this.xSize, -10, this.tile);

        String s1 = StringUtil.localize("info." + ActuallyAdditions.MODID + ".gui.inbound");
        String s2 = StringUtil.localize("info." + ActuallyAdditions.MODID + ".gui.outbound");
        this.fontRenderer.drawString(s1, 46 - this.fontRenderer.getStringWidth(s1) / 2, 80, StringUtil.DECIMAL_COLOR_GRAY_TEXT);
        this.fontRenderer.drawString(s2, 131 - this.fontRenderer.getStringWidth(s2) / 2, 80, StringUtil.DECIMAL_COLOR_GRAY_TEXT);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(AssetUtil.GUI_INVENTORY_LOCATION);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop + 93, 0, 0, 176, 86);

        this.mc.getTextureManager().bindTexture(RES_LOC);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 176, 93);

    }
}