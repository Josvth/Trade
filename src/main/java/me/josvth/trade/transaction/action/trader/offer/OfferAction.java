package me.josvth.trade.transaction.action.trader.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.trader.TraderAction;
import me.josvth.trade.transaction.action.trader.status.DenyAction;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.transaction.offer.Offer;

import java.util.Map;
import java.util.TreeMap;


public abstract class OfferAction extends TraderAction {

    private final TreeMap<Integer, Offer> changes = new TreeMap<Integer, Offer>();

    public OfferAction(Trader trader) {
        super(trader);
    }

    public TreeMap<Integer, Offer> getChanges() {
        return changes;
    }

    public void updateOffers() {
        for (Map.Entry<Integer, ? extends Offer> entry : getChanges().entrySet()) {
            getTrader().getOffers().set(entry.getKey(), entry.getValue());
        }
    }

    public void updateSlots() {
        final int[] slots = getSlotArray();
        TradeSlot.updateTradeSlots(getTrader().getHolder(), true, slots);
        MirrorSlot.updateMirrors(getOtherTrader().getHolder(), true, slots);
    }

    public void denyAccepts() {
        new DenyAction(getTrader(), DenyAction.Reason.OWN_OFFER_CHANGED).execute();
        new DenyAction(getOtherTrader(), DenyAction.Reason.OTHERS_OFFER_CHANGED).execute();
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
