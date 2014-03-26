package me.josvth.trade.transaction.action.trader.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.offer.Offer;
import me.josvth.trade.transaction.offer.OfferList;
import me.josvth.trade.transaction.offer.OfferMutationResult;
import me.josvth.trade.transaction.offer.StackableOffer;

/**
 * Action that handles addition and subtraction of an offer to a offer list of a trader
 */
public class ChangeOfferAction extends OfferAction {

    private Offer offer = null;
    private boolean add = true;

    private OfferMutationResult result;

    public ChangeOfferAction(Trader trader, OfferList list) {
        super(trader, list);
    }

    public ChangeOfferAction(Trader trader, OfferList list, Offer offer) {
        super(trader, list);
        this.offer = offer;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public void setAddition(boolean add) {
        this.add = add;
    }

    public boolean isAdd() {
        return add;
    }

    public int getCurrentAmount() {
        if (result == null) {
            throw new IllegalStateException("No result!");
        }
        return result.getCurrentAmount();
    }

    public boolean isComplete() {
        if (result == null) {
            throw new IllegalStateException("No result!");
        }
        return result.isComplete();
    }

    public int getRemaining() {
        if (result == null) {
            throw new IllegalStateException("No result!");
        }
        return result.getRemaining();
    }

    public final int getInitialAmount() {
        if (offer instanceof StackableOffer) {
            return ((StackableOffer) offer).getAmount();
        } else {
            return 1;
        }
    }

    public final int getChangedAmount() {
        if (result == null) {
            throw new IllegalStateException("No result!");
        }
        return getInitialAmount() - getRemaining();
    }

    @Override
    public void execute() {

        if (offer == null) {
            throw new IllegalStateException("Offer may not be null on execute.");
        }

        if (add) {
            result = list.add(offer);
        } else {
            result = list.remove(offer);
        }

        setChanges(result.getChanges());

        if (!getChanges().isEmpty()) {
            updateOffers();
            updateSlots();
        }

        denyAccepts();

    }





}
