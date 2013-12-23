package me.josvth.trade.transaction.inventory;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.action.CloseAction;
import me.josvth.trade.transaction.action.RefuseAction;
import me.josvth.trade.transaction.offer.*;
import me.josvth.trade.transaction.Trader;

import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Iterator;
import java.util.Map;

public class TransactionHolder implements InventoryHolder {

	private final Trade plugin;

	private final Trader trader;

	private Inventory inventory;

	public TransactionHolder(Trade trade, Trader trader) {
		this.plugin = trade;
		this.trader = trader;
	}

	public Trader getTrader() {
		return trader;
	}

	//TODO THIS METHOD IS USE A LOT!
	public Trader getOtherTrader() {
		return trader.getOtherTrader();
	}

	//TODO THIS METHOD IS USE A LOT!
	public TransactionHolder getOtherHolder() {
		return getOtherTrader().getHolder();
	}

	public Layout getLayout() {
		return trader.getLayout();
	}

	public Transaction getTransaction() {
		return trader.getTransaction();
	}

	public OfferList getOffers() {
		return trader.getOffers();
	}

	@Override
	public Inventory getInventory() {

		if (inventory == null) {
			inventory = Bukkit.createInventory(this, getLayout().getInventorySize(), getLayout().generateTitle(this));

			for (Slot slot : getLayout().getSlots()) {
				if (slot != null) slot.update(this);
			}
		}

		return inventory;

	}

	// Event handling
	public void onClick(InventoryClickEvent event) {

		if (event.getAction() == InventoryAction.UNKNOWN || event.getAction() == InventoryAction.NOTHING) return;

		if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {  // TODO MAKE THIS WORK
			event.setCancelled(true);
			return;
		}

		if (event.getRawSlot() >= getLayout().getInventorySize()) { // Player is clicking lower inventory of InventoryView

			if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

				final Iterator<Map.Entry<Integer, ItemOffer>> iterator = getOffers().getOfClass(ItemOffer.class).entrySet().iterator();

				// TODO make this not use getOffers().add()
//				final ItemOffer itemTradeable = new ItemOffer(event.getCurrentItem());
//
//				final HashMap<Integer, Offer> remaining = trader.getOffers().add(itemTradeable); // TODO Clone item here?
//
//				if (remaining.get(0) != null)
//					event.setCurrentItem(remaining.get(0).getDisplayItem());
//				else
//					event.setCurrentItem(null);

				// TODO Do this in the offer list?
				TradeSlot.updateTradeSlots(this, true);
				MirrorSlot.updateMirrors(this, true);

				event.setCancelled(true);

				// TODO Test this.

			}

		} else if (event.getRawSlot() != -999) {	// Player is clicking upper inventory of InventoryView (our inventory)

			Slot slot = getLayout().getSlots()[event.getSlot()];

			if (slot != null) {
				slot.onClick(event);
			} else {
				event.setCancelled(true);
			}

		}

	}

	public void onDrag(InventoryDragEvent event) {

		if (event.getInventory().getHolder() instanceof TransactionHolder) {

			for (int slotIndex : event.getInventorySlots() ) {
				// Cancel if we are dragging outside the inventory.
				if (slotIndex >= getLayout().getSlots().length) {
					event.setCancelled(true);
					return;
				}

				final Slot slot = getLayout().getSlots()[slotIndex];

				// Cancel if the slot is empty or not a trade slot
				if (slot == null || !(slot instanceof TradeSlot)) {
					event.setCancelled(true);
					return;
				}

				final Offer offer = ((TradeSlot) slot).getSlotContents(this);

				// Return if this offer does not support drag events
				if (offer != null && !offer.isDraggable()) {
					event.setCancelled(true);
					return;
				}

			}

			for (int s : event.getInventorySlots()) {
				final Slot slot = getLayout().getSlots()[s];
				slot.onDrag(event);
			}

		}
	}

	public void onClose(InventoryCloseEvent event) {
		if (getTransaction().getStage() == Transaction.Stage.IN_PROGRESS) {
			if (getTransaction().getManager().getOptions().allowInventoryClosing()) {
                new CloseAction(getTrader()).execute();
			} else {
                new RefuseAction(getTrader()).execute();
            }
		}
	}

    public boolean hasViewers() {
        return (getInventory().getViewers() != null && getInventory().getViewers().size() > 0);
    }
}

