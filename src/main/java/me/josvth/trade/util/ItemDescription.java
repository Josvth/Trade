package me.josvth.trade.util;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import me.josvth.bukkitformatlibrary.managers.FormatManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemDescription {

	private Material material = Material.AIR;
	private int amount = 0;
	private byte data = 0;
	private short damage = 0;

	private FormattedMessage displayName = null;
	private List<FormattedMessage> lore = null;

	public ItemDescription() {

	}

	public ItemDescription(Material material, int amount) {
		this(material, amount, (short) 0, (byte) 0, null, null);
	}

	public ItemDescription(Material material, int amount, byte data) {
		this(material, amount, (short) 0, data, null, null);
	}

	public ItemDescription(Material material, int amount, short damage, byte data, FormattedMessage displayName, List<FormattedMessage> lore) {
		setMaterial(material);
		setAmount(amount);
		setDamage(damage);
		setData(data);

		setDisplayName(displayName);
		setLore(lore);
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		Validate.notNull(material, "Material can't be null.");
		this.material = material;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Amount can't be lower than 0");
		}
		this.amount = amount;
	}

	public byte getData() {
		return data;
	}

	public void setData(byte data) {
		if (data < 0) {
			throw new IllegalArgumentException("Data can't be lower than 0");
		}
		this.data = data;
	}

	public short getDamage() {
		return damage;
	}

	public void setDamage(short damage) {
		if (damage < 0) {
			throw new IllegalArgumentException("Damage can't be lower than 0");
		}
		this.damage = damage;
	}

	public FormattedMessage getDisplayName() {
		return displayName;
	}

	public void setDisplayName(FormattedMessage displayName) {
		this.displayName = displayName;
	}

	public List<FormattedMessage> getLore() {
		return lore;
	}

	public void setLore(List<FormattedMessage> lore) {
		this.lore = lore;
	}

	public ItemStack create(String... arguments) {

		final ItemStack stack = new ItemStack(material, amount, damage, data);

		if (displayName != null || lore != null) {

			final ItemMeta meta = stack.getItemMeta();

			if (displayName != null) {
				meta.setDisplayName(displayName.get(arguments));
			}

			if (lore != null) {
				final List<String> loreList = new ArrayList<String>();
				for (FormattedMessage message : lore) {
					loreList.add(message.get(arguments));
				}
				meta.setLore(loreList);
			}

			stack.setItemMeta(meta);

		}

		return stack;

	}

	/*
	item:
		material: WOOL			(Deprecated: material: 35)
		damage: 0 				(Optional, default: 0)
		data: 0           		(Optional, default: 0)
		amount: 1         		(Optional, default: 0)
		display-name: Accept  	(Optional)
		lore:                 	(Optional)
			- "This"
			- "is"
			- "lore"
	*/
	public static final ItemDescription fromSection(ConfigurationSection section, FormatManager formatManager) {

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

		List<FormattedMessage> lore = null;

		final List<String> loreList = section.getStringList("lore");

		if (!loreList.isEmpty()) {
			lore = new ArrayList<FormattedMessage>(loreList.size());
			for (String loreString : loreList) {
				lore.add(new FormattedMessage(formatManager.preformatMessage(loreString)));
			}
		}

		return new ItemDescription(
				material,
				section.getInt("amount", 0),
				(short) section.getInt("damage", 0),
				(byte) section.getInt("data", 0),
				new FormattedMessage(formatManager.preformatMessage(section.getString("display-name"))),
				lore);

	}

    public static final ItemDescription fromItemStack(ItemStack itemStack) {

        if (itemStack == null) {
            return null;
        }

        final String displayName;
        final ArrayList<FormattedMessage> message;

        if (itemStack.hasItemMeta()) {
           final ItemMeta meta = itemStack.getItemMeta();
           displayName = meta.getDisplayName();
           meta.
        } else {
            displayName = null;
            message = null;
        }
    }

}
