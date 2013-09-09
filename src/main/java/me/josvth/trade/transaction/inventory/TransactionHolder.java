package me.josvth.trade.transaction.inventory;

import me.josvth.trade.Trade;
import me.josvth.trade.goods.ItemTradeable;
import me.josvth.trade.goods.Tradeable;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.OfferList;
import me.josvth.trade.transaction.Trader;

import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;

public class TransactionHolder implements InventoryHolder {

	private final Trade plugin;

	private final Trader trader;

	private final Inventory inventory;

	private final Slot[] slots;	// All inventory slots ordered by id

	public TransactionHolder(Trade trade, Trader trader, int size, String title, Slot[] slots) {
		this.plugin = trade;
		this.trader = trader;
		this.inventory = Bukkit.createInventory(this, size, title);
		this.slots = slots;
	}

	public Trader getTrader() {
		return trader;
	}

	public OfferList getOffers() {
		return trader.getOffers();
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	// Event handling
	public void onClick(InventoryClickEvent event) {

		if (event.getAction() == InventoryAction.UNKNOWN || event.getAction() == InventoryAction.NOTHING) return;

		if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {  // TODO MAKE THIS WORK
			event.setCancelled(true);
			return;
		}

		if (event.getRawSlot() >= slots.length) { // Player is clicking lower inventory of InventoryView

			if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

				HashMap<Integer, Tradeable> remaining = trader.getOffers().add(new ItemTradeable(event.getCurrentItem())); // TODO Clone item here?

				if (remaining.get(0) != null)
					event.setCurrentItem(remaining.get(0).getDisplayItem());
				else
					event.setCurrentItem(null);

				event.setCancelled(true);

				// TODO Test this.

			}

		} else {	// Player is clicking upper inventory of InventoryView (our inventory)

			Slot slot = slots[event.getSlot()];

			if (slot != null) {
				if (slot.onClick(event))	// We let the slot handle the event and let it return if it needs updating
					Bukkit.getScheduler().runTask(plugin, new SlotUpdateTask(this, slot));
			} else {
				event.setCancelled(true);
			}

		}

	}

	public void onDrag(InventoryDragEvent event) {
		for (int slot : event.getInventorySlots())
			if (slots[slot] == null || !(slots[slot] instanceof TradeSlot))
				event.setCancelled(true);
		for (int slot : event.getInventorySlots())
			slots[slot].onDrag(event);

	}

	public void onClose(InventoryCloseEvent event) {

	}

}

