package tfar.thehandofgod.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class BiggerItemStackHandler extends ItemStackHandler {

    public BiggerItemStackHandler(int size) {
        super(size);
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return getSlotLimit(slot);
    }

}
