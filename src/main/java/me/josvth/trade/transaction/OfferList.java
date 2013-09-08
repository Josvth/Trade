package me.josvth.trade.transaction;

import me.josvth.trade.goods.Tradeable;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class OfferList {

	private final Tradeable[] offers;

	public OfferList(int size) {
		this.offers = new Tradeable[size];
	}

	public Tradeable get(int tradeSlot) {
		return offers[tradeSlot];
	}

	public void set(int slot, Tradeable tradeable) {
		offers[slot] = tradeable;
	}

	public HashMap<Integer, Tradeable> add(Tradeable... tradeable) {

		HashMap<Integer, Tradeable> remainders = new HashMap<Integer, Tradeable>();

		for (int tradeableIndex = 0; tradeableIndex < tradeable.length; tradeableIndex++) {

			int emptyOfferIndex = -1;

			Tradeable remaining = tradeable[tradeableIndex];

			int offerIndex = 0;

			while (offerIndex < offers.length && remaining != null) {

				Tradeable offer = offers[offerIndex];

				if (offer != null) {		// if we can add we add
				 	remaining = offer.add(remaining);
				} else if (emptyOfferIndex == -1) {
					emptyOfferIndex = offerIndex;	// If this is the first empty spot we found store it.
				}

				offerIndex++;

			}

			if (remaining != null) {	// add remaining to empty slot

				if (emptyOfferIndex != -1) {
					offers[emptyOfferIndex] = remaining;
				} else {
					remainders.put(offerIndex, remaining);
				}

			}

		}

		return remainders;

	}


}
