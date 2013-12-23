package me.josvth.trade.offer;

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

    public void addExperience(int experience) {

        final ExperienceManager expManager = new ExperienceManager(getTrader().getPlayer());

        if (!expManager.hasExp(experience)) {
            trader.getFormattedMessage("experience.insufficient").send(getTrader().getPlayer(), "%experience%", String.valueOf(experience));
            return;
        }

        final LinkedList<Integer> changedIndexes = new LinkedList<Integer>();

        // First we try and fill up existing experience offers
        final Iterator<Map.Entry<Integer, ExperienceOffer>> iterator = getOfClass(ExperienceOffer.class).entrySet().iterator();

        int remaining = experience;

        int currentLevels = 0;

        while (iterator.hasNext()) {

            final Map.Entry<Integer, ExperienceOffer> entry = iterator.next();

            // Meanwhile we count the current levels we add our addition later
            currentLevels += entry.getValue().getExperience();

            if (experience > 0) {

                final int overflow = entry.getValue().add(experience);

                // If we have added something change the remaining levels and add this slot to the changed indexes
                if (overflow < remaining) {
                    changedIndexes.add(entry.getKey());
                    remaining = overflow;
                }

            }

        }

        // Next put the remaining levels in empty offer slots
        if (remaining > 0) {

            int firstEmpty = getFirstEmpty();

            while (remaining > 0 && firstEmpty != -1) {

                final int overflow = remaining - 64;

                if (overflow <= 0) {
                    set(firstEmpty, createExperienceOffer(firstEmpty, experience));
                    remaining = 0;
                } else {
                    set(firstEmpty, createExperienceOffer(firstEmpty, 64));
                    remaining = -1 * overflow;
                    firstEmpty = getFirstEmpty();
                }

                changedIndexes.add(firstEmpty);

            }

        }

        // Calculated add experience
        final int added = experience - remaining;

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
            ExperienceSlot.updateExperienceSlots(getHolder(), true, currentLevels + added);

        }

        // Take experience from player
        expManager.changeExp(-1 * added);

        trader.getFormattedMessage("experience.added.self").send(trader.getPlayer(), "%experience%", String.valueOf(added));
        if (trader.getOtherTrader().hasFormattedMessage("experience.added.other")) {
            trader.getOtherTrader().getFormattedMessage("experience.added.other").send(trader.getOtherTrader().getPlayer(), "%player%", trader.getName(), "%experience%", String.valueOf(added));
        }

        trader.getOtherTrader().cancelAccept();

    }

	public void removeExperience(int experience) {

		final LinkedList<Integer> changedIndexes = new LinkedList<Integer>();

		// TODO TAKE ACCOUNT OF ORDER
		// First we try and remove from existing experience offers
		final Iterator<Map.Entry<Integer, ExperienceOffer>> iterator = getOfClass(ExperienceOffer.class).entrySet().iterator();

		int currentLevels = 0;

        int remaining = experience;

		while (iterator.hasNext()) {

			final Map.Entry<Integer, ExperienceOffer> entry = iterator.next();

            currentLevels += entry.getValue().getExperience();

			if (remaining > 0)  {

				final int overflow = entry.getValue().remove(remaining);

				if (overflow < remaining) {
					changedIndexes.add(entry.getKey());
                    remaining = overflow;

					// TODO What about this?
					if (entry.getValue().getExperience() == 0) {
						set(entry.getKey(), null);
					}

				}

			}

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
			ExperienceSlot.updateExperienceSlots(getHolder(), true, currentLevels);

		}

        final int removedExperience = experience - remaining;

        trader.getFormattedMessage("experience.removed.self").send(trader.getPlayer(), "%experience%", String.valueOf(removedExperience));

        // Give the player experience
        new ExperienceManager(getTrader().getPlayer()).changeExp(removedExperience);

        if (trader.getOtherTrader().hasFormattedMessage("experience.removed.other") && removedExperience > 0) {
            trader.getOtherTrader().getFormattedMessage("experience.removed.other").send(trader.getOtherTrader().getPlayer(), "%player%", trader.getName(), "%experience%", String.valueOf(removedExperience));
        }

        trader.getOtherTrader().cancelAccept();

    }

	public ExperienceOffer createExperienceOffer(int index, int levels) {
		final ExperienceOffer offer = getTrader().getLayout().getOfferDescription(ExperienceOffer.class).createOffer(this, index);
		offer.setExperience(levels);
		return offer;
	}

    public ItemOffer createItemOffer(int index, ItemStack itemStack) {
        final ItemOffer offer = getTrader().getLayout().getOfferDescription(ItemOffer.class).createOffer(this, index);
        offer.setItem(itemStack);
        return offer;
    }

}
