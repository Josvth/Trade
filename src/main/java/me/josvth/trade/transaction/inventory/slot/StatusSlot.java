package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.inventory.ItemDescription;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class StatusSlot extends Slot{

	private final ItemDescription consideringDescription;
	private final ItemDescription acceptedDescription;

	public StatusSlot(int slot, ItemDescription consideringDescription, ItemDescription acceptedDescription) {
		super(slot);
		this.consideringDescription = consideringDescription;
		this.acceptedDescription = acceptedDescription;
	}

	@Override
	public void update(TransactionHolder holder) {
		if (holder.getTrader().getOther().hasAccepted()) {
			setSlot(holder, consideringDescription.create());
		} else {
			setSlot(holder, acceptedDescription.create());
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
