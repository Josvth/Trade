package me.josvth.trade.transaction;

import me.josvth.trade.goods.ItemTradeable;
import me.josvth.trade.goods.Tradeable;

import java.util.*;

public class OfferList {

	private final Trader trader;

	private final Tradeable[] offers;

	public OfferList(Trader trader, int size) {
		this.trader = trader;
		this.offers = new Tradeable[size];
	}

	public Tradeable get(int tradeSlot) {
		return offers[tradeSlot];
	}

	public void set(int slot, Tradeable tradeable) {
		offers[slot] = tradeable;
	}

	public HashMap<Integer, Tradeable> add(Tradeable... tradeable) {

		final HashMap<Integer, Tradeable> remainders = new HashMap<Integer, Tradeable>();

		for (int tradeableIndex = 0; tradeableIndex < tradeable.length; tradeableIndex++) {

			Tradeable remaining = tradeable[tradeableIndex];

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

	public HashMap<Integer, Tradeable> remove(Tradeable... tradeable) {

		// TODO Make this deal with lowest stack size first

		final HashMap<Integer, Tradeable> remainders = new HashMap<Integer, Tradeable>();

		for (int tradeableIndex = 0; tradeableIndex < tradeable.length; tradeableIndex++) {

			Tradeable remaining = tradeable[tradeableIndex];

			int offerIndex = 0;

			while (offerIndex < offers.length && remaining != null) {

				final Tradeable offer = offers[offerIndex];

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

	public void revert() {
		for (Tradeable tradeable : offers) {
			if (tradeable != null ) {
				tradeable.grant(trader);
			}
		}
	}

//	public <T extends Tradeable> HashMap<Integer, T> getByClass(Class<T> clazz) {
//
//		final HashMap<Integer, T> tradeables = new HashMap<Integer, T>();
//
//		for (int i = 0; i < offers.length; i++) {
//			Tradeable tradeable = offers[i];
//			if (clazz.isInstance(tradeable)) {
//				tradeables.put(i, (T) tradeable);
//			}
//		}
//
//		return tradeables;
//
//	}

	@Override
	public String toString() {

		final StringBuilder builder = new StringBuilder("Contents:");

		for (Tradeable tradeable : offers) {
			builder.append((tradeable == null)? "\n null" : "\n " + tradeable.toString());
		}

		return builder.toString();

	}

	public <T extends Tradeable> HashMap<Integer, T> getOfClass(Class<T> clazz) {

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

}
