package me.josvth.trade.transaction.offer;

import java.util.TreeMap;

public class OfferResponse {

    private final Offer offer;

    private final TreeMap<Integer, Offer> changedSlots = new TreeMap<Integer, Offer>();

    private int currentAmount = 0;

    public OfferResponse(Offer offer) {
        this.offer = offer;
    }

    public Offer getOffer() {
        return offer;
    }

    public TreeMap<Integer, Offer> getChangedSlots() {
        return changedSlots;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }

}
