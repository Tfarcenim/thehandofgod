package tfar.thehandofgod.menu;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class TeleportContainer extends Container {
    public final InventoryPlayer playerInventory;

    private final ItemStack hand;

    public TeleportContainer(InventoryPlayer playerInventory, ItemStack hand) {
        this.playerInventory = playerInventory;
        this.hand = hand;
        addPlayerSlots(9,102);
    }

    protected void addPlayerSlots(int x, int y) {
        for (int l = 0; l < 3; ++l) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + l * 9 + 9, j * 18 + x, l * 18 + y));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerInventory, i, i * 18 + x, y + 58));
        }
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;//this.stackHandler.isUsableByPlayer(playerIn);
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
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        super.onCraftMatrixChanged(inventoryIn);
        saveCoordsToHand();
    }

    private void saveCoordsToHand() {
        if (!playerInventory.player.world.isRemote) {
            if (hand.hasTagCompound()) {
                hand.getTagCompound().removeTag("ench");
            }
           /* for (int i = 0; i < stackHandler.getSlots(); i++) {
                ItemStack stack = stackHandler.getStackInSlot(i);
                NBTTagList tagList = ItemEnchantedBook.getEnchantments(stack);

                for (int j = 0; j < tagList.tagCount(); ++j) {
                    NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(j);
                    int id = nbttagcompound.getShort("id");
                    Enchantment enchantment = Enchantment.getEnchantmentByID(id);

                    if (enchantment != null) {
                        hand.addEnchantment(enchantment, nbttagcompound.getShort("lvl"));
                    }
                }
            }*/
        }
    }
}
