package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Slot {

	protected final TransactionHolder holder;

	protected final int slot;

	public Slot(TransactionHolder holder, int slot) {
		this.holder = holder;
		this.slot = slot;
	}

	public TransactionHolder getHolder() {
		return holder;
	}

	public Inventory getInventory() {
		return holder.getInventory();
	}

	public void setInventoryItem(ItemStack item) {
		getInventory().setItem(slot, item);
	}

	public ItemStack getInventoryItem() {
		return getInventory().getItem(slot);
	}

	// Event handling
	public boolean onClick(InventoryClickEvent event) {
		return false;
	}

	public boolean onDrag(InventoryDragEvent event) {
		return false;
	}

	public void update() {

	}

}
