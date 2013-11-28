package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class StatusSlot extends Slot{

	private final ItemStack consideringItem;
	private final ItemStack acceptedItem;

	public StatusSlot(int slot, ItemStack consideringItem, ItemStack acceptedItem) {
		super(slot);
		this.consideringItem = consideringItem;
		this.acceptedItem = acceptedItem;
	}

	@Override
	public void update(TransactionHolder holder) {
		if (holder.getOtherTrader().hasAccepted()) {
			setSlot(holder, ItemStackUtils.argument(acceptedItem.clone(), "%player%", holder.getOtherTrader().getName()));
		} else {
			setSlot(holder, ItemStackUtils.argument(consideringItem.clone(),  "%player%", holder.getOtherTrader().getName()));
		}
	}

	public static void updateStatusSlots(TransactionHolder holder, boolean nextTick) {

		final Set<StatusSlot> slots = holder.getLayout().getSlotsOfType(StatusSlot.class);

		if (!nextTick) {
			for (Slot slot : slots) {
				slot.update(holder);
			}
		} else if (!slots.isEmpty()) {
			Bukkit.getScheduler().runTask(Trade.getInstance(), new SlotUpdateTask(holder, slots));
		}

	}

}
