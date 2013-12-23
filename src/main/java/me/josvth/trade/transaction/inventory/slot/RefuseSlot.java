package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.action.RefuseAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class RefuseSlot extends Slot {

    private final ItemStack refuseItem;

    public RefuseSlot(int slot, ItemStack refuseItem) {
        super(slot);
        this.refuseItem = refuseItem;
    }

    @Override
    public void onClick(InventoryClickEvent event) {

        final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

        new RefuseAction(holder.getTrader(), RefuseAction.Method.BUTTON).execute();

        event.setCancelled(true);

    }

    @Override
    public void update(TransactionHolder holder) {
        setSlot(holder, refuseItem);
    }

}
