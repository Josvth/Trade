package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.action.trader.status.RefuseAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.interact.ClickContext;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.inventory.ItemStack;

public class RefuseSlot extends Slot {

    private ItemStack refuseItem;

    public RefuseSlot(int slot, TransactionHolder holder) {
        super(slot, holder);
    }

    public static RefuseSlot deserialize(int slotID, TransactionHolder holder, SlotDescription description) {
        final RefuseSlot slot = new RefuseSlot(slotID, holder);
        slot.setRefuseItem(ItemStackUtils.fromSection(description.getConfiguration().getConfigurationSection("refuse-item"), Trade.getInstance().getMessageManager()));
        return slot;
    }

    public ItemStack getRefuseItem() {
        return refuseItem;
    }

    public void setRefuseItem(ItemStack refuseItem) {
        this.refuseItem = refuseItem;
    }

    @Override
    public boolean onClick(ClickContext context) {

        new RefuseAction(holder.getTrader(), RefuseAction.Reason.BUTTON).execute();

        context.getEvent().setCancelled(true);

        return true;

    }

    @Override
    public void update() {
        setGUIItem(refuseItem);
    }

}
