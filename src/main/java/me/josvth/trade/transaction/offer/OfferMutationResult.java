package me.josvth.trade.transaction.offer;

import java.util.Map;

public class OfferMutationResult {

    private final Offer offer;
    private final Type type;

    int currentAmount = 0;
    int remaining = 0;
    Map<Integer, Offer> changes = null;

    public OfferMutationResult(Offer offer, Type type) {
        this.offer = offer;
        this.type = type;

        if (offer instanceof StackableOffer) {
            remaining = ((StackableOffer) offer).getAmount();
        } else {
            remaining = 0;
        }
    }

    public Offer getOffer() {
        return offer;
    }

    public Type getType() {
        return type;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }

    public boolean isComplete() {
        return remaining == 0;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public Map<Integer, Offer> getChanges() {
        return changes;
    }

    public void setChanges(Map<Integer, Offer> changes) {
        this.changes = changes;
    }

    public enum Type {
        ADDITION,
        REMOVAL;
    }
}
