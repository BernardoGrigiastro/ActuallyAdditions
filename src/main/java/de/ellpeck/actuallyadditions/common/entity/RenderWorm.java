package de.ellpeck.actuallyadditions.entity;

import de.ellpeck.actuallyadditions.items.InitItems;
import de.ellpeck.actuallyadditions.util.AssetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWorm extends Render<EntityWorm> {

    private static ItemStack stack = ItemStack.EMPTY;

    public static void fixItemStack() {
        stack = new ItemStack(InitItems.itemWorm);
    }

    protected RenderWorm(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityWorm entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    @Override
    public void doRender(EntityWorm entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        GlStateManager.translate(x, y + 0.7F, z);
        double boop = Minecraft.getSystemTime() / 70D;
        GlStateManager.rotate(-(float) (boop % 360), 0, 1, 0);
        GlStateManager.translate(0, 0, 0.4);

        stack.setStackDisplayName(entity.getName());
        AssetUtil.renderItemInWorld(stack);

        GlStateManager.popMatrix();
    }
}
