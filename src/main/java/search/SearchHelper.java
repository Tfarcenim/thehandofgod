package search;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Locale;

public class SearchHelper {

    public static List<ItemStack> search(String text) {
        return Minecraft.getMinecraft().getSearchTree(SearchTreeManager.ITEMS).search(text.toLowerCase(Locale.ROOT));
    }
}
