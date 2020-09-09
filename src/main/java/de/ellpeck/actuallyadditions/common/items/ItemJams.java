package de.ellpeck.actuallyadditions.common.items;

import de.ellpeck.actuallyadditions.common.ActuallyAdditions;
import de.ellpeck.actuallyadditions.common.items.base.ItemFoodBase;
import de.ellpeck.actuallyadditions.common.items.metalists.TheJams;
import de.ellpeck.actuallyadditions.common.util.IColorProvidingItem;
import de.ellpeck.actuallyadditions.common.util.StringUtil;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemJams extends ItemFoodBase implements IColorProvidingItem {

    public static final TheJams[] ALL_JAMS = TheJams.values();

    public ItemJams(String name) {
        super(0, 0.0F, false, name);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setAlwaysEdible();
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return stack.getItemDamage() >= ALL_JAMS.length ? StringUtil.BUGGED_ITEM_NAME : this.getTranslationKey() + "_" + ALL_JAMS[stack.getItemDamage()].name;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return stack.getItemDamage() >= ALL_JAMS.length ? EnumRarity.COMMON : ALL_JAMS[stack.getItemDamage()].rarity;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (this.isInCreativeTab(tab)) {
            for (int j = 0; j < ALL_JAMS.length; j++) {
                list.add(new ItemStack(this, 1, j));
            }
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase player) {
        ItemStack stackToReturn = super.onItemUseFinish(stack, world, player);

        if (player instanceof EntityPlayer && !world.isRemote && stack.getItemDamage() < ALL_JAMS.length) {
            PotionEffect firstEffectToGet = new PotionEffect(Potion.getPotionById(ALL_JAMS[stack.getItemDamage()].firstEffectToGet), 200);
            player.addPotionEffect(firstEffectToGet);

            PotionEffect secondEffectToGet = new PotionEffect(Potion.getPotionById(ALL_JAMS[stack.getItemDamage()].secondEffectToGet), 600);
            player.addPotionEffect(secondEffectToGet);

            ItemStack returnItem = new ItemStack(Items.GLASS_BOTTLE);
            if (!((EntityPlayer) player).inventory.addItemStackToInventory(returnItem.copy())) {
                EntityItem entityItem = new EntityItem(player.world, player.posX, player.posY, player.posZ, returnItem.copy());
                entityItem.setPickupDelay(0);
                player.world.spawnEntity(entityItem);
            }
        }
        return stackToReturn;
    }

    @Override
    public int getHealAmount(ItemStack stack) {
        return stack.getItemDamage() >= ALL_JAMS.length ? 0 : ALL_JAMS[stack.getItemDamage()].healAmount;
    }

    @Override
    public float getSaturationModifier(ItemStack stack) {
        return stack.getItemDamage() >= ALL_JAMS.length ? 0 : ALL_JAMS[stack.getItemDamage()].saturation;
    }

    @Override
    protected void registerRendering() {
        for (int i = 0; i < ALL_JAMS.length; i++) {
            ActuallyAdditions.PROXY.addRenderRegister(new ItemStack(this, 1, i), this.getRegistryName(), "inventory");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemColor getItemColor() {
        return (stack, pass) -> pass > 0 ? stack.getItemDamage() >= ALL_JAMS.length ? 0xFFFFFF : ALL_JAMS[stack.getItemDamage()].color : 0xFFFFFF;
    }
}