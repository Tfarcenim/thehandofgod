package tfar.thehandofgod.inventory;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import tfar.thehandofgod.menu.ConfigureEnchantmentContainer;

public class EnchBookSlot extends SlotItemHandler {

    private final ConfigureEnchantmentContainer configureEnchantmentContainer;

    public EnchBookSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ConfigureEnchantmentContainer configureEnchantmentContainer) {
        super(itemHandler, index, xPosition, yPosition);
        this.configureEnchantmentContainer = configureEnchantmentContainer;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        configureEnchantmentContainer.onCraftMatrixChanged(null);
    }
}
