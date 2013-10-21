package me.josvth.trade.transaction.inventory;

import me.josvth.trade.transaction.inventory.slot.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class TransactionLayout {

	private final Slot[] slots;

	private final int rows;

	private int offerSize = 4;

	public TransactionLayout(int rows, Slot[] slots) {
		this.rows = rows;
		this.slots = slots;
	}

	public int getRows() {
		return rows;
	}

	public int getOfferSize() {
		return offerSize;
	}

	public Slot[] getSlots() {
		return slots;
	}

	public int getInventorySize() {
		return rows * 9;
	}

	public String generateTitle(TransactionHolder holder) {
		return "This is a test title.";
	}

	public <T extends Slot> Set<T> getSlotsOfType(Class<T> clazz) {

		final Set<T> set = new HashSet<T>();

		for (Slot slot : slots) {
			if (clazz.isInstance(slot)) {
				set.add((T) slot);
			}
		}

		return set;

	}
}
