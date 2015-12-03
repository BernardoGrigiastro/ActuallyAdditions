/*
 * This file ("Lens.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://github.com/Ellpeck/ActuallyAdditions/blob/master/README.md
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015 Ellpeck
 */

package ellpeck.actuallyadditions.items.lens;

import ellpeck.actuallyadditions.tile.TileEntityAtomicReconstructor;
import ellpeck.actuallyadditions.util.WorldPos;
import net.minecraft.item.Item;

public abstract class Lens{

    protected Item lensItem;

    public abstract boolean invoke(WorldPos hitBlock, TileEntityAtomicReconstructor tile);

    public abstract float[] getColor();

    public abstract int getDistance();

    public Lens register(){
        Lenses.allLenses.add(this);
        return this;
    }

    public void setLensItem(Item item){
        this.lensItem = item;
    }
}
