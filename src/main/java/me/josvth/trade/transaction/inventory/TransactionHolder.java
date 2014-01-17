package me.josvth.trade.transaction.inventory;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.action.trader.offer.ChangeOfferAction;
import me.josvth.trade.transaction.action.trader.status.CloseAction;
import me.josvth.trade.transaction.action.trader.status.RefuseAction;
import me.josvth.trade.transaction.offer.*;
import me.josvth.trade.transaction.Trader;

import me.josvth.trade.transaction.Transaction;
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

                new ChangeOfferAction(getTrader(), ItemOffer.create(trader, event.getCurrentItem().clone()), true).execute();

                event.setCancelled(true);

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

        // First we check in which parts of the inventory the player tries to drag items
        boolean upper = false;
        boolean lower = false;

        final Iterator<Integer> iterator = event.getRawSlots().iterator();
        while (!(upper && lower) && iterator.hasNext()) {
            if (iterator.next() >= getLayout().getInventorySize()) {
                lower = true;
            } else {
                upper = true;
            }
        }

        // We don't support dragging in both upper and lower inventories
        if (upper && lower) {
            event.setCancelled(true);
            return;
        }

        // However dragging in the lower part is just fine
        if (lower) {
            return;
        }

        for (int slotIndex : event.getInventorySlots() ) {

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

    public void onClose(InventoryCloseEvent event) {
        if (getTransaction().getStage() == Transaction.Stage.IN_PROGRESS) {
            if (getTransaction().getManager().getOptions().allowInventoryClosing()) {
                new CloseAction(getTrader()).execute();
            } else {
                new RefuseAction(getTrader(), RefuseAction.Reason.CLOSE).execute();
            }
        }
    }

    public boolean hasViewers() {
        return (getInventory().getViewers() != null && getInventory().getViewers().size() > 0);
    }
}

