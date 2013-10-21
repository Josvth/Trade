package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CloseSlot extends Slot {

	private final ItemStack closeItem;

	public CloseSlot(int slot, ItemStack closeItem) {
		super(slot);
		this.closeItem = closeItem;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		holder.getTrader().closeInventory();

		event.setCancelled(true);

	}

	@Override
	public void update(TransactionHolder holder) {
		setSlot(holder, closeItem);
	}

}
