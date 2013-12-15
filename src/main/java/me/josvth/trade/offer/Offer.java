package me.josvth.trade.offer;

import me.josvth.trade.offer.description.OfferDescription;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Offer {

	protected final OfferList list;
   	protected final int offerIndex;

	public Offer(OfferList list, int offerIndex) {
		this.list = list;
		this.offerIndex = offerIndex;
	}

    public OfferDescription<? extends Offer> getDescription() {
        return list.getTrader().getLayout().getOfferDescription(this.getClass());
    }

    public abstract ItemStack createItem();

    public abstract ItemStack createMirror(TransactionHolder holder);

	public double getAmount() {
		return 0;
	}

	public boolean isFull() {
		return false;
	}

	public boolean isWorthless() {
		return getAmount() == 0.0;
	}

	public abstract void grant(Trader trader);

	// Event handling
	public void onClick(InventoryClickEvent event) {

	}

	public boolean isDraggable() {
		return false;
	}

	public void onDrag(int slot, InventoryDragEvent event) {

	}

}
