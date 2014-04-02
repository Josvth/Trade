package me.josvth.trade.transaction.inventory;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.action.trader.status.CloseAction;
import me.josvth.trade.transaction.action.trader.status.RefuseAction;
import me.josvth.trade.transaction.inventory.click.ClickContext;
import me.josvth.trade.transaction.inventory.offer.ItemOffer;
import me.josvth.trade.transaction.inventory.offer.Offer;
import me.josvth.trade.transaction.inventory.offer.OfferList;
import me.josvth.trade.transaction.inventory.slot.Slot;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class TransactionHolder implements InventoryHolder {

    private final Trade plugin;

    private final Trader trader;
    private final OfferList inventoryList;

    private Slot[] slots;

    private Inventory inventory;

    private Offer cursorOffer;

    public TransactionHolder(Trade trade, Trader trader) {
        this.plugin = trade;

        this.trader = trader;
        this.inventoryList = new OfferList(trader, LayoutManager.PLAYER_INVENTORY_SIZE, OfferList.Type.INVENTORY);

        this.slots = getLayout().createSlots(this);
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

    public Offer getCursorOffer() {
        return cursorOffer;
    }

    public void setCursorOffer(Offer offer, boolean update) {
        this.cursorOffer = offer;
        if (update) {
            updateCursorOffer();
        }
    }

    public void updateCursorOffer() {
        final TransactionHolder holder = this;
        Bukkit.getScheduler().runTask(getTransaction().getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (getCursorOffer() == null) {
                    getTrader().getPlayer().setItemOnCursor(null);
                } else {
                    final ItemStack cursorItem = getCursorOffer().createItem(holder);
                    if (cursorItem == null) {
                        setCursorOffer(null, false);
                    }
                    getTrader().getPlayer().setItemOnCursor(cursorItem);
                }
            }
        });
    }

    public void updateAllSlots() {
        for (Slot slot : slots) {
            if (slot != null) {
                slot.update();
            }
        }
        updateCursorOffer();
    }

    public void updateInventoryList() {
        for (int i = 0; i < trader.getPlayer().getInventory().getSize(); i++) {
            final ItemStack itemStack = trader.getPlayer().getInventory().getItem(i);
            if (itemStack != null) {
                inventoryList.set(i, new ItemOffer(itemStack));
            }
        }
    }

    @Override
    public Inventory getInventory() {
        if (inventory == null) {
            inventory = Bukkit.createInventory(this, getLayout().getGuiSize(), getLayout().generateTitle(this));
        }
        return inventory;
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

    public OfferList getOfferList() {
        return trader.getOffers();
    }

    public OfferList getInventoryList() {
        return inventoryList;
    }

    // Event handling
    public void onClick(InventoryClickEvent event) {

        final Slot slot;

        if (event.getRawSlot() >= slots.length || event.getRawSlot() == -999) {
            slot = null;
        } else {
            slot = slots[event.getRawSlot()];
        }

        final ClickContext context = new ClickContext(this, event, slot);

        if (getCursorOffer() != null) {
            if (getCursorOffer().onCursorClick(context)) {
                return;
            }
        }

        if (slot == null) {
            event.setCancelled(true);
        } else {
            slot.onClick(event);
        }

    }

    public void onDrag(InventoryDragEvent event) {

        event.setCancelled(true);

//        // First we check in which parts of the inventory the player tries to drag items
//        boolean upper = false;
//        boolean lower = false;
//
//        final Iterator<Integer> iterator = event.getRawSlots().iterator();
//        while (!(upper && lower) && iterator.hasNext()) {
//            if (iterator.next() >= getLayout().getGuiSize()) {
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

