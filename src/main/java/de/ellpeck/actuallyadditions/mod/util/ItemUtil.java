package de.ellpeck.actuallyadditions.mod.util;

import java.util.Arrays;
import java.util.List;

import de.ellpeck.actuallyadditions.mod.ActuallyAdditions;
import de.ellpeck.actuallyadditions.mod.RegistryHandler;
import de.ellpeck.actuallyadditions.mod.blocks.base.BlockItemBase;
import de.ellpeck.actuallyadditions.mod.creative.CreativeTab;
import de.ellpeck.actuallyadditions.mod.util.compat.IMCHandler;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class ItemUtil {

    public static Item getItemFromName(String name) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
    }

    @Deprecated // canitzp: should be removed
    public static void registerBlock(Block block, BlockItemBase itemBlock, String name, boolean addTab) {
        block.setTranslationKey(ActuallyAdditions.MODID + "." + name);

        block.setRegistryName(ActuallyAdditions.MODID, name);
        RegistryHandler.BLOCKS_TO_REGISTER.add(block);

        itemBlock.setRegistryName(block.getRegistryName());
        RegistryHandler.ITEMS_TO_REGISTER.add(itemBlock);

        block.setCreativeTab(addTab ? CreativeTab.INSTANCE : null);

        IMCHandler.doBlockIMC(block);

        if (block instanceof IColorProvidingBlock) {
            ActuallyAdditions.PROXY.addColoredBlock(block);
        }
    }

    @Deprecated // canitzp: should be removed
    public static void registerItem(Item item, String name, boolean addTab) {
        item.setTranslationKey(ActuallyAdditions.MODID + "." + name);

        item.setRegistryName(ActuallyAdditions.MODID, name);
        RegistryHandler.ITEMS_TO_REGISTER.add(item);

        item.setCreativeTab(addTab ? CreativeTab.INSTANCE : null);

        IMCHandler.doItemIMC(item);

        if (item instanceof IColorProvidingItem) {
            ActuallyAdditions.PROXY.addColoredItem(item);
        }
    }

    public static boolean contains(ItemStack[] array, ItemStack stack, boolean checkWildcard) {
        return getPlaceAt(array, stack, checkWildcard) != -1;
    }

    public static int getPlaceAt(ItemStack[] array, ItemStack stack, boolean checkWildcard) {
        return getPlaceAt(Arrays.asList(array), stack, checkWildcard);
    }

    public static int getPlaceAt(List<ItemStack> list, ItemStack stack, boolean checkWildcard) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (!StackUtil.isValid(stack) && !StackUtil.isValid(list.get(i)) || areItemsEqual(stack, list.get(i), checkWildcard)) { return i; }
            }
        }
        return -1;
    }

    public static boolean areItemsEqual(ItemStack stack1, ItemStack stack2, boolean checkWildcard) {
        return StackUtil.isValid(stack1) && StackUtil.isValid(stack2) && (stack1.isItemEqual(stack2) || checkWildcard && stack1.getItem() == stack2.getItem() && (stack1.getItemDamage() == Util.WILDCARD || stack2.getItemDamage() == Util.WILDCARD));
    }

    /**
     * Returns true if list contains stack or if both contain null
     */
    public static boolean contains(List<ItemStack> list, ItemStack stack, boolean checkWildcard) {
        return !(list == null || list.isEmpty()) && getPlaceAt(list, stack, checkWildcard) != -1;
    }

    public static void addEnchantment(ItemStack stack, Enchantment e, int level) {
        if (!hasEnchantment(stack, e)) {
            stack.addEnchantment(e, level);
        }
    }

    public static boolean hasEnchantment(ItemStack stack, Enchantment e) {
        NBTTagList ench = stack.getEnchantmentTagList();
        if (ench != null) {
            for (int i = 0; i < ench.tagCount(); i++) {
                short id = ench.getCompoundTagAt(i).getShort("id");
                if (id == Enchantment.getEnchantmentID(e)) { return true; }
            }
        }
        return false;
    }

    public static void removeEnchantment(ItemStack stack, Enchantment e) {
        NBTTagList ench = stack.getEnchantmentTagList();
        if (ench != null) {
            for (int i = 0; i < ench.tagCount(); i++) {
                short id = ench.getCompoundTagAt(i).getShort("id");
                if (id == Enchantment.getEnchantmentID(e)) {
                    ench.removeTag(i);
                }
            }
            if (ench.isEmpty() && stack.hasTagCompound()) {
                stack.getTagCompound().removeTag("ench");
            }
        }
    }

    public static boolean canBeStacked(ItemStack stack1, ItemStack stack2) {
        return ItemStack.areItemsEqual(stack1, stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    public static boolean isEnabled(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean("IsEnabled");
    }

    public static void changeEnabled(EntityPlayer player, EnumHand hand) {
        changeEnabled(player.getHeldItem(hand));
    }

    public static void changeEnabled(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        boolean isEnabled = isEnabled(stack);
        stack.getTagCompound().setBoolean("IsEnabled", !isEnabled);
    }
}
