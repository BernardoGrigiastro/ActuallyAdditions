package de.ellpeck.actuallyadditions.booklet.page;

import de.ellpeck.actuallyadditions.api.booklet.internal.GuiBookletBase;
import de.ellpeck.actuallyadditions.common.util.AssetUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PagePicture extends BookletPage {

    private final ResourceLocation resLoc;
    private final int yTextOffset;

    public PagePicture(int localizationKey, ResourceLocation resLoc, int yTextOffset, int priority) {
        super(localizationKey, priority);
        this.resLoc = resLoc;
        this.yTextOffset = yTextOffset;
    }

    public PagePicture(int localizationKey, ResourceLocation resLoc, int yTextOffset) {
        super(localizationKey);
        this.yTextOffset = yTextOffset;
        this.resLoc = resLoc;
    }

    public PagePicture(int localizationKey, String pictureLocation, int yTextOffset) {
        this(localizationKey, AssetUtil.getBookletGuiLocation(pictureLocation), yTextOffset);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawScreenPre(GuiBookletBase gui, int startX, int startY, int mouseX, int mouseY, float partialTicks) {
        super.drawScreenPre(gui, startX, startY, mouseX, mouseY, partialTicks);

        gui.mc.getTextureManager().bindTexture(this.resLoc);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GuiUtils.drawTexturedModalRect(startX - 6, startY - 7, 0, 0, 256, 256, 0);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();

        PageTextOnly.renderTextToPage(gui, this, startX + 6, startY - 7 + this.yTextOffset);
    }
}
