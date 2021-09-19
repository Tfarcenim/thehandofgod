package tfar.thehandofgod.inventory;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import tfar.thehandofgod.menu.ConfigurePotionContainer;

public class PotionSlot extends SlotItemHandler {

    private final ConfigurePotionContainer configurePotionContainer;

    public PotionSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ConfigurePotionContainer configurePotionContainer) {
        super(itemHandler, index, xPosition, yPosition);
        this.configurePotionContainer = configurePotionContainer;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        configurePotionContainer.onCraftMatrixChanged(null);
    }
}
