package me.josvth.trade.transaction.inventory.slot;

import org.bukkit.inventory.ItemStack;

public class StatusSlot extends Slot{

	private final ItemStack consideringItem;
	private final ItemStack acceptedItem;

	public StatusSlot(int slot, ItemStack consideringItem, ItemStack acceptedItem) {
		super(slot);
		this.consideringItem = consideringItem;
		this.acceptedItem = acceptedItem;
	}


}
