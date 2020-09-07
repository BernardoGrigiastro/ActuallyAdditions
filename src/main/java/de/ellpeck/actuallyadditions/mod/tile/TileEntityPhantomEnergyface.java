package de.ellpeck.actuallyadditions.mod.tile;

import de.ellpeck.actuallyadditions.mod.blocks.BlockPhantom;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileEntityPhantomEnergyface extends TileEntityPhantomface implements ISharingEnergyProvider {

    public TileEntityPhantomEnergyface() {
        super("energyface");
        this.type = BlockPhantom.Type.ENERGYFACE;
    }

    @Override
    public boolean isBoundThingInRange() {
        if (super.isBoundThingInRange()) {
            TileEntity tile = this.world.getTileEntity(this.boundPosition);
            if (tile != null && !(tile instanceof TileEntityLaserRelayEnergy)) {
                for (EnumFacing facing : EnumFacing.values()) {
                    if (tile.hasCapability(CapabilityEnergy.ENERGY, facing)) { return true; }
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isCapabilitySupported(Capability<?> capability) {
        return capability == CapabilityEnergy.ENERGY;
    }

    @Override
    public int getEnergyToSplitShare() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean doesShareEnergy() {
        return true;
    }

    @Override
    public EnumFacing[] getEnergyShareSides() {
        return EnumFacing.values();
    }

    @Override
    public boolean canShareTo(TileEntity tile) {
        return true;
    }
}
