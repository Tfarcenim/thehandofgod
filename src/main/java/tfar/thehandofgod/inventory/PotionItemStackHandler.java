package tfar.thehandofgod.inventory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class PotionItemStackHandler extends ItemStackHandler {

    public PotionItemStackHandler(int slots) {
        super(slots);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() == Items.POTIONITEM && super.isItemValid(slot, stack);
    }
}
