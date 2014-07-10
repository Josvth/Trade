package me.josvth.trade.transaction.inventory;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.action.trader.status.CloseAction;
import me.josvth.trade.transaction.action.trader.status.RefuseAction;
import me.josvth.trade.transaction.inventory.interact.ClickContext;
import me.josvth.trade.transaction.inventory.interact.DragContext;
import me.josvth.trade.transaction.inventory.offer.ItemOffer;
import me.josvth.trade.transaction.inventory.offer.Offer;
import me.josvth.trade.transaction.inventory.offer.OfferList;
import me.josvth.trade.transaction.inventory.slot.OutsideSlot;
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
    private OutsideSlot outsideSlot;

    private Inventory inventory;

    private Offer cursorOffer;

    public TransactionHolder(Trade trade, Trader trader) {
        this.plugin = trade;

        this.trader = trader;
        this.inventoryList = new OfferList(trader, LayoutManager.PLAYER_INVENTORY_SIZE, OfferList.Type.INVENTORY);

        this.slots          = getLayout().createSlots(this);
        this.outsideSlot    = getLayout().getOutsideSlot(this);
    }

    public Slot[] getSlots() {
        return slots;
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

        if (event.getRawSlot() == -999) {
            slot = null;    // TODO implement outside slot
        } else {
            slot = slots[event.getRawSlot()];
        }

        final ClickContext context = new ClickContext(this, event, slot);

        // First we pass the event to the cursor if there is one
        if (getCursorOffer() != null) {
            getCursorOffer().onCursorClick(context);
        }

        // If the cursor didn't handle the event we pass it to the slot
        if (!context.isHandled() && slot != null) {
            slot.onClick(context);
        }

        // If the event is not handled we cancel it
        if (!context.isHandled()) {
            event.setCancelled(true);
        }

        // Display debug information
        if (getTransaction().getPlugin().isDebugMode()) {
            StringBuilder builder = new StringBuilder("[DEBUG]\n");
            builder.append("Event:   ").append("onClick\n");
            builder.append("Trader:  ").append(getTrader().getName()).append("\n");
            if (context.isHandled()) {
                builder.append("Handled: ").append(context.getExecutedBehaviour().getName()).append("\n");
            } else {
                builder.append("Handled: ").append(false).append("\n");
            }
            builder.append("Slot:    ").append(slot).append("\n");
            builder.append("Cursor:  ").append(cursorOffer).append("\n");
            builder.append("Offers:  ").append(trader.getOffers().toString());

            getTransaction().getPlugin().getLogger().info(builder.toString());
            getTrader().getPlayer().sendMessage(builder.toString());
        }

    }

    public void onDrag(InventoryDragEvent event) {

        if (!getTransaction().getManager().getOptions().getAllowDragging()) {
            event.setCancelled(true);
            return;
        }

        // We find all slot classes that were involved in this event
        final Set<Slot> slots = new HashSet<Slot>(event.getRawSlots().size());

        for (int slotID : event.getRawSlots()) {
            if (slotID == -999) {
                slots.add(null);    // TODO implement outside slot
            } else {
                slots.add(this.slots[slotID]);
            }
        }

        final DragContext context = new DragContext(this, event, slots);

        // Pass the event to the cursor
        if (getCursorOffer() != null) {
            if (getCursorOffer().onCursorDrag(context)) {
                return;
            }
        }

        // Go through all slots
        for (Slot slot : slots) {
            if (slot != null) {
                slot.onDrag(context);
            }
        }

        // Display debug information
        if (getTransaction().getPlugin().isDebugMode()) {
            StringBuilder builder = new StringBuilder("[DEBUG]\n");
            builder.append("Event:   ").append("onDrag\n");
            builder.append("Trader:  ").append(getTrader().getName()).append("\n");
            builder.append("Cursor:  ").append(cursorOffer).append("\n");
            builder.append("Offers:  ").append(trader.getOffers().toString());

            getTransaction().getPlugin().getLogger().info(builder.toString());
            getTrader().getPlayer().sendMessage(builder.toString());
        }

    }

    public void onClose(InventoryCloseEvent event) {
        if (getTransaction().getStage() == Transaction.Stage.IN_PROGRESS) {
            if (getTransaction().getManager().getOptions().getAllowInventoryClosing()) {
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

