package me.josvth.trade.offer;

import me.josvth.trade.offer.description.OfferDescription;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Offer <T extends OfferDescription> {

	protected final OfferList list;
   	protected final int offerIndex;

    protected final T description;

	public Offer(OfferList list, int offerIndex, T description) {
		this.list = list;
		this.offerIndex = offerIndex;
        this.description = description;
	}

    public T getDescription() {
        return description;
    }

	public ItemStack getDisplayItem() {
        return getDescription().createItem(this);
    }

	public ItemStack getMirrorItem(TransactionHolder holder) {
        return getDescription().createMirrorItem(this, holder);
    }

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
