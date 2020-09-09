package de.ellpeck.actuallyadditions.common.tile;

import java.util.ArrayList;
import java.util.List;

import de.ellpeck.actuallyadditions.common.network.gui.IButtonReactor;
import de.ellpeck.actuallyadditions.common.util.ItemStackHandlerAA.IAcceptor;
import de.ellpeck.actuallyadditions.common.util.StackUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;

public class TileEntityRangedCollector extends TileEntityInventoryBase implements IButtonReactor {

    public static final int RANGE = 6;
    public FilterSettings filter = new FilterSettings(12, true, true, false, false, 0, -1000);

    public TileEntityRangedCollector() {
        super(6, "rangedCollector");
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, NBTType type) {
        super.writeSyncableNBT(compound, type);

        this.filter.writeToNBT(compound, "Filter");
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, NBTType type) {
        super.readSyncableNBT(compound, type);

        this.filter.readFromNBT(compound, "Filter");
    }

    @Override
    public boolean isRedstoneToggle() {
        return true;
    }

    @Override
    public void activateOnPulse() {
        List<EntityItem> items = this.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.pos.getX() - RANGE, this.pos.getY() - RANGE, this.pos.getZ() - RANGE, this.pos.getX() + RANGE, this.pos.getY() + RANGE, this.pos.getZ() + RANGE));
        if (!items.isEmpty()) {
            for (EntityItem item : items) {
                if (!item.isDead && !item.cannotPickup() && StackUtil.isValid(item.getItem())) {
                    ItemStack toAdd = item.getItem().copy();
                    if (this.filter.check(toAdd)) {
                        ArrayList<ItemStack> checkList = new ArrayList<>();
                        checkList.add(toAdd);
                        if (StackUtil.canAddAll(this.inv, checkList, false)) {
                            StackUtil.addAll(this.inv, checkList, false);
                            ((WorldServer) this.world).spawnParticle(EnumParticleTypes.CLOUD, false, item.posX, item.posY + 0.45F, item.posZ, 5, 0, 0, 0, 0.03D);
                            item.setDead();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!this.world.isRemote) {
            if (!this.isRedstonePowered && !this.isPulseMode) {
                this.activateOnPulse();
            }

            if (this.filter.needsUpdateSend() && this.sendUpdateWithInterval()) {
                this.filter.updateLasts();
            }
        }
    }

    @Override
    public IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation;
    }

    @Override
    public void onButtonPressed(int buttonID, EntityPlayer player) {
        this.filter.onButtonPressed(buttonID);
    }
}
