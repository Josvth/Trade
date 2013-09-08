package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.TransactionLayout;

public class SlotInfo {

	private final TransactionLayout layout;

	private final SlotType type;

	private final int slot;

	public SlotInfo(TransactionLayout layout, SlotType type, int slot) {
	    this.layout = layout;
		this.type = type;
		this.slot = slot;
	}

	public Slot createSlot(TransactionHolder holder) {
		return null;
	}

	public SlotType getType() {
		return type;
	}

	public enum SlotType {
		TRADE,
		MIRROR,
		ACCEPT,
		STATUS,
		REFUSE,
		CLOSE;
	}
}
