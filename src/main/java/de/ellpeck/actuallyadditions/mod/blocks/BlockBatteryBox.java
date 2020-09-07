package de.ellpeck.actuallyadditions.mod.blocks;

import de.ellpeck.actuallyadditions.mod.blocks.base.BlockContainerBase;
import de.ellpeck.actuallyadditions.mod.items.ItemBattery;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityBatteryBox;
import de.ellpeck.actuallyadditions.mod.util.StackUtil;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBatteryBox extends BlockContainerBase {

    public BlockBatteryBox(String name) {
        super(Material.ROCK, name);
        this.setHarvestLevel("pickaxe", 0);
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BlockSlabs.AABB_BOTTOM_HALF;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityBatteryBox();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBatteryBox) {
            TileEntityBatteryBox box = (TileEntityBatteryBox) tile;
            ItemStack stack = player.getHeldItem(hand);

            if (StackUtil.isValid(stack)) {
                if (stack.getItem() instanceof ItemBattery && !StackUtil.isValid(box.inv.getStackInSlot(0))) {
                    box.inv.setStackInSlot(0, stack.copy());
                    player.setHeldItem(hand, StackUtil.getEmpty());
                    return true;
                }
            } else {
                ItemStack inSlot = box.inv.getStackInSlot(0);
                if (StackUtil.isValid(inSlot)) {
                    player.setHeldItem(hand, inSlot.copy());
                    box.inv.setStackInSlot(0, StackUtil.getEmpty());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }
}
