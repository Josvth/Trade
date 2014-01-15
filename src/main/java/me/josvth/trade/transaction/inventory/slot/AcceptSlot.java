package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.AcceptAction;
import me.josvth.trade.transaction.action.DenyAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class AcceptSlot extends Slot {

	private final ItemStack acceptItem;
	private final ItemStack pendingItem;

	public AcceptSlot(int slot, ItemStack acceptDescription, ItemStack pendingDescription) {
		super(slot);
		this.acceptItem = acceptDescription;
		this.pendingItem = pendingDescription;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

        final Trader trader = holder.getTrader();

        if (trader.hasAccepted()) {
            new DenyAction(trader, DenyAction.Reason.BUTTON).execute();
        } else {
            new AcceptAction(trader, AcceptAction.Reason.BUTTON).execute();
        }

		event.setCancelled(true);

	}

	public static void updateAcceptSlots(TransactionHolder holder, boolean nextTick) {

		final Set<AcceptSlot> slots = holder.getLayout().getSlotsOfType(AcceptSlot.class);

		if (!nextTick) {
		   for (Slot slot : slots) {
			   slot.update(holder);
		   }
		} else if (!slots.isEmpty()) {
			Bukkit.getScheduler().runTask(Trade.getInstance(), new SlotUpdateTask(holder, slots));
		}

	}

	public void update(TransactionHolder holder) {

		if (holder.getTrader().hasAccepted()) {
			setSlot(holder, pendingItem);
		} else {
			setSlot(holder, acceptItem);
		}

	}

}
