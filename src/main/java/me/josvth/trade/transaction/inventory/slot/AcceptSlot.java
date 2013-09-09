package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class AcceptSlot extends Slot {

	private final ItemStack acceptItem;
	private final ItemStack denyItem;

	public AcceptSlot(int slot, ItemStack acceptItem, ItemStack denyItem) {
		super(slot);
		this.acceptItem = acceptItem;
		this.denyItem = denyItem;
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {

		TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		if (holder.getTrader().hasAccepted())
			holder.getTrader().deny();
		else
			holder.getTrader().accept();

		return true;
	}

	public void update(TransactionHolder holder) {
		if (holder.getTrader().hasAccepted())
			setSlot(holder, denyItem);
		else
			setSlot(holder, denyItem);
	}

}
