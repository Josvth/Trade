package me.josvth.trade.transaction.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.ExperienceSlot;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.util.ExperienceManager;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class OfferList {

	private final Trader trader;

	private final Offer[] offers;

	public OfferList(Trader trader, int size) {
		this.trader = trader;
		this.offers = new Offer[size];
	}

    public Trader getTrader() {
        return trader;
    }

	public TransactionHolder getHolder() {
		return getTrader().getHolder();
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

    public TreeMap<Integer, Offer> getOfType(String type) {

        final TreeMap<Integer, Offer> found = new TreeMap<Integer, Offer>();

        for (int i = 0; i < offers.length; i++) {
            if (type.equalsIgnoreCase(offers[i].getType())) {
                found.put(i, offers[i]);
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
