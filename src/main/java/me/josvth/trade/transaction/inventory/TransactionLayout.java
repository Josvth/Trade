package me.josvth.trade.transaction.inventory;

import me.josvth.trade.transaction.inventory.slot.Slot;

public class TransactionLayout {

	private final Slot[] slots;

	private final int rows;

	private int offerSize;

	public TransactionLayout(int rows, Slot[] slots) {
		this.rows = rows;
		this.slots = slots;
	}

	public int getRows() {
		return rows;
	}

	public int getSlotInfo() {
		return rows * 9;
	}

	public int getOfferSize() {
		return offerSize;
	}

	public Slot[] getSlots() {
		return slots;
	}

}
