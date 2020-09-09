package de.ellpeck.actuallyadditions.booklet.misc;

import java.util.List;

import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.booklet.IBookletChapter;
import de.ellpeck.actuallyadditions.api.booklet.IBookletPage;
import de.ellpeck.actuallyadditions.api.booklet.internal.GuiBookletBase;
import de.ellpeck.actuallyadditions.booklet.gui.GuiEntry;
import de.ellpeck.actuallyadditions.booklet.gui.GuiMainPage;
import de.ellpeck.actuallyadditions.booklet.gui.GuiPage;
import de.ellpeck.actuallyadditions.common.util.ItemUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class BookletUtils {

    public static IBookletPage findFirstPageForStack(ItemStack stack) {
        for (IBookletPage page : ActuallyAdditionsAPI.BOOKLET_PAGES_WITH_ITEM_OR_FLUID_DATA) {
            List<ItemStack> stacks = NonNullList.create();
            page.getItemStacksForPage(stacks);
            if (stacks != null && !stacks.isEmpty()) {
                for (ItemStack pageStack : stacks) {
                    if (ItemUtil.areItemsEqual(pageStack, stack, true)) { return page; }
                }
            }
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static GuiPage createBookletGuiFromPage(GuiScreen previousScreen, IBookletPage page) {
        GuiMainPage mainPage = new GuiMainPage(previousScreen);

        IBookletChapter chapter = page.getChapter();
        GuiEntry entry = new GuiEntry(previousScreen, mainPage, chapter.getEntry(), chapter, "", false);

        return createPageGui(previousScreen, entry, page);
    }

    @SideOnly(Side.CLIENT)
    public static GuiPage createPageGui(GuiScreen previousScreen, GuiBookletBase parentPage, IBookletPage page) {
        IBookletChapter chapter = page.getChapter();

        IBookletPage[] allPages = chapter.getAllPages();
        int pageIndex = chapter.getPageIndex(page);
        IBookletPage page1;
        IBookletPage page2;

        if (page.shouldBeOnLeftSide()) {
            page1 = page;
            page2 = pageIndex >= allPages.length - 1 ? null : allPages[pageIndex + 1];
        } else {
            page1 = pageIndex <= 0 ? null : allPages[pageIndex - 1];
            page2 = page;
        }

        return new GuiPage(previousScreen, parentPage, page1, page2);
    }

    public static IBookletPage getBookletPageById(String id) {
        if (id != null) {
            for (IBookletChapter chapter : ActuallyAdditionsAPI.ALL_CHAPTERS) {
                for (IBookletPage page : chapter.getAllPages()) {
                    if (id.equals(page.getIdentifier())) { return page; }
                }
            }
        }
        return null;
    }
}
