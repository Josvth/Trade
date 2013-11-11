package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.ItemDescription;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class RefuseSlot extends Slot {

	private final ItemDescription refuseDescription;

	public RefuseSlot(int slot, ItemDescription refuseDescription) {
		super(slot);
		this.refuseDescription = refuseDescription;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		holder.getTrader().setRefused(true);

		event.setCancelled(true);

	}

	@Override
	public void update(TransactionHolder holder) {
		setSlot(holder, refuseDescription.create());
	}

}
