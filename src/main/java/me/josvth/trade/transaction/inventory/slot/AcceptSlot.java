package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.trader.status.AcceptAction;
import me.josvth.trade.transaction.action.trader.status.DenyAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.interact.ClickContext;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class AcceptSlot extends Slot {

    private ItemStack acceptItem;
    private ItemStack acceptedItem;

    public AcceptSlot(int slot, TransactionHolder holder) {
        super(slot, holder);
    }

    public static void updateAcceptSlots(TransactionHolder holder, boolean nextTick) {

        final Set<AcceptSlot> slots = holder.getSlotsOfType(AcceptSlot.class);

        if (!nextTick) {
            for (Slot slot : slots) {
                slot.update();
            }
        } else if (!slots.isEmpty()) {
            Bukkit.getScheduler().runTask(Trade.getInstance(), new SlotUpdateTask(slots));
        }

    }

    public static AcceptSlot deserialize(int slotID, TransactionHolder holder, SlotDescription description) {
        final AcceptSlot slot = new AcceptSlot(slotID, holder);
        slot.setAcceptItem(ItemStackUtils.fromSection(description.getConfiguration().getConfigurationSection("accept-item"), Trade.getInstance().getMessageManager()));
        slot.setAcceptedItem(ItemStackUtils.fromSection(description.getConfiguration().getConfigurationSection("accepted-item"), Trade.getInstance().getMessageManager()));
        return slot;
    }

    public ItemStack getAcceptItem() {
        return acceptItem;
    }

    public void setAcceptItem(ItemStack acceptItem) {
        this.acceptItem = acceptItem;
    }

    public ItemStack getAcceptedItem() {
        return acceptedItem;
    }

    public void setAcceptedItem(ItemStack acceptedItem) {
        this.acceptedItem = acceptedItem;
    }

    @Override
    public boolean onClick(ClickContext context) {

        final Trader trader = holder.getTrader();

        if (trader.hasAccepted()) {
            new DenyAction(trader, DenyAction.Reason.BUTTON).execute();
        } else {
            new AcceptAction(trader, AcceptAction.Reason.BUTTON).execute();
        }

        context.getEvent().setCancelled(true);

        return true;

    }

    public void update() {

        if (holder.getTrader().hasAccepted()) {
            setGUIItem(acceptedItem);
        } else {
            setGUIItem(acceptItem);
        }

    }

}
