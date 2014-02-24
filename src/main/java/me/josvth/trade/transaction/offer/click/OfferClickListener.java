package me.josvth.trade.transaction.offer.click;

import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.offer.Offer;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class OfferClickListener {

    public abstract boolean onClick(InventoryClickEvent event, Slot slot, Offer offer);

}
