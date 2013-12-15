package me.josvth.trade.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.ExperienceSlot;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
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

	public int addExperience(int levels) {

		final LinkedList<Integer> changedIndexes = new LinkedList<Integer>();

		// First we try and fill up existing experience offers
		final Iterator<Map.Entry<Integer, ExperienceOffer>> iterator = getOfClass(ExperienceOffer.class).entrySet().iterator();

		int currentLevels = 0;

		while (iterator.hasNext()) {

			final Map.Entry<Integer, ExperienceOffer> entry = iterator.next();

			if (levels > 0) {

				final int remaining = entry.getValue().add(levels);

				// If we have added something change the remaining levels and add this slot to the changed indexes
				if (remaining < levels) {
					changedIndexes.add(entry.getKey());
					levels = remaining;
				}

			}

			// Meanwhile we count the current levels
			currentLevels += entry.getValue().getLevels();

		}


		// Next put the remaining levels in empty offer slots
		if (levels > 0) {

			int firstEmpty = getFirstEmpty();

			while (levels > 0 && firstEmpty != -1) {

				final int remainder = levels - 64;

				if (remainder <= 0) {
					set(firstEmpty, createExperienceOffer(firstEmpty, levels));
					levels = 0;
				} else {
					set(firstEmpty, createExperienceOffer(firstEmpty, 64));
					levels = -1 * remainder;
					firstEmpty = getFirstEmpty();
				}

				changedIndexes.add(firstEmpty);

			}

		}

		// If we changed anything we update the holder and mirror
		if (!changedIndexes.isEmpty()) {

			// We place our changed indexes into an array
			final int[] indexesArray = new int[changedIndexes.size()];

			int i = 0;
			for (int index : changedIndexes) {
				indexesArray[i] = index;
				i++;
			}

			TradeSlot.updateTradeSlots(getHolder(), true, indexesArray);
			MirrorSlot.updateMirrors(getHolder().getOtherHolder(), true, indexesArray);
			ExperienceSlot.updateExperienceSlots(getHolder(), true, currentLevels - levels);

		}

		return levels;

	}

	public int removeExperience(int levels) {

		final LinkedList<Integer> changedIndexes = new LinkedList<Integer>();

		// TODO TAKE ACCOUNT OF ORDER
		// First we try and remove from existing experience offers
		final Iterator<Map.Entry<Integer, ExperienceOffer>> iterator = getOfClass(ExperienceOffer.class).entrySet().iterator();

		int currentLevels = 0;

		while (iterator.hasNext() && levels > 0) {

			final Map.Entry<Integer, ExperienceOffer> entry = iterator.next();

			if (levels > 0)  {

				final int remaining = entry.getValue().remove(levels);

				if (remaining < levels) {
					changedIndexes.add(entry.getKey());
					levels = remaining;

					// TODO What about this?
					if (entry.getValue().getLevels() == 0) {
						set(entry.getKey(), null);
					}

				}

			}

			currentLevels += entry.getValue().getLevels();

		}

		// If we changed anything we update the holder and mirror
		if (!changedIndexes.isEmpty()) {

			// We place our changed indexes into an array
			final int[] indexesArray = new int[changedIndexes.size()];

			int i = 0;
			for (int index : changedIndexes) {
				indexesArray[i] = index;
			}

			TradeSlot.updateTradeSlots(getHolder(), true, indexesArray);
			MirrorSlot.updateMirrors(getHolder().getOtherHolder(), true, indexesArray);
			ExperienceSlot.updateExperienceSlots(getHolder(), true, currentLevels - levels);

		}

		return levels;

	}

	public ExperienceOffer createExperienceOffer(int index, int levels) {
		final ExperienceOffer offer = getTrader().getLayout().getOfferDescription(ExperienceOffer.class).createOffer(this, index);
		offer.setLevels(levels);
		return offer;
	}

    public ItemOffer createItemOffer(int index, ItemStack itemStack) {
        final ItemOffer offer = (ItemOffer) getTrader().getLayout().getOfferDescription(ItemOffer.class).createOffer(this, index);
        offer.setItem(itemStack);
        return offer;
    }

}
