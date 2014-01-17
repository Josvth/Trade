package me.josvth.trade.transaction.action.trader.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.ActionProvoker;
import me.josvth.trade.transaction.offer.Offer;
import me.josvth.trade.transaction.offer.OfferList;
import me.josvth.trade.transaction.offer.StackableOffer;

import java.util.*;

/**
 * Action that handles addition and subtraction of an offer to a offer list of a trader
 */
public class ChangeOfferAction extends OfferAction {

    private final Offer offer;
    private final boolean add;

    private final TreeMap<Integer, Offer> changes = new TreeMap<Integer, Offer>();
    private int currentAmount = 0;
    private boolean complete = false;
    private int remaining = 0;

    public ChangeOfferAction(ActionProvoker provoker, Trader trader, Offer offer, boolean add) {
        super(provoker, trader);
        this.offer = offer;
        this.add = add;
    }

    @Override
    public Map<Integer, ? extends Offer> getChanges() {
        return changes;
    }

    public final Offer getOffer() {
        return offer;
    }

    public final boolean isAdd() {
        return add;
    }

    public final int getCurrentAmount() {
        return currentAmount;
    }

    public final boolean isComplete() {
        return complete;
    }

    public final int getRemaining() {
        return remaining;
    }

    public final int getInitialAmount() {
        if (offer instanceof StackableOffer) {
            return ((StackableOffer) offer).getAmount();
        } else {
            return 1;
        }
    }

    public final int getChangedAmount() {
        return getInitialAmount() - remaining;
    }

    @Override
    public void execute() {

        if (add) {

            if (offer instanceof StackableOffer) {
                addStackable((StackableOffer) offer);
            } else {
                add(offer);
            }

        } else {

            if (offer instanceof StackableOffer) {
                removeStackable((StackableOffer) offer);
            } else {
                remove(offer);
            }

        }

        // Execute super if we changed something
        if (!changes.isEmpty()) {
            super.execute();
        }

    }


    private void addStackable(StackableOffer offer) {

        final OfferList offerList = getTrader().getOffers();

        // Set result variables
        currentAmount = 0;
        complete = false;
        remaining = offer.getAmount();

        // First we try and fill up existing offers
        for (Map.Entry<Integer, ? extends StackableOffer> entry : offerList.getOfClass(offer.getClass()).entrySet()) {

            if (remaining > 0) {

                final int overflow = entry.getValue().add(remaining);

                // If we have added something change the remaining levels and add this slot to the changed indexes
                if (overflow < remaining) {
                    changes.put(entry.getKey(), entry.getValue()); // We keep track of what we changed
                    remaining = overflow;
                }

            }

            currentAmount += entry.getValue().getAmount(); // We count the total amount currently offered

        }

        // Next put the remaining levels in empty offer slots
        if (remaining > 0) {

            int firstEmpty = offerList.getFirstEmpty();

            while (remaining > 0 && firstEmpty != -1) {

                final int overflow = remaining - offer.getMaxAmount();

                if (overflow <= 0) {

                    final StackableOffer clone = offer.clone();

                    offerList.set(firstEmpty, clone);
                    changes.put(firstEmpty, clone); // We keep track of what we changed

                    currentAmount += remaining;

                    remaining = 0; // Set the amount to 0 to make the user know there's nothing left
                    complete = true; // Set our complete boolean

                    firstEmpty = -1; // End the loop

                } else {

                    // We fill the slot up with a full stack of the offer
                    final StackableOffer fullStack = offer.clone();
                    fullStack.setAmount(offer.getMaxAmount());

                    offerList.set(firstEmpty, fullStack);
                    changes.put(firstEmpty, fullStack); // We keep track of what we changed

                    currentAmount += fullStack.getMaxAmount();

                    remaining = -1 * overflow;

                    firstEmpty = offerList.getFirstEmpty();

                }

            }

        }

        complete = remaining == 0; // Set our complete boolean

    }

    private void add(Offer offer) {

        final OfferList offerList = getTrader().getOffers();

        currentAmount = offerList.getOfClass(offer.getClass()).size();

        final int empty = offerList.getFirstEmpty();

        if (empty != -1) {
            offerList.set(empty, offer.clone());
            changes.put(empty, offer.clone());
            complete = true;
        }

    }

    private void remove(Offer offer) {

        final OfferList offerList = getTrader().getOffers();

        final TreeMap<Integer, Offer> current = offerList.getOfType(offer.getType());

        if (!current.isEmpty()) {
            offerList.set(current.lastKey(), null);
            changes.put(current.lastKey(), null);
            complete = true;
        }

    }

    private void removeStackable(StackableOffer stackableOffer) {

        final OfferList offerList = getTrader().getOffers();

        // Set result variables
        currentAmount = 0;
        complete = false;
        remaining = stackableOffer.getAmount();

        // TODO lowest amount first
        // First we try and remove from existing offers
        for (Map.Entry<Integer, ? extends StackableOffer> entry : offerList.getOfClass(stackableOffer.getClass()).entrySet()) {

            if (remaining > 0) {

                final int overflow = entry.getValue().remove(stackableOffer.getAmount());

                if (overflow < remaining) {    // We only changed something if the overflow is smaller then the amount

                    remaining = overflow;

                    if (entry.getValue().getAmount() == 0) {    // If the amount of the changed offer is 0 we remove it
                        offerList.set(entry.getKey(), null);
                        changes.put(entry.getKey(), null);
                    } else {
                        changes.put(entry.getKey(), entry.getValue());
                    }

                }

            }

            currentAmount += entry.getValue().getAmount();

        }

        // We set our boolean if we removed everything
        complete = remaining == 0;

    }


}
