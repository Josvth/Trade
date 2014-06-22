package me.josvth.trade.transaction.inventory.offer;

import java.util.Map;

public class OfferMutationResult {

    private final Offer offer;
    private final Type type;

    double currentAmount = 0;
    double remaining = 0;
    Map<Integer, Offer> changes = null;

    public OfferMutationResult(Offer offer, Type type) {
        this.offer = offer;
        this.type = type;

        remaining =  offer.getAmount();

    }

    public Offer getOffer() {
        return offer;
    }

    public Type getType() {
        return type;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public boolean isComplete() {
        return remaining == 0;
    }

    public double getRemaining() {
        return remaining;
    }

    public void setRemaining(double remaining) {
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
