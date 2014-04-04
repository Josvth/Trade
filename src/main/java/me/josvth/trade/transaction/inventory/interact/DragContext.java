package me.josvth.trade.transaction.inventory.interact;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.offer.Offer;
import me.josvth.trade.transaction.inventory.offer.OfferList;
import me.josvth.trade.transaction.inventory.slot.Slot;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Set;

public class DragContext {

    private final TransactionHolder holder;
    private final InventoryDragEvent event;
    private final Set<Slot> slots;

    public DragContext(TransactionHolder holder, InventoryDragEvent event, Set<Slot> slots ) {
        this.holder = holder;
        this.event = event;
        this.slots = slots;
    }

    public TransactionHolder getHolder() {
        return holder;
    }

    public InventoryDragEvent getEvent() {
        return event;
    }

    public Set<Slot> getSlots() {
        return slots;
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
