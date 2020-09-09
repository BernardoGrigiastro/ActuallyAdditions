package de.ellpeck.actuallyadditions.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotImmovable extends Slot {

    public SlotImmovable(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public void putStack(ItemStack stack) {

    }

    @Override
    public ItemStack decrStackSize(int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }
}
