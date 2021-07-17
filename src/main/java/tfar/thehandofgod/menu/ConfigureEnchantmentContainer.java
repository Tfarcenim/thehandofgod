package tfar.thehandofgod.menu;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.ItemStackHandler;
import tfar.thehandofgod.inventory.EnchBookSlot;

import java.util.Map;

public class ConfigureEnchantmentContainer extends Container {
    public final InventoryPlayer playerInventory;

    public final ItemStackHandler stackHandler;
    private final ItemStack hand;

    public ConfigureEnchantmentContainer(InventoryPlayer playerInventory, ItemStackHandler enchInventory, ItemStack hand) {
        this.playerInventory = playerInventory;
        this.stackHandler = enchInventory;
        this.hand = hand;
        int i = -18;

        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 3; ++k) {
                this.addSlotToContainer(new EnchBookSlot(enchInventory, k + j * 3, 18 * 3 + 8 + k * 18, 17 + j * 18, this));
            }
        }

        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlotToContainer(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 102 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 160 + i));
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
        saveBooksToHand();
    }

    private void saveBooksToHand() {
        if (!playerInventory.player.world.isRemote) {
            if (hand.hasTagCompound()) {
                hand.getTagCompound().removeTag("ench");
            }
            for (int i = 0; i < stackHandler.getSlots(); i++) {
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
            }
        }
    }
}
