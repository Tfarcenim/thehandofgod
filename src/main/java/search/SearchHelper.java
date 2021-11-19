package search;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import tfar.thehandofgod.client.Client;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

public class SearchHelper {


    public static final Map<Character, Predicate<String>> searchKeys = new HashMap<>();

    static {
        searchKeys.put('@',SearchHelper::searchModName);
    }

    public static boolean searchModName(String s) {
        return false;
    }

    public static List<ItemStack> search(String text) {

        String[] elements = text.split(" ");

        String element = elements[0];

        char first = element.charAt(0);

        if (searchKeys.containsKey(first)) {
            return Minecraft.getMinecraft().getSearchTree(Client.ALL_ITEMS).search(element.toLowerCase(Locale.ROOT));
        } else {
            return Minecraft.getMinecraft().getSearchTree(Client.ALL_ITEMS).search(element.toLowerCase(Locale.ROOT));

            //return Minecraft.getMinecraft().getSearchTree(SearchTreeManager.ITEMS).search(element.toLowerCase(Locale.ROOT));
        }
    }
}
