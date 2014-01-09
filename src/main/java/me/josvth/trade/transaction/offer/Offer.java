package me.josvth.trade.transaction.offer;

import me.josvth.trade.transaction.offer.description.OfferDescription;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Offer {

    public OfferDescription<? extends Offer> getDescription(Trader trader) {
        return trader.getLayout().getOfferDescription(this.getClass());
    }

    public abstract String getType();

    public abstract ItemStack createItem(TransactionHolder holder);

    public abstract ItemStack createMirror(TransactionHolder holder);

	public abstract void grant(Trader trader);

	// Event handling
	public void onClick(InventoryClickEvent event, int offerIndex) {

	}

	public boolean isDraggable() {
		return false;
	}

    public void onDrag(InventoryDragEvent event, int offerIndex, int slotIndex) {

	}

}
