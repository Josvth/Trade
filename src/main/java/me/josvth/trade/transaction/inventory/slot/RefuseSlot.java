package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class RefuseSlot extends Slot {

	private final ItemStack refuseItem;
	private final boolean keepMeta;

	public RefuseSlot(int slot, ItemStack refuseItem, boolean keepMeta) {
		super(slot);
		this.refuseItem = refuseItem;
		this.keepMeta = keepMeta;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		holder.getTrader().refuse();

		event.setCancelled(true);

	}

	@Override
	public void update(TransactionHolder holder) {
		setSlot(holder, refuseItem);
	}

}
