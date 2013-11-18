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

	@Override
	public String toString() {

		final StringBuilder builder = new StringBuilder("Contents:");

		for (Offer offer : offers) {
			builder.append((offer == null)? "\n null" : "\n " + offer.toString());
		}

		return builder.toString();

	}

	public <T extends Offer> TreeMap<Integer, T> getOfClass(Class<T> clazz) {

		final TreeMap<Integer, T> found = new TreeMap<Integer, T>();

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
