package me.josvth.trade.transaction.inventory.slot;


import me.josvth.trade.Trade;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.action.trader.offer.SetOfferAction;
import me.josvth.trade.transaction.inventory.LayoutManager;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.offer.Offer;
import org.bukkit.Bukkit;

import java.util.Iterator;
import java.util.Set;

public class InventorySlot extends ContentSlot {

    private int inventorySlot = 0;

    public InventorySlot(int slot, TransactionHolder holder) {
        super(slot, holder);
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    public void setInventorySlot(int inventorySlot) {
        this.inventorySlot = inventorySlot;
    }

    @Override
    public Offer getContents() {
        return holder.getInventoryList().get(inventorySlot);
    }

    @Override
    public void setContents(Offer contents) {
        SetOfferAction offerAction = new SetOfferAction(holder.getTrader(), holder.getInventoryList());
        offerAction.setOffer(getInventorySlot(), contents);
        offerAction.execute();
    }

    @Override
    public void update() {
        holder.getTrader().getPlayer().getInventory().setItem(getInventorySlot(), (getContents() == null)? null : getContents().createItem(holder));
    }

    public static void updateInventorySlots(TransactionHolder holder, boolean nextTick, int... inventorySlot) {

        final Set<InventorySlot> slots = holder.getSlotsOfType(InventorySlot.class);

        final Iterator<InventorySlot> iterator = slots.iterator();

        while (iterator.hasNext()) {

            final InventorySlot slot = iterator.next();

            boolean notUpdated = true;
            for (int i = 0; i < inventorySlot.length && notUpdated; i++) {
                if (slot.getInventorySlot() == inventorySlot[i]) {
                    if (!nextTick) {
                        slot.update();
                    }
                    notUpdated = false;
                }
            }

            if (notUpdated) {
                iterator.remove();
            }

        }

        if (nextTick && !slots.isEmpty()) {
            Bukkit.getScheduler().runTask(Trade.getInstance(), new SlotUpdateTask(slots));
        }

    }

    public static InventorySlot deserialize(int slotID, TransactionHolder holder, SlotDescription description) {
        final InventorySlot slot = new InventorySlot(slotID, holder);
        slot.setInventorySlot(slotID - LayoutManager.PLAYER_INVENTORY_SIZE);
        return slot;
    }

}
