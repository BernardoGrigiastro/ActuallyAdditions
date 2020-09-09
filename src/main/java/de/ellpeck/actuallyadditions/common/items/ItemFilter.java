package de.ellpeck.actuallyadditions.common.items;

import java.util.List;

import de.ellpeck.actuallyadditions.common.ActuallyAdditions;
import de.ellpeck.actuallyadditions.common.inventory.ContainerFilter;
import de.ellpeck.actuallyadditions.common.inventory.GuiHandler;
import de.ellpeck.actuallyadditions.common.items.base.ItemBase;
import de.ellpeck.actuallyadditions.common.util.ItemStackHandlerAA;
import de.ellpeck.actuallyadditions.common.util.StackUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemFilter extends ItemBase {

    public ItemFilter(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && hand == EnumHand.MAIN_HAND) {
            player.openGui(ActuallyAdditions.INSTANCE, GuiHandler.GuiTypes.FILTER.ordinal(), world, (int) player.posX, (int) player.posY, (int) player.posZ);
        }
        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);

        ItemStackHandlerAA inv = new ItemStackHandlerAA(ContainerFilter.SLOT_AMOUNT);
        ItemDrill.loadSlotsFromNBT(inv, stack);
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack slot = inv.getStackInSlot(i);
            if (StackUtil.isValid(slot)) {
                tooltip.add(slot.getItem().getItemStackDisplayName(slot));
            }
        }
    }
}
