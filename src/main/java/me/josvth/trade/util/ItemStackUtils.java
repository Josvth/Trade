package me.josvth.trade.util;

import me.josvth.bukkitformatlibrary.message.FormattedMessage;
import me.josvth.bukkitformatlibrary.message.managers.MessageManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class ItemStackUtils {

    public static ItemStack argument(ItemStack itemStack, String... arguments) {

        if (itemStack == null) {
            return itemStack;
        }

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

    /*
    item:
        material: WOOL			(Deprecated: material: 35)
        damage: 0				(Optional, default: 0)
        data: 0				 	(Optional, default: 0)
        amount: 1				(Optional, default: 0)
        display-name: Accept	(Optional)
        lore:		 			(Optional)
            - "This"
            - "is"
            - "lore"
    */
    public static ItemStack fromSection(ConfigurationSection section, MessageManager messageManager) {

        if (section == null) {
            return null;
        }

        final Material material = Material.matchMaterial(section.getString("material"));

        if (material == null) {
            return null;
        }

        final List<String> lore = section.getStringList("lore");

        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, messageManager.preformatMessage(lore.get(i)));
        }

        return setMeta(new ItemStack(material, section.getInt("amount"), (short) section.getInt("damage"), (byte) section.getInt("data")),
                messageManager.preformatMessage(section.getString("display-name")),
                lore);

    }

    public static ItemStack create(Material type, int amount, short durability, MaterialData data, ItemMeta meta) {
        final ItemStack item = new ItemStack(type, amount, durability);
        item.setData(data);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack[] split(ItemStack currentItem) {
        final ItemStack[] stacks = new ItemStack[2];
        stacks[0] = currentItem.clone();
        stacks[0].setAmount(currentItem.getAmount() / 2);
        stacks[1] = currentItem.clone();
        stacks[1].setAmount(currentItem.getAmount() / 2);
        if (stacks[0].getAmount() + stacks[1].getAmount() < currentItem.getAmount()) {
            stacks[0].setAmount(stacks[0].getAmount() + 1);
        }
        return stacks;
    }
}
