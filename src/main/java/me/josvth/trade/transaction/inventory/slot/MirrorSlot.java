package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.offer.Offer;
import org.bukkit.Bukkit;

import java.util.Iterator;
import java.util.Set;

public class MirrorSlot extends Slot {

    private int offerIndex = 1;

    public MirrorSlot(int slot, TransactionHolder holder) {
        super(slot, holder);
    }

    public static void updateMirrors(TransactionHolder holder, boolean nextTick, int... offerIndex) {

        final Set<MirrorSlot> slots = holder.getSlotsOfType(MirrorSlot.class);

        final Iterator<MirrorSlot> iterator = slots.iterator();

        while (iterator.hasNext()) {
            final MirrorSlot slot = iterator.next();

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

    public static MirrorSlot deserialize(int slotID, TransactionHolder holder, SlotDescription description) {
        final MirrorSlot slot = new MirrorSlot(slotID, holder);
        slot.setOfferIndex(description.getConfiguration().getInt("offer-index", 0));
        return slot;
    }

    public int getOfferIndex() {
        return offerIndex;
    }

    public void setOfferIndex(int offerIndex) {
        this.offerIndex = offerIndex;
    }

    @Override
    public void update() {
        final Offer offer = holder.getOtherHolder().getOfferList().get(offerIndex);

        if (offer != null) {
            holder.getInventory().setItem(slot, offer.createMirrorItem(holder));
        } else {
            holder.getInventory().setItem(slot, null);
        }

    }


}
