package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.action.trader.offer.SetOfferAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.offer.Offer;
import org.bukkit.Bukkit;

import java.util.Iterator;
import java.util.Set;

public class TradeSlot extends ContentSlot {

    private int offerIndex;

    public TradeSlot(int slot, TransactionHolder holder) {
        super(slot, holder);
    }

    public int getOfferIndex() {
        return offerIndex;
    }

    public void setOfferIndex(int offerIndex) {
        this.offerIndex = offerIndex;
    }

    @Override
    public void setContents(Offer offer) {
        SetOfferAction offerAction = new SetOfferAction(holder.getTrader(), holder.getOfferList());
        offerAction.setOffer(getOfferIndex(), offer);
        offerAction.execute();
    }

    @Override
    public Offer getContents() {
        return holder.getOfferList().get(getOfferIndex());
    }

    @Override
    public void update() {

        final Offer offer = getContents();

        if (offer != null) {
            holder.getInventory().setItem(slot, offer.createItem(holder));
        } else {
            holder.getInventory().setItem(slot, null);
        }

    }

    public static void updateTradeSlots(TransactionHolder holder, boolean nextTick, int... offerIndex) {

        final Set<TradeSlot> slots = holder.getSlotsOfType(TradeSlot.class);

        final Iterator<TradeSlot> iterator = slots.iterator();

        while (iterator.hasNext()) {

            final TradeSlot slot = iterator.next();

            boolean notUpdated = true;
            for (int i = 0; i < offerIndex.length && notUpdated; i++) {
                if (slot.getOfferIndex() == offerIndex[i]) {
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

    public static TradeSlot deserialize(int slotID, TransactionHolder holder, SlotDescription description) {
        final TradeSlot slot = new TradeSlot(slotID, holder);
        slot.setOfferIndex(description.getConfiguration().getInt("offer-index", 0));
        return slot;
    }

}
