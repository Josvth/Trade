package me.josvth.trade.transaction.inventory.interact;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.offer.Offer;
import me.josvth.trade.transaction.inventory.offer.OfferList;
import me.josvth.trade.transaction.inventory.slot.Slot;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickContext {

    private final TransactionHolder holder;
    private final InventoryClickEvent event;
    private final Slot slot;

    private ClickBehaviour executedBehaviour;
    private boolean handled = false;

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

    public void setExecutedBehaviour(ClickBehaviour executed) {
        this.executedBehaviour = executed;
    }

    public ClickBehaviour getExecutedBehaviour() {
        return executedBehaviour;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public Trader getTrader() {return holder.getTrader(); }

    public Offer getCursorOffer() {
        return holder.getCursorOffer();
    }

    public OfferList getOffersList() {
        return holder.getOfferList();
    }

    public OfferList getInventoryList() {
        return holder.getInventoryList();
    }

    public void setCursorOffer(Offer offer, boolean update) {
        holder.setCursorOffer(offer, update);
    }

    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }


}
