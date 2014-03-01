package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.offer.Offer;
import org.bukkit.Bukkit;

import java.util.Iterator;
import java.util.Set;

public class MirrorSlot extends Slot {

	private final int offerIndex;

	public MirrorSlot(int slot, int offerIndex) {
   		super(slot);
		this.offerIndex = offerIndex;
	}

	public int getOfferIndex() {
		return offerIndex;
	}

	@Override
	public void update(TransactionHolder holder) {
		final Offer offer = holder.getOtherHolder().getOffers().get(offerIndex);

		if (offer != null) {
			holder.getInventory().setItem(slot, offer.createMirrorItem(holder));
		} else {
			holder.getInventory().setItem(slot, null);
		}

	}

	public static void updateMirrors(TransactionHolder holder, boolean nextTick, int... offerIndex) {

		final Set<MirrorSlot> slots = holder.getLayout().getSlotsOfType(MirrorSlot.class);

		final Iterator<MirrorSlot> iterator = slots.iterator();

		while (iterator.hasNext()) {
			final MirrorSlot slot = iterator.next();

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

	public static void updateMirrors(TransactionHolder holder, boolean nextTick) {

		final Set<MirrorSlot> slots = holder.getLayout().getSlotsOfType(MirrorSlot.class);

		if (!nextTick) {
			for (Slot slot : slots) {
				slot.update(holder);
			}
		} else if (!slots.isEmpty()) {
			Bukkit.getScheduler().runTask(Trade.getInstance(), new SlotUpdateTask(holder, slots));
		}

	}

}
