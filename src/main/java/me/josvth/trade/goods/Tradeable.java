package me.josvth.trade.goods;

import me.josvth.trade.transaction.Trader;
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

	public <T extends Tradeable> T add(T tradeable) {
		return tradeable;
	}

	public <T extends Tradeable> T remove(T tradeable) {
		return tradeable;
	}

	public abstract ItemStack getDisplayItem();

	public double getAmount() {
		return 0;
	}

	public boolean isFull() {
		return false;
	}

	public boolean isWorthless() {
		return getAmount() == 0.0;
	}

	public abstract void grant(Trader trader);

	// Event handling
	public boolean onClick(InventoryClickEvent event) {
		return false;
	}

	public boolean onDrag(int slot, InventoryDragEvent event) {
		return false;
	}

}
