package de.ellpeck.actuallyadditions.common.crafting;

import de.ellpeck.actuallyadditions.common.items.InitItems;
import de.ellpeck.actuallyadditions.common.items.ItemKnife;
import de.ellpeck.actuallyadditions.common.items.metalists.TheMiscItems;
import de.ellpeck.actuallyadditions.common.util.StackUtil;
import de.ellpeck.actuallyadditions.common.util.crafting.RecipeHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeBioMash extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    public RecipeBioMash(ResourceLocation location) {
        RecipeHelper.addRecipe(location.getPath(), this);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        boolean foundFood = false;
        boolean hasKnife = false;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (StackUtil.isValid(stack)) {
                if (stack.getItem() instanceof ItemKnife) {
                    if (hasKnife) {
                        return false;
                    } else {
                        hasKnife = true;
                    }
                } else if (stack.getItem() instanceof ItemFood) {
                    foundFood = true;
                } else {
                    return false;
                }
            }
        }

        return foundFood && hasKnife;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        int amount = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (StackUtil.isValid(stack)) {
                if (stack.getItem() instanceof ItemFood) {
                    ItemFood food = (ItemFood) stack.getItem();
                    float heal = food.getHealAmount(stack);
                    float sat = food.getSaturationModifier(stack);

                    amount += MathHelper.ceil(heal * sat);
                }
            }
        }

        if (amount > 0 && amount <= 64) {
            return new ItemStack(InitItems.itemMisc, amount, TheMiscItems.MASHED_FOOD.ordinal());
        } else {
            return StackUtil.getEmpty();
        }
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height > 5;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return StackUtil.getEmpty();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }
}
