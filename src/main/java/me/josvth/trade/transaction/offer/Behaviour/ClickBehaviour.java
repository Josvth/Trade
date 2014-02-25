package me.josvth.trade.transaction.offer.behaviour;

import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.offer.Offer;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class ClickBehaviour {

    public abstract boolean onClick(InventoryClickEvent event, Slot slot, Offer offer);

}
