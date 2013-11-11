package me.josvth.trade.offer;

import me.josvth.trade.offer.Offer;
import me.josvth.trade.transaction.Trader;

import java.util.*;

public class OfferList {

	private final Offer[] offers;

	public OfferList(int size) {
		this.offers = new Offer[size];
	}

	public Offer get(int tradeSlot) {
		return offers[tradeSlot];
	}

	public void set(int slot, Offer offer) {
		offers[slot] = offer;
	}

	public HashMap<Integer, Offer> add(Offer... offer) {

		final HashMap<Integer, Offer> remainders = new HashMap<Integer, Offer>();

		for (int tradeableIndex = 0; tradeableIndex < offer.length; tradeableIndex++) {

			Offer remaining = offer[tradeableIndex];

			int firstEmpty = -1;

			int oi = 0;

			while (oi < offers.length && remaining != null) {

				if (offers[oi] != null) {
					remaining = offers[oi].add(remaining);
				} else if (firstEmpty == -1) {
					firstEmpty = oi;
				}

				oi++;

			}

			if (remaining != null) {	// Check if we still have remaining things to add

				if (firstEmpty != -1) {
					offers[firstEmpty] = remaining;
				} else {
					remainders.put(tradeableIndex, remaining);
				}

			}

		}

		return remainders;

	}

	public HashMap<Integer, Offer> remove(Offer... tradeable) {

		// TODO Make this deal with lowest stack size first

		final HashMap<Integer, Offer> remainders = new HashMap<Integer, Offer>();

		for (int tradeableIndex = 0; tradeableIndex < tradeable.length; tradeableIndex++) {

			Offer remaining = tradeable[tradeableIndex];

			int offerIndex = 0;

			while (offerIndex < offers.length && remaining != null) {

				final Offer offer = offers[offerIndex];

				if (offer != null) {
					remaining = offer.remove(remaining);

					if (offer.isWorthless()) {
						offers[offerIndex] = null;
					}

				}

				offerIndex++;

			}

			if (remaining != null) {
				remainders.put(tradeableIndex, remaining);
			}

		}

		return remainders;

	}

//	public <T extends Offer> HashMap<Integer, T> getByClass(Class<T> clazz) {
//
//		final HashMap<Integer, T> tradeables = new HashMap<Integer, T>();
//
//		for (int i = 0; i < offers.length; i++) {
//			Offer offer = offers[i];
//			if (clazz.isInstance(offer)) {
//				tradeables.put(i, (T) offer);
//			}
//		}
//
//		return tradeables;
//
//	}

	@Override
	public String toString() {

		final StringBuilder builder = new StringBuilder("Contents:");

		for (Offer offer : offers) {
			builder.append((offer == null)? "\n null" : "\n " + offer.toString());
		}

		return builder.toString();

	}

	public <T extends Offer> HashMap<Integer, T> getOfClass(Class<T> clazz) {

		final HashMap<Integer, T> found = new HashMap<Integer, T>();

		for (int i = 0; i < offers.length; i++) {
			if (clazz.isInstance(offers[i])) {
				found.put(i, (T) offers[i]);
			}
		}

		return found;

	}

	public int getFirstEmpty() {
		for (int i = 0; i < offers.length; i++) {
			if (offers[i] == null) {
				return i;
			}
		}
		return -1;
	}

	public void grant(Trader trader) {
		for (Offer offer : offers) {
			if (offer != null) {
				offer.grant(trader);
			}
		}
	}

}
