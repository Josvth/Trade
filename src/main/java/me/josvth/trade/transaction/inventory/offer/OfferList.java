package me.josvth.trade.transaction.inventory.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

// TODO Cleanup offer list implementation
public class OfferList {

    private final Trader trader;
    private final Type type;

    private Offer[] offers;

    public OfferList(Trader trader, int size, Type type) {
        this.trader = trader;
        this.offers = new Offer[size];
        this.type = type;
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

        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < offers.length; i++) {
            builder.append("[").append(i).append("] ").append((offers[i] == null) ? null : offers[i].toString()).append(" ");
        }

        return builder.toString();

    }

    public <T> TreeMap<Integer, T> getOfClass(Class<T> clazz) {

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
            if (offers[i] != null && type.equalsIgnoreCase(offers[i].getType())) {
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

    public void grant(Trader trader, boolean nextTick) {
        for (Offer offer : offers) {
            if (offer != null) {
                offer.grant(trader, nextTick);
            }
        }
    }

    public OfferMutationResult add(Offer offer) {

        final Map<Integer, Offer> changes = new HashMap<Integer, Offer>();

        final OfferMutationResult result = new OfferMutationResult(offer, OfferMutationResult.Type.ADDITION);
        result.setChanges(changes);

        // First we try and fill up existing offers
        for (Map.Entry<Integer, Offer> entry : getOfType(offer.getType()).entrySet()) {

            if (offer.isSimilar(entry.getValue())) {

                if (result.getRemaining() > 0) {

                    final double overflow = entry.getValue().add(result.getRemaining());

                    // If we have added something change the remaining levels and add this slot to the changed indexes
                    if (overflow < result.getRemaining()) {
                        changes.put(entry.getKey(), entry.getValue()); // We keep track of what we changed
                        result.setRemaining(overflow);
                    }

                }

                result.setCurrentAmount(result.getCurrentAmount() + entry.getValue().getAmount()); // We count the total amount currently offered

            }

        }

        // Next put the remaining levels in empty offer slots
        if (result.getRemaining() > 0) {

            int firstEmpty = getFirstEmpty();

            while (result.getRemaining() > 0 && firstEmpty != -1) {

                final double overflow = result.getRemaining() - offer.getMaxAmount();

                if (overflow <= 0) {

                    final Offer clone = offer.clone();
                    clone.setAmount(result.getRemaining());

                    set(firstEmpty, clone);
                    changes.put(firstEmpty, clone); // We keep track of what we changed

                    result.setCurrentAmount(result.getCurrentAmount() + result.getRemaining());

                    result.setRemaining(0);     // Set the amount to 0 to make the user know there's nothing left

                    firstEmpty = -1; // End the loop

                } else {

                    // We fill the slot up with a full stack of the offer
                    final Offer fullStack = offer.clone();
                    fullStack.setAmount(offer.getMaxAmount());

                    set(firstEmpty, fullStack);
                    changes.put(firstEmpty, fullStack); // We keep track of what we changed

                    result.setCurrentAmount(result.getCurrentAmount() + fullStack.getMaxAmount());

                    result.setRemaining(overflow);

                    firstEmpty = getFirstEmpty();

                }

            }

        }


        return result;

    }

    public OfferMutationResult remove(Offer offer) {

        final Map<Integer, Offer> changes = new HashMap<Integer, Offer>();

        final OfferMutationResult result = new OfferMutationResult(offer, OfferMutationResult.Type.ADDITION);
        result.setChanges(changes);

        // TODO lowest amount first
        // First we try and remove from existing offers
        for (Map.Entry<Integer, Offer> entry : getOfType(offer.getType()).entrySet()) {

            if (offer.isSimilar(entry.getValue())) {
                if (result.getRemaining() > 0) {

                    final double overflow = entry.getValue().remove(result.getRemaining());

                    if (overflow < result.getRemaining()) {    // We only changed something if the overflow is smaller then the amount

                        result.setRemaining(overflow);

                        if (entry.getValue().getAmount() == 0) {    // If the amount of the changed offer is 0 we remove it
                            set(entry.getKey(), null);
                            changes.put(entry.getKey(), null);
                        } else {
                            changes.put(entry.getKey(), entry.getValue());
                        }

                    }

                }

                result.setCurrentAmount(result.getCurrentAmount() + entry.getValue().getAmount());
            }

        }

        return result;

    }

    public Type getType() {
        return type;
    }


    public enum Type {
        TRADE,
        INVENTORY
    }

}
