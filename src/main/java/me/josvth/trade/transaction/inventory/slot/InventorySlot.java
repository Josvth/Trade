package me.josvth.trade.transaction.inventory.slot;


import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.inventory.LayoutManager;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.offer.Offer;
import org.bukkit.Bukkit;

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
        Bukkit.getScheduler().runTask(holder.getTransaction().getPlugin(), new SlotUpdateTask(this));
    }

    @Override
    public void update() {
        holder.getTrader().getPlayer().getInventory().setItem(getInventorySlot(), (getContents() == null)? null : getContents().createItem(holder));
    }

    public static InventorySlot deserialize(int slotID, TransactionHolder holder, SlotDescription description) {
        final InventorySlot slot = new InventorySlot(slotID, holder);
        slot.setInventorySlot(slotID - LayoutManager.PLAYER_INVENTORY_SIZE);
        return slot;
    }

}
