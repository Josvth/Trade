package me.josvth.trade.goods;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Tradeable {

	protected final TradeableType type;

	public Tradeable(TradeableType type) {
		this.type = type;
	}

	public TradeableType getType() {
		return type;
	}

	public abstract <T extends Tradeable> T add(T tradeable);

	public abstract ItemStack getDisplayItem();

	public abstract boolean isWorthless();

	// Event handling
	public boolean onClick(InventoryClickEvent event) {
		return false;
	}

	public boolean onDrag(int slot, InventoryDragEvent event) {
		return false;
	}

	public enum TradeableType {
		ITEM,
		EXPERIENCE,
		MONEY;
	}

}
