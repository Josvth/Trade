package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.goods.Tradeable;
import me.josvth.trade.transaction.OfferList;
import me.josvth.trade.transaction.inventory.TransactionHolder;

public class MirrorSlot extends Slot {

	private final int mirrorSlot;

	public MirrorSlot(int slot, int mirrorSlot) {
   		super(slot);
		this.mirrorSlot = mirrorSlot;
	}

	@Override
	public void update(TransactionHolder holder) {
		Tradeable tradeable = holder.getOffers().get(mirrorSlot);

		if (tradeable != null)
			holder.getInventory().setItem(slot, tradeable.getDisplayItem());
	}
}
