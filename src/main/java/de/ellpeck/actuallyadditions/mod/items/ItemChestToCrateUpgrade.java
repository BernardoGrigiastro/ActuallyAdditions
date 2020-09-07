package de.ellpeck.actuallyadditions.mod.items;

import de.ellpeck.actuallyadditions.mod.items.base.ItemBase;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityInventoryBase;
import de.ellpeck.actuallyadditions.mod.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public class ItemChestToCrateUpgrade extends ItemBase {

    private final Class<? extends TileEntity> start;
    private final IBlockState end;

    public ItemChestToCrateUpgrade(String name, Class<? extends TileEntity> start, IBlockState end) {
        super(name);
        this.start = start;
        this.end = end;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack heldStack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            TileEntity tileHit = world.getTileEntity(pos);
            if (tileHit != null && this.start.isInstance(tileHit)) {
                if (!world.isRemote) {

                    //Copy Slots
                    IItemHandlerModifiable chest = null;
                    if (tileHit instanceof IInventory) {
                        chest = new InvWrapper((IInventory) tileHit);
                    } else if (tileHit instanceof TileEntityInventoryBase) {
                        chest = ((TileEntityInventoryBase) tileHit).inv;
                    }

                    if (chest != null) {
                        ItemStack[] stacks = new ItemStack[chest.getSlots()];
                        for (int i = 0; i < stacks.length; i++) {
                            ItemStack aStack = chest.getStackInSlot(i);
                            stacks[i] = aStack.copy();
                        }

                        //Set New Block
                        world.playEvent(2001, pos, Block.getStateId(world.getBlockState(pos)));

                        world.removeTileEntity(pos);
                        world.setBlockState(pos, this.end, 2);
                        if (!player.capabilities.isCreativeMode) heldStack.shrink(1);

                        //Copy Items into new Chest
                        TileEntity newTileHit = world.getTileEntity(pos);
                        if (newTileHit instanceof TileEntityInventoryBase) {
                            IItemHandlerModifiable newChest = ((TileEntityInventoryBase) newTileHit).inv;

                            for (int i = 0; i < stacks.length; i++) {
                                if (StackUtil.isValid(stacks[i])) {
                                    if (newChest.getSlots() > i) {
                                        newChest.setStackInSlot(i, stacks[i].copy());
                                    }
                                }
                            }
                        }
                    }
                }
                return EnumActionResult.SUCCESS;
            }
        }

        return EnumActionResult.PASS;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }
}
