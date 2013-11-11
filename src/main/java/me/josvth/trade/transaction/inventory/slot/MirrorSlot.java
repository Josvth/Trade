package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.goods.Tradeable;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Iterator;
import java.util.Set;

public class MirrorSlot extends Slot {

	private final int mirrorSlot;

	public MirrorSlot(int slot, int mirrorSlot) {
   		super(slot);
		this.mirrorSlot = mirrorSlot;
	}

	public int getMirrorSlot() {
		return mirrorSlot;
	}

	@Override
	public void update(TransactionHolder holder) {
		final Tradeable tradeable = holder.getOtherTrader().getOffers().get(mirrorSlot);

		if (tradeable != null) {
			holder.getInventory().setItem(slot, tradeable.getDisplayItem());
		} else {
			holder.getInventory().setItem(slot, null);
		}

	}

	public static void updateMirrors(int tradeSlot, TransactionHolder holder, boolean nextTick) {

		final Set<MirrorSlot> slots = holder.getLayout().getSlotsOfType(MirrorSlot.class);

		final Iterator<MirrorSlot> iterator = slots.iterator();

		while (iterator.hasNext()) {
			final MirrorSlot mirrorSlot = iterator.next();
			if (mirrorSlot.getMirrorSlot() == tradeSlot) {
				if (!nextTick) {
					mirrorSlot.update(holder);
				}
			} else {
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
