package de.ellpeck.actuallyadditions.common.jei.compost;

import java.util.Arrays;

import de.ellpeck.actuallyadditions.api.booklet.IBookletPage;
import de.ellpeck.actuallyadditions.common.blocks.InitBlocks;
import de.ellpeck.actuallyadditions.booklet.misc.BookletUtils;
import de.ellpeck.actuallyadditions.common.jei.RecipeWrapperWithButton;
import de.ellpeck.actuallyadditions.common.tile.TileEntityCompost;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class CompostRecipeWrapper extends RecipeWrapperWithButton {

    public final CompostRecipe recipe;

    public CompostRecipeWrapper(CompostRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(this.recipe.getInput().getMatchingStacks()));
        ingredients.setOutput(VanillaTypes.ITEM, this.recipe.getOutput());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        int bake_time = TileEntityCompost.COMPOST_TIME_TICKS / 20;
        minecraft.fontRenderer.drawString(bake_time + "s", 28, 3, 0xFFFFFF, true);
        //super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);  Not sure the button needs to be here.
    }

    @Override
    public int getButtonX() {
        return 32;
    }

    @Override
    public int getButtonY() {
        return 35;
    }

    @Override
    public IBookletPage getPage() {
        return BookletUtils.findFirstPageForStack(new ItemStack(InitBlocks.blockCompost));
    }
}
