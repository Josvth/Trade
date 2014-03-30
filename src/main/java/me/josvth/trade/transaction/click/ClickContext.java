package me.josvth.trade.transaction.click;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.offer.Offer;
import me.josvth.trade.transaction.offer.OfferList;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickContext {

    private final TransactionHolder holder;
    private final InventoryClickEvent event;
    private final Slot slot;

    public ClickContext(TransactionHolder holder, InventoryClickEvent event, Slot slot) {
        this.holder = holder;
        this.event = event;
        this.slot = slot;
    }

    public TransactionHolder getHolder() {
        return holder;
    }

    public InventoryClickEvent getEvent() {
        return event;
    }

    public Slot getSlot() {
        return slot;
    }

    public Offer getCursorOffer() {
        return holder.getCursorOffer();
    }

    public OfferList getOffersList() {
        return holder.getOfferList();
    }

    public OfferList getInventoryList() {
        return holder.getInventoryList();
    }
}
