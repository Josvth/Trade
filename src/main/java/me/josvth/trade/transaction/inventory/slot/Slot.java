package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Slot {

	protected final int slot;
    protected final TransactionHolder holder;

	public Slot(int slot, TransactionHolder holder) {
        this.slot = slot;
        this.holder = holder;
    }

    public int getSlot() {
        return slot;
    }

    protected void setGUIItem(ItemStack stack) {
		holder.getInventory().setItem(slot, stack);
	}

	protected ItemStack getGUIItem() {
		return holder.getInventory().getItem(slot);
	}

	// Event handling
	public void onClick(InventoryClickEvent event) {
		event.setCancelled(true);
	}

	public void onDrag(InventoryDragEvent event) {
		event.setCancelled(true);
	}

	public void update() {

	}

}
