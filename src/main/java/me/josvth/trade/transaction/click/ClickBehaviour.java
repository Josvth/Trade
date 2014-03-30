package me.josvth.trade.transaction.click;

import me.josvth.trade.transaction.offer.Offer;

public abstract class ClickBehaviour {

    public abstract boolean onClick(ClickContext context, Offer offer);

}
