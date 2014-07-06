package me.josvth.trade.transaction.inventory.interact;

import me.josvth.trade.transaction.inventory.offer.Offer;

public abstract class ClickBehaviour {

    public abstract boolean onClick(ClickContext context, Offer offer);

    public String getName() {
        return "UNKNOWN";
    }

}
