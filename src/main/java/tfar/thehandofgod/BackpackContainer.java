package tfar.thehandofgod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class BackpackContainer extends Container {
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
