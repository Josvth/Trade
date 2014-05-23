package me.josvth.trade.transaction.action.trader.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.trader.TraderAction;
import me.josvth.trade.transaction.action.trader.status.DenyAction;
import me.josvth.trade.transaction.inventory.offer.ExperienceOffer;
import me.josvth.trade.transaction.inventory.offer.MoneyOffer;
import me.josvth.trade.transaction.inventory.offer.Offer;
import me.josvth.trade.transaction.inventory.offer.OfferList;
import me.josvth.trade.transaction.inventory.slot.*;

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

        // TODO THIS IS HORRIBLE! COME UP WITH A FIX
        double newExperience = ExperienceSlot.getExperience(list);
        double newMoney = MoneySlot.getMoney(list);

        for (Map.Entry<Integer, ? extends Offer> entry : getChanges().entrySet()) {

            if (list.get(entry.getKey()) instanceof ExperienceOffer) {
                newExperience -= ((ExperienceOffer) list.get(entry.getKey())).getAmount();
            }

            if (list.get(entry.getKey()) instanceof MoneyOffer) {
                newMoney -= ((MoneyOffer) list.get(entry.getKey())).getAmount();
            }

            if (entry.getValue() instanceof ExperienceOffer) {
                newExperience += ((ExperienceOffer) entry.getValue()).getAmount();
            }

            if (entry.getValue() instanceof MoneyOffer) {
                newMoney += ((MoneyOffer) entry.getValue()).getAmount();
            }

            list.set(entry.getKey(), entry.getValue());

        }

        // TODO UGLY!!!
        if (list.getType() == OfferList.Type.TRADE) {
            ExperienceSlot.updateExperienceSlots(list.getHolder(), true, newExperience);
            MoneySlot.updateMoneySlots(list.getHolder(), true, newMoney);
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
        for (Integer integer : changes.keySet()) {
            array[i] = integer;
            i++;
        }
        return array;
    }

}
