package me.josvth.trade.transaction.inventory;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemDescription {

	private Material material = Material.AIR;
	private int amount = 0;
	private byte data = 0;
	private short damage = 0;

	private String displayName = null;
	private List<String> lore = null;

	private boolean overrideMeta = true;

	public ItemDescription() {

	}

	public ItemDescription(Material material, int amount) {
		this(material, amount, (short) 0, (byte) 0, null, null, true);
	}

	public ItemDescription(Material material, int amount, byte data) {
		this(material, amount, (short) 0, data, null, null, true);
	}

	public ItemDescription(Material material, int amount, short damage, byte data, String displayName, List<String> lore, boolean overrideMeta) {
	    setMaterial(material);
		setAmount(amount);
		setDamage(damage);
		setData(data);

		setDisplayName(displayName);
		setLore(lore);

		setOverrideMeta(overrideMeta);
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<String> getLore() {
		return lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public boolean overrideMeta() {
		return overrideMeta;
	}

	public void setOverrideMeta(boolean overrideMeta) {
		this.overrideMeta = overrideMeta;
	}

	public ItemStack create() {

		final ItemStack stack = new ItemStack(material, amount, damage, data);

		if (displayName != null || lore != null) {

			final ItemMeta meta = stack.getItemMeta();

			if (displayName != null) {
				meta.setDisplayName(displayName);
			}

			if (lore != null) {
				meta.setLore(meta.getLore());
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
		override-meta: true		(Optional, default: true)
	*/
	public static final ItemDescription fromSection(ConfigurationSection section) {

		if (section == null) {
			return null;
		}

		final Material material;

		if (section.isInt("id")) {
			material = Material.getMaterial(section.getInt("id"));
		} else if (section.isString("id")) {
			material = Material.getMaterial(section.getString("id"));
		} else {
			return null;
		}

		if (material == null) {
			return null;
		}

		final int amount = section.getInt("amount", 0);
		final short damage = (short) section.getInt("damage", 0);
		final byte data = (byte) section.getInt("data", 0);

		final String displayName = section.getString("display-name");
		final List<String> lore = section.getStringList("lore");
		final boolean overrideMeta = section.getBoolean("override-meta", true);

		return new ItemDescription(material, amount, damage, data, displayName, lore, overrideMeta);

	}



}
