package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.offer.Offer;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Set;

public class TradeSlot extends Slot {

	private final int offerIndex;

	public TradeSlot(int slot, int offerIndex) {
		super(slot);
		this.offerIndex = offerIndex;
	}

	public Offer getSlotContents(TransactionHolder holder) {
		return holder.getOffers().get(offerIndex);
	}

	public int getOfferIndex() {
		return offerIndex;
	}

	// Event handling
	@Override
	public void onClick(InventoryClickEvent event) {

        final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		final Offer offer = getSlotContents(holder);

		// If we have a offer on this slot we let the offer handle the event
		if (offer == null) {

			// TODO An alternative would be to determine the new item in the update() method instead of during the event.

			ItemStack newItem = null;

			switch (event.getAction()) {
				case PLACE_ALL:
					newItem = event.getCursor().clone();
					break;
				case PLACE_ONE:
					newItem = event.getCursor().clone();
					newItem.setAmount(1);
					break;
				default:
					throw new IllegalStateException("Not handled action: " + event.getAction().name());
			}

			holder.getOffers().set(offerIndex, (newItem == null)? null : holder.getOffers().createItemOffer(offerIndex, newItem));

            // Cancels the other players accept if he had accepted
            holder.getOtherTrader().cancelAccept();

			MirrorSlot.updateMirrors(holder.getOtherHolder(), true);

		} else {

			offer.onClick(event);

		}

	}

	@Override
	public void onDrag(InventoryDragEvent event) {

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		final Offer offer = getSlotContents(holder);

		if (offer != null) {
			offer.onDrag(slot, event);
		} else if (event.getNewItems().containsKey(slot)) {

			holder.getOffers().set(offerIndex, holder.getOffers().createItemOffer(offerIndex, event.getNewItems().get(slot).clone()));

            // Cancels the other players accept if he had accepted
            holder.getOtherTrader().cancelAccept();

			MirrorSlot.updateMirrors(holder.getOtherHolder(), true, offerIndex);

		}

	}

	@Override
	public void update(TransactionHolder holder) {

		final Offer offer = getSlotContents(holder);

		if (offer != null) {
			holder.getInventory().setItem(slot, offer.createItem());
		} else {
			holder.getInventory().setItem(slot, null);
		}

	}

	public static void updateTradeSlots(TransactionHolder holder, boolean nextTick, int... offerIndex) {

		final Set<TradeSlot> slots = holder.getLayout().getSlotsOfType(TradeSlot.class);

		final Iterator<TradeSlot> iterator = slots.iterator();

		while (iterator.hasNext()) {

            final TradeSlot slot = iterator.next();

            boolean notUpdated = true;
            for (int i = 0; i < offerIndex.length && notUpdated; i++) {
                if (slot.getOfferIndex() == offerIndex[i]) {
                    if (!nextTick) {
                        slot.update(holder);
                    }
                    notUpdated = false;
                }
            }

            if (notUpdated) {
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
