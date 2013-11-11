package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.offer.ItemOffer;
import me.josvth.trade.offer.Offer;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Set;

public class TradeSlot extends Slot {

	private final int tradeSlot;

	public TradeSlot(int slot, int tradeSlot) {
		super(slot);
		this.tradeSlot = tradeSlot;
	}

	// Event handling
	@Override
	public void onClick(InventoryClickEvent event) {

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		final Offer offer = holder.getOffers().get(tradeSlot);

		// We cancel the others accept if they had accepted
		if (holder.getOtherTrader().hasAccepted()) {
			holder.getOtherTrader().setAccepted(false);

			// Update slots
			AcceptSlot.updateAcceptSlots(holder.getOtherHolder(), true);
			StatusSlot.updateStatusSlots(holder, true);

			// Notify player
			Trade.getInstance().getFormatManager().getMessage("trading.offer-changed").send(holder.getOtherTrader().getPlayer(), "%player%", holder.getTrader().getName());
		}

		// If we have a offer on this slot we let the offer handle the event
		if (offer == null) {

			// TODO An alternative would be to determine the new item in the update() method instead of during the event.

			ItemStack newItem = null;

			((Player) event.getWhoClicked()).sendMessage(event.getAction().name());

			switch (event.getAction()) {
				case PLACE_ALL:
					newItem = event.getCursor().clone();
					break;
				case PLACE_SOME:
					throw new IllegalStateException("PLACE_SOME");
				case PLACE_ONE:
					newItem = event.getCursor().clone();
					newItem.setAmount(1);
					break;
				default:
					throw new IllegalStateException("Not handled action: " + event.getAction().name());
			}

			holder.getOffers().set(tradeSlot, (newItem == null)? null : new ItemOffer(newItem));

			MirrorSlot.updateMirrors(tradeSlot, holder.getOtherHolder(), true);

		} else {

			offer.onClick(event);

			if (offer.isWorthless()) {
				holder.getOffers().set(tradeSlot, null);
			}

			TradeSlot.updateTradeSlots(tradeSlot, holder, true);
			MirrorSlot.updateMirrors(tradeSlot, holder.getOtherHolder(), true);

		}

	}

	public boolean isEmpty(TransactionHolder holder) {
		return holder.getOffers().get(tradeSlot) == null;
	}

	@Override
	public void onDrag(InventoryDragEvent event) {

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		if (event.getNewItems().containsKey(slot)) {
			holder.getOffers().set(tradeSlot, new ItemOffer(event.getNewItems().get(slot)));
		} else {
			holder.getOffers().set(tradeSlot, null);
		}

		MirrorSlot.updateMirrors(tradeSlot, holder.getOtherHolder(), true);

	}

	@Override
	public void update(TransactionHolder holder) {

		final Offer offer = holder.getOffers().get(tradeSlot);

		if (offer != null) {
			holder.getInventory().setItem(slot, offer.getDisplayItem());
		} else {
			holder.getInventory().setItem(slot, null);
		}

	}

	public int getTradeSlot() {
		return tradeSlot;
	}

	public static void updateTradeSlots(int tradeSlot, TransactionHolder holder, boolean nextTick) {

		final Set<TradeSlot> slots = holder.getLayout().getSlotsOfType(TradeSlot.class);

		final Iterator<TradeSlot> iterator = slots.iterator();

		while (iterator.hasNext()) {
			final TradeSlot slot = iterator.next();
			if (slot.getTradeSlot() == tradeSlot) {
				if (!nextTick) {
					slot.update(holder);
				}
			} else {
				iterator.remove();
			}
		}

		if (nextTick && !slots.isEmpty()) {
			Bukkit.getScheduler().runTask(Trade.getInstance(), new SlotUpdateTask(holder, slots));
		}

	}

	public static void updateTradeSlots(TransactionHolder holder, boolean nextTick) {

		final Set<TradeSlot> slots = holder.getLayout().getSlotsOfType(TradeSlot.class);

		if (!nextTick) {
			for (Slot slot : slots) {
				slot.update(holder);
			}
		} else if (!slots.isEmpty()) {
			Bukkit.getScheduler().runTask(Trade.getInstance(), new SlotUpdateTask(holder, slots));
		}

	}

}
