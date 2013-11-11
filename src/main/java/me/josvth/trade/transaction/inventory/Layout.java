package me.josvth.trade.transaction.inventory;

import me.josvth.trade.offer.*;
import me.josvth.trade.offer.ExperienceOffer;
import me.josvth.trade.offer.Offer;
import me.josvth.trade.transaction.inventory.slot.*;

import java.util.HashSet;
import java.util.Set;

public class Layout {

	private final String name;
	private final int rows;

	private Slot[] slots;
	private int offerSize = 4;

	private int priority = -1;
	private String permission = null;
	private boolean shared = true;

	public Layout(String name, int rows, int offerSize) {
		this.name = name;
		this.rows = rows;
		this.offerSize = offerSize;
		this.slots = new Slot[rows * 9];
	}

	public String getName() {
		return name;
	}

	public int getRows() {
		return rows;
	}

	public int getOfferSize() {
		return offerSize;
	}

	public void setOfferSize(int size) {
		this.offerSize = size;
	}

	public Slot[] getSlots() {
		return slots;
	}

	public void setSlots(Slot[] slots) {
		if (slots.length != rows*9) {
			throw new IllegalArgumentException("Array length ("+ slots.length+") does not match layout size ("+rows*9+").");
		}
		this.slots = slots;
	}
	public int getInventorySize() {
		return rows * 9;
	}

	public String generateTitle(TransactionHolder holder) {
		return "This is a test title.";
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
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

	public <T extends Offer> T createTradeable(Class<T> clazz) {

		if (ItemOffer.class.isAssignableFrom(clazz)) {
			return (T) new ItemOffer();
		} else if (ExperienceOffer.class.isAssignableFrom(clazz)) {
			return (T) new ExperienceOffer();
		} else if (MoneyOffer.class.isAssignableFrom(clazz)) {
			return (T) new MoneyOffer();
		}

		throw new IllegalArgumentException("Class is not supported.");

	}
}
