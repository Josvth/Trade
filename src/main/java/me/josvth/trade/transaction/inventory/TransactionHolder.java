package me.josvth.trade.transaction.inventory;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.action.trader.status.CloseAction;
import me.josvth.trade.transaction.action.trader.status.RefuseAction;
import me.josvth.trade.transaction.offer.*;
import me.josvth.trade.transaction.Trader;

import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.util.ItemStackUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

    private Offer cursorOffer;

    public TransactionHolder(Trade trade, Trader trader) {
        this.plugin = trade;
        this.trader = trader;
    }

    public Trader getTrader() {
        return trader;
    }

    public Trader getOtherTrader() {
        return trader.getOtherTrader();
    }

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

    public Offer getCursorOffer() {
        return cursorOffer;
    }

    public void setCursorOffer(final Offer offer) {

        this.cursorOffer = offer;

        final TransactionHolder holder = this;

        Bukkit.getScheduler().runTask(getTransaction().getPlugin(), new Runnable() {
            @Override
            public void run() {
                getTrader().getPlayer().setItemOnCursor((offer == null)? null : offer.createItem(holder));
            }
        });

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

        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {  // TODO MAKE THIS WORK
            event.setCancelled(true);
            return;
        }

        if (event.getRawSlot() < getLayout().getInventorySize() && event.getRawSlot() != -999) {

            // If the player clicked a transaction slot we let that slot handle the event
            final Slot clickedSlot = getLayout().getSlots()[event.getSlot()];

            if (clickedSlot != null) {
                clickedSlot.onClick(event);
            } else {
                event.setCancelled(true);
            }

            ((Player) event.getWhoClicked()).sendMessage("Cursor: " + getCursorOffer());

            return;

        }

        // If we have a cursor offer we let that handle the event
        if (getCursorOffer() != null) {
            getCursorOffer().onCursorClick(event, null);

            ((Player) event.getWhoClicked()).sendMessage("Cursor: " + getCursorOffer());

            return;
        }

        // If we didn't click a slot or have a cursor offer we handle the event here
        switch (event.getAction()) {
            case PICKUP_ALL:
                setCursorOffer(new ItemOffer(event.getCurrentItem().clone()));
                break;
            case PICKUP_HALF:
                setCursorOffer(new ItemOffer(ItemStackUtils.split(event.getCurrentItem())[0]));
            case NOTHING:
                break;
            default:
                event.setCancelled(true);
                throw new IllegalStateException("UNHANDLED ACTION: " + event.getAction().name());
        }

        ((Player) event.getWhoClicked()).sendMessage("Cursor: " + getCursorOffer());

    }

    public void onDrag(InventoryDragEvent event) {

        event.setCancelled(true);

//        // First we check in which parts of the inventory the player tries to drag items
//        boolean upper = false;
//        boolean lower = false;
//
//        final Iterator<Integer> iterator = event.getRawSlots().iterator();
//        while (!(upper && lower) && iterator.hasNext()) {
//            if (iterator.next() >= getLayout().getInventorySize()) {
//                lower = true;
//            } else {
//                upper = true;
//            }
//        }
//
//        // We don't support dragging in both upper and lower inventories
//        if (upper && lower) {
//            event.setCancelled(true);
//            return;
//        }
//
//        // However dragging in the lower part is just fine
//        if (lower) {
//            return;
//        }
//
//        for (int slotIndex : event.getInventorySlots() ) {
//
//            final Slot slot = getLayout().getSlots()[slotIndex];
//
//            // Cancel if the slot is empty or not a trade slot
//            if (slot == null || !(slot instanceof TradeSlot)) {
//                event.setCancelled(true);
//                return;
//            }
//
//            final Offer offer = ((TradeSlot) slot).getSlotContents(this);
//
//            // Return if this offer does not support drag events
//            if (offer != null && !offer.isDraggable()) {
//                event.setCancelled(true);
//                return;
//            }
//
//        }
//
//        for (int s : event.getInventorySlots()) {
//            final Slot slot = getLayout().getSlots()[s];
//            slot.onDrag(event);
//        }

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

    public Economy getEconomy() {
        return getTransaction().getPlugin().getEconomy();
    }


}

