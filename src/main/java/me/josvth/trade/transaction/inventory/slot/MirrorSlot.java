package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.goods.Tradeable;
import me.josvth.trade.transaction.OfferList;
import me.josvth.trade.transaction.inventory.TransactionHolder;

public class MirrorSlot extends Slot {

	private final int mirrorSlot;
	private final OfferList offerList;

	public MirrorSlot(TransactionHolder holder, int slot, int mirrorSlot) {
   		super(holder, slot);
		this.mirrorSlot = mirrorSlot;
		this.offerList = holder.getTrader().getOther().getOffers();
	}

	@Override
	public void update() {

		Tradeable tradeable = offerList.get(mirrorSlot);

		if (tradeable != null)
			setInventoryItem(tradeable.getDisplayItem());

	}
}
