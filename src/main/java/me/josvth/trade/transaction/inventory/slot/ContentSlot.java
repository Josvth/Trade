package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.offer.Offer;

public abstract class ContentSlot extends Slot {

    public ContentSlot(int slot, TransactionHolder holder) {
        super(slot, holder);
    }

    public abstract Offer getContents();

    public abstract void setContents(Offer contents);

}
