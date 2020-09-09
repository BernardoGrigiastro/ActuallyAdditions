package de.ellpeck.actuallyadditions.common.items;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import de.ellpeck.actuallyadditions.common.ActuallyAdditions;
import de.ellpeck.actuallyadditions.common.config.values.ConfigStringListValues;
import de.ellpeck.actuallyadditions.common.items.base.ItemToolAA;
import de.ellpeck.actuallyadditions.common.util.IColorProvidingItem;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAllToolAA extends ItemToolAA implements IColorProvidingItem {

    public final int color;

    public ItemAllToolAA(ToolMaterial toolMat, String repairItem, String unlocalizedName, IRarity rarity, int color) {
        super(4.0F, -2F, toolMat, repairItem, unlocalizedName, rarity, new HashSet<>());
        this.color = color;

        this.setMaxDamage(toolMat.getMaxUses() * 4);
        this.setHarvestLevels(toolMat.getHarvestLevel());
    }

    public ItemAllToolAA(ToolMaterial toolMat, ItemStack repairItem, String unlocalizedName, IRarity rarity, int color) {
        super(4.0F, -2F, toolMat, repairItem, unlocalizedName, rarity, new HashSet<>());
        this.color = color;

        this.setMaxDamage(toolMat.getMaxUses() * 4);
        this.setHarvestLevels(toolMat.getHarvestLevel());
    }

    private void setHarvestLevels(int amount) {
        for (String s : this.getToolClasses(null)) {
            this.setHarvestLevel(s, amount);
        }
    }

    @Override
    protected void registerRendering() {
        ActuallyAdditions.PROXY.addRenderRegister(new ItemStack(this), new ResourceLocation(ActuallyAdditions.MODID, "item_paxel"), "inventory");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!playerIn.isSneaking()) return Items.IRON_HOE.onItemUse(playerIn, worldIn, pos, hand, side, hitX, hitY, hitZ);
        return Items.IRON_SHOVEL.onItemUse(playerIn, worldIn, pos, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        return this.hasExtraWhitelist(state.getBlock()) || state.getMaterial().isToolNotRequired() || state.getBlock() == Blocks.SNOW_LAYER || state.getBlock() == Blocks.SNOW || (state.getBlock() == Blocks.OBSIDIAN ? this.toolMaterial.getHarvestLevel() >= 3 : state.getBlock() != Blocks.DIAMOND_BLOCK && state.getBlock() != Blocks.DIAMOND_ORE ? state.getBlock() != Blocks.EMERALD_ORE && state.getBlock() != Blocks.EMERALD_BLOCK ? state.getBlock() != Blocks.GOLD_BLOCK && state.getBlock() != Blocks.GOLD_ORE ? state.getBlock() != Blocks.IRON_BLOCK && state.getBlock() != Blocks.IRON_ORE ? state.getBlock() != Blocks.LAPIS_BLOCK && state.getBlock() != Blocks.LAPIS_ORE ? state.getBlock() != Blocks.REDSTONE_ORE && state.getBlock() != Blocks.LIT_REDSTONE_ORE ? state.getMaterial() == Material.ROCK || state.getMaterial() == Material.IRON || state.getMaterial() == Material.ANVIL : this.toolMaterial.getHarvestLevel() >= 2 : this.toolMaterial.getHarvestLevel() >= 1 : this.toolMaterial.getHarvestLevel() >= 1 : this.toolMaterial.getHarvestLevel() >= 2 : this.toolMaterial.getHarvestLevel() >= 2 : this.toolMaterial.getHarvestLevel() >= 2);
    }

    private boolean hasExtraWhitelist(Block block) {
        String name = block.getRegistryName().toString();
        for (String list : ConfigStringListValues.PAXEL_EXTRA_MINING_WHITELIST.getValue()) {
            if (list.equals(name)) { return true; }
        }
        return false;
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return Sets.newHashSet("pickaxe", "axe", "shovel");
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        if (state.getBlock() == Blocks.WEB) {
            return 15.0F;
        } else {
            return this.hasExtraWhitelist(state.getBlock()) || state.getBlock().getHarvestTool(state) == null || state.getBlock().getHarvestTool(state).isEmpty() || this.getToolClasses(stack).contains(state.getBlock().getHarvestTool(state)) ? this.efficiency : 1.0F;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IItemColor getItemColor() {
        return (stack, pass) -> pass > 0 ? ItemAllToolAA.this.color : 0xFFFFFF;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment.type.canEnchantItem(Items.DIAMOND_SWORD);
    }
}