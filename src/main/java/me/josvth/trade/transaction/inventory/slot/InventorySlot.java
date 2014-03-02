package me.josvth.trade.transaction.inventory.slot;


import me.josvth.trade.transaction.inventory.LayoutManager;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.offer.Offer;

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
        holder.getInventoryList().set(inventorySlot, contents);
    }

    public static InventorySlot deserialize(int slotID, TransactionHolder holder, SlotDescription description) {
        final InventorySlot slot = new InventorySlot(slotID, holder);
        slot.setInventorySlot(slotID - LayoutManager.PLAYER_INVENTORY_SIZE);
        return slot;
    }

}
