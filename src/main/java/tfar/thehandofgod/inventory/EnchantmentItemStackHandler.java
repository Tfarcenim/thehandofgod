package tfar.thehandofgod.inventory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class EnchantmentItemStackHandler extends ItemStackHandler {

    public EnchantmentItemStackHandler(int slots) {
        super(slots);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() == Items.ENCHANTED_BOOK && super.isItemValid(slot, stack);
    }
}
