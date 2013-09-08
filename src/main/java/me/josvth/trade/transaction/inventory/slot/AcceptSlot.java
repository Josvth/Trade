package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AcceptSlot extends Slot {

	private final ItemStack acceptItem;
	private final ItemStack denyItem;

	public AcceptSlot(TransactionHolder holder, int slot, ItemStack acceptItem, ItemStack denyItem) {
		super(holder, slot);
		this.acceptItem = acceptItem;
		this.denyItem = denyItem;
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		if (holder.getTrader().hasAccepted())
			holder.getTrader().deny();
		else
			holder.getTrader().accept();
		return true;
	}

	@Override
	public void update() {
		if (holder.getTrader().hasAccepted())
			setInventoryItem(denyItem);
		else
			setInventoryItem(acceptItem);
	}
}
