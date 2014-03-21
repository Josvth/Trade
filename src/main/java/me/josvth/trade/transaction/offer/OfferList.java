package me.josvth.trade.transaction.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class OfferList {

	private final Trader trader;

	private Offer[] offers;

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

    public Offer[] getContents() {
        return offers;
    }

    public void setContents(Offer[] contents) {
        Validate.notNull(contents, "Contents can't be null.");
        this.offers = contents;
    }

	public Offer get(int slot) {
		return offers[slot];
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

    public OfferMutationResult add(Offer offer) {

        final Map<Integer, Offer> changes = new HashMap<Integer, Offer>();

        final OfferMutationResult result = new OfferMutationResult(offer, OfferMutationResult.Type.ADDITION);
        result.setChanges(changes);

        if (offer instanceof StackableOffer) {

            final StackableOffer stackableOffer = (StackableOffer) offer;

            // First we try and fill up existing offers
            for (Map.Entry<Integer, ? extends StackableOffer> entry : getOfClass(stackableOffer.getClass()).entrySet()) {

                if (result.getRemaining() > 0) {

                    final int overflow = entry.getValue().add(result.getRemaining());

                    // If we have added something change the remaining levels and add this slot to the changed indexes
                    if (overflow < result.getRemaining()) {
                        changes.put(entry.getKey(), entry.getValue()); // We keep track of what we changed
                        result.setRemaining(overflow);
                    }

                }

                result.setCurrentAmount(result.getCurrentAmount() + entry.getValue().getAmount()); // We count the total amount currently offered

            }

            // Next put the remaining levels in empty offer slots
            if (result.getRemaining() > 0) {

                int firstEmpty = getFirstEmpty();

                while (result.getRemaining() > 0 && firstEmpty != -1) {

                    final int overflow = result.getRemaining() - stackableOffer.getMaxAmount();

                    if (overflow <= 0) {

                        final StackableOffer clone = stackableOffer.clone();

                        set(firstEmpty, clone);
                        changes.put(firstEmpty, clone); // We keep track of what we changed

                        result.setCurrentAmount(result.getCurrentAmount() + result.getRemaining());

                        result.setRemaining(0);     // Set the amount to 0 to make the user know there's nothing left

                        firstEmpty = -1; // End the loop

                    } else {

                        // We fill the slot up with a full stack of the offer
                        final StackableOffer fullStack = stackableOffer.clone();
                        fullStack.setAmount(stackableOffer.getMaxAmount());

                        set(firstEmpty, fullStack);
                        changes.put(firstEmpty, fullStack); // We keep track of what we changed

                        result.setCurrentAmount(result.getCurrentAmount() + fullStack.getMaxAmount());

                        result.setRemaining(-1 * overflow);

                        firstEmpty = getFirstEmpty();

                    }

                }

            }

        } else {

            final int empty = getFirstEmpty();

            if (empty != -1) {
                set(empty, offer.clone());
                changes.put(empty, offer.clone());
                result.setRemaining(0);
                result.setCurrentAmount(getOfClass(offer.getClass()).size() + 1);
            } else {
                result.setCurrentAmount(getOfClass(offer.getClass()).size());
            }

        }

        return result;

    }

    public OfferMutationResult remove(Offer offer) {

        final Map<Integer, Offer> changes = new HashMap<Integer, Offer>();

        final OfferMutationResult result = new OfferMutationResult(offer, OfferMutationResult.Type.ADDITION);
        result.setChanges(changes);

        if (offer instanceof StackableOffer) {

            final StackableOffer stackableOffer = (StackableOffer) offer;

            // TODO lowest amount first
            // First we try and remove from existing offers
            for (Map.Entry<Integer, Offer> entry : getOfType(stackableOffer.getType()).entrySet()) {

                if (result.getRemaining() > 0 && entry.getValue() instanceof StackableOffer) {

                    final int overflow = ((StackableOffer) entry.getValue()).remove(stackableOffer.getAmount());

                    if (overflow < result.getRemaining()) {    // We only changed something if the overflow is smaller then the amount

                        result.setRemaining(overflow);

                        if (((StackableOffer) entry.getValue()).getAmount() == 0) {    // If the amount of the changed offer is 0 we remove it
                            set(entry.getKey(), null);
                            changes.put(entry.getKey(), null);
                        } else {
                            changes.put(entry.getKey(), entry.getValue());
                        }

                    }

                }

                result.setCurrentAmount(result.getCurrentAmount() + ((entry.getValue() instanceof StackableOffer)? ((StackableOffer)entry.getValue()).getAmount() : 1));

            }

        } else {

            final TreeMap<Integer, Offer> current = getOfType(offer.getType());

            if (!current.isEmpty()) {
                set(current.lastKey(), null);
                changes.put(current.lastKey(), null);
                result.setRemaining(0);
                result.setCurrentAmount(current.size() - 1);
            } else {
                result.setCurrentAmount(current.size());
            }

        }

        return result;

    }


}
