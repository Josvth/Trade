package me.josvth.trade.transaction.inventory.interact;

import me.josvth.trade.transaction.inventory.offer.Offer;

public abstract class DragBehaviour {

    public abstract boolean onDrag(DragContext context, Offer offer);

}
