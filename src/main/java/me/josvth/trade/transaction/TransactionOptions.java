package me.josvth.trade.transaction;

import org.bukkit.configuration.ConfigurationSection;

public class TransactionOptions {

	private boolean allowInventoryClosing = true;

	public void load(ConfigurationSection section) {
		setAllowInventoryClosing(section.getBoolean("allow-inventory-closing", true));
	}

	public boolean allowInventoryClosing() {
		return allowInventoryClosing;
	}

	public void setAllowInventoryClosing(boolean allowInventoryClosing) {
		this.allowInventoryClosing = allowInventoryClosing;
	}
}
