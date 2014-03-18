package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.offer.Offer;
import me.josvth.trade.transaction.offer.behaviour.ClickCategory;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class ContentSlot extends Slot {

    public ContentSlot(int slot, TransactionHolder holder) {
        super(slot, holder);
    }

    public abstract Offer getContents();

    public abstract void setContents(Offer contents);

    @Override
    public void onClick(InventoryClickEvent event) {

        if (holder.getCursorOffer() != null) {
            holder.getCursorOffer().onClick(event, this, ClickCategory.CURSOR);
            return;
        }

        final Offer offer = getContents();

        // If we have a offer on this slot we let the offer handle the event
        if (offer != null) {
            offer.onClick(event, this, ClickCategory.SLOT);
            return;
        }

        event.setCancelled(true);

    }

}
