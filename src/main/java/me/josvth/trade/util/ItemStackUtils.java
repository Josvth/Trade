package me.josvth.trade.util;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemStackUtils {

    public static ItemStack argument(ItemStack itemStack, String... arguments) {
        if (itemStack.hasItemMeta()) {
            final ItemMeta meta = itemStack.getItemMeta();
            if (meta.hasDisplayName()) {
                meta.setDisplayName(new FormattedMessage(meta.getDisplayName()).get(arguments));
            }
            if (meta.hasLore()) {
                final ArrayList<String> lore = new ArrayList<String>(meta.getLore().size());
                for (String string : meta.getLore()) {
                    lore.add(new FormattedMessage(string).get(arguments));
                }
                meta.setLore(lore);
            }
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    public static ItemStack setMeta(ItemStack itemStack, String displayName, List<String> lore) {
        final ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setMeta(ItemStack itemStack, String... lore) {
        final ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
