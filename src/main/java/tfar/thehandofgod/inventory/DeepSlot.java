package tfar.thehandofgod.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class DeepSlot extends SlotItemHandler {

  public DeepSlot(ItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
    super(itemHandler, index, xPosition, yPosition);
  }

  @Override
  public int getItemStackLimit(@Nonnull ItemStack stack) {
    return getSlotStackLimit();
  }
}
