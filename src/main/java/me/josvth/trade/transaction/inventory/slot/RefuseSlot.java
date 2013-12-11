package me.josvth.trade.transaction.inventory.slot;

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

//        if (!holder.getTrader().hasAccepted()) {
//
//            holder.getTrader().setAccepted(true);
//            // TODO send accepted message
//
//            if (holder.getOtherTrader().hasAccepted()) {
//                holder.getOtherTrader().setAccepted(false);
//                // TODO send cancel accept message
//
//                AcceptSlot.updateAcceptSlots(holder.getOtherHolder(), true);
//                StatusSlot.updateStatusSlots(holder, true);
//
//            } else {
//                // TODO send other accepted message
//            }
//
//            AcceptSlot.updateAcceptSlots(holder, true);
//            StatusSlot.updateStatusSlots(holder.getOtherHolder(), true);
//
//        }

        event.setCancelled(true);


    }

	@Override
	public void update(TransactionHolder holder) {
		setSlot(holder, refuseItem);
	}

}
