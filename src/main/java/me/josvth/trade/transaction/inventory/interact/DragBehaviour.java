package me.josvth.trade.transaction.inventory.interact;

import me.josvth.trade.transaction.inventory.offer.Offer;
import me.josvth.trade.transaction.inventory.slot.Slot;

public abstract class DragBehaviour {

    public abstract boolean onDrag(DragContext context, Slot slot, Offer offer);

}
