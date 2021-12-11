package tfar.thehandofgod.client.font;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tfar.thehandofgod.HandOfGodItem;

import javax.annotation.Nullable;

public class ItemRainbowName extends HandOfGodItem {

    public ItemRainbowName(Item item) {
        super();
    }

    @SideOnly(Side.CLIENT)
    @Nullable
    @Override
    public FontRenderer getFontRenderer(ItemStack stack) {
        return RainbowFontRenderer.get();
    }
}