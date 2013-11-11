package me.josvth.trade.offer;

import me.josvth.trade.transaction.Trader;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Offer {

	protected final OfferList list;
   	protected final int offerID;

	public Offer(OfferList list, int offerID) {
		this.list = list;
		this.offerID = offerID;
	}

	public <T extends Offer> T add(T tradeable) {
		return tradeable;
	}

	public <T extends Offer> T remove(T tradeable) {
		return tradeable;
	}

	public abstract ItemStack getDisplayItem();

	public ItemStack getOtherDisplayItem() {
		return getDisplayItem();
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

	public void onDrag(int slot, InventoryDragEvent event) {

	}

}
