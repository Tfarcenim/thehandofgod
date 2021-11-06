package tfar.thehandofgod.menu;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BadCreativeMenu extends Container {

    public static final int SLOTS = 54;

    public final InventoryPlayer playerInventory;

    public final ItemStackHandler handler = new ItemStackHandler(SLOTS);

    /**
     * the list of items in this container
     */
    public NonNullList<ItemStack> itemList = NonNullList.create();

    public BadCreativeMenu(InventoryPlayer playerInventory) {
        this.playerInventory = playerInventory;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new SlotItemHandler(handler, j + i * 9, j * 18 + 8, i * 18 + 18));
            }
        }

        addPlayerSlots(8, 140);
    }

    protected void addPlayerSlots(int x, int y) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, j * 18 + x, i * 18 + y));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerInventory, i, i * 18 + x, y + 58));
        }
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 9) {
                if (!this.mergeItemStack(itemstack1, 9, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }


    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    public boolean canScroll() {
        return this.itemList.size() > SLOTS;
    }

    /**
     * Updates the gui slots ItemStack's based on scroll position.
     */
    public void scrollTo(float pos) {
        int i = (this.itemList.size() + 9 - 1) / 9 - 6;
        int j = (int) (pos * i + 0.5D);

        if (j < 0) {
            j = 0;
        }

        for (int k = 0; k < 5; ++k) {
            for (int l = 0; l < 9; ++l) {
                int i1 = l + (k + j) * 9;

                if (i1 >= 0 && i1 < this.itemList.size()) {
                    handler.setStackInSlot(l + k * 9, this.itemList.get(i1));
                } else {
                    handler.setStackInSlot(l + k * 9, ItemStack.EMPTY);
                }
            }
        }
    }

}
