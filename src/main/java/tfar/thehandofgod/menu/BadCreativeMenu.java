package tfar.thehandofgod.menu;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import tfar.thehandofgod.inventory.LockedSlot;

import java.util.List;

public class BadCreativeMenu extends Container {

    public static final int SLOTS = 54;

    public final InventoryPlayer playerInventory;

    protected final ItemStackHandler handler = new ItemStackHandler(SLOTS);

    public BadCreativeMenu(InventoryPlayer playerInventory) {
        this.playerInventory = playerInventory;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new LockedSlot(handler, j + i * 9, j * 18 + 8, i * 18 + 18));
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
    @Override
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
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
            InventoryPlayer inventoryplayer = player.inventory;
            if (slotId >= 0) {
                Slot slot = this.inventorySlots.get(slotId);
                //check for our custom slot
                if (slot instanceof LockedSlot) {
                    ItemStack mouseStack = inventoryplayer.getItemStack();
                    if (mouseStack.isItemEqual(slot.getStack())) {
                        if (mouseStack.getCount() < mouseStack.getMaxStackSize())
                        mouseStack.grow(1);
                    } else if (mouseStack.isEmpty()) {
                        inventoryplayer.setItemStack(slot.getStack().copy());
                    } else {
                        inventoryplayer.setItemStack(ItemStack.EMPTY);
                    }
                }
            }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    public void updateDisplay(List<ItemStack> stacks) {
        int display = Math.min(BadCreativeMenu.SLOTS,stacks.size());
        for (int i = 0; i < BadCreativeMenu.SLOTS;i++) {
            handler.setStackInSlot(i,i < display ? stacks.get(i) : ItemStack.EMPTY);
        }
    }
}
