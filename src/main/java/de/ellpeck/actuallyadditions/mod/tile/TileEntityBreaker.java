package de.ellpeck.actuallyadditions.mod.tile;

import de.ellpeck.actuallyadditions.mod.util.ItemStackHandlerAA.IAcceptor;
import de.ellpeck.actuallyadditions.mod.util.StackUtil;
import de.ellpeck.actuallyadditions.mod.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.IFluidBlock;

public class TileEntityBreaker extends TileEntityInventoryBase {

    public boolean isPlacer;
    private int currentTime;

    public TileEntityBreaker(int slots, String name) {
        super(slots, name);
    }

    public TileEntityBreaker() {
        super(9, "breaker");
        this.isPlacer = false;
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, NBTType type) {
        super.writeSyncableNBT(compound, type);
        if (type != NBTType.SAVE_BLOCK) {
            compound.setInteger("CurrentTime", this.currentTime);
        }
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, NBTType type) {
        super.readSyncableNBT(compound, type);
        if (type != NBTType.SAVE_BLOCK) {
            this.currentTime = compound.getInteger("CurrentTime");
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!this.world.isRemote) {
            if (!this.isRedstonePowered && !this.isPulseMode) {
                if (this.currentTime > 0) {
                    this.currentTime--;
                    if (this.currentTime <= 0) {
                        this.doWork();
                    }
                } else {
                    this.currentTime = 15;
                }
            }
        }
    }

    @Override
    public IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation;
    }

    private void doWork() {
        EnumFacing side = WorldUtil.getDirectionByPistonRotation(this.world.getBlockState(this.pos));
        BlockPos breakCoords = this.pos.offset(side);
        IBlockState stateToBreak = this.world.getBlockState(breakCoords);
        Block blockToBreak = stateToBreak.getBlock();

        if (!this.isPlacer && blockToBreak != Blocks.AIR && !(blockToBreak instanceof BlockLiquid) && !(blockToBreak instanceof IFluidBlock) && stateToBreak.getBlockHardness(this.world, breakCoords) >= 0.0F) {
            NonNullList<ItemStack> drops = NonNullList.create();
            blockToBreak.getDrops(drops, this.world, breakCoords, stateToBreak, 0);
            float chance = WorldUtil.fireFakeHarvestEventsForDropChance(this, drops, this.world, breakCoords);

            if (chance > 0 && this.world.rand.nextFloat() <= chance) {
                if (StackUtil.canAddAll(this.inv, drops, false)) {
                    this.world.destroyBlock(breakCoords, false);
                    StackUtil.addAll(this.inv, drops, false);
                    this.markDirty();
                }
            }
        } else if (this.isPlacer) {
            int slot = StackUtil.findFirstFilled(this.inv);
            if (slot == -1) return;
            this.inv.setStackInSlot(slot, WorldUtil.useItemAtSide(side, this.world, this.pos, this.inv.getStackInSlot(slot)));
        }
    }

    @Override
    public boolean isRedstoneToggle() {
        return true;
    }

    @Override
    public void activateOnPulse() {
        this.doWork();
    }

}
