package me.josvth.trade.util;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import me.josvth.bukkitformatlibrary.managers.FormatManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
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
	public static final ItemStack fromSection(ConfigurationSection section, FormatManager formatManager) {

		if (section == null) {
			return null;
		}

		final Material material;

		if (section.isInt("material")) {
			material = Material.getMaterial(section.getInt("material"));
		} else if (section.isString("material")) {
			material = Material.getMaterial(section.getString("material"));
		} else {
			return null;
		}

		if (material == null) {
			return null;
		}


		final List<String> lore = section.getStringList("lore");

		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, formatManager.preformatMessage(lore.get(i)));
		}

		return setMeta(new ItemStack(material, section.getInt("amount", 0), (short) section.getInt("damage", 0), (byte) section.getInt("data", 0)),
				formatManager.preformatMessage(section.getString("display-name")),
				lore);

	}



}
