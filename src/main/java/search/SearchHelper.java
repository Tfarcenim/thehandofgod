package search;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.item.ItemStack;
import tfar.thehandofgod.client.Client;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchHelper {


    public static final Map<Character, SearchTreeManager.Key<ItemStack>> searchKeys = new HashMap<>();

    static {
        searchKeys.put('@',Client.MOD_NAMES);
        searchKeys.put('#',Client.TOOLTIPS);
        searchKeys.put('$',Client.OREDICT);
        searchKeys.put('%',Client.CREATIVE_TAB);
        searchKeys.put('^',Client.COLOR);
        searchKeys.put('&',Client.RESOURCE_ID);
    }

    public static boolean searchModName(String s) {
        return false;
    }

    public static List<ItemStack> search(String text) {

        String[] elements = text.split(" ");

        String element = elements[0];

        char first = element.charAt(0);

        if (searchKeys.containsKey(first)) {
            return Minecraft.getMinecraft().getSearchTree(searchKeys.get(first)).search(element.substring(1).toLowerCase(Locale.ROOT));
        } else {
            return Minecraft.getMinecraft().getSearchTree(Client.ALL_ITEMS).search(element.toLowerCase(Locale.ROOT));
        }
    }
}
