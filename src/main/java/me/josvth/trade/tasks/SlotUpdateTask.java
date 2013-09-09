package me.josvth.trade.tasks;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.Slot;

public class SlotUpdateTask implements Runnable {

	private final TransactionHolder holder;
	private final Slot slot;

	public SlotUpdateTask(TransactionHolder holder, Slot slot) {
		this.holder = holder;
		this.slot = slot;
	}

	@Override
	public void run() {
		slot.update(holder);
	}

}
