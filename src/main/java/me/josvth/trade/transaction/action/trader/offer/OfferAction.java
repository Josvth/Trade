package me.josvth.trade.transaction.action.trader.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.trader.TraderAction;
import me.josvth.trade.transaction.action.trader.status.DenyAction;
import me.josvth.trade.transaction.inventory.slot.InventorySlot;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.transaction.offer.Offer;
import me.josvth.trade.transaction.offer.OfferList;

import java.util.Map;
import java.util.TreeMap;

// TODO Make these actions work for the inventory offer list as well
public abstract class OfferAction extends TraderAction {

    protected final OfferList list;

    private Map<Integer, Offer> changes = new TreeMap<Integer, Offer>();

    public OfferAction(Trader trader, OfferList list) {
        super(trader);
        this.list = list;
    }

    public Map<Integer, Offer> getChanges() {
        return changes;
    }

    public void setChanges(Map<Integer, Offer> changes) {
        this.changes = changes;
    }

    public void updateOffers() {
        for (Map.Entry<Integer, ? extends Offer> entry : getChanges().entrySet()) {
            list.set(entry.getKey(), entry.getValue());
        }
    }

    public void updateSlots() {
        final int[] slots = getSlotArray();
        if (list.getType() == OfferList.Type.TRADE) {
            TradeSlot.updateTradeSlots(list.getHolder(), true, slots);
            MirrorSlot.updateMirrors(list.getHolder().getOtherHolder(), true, slots);
        } else {
            InventorySlot.updateInventorySlots(list.getHolder(), true, slots);
        }
    }

    public void denyAccepts() {
        new DenyAction(list.getTrader(), DenyAction.Reason.OWN_OFFER_CHANGED).execute();
        new DenyAction(list.getTrader().getOtherTrader(), DenyAction.Reason.OTHERS_OFFER_CHANGED).execute();
    }

    public int[] getSlotArray() {
        final int[] array = new int[changes.size()];
        int i = 0;
        for(Integer integer : changes.keySet()) {
            array[i] = integer;
            i++;
        }
        return array;
    }

}
