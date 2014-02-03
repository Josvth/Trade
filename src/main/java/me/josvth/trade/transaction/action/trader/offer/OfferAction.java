package me.josvth.trade.transaction.action.trader.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.ActionProvoker;
import me.josvth.trade.transaction.action.trader.status.DenyAction;
import me.josvth.trade.transaction.action.trader.TraderAction;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.transaction.offer.ItemOffer;
import me.josvth.trade.transaction.offer.Offer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents an abstract action in which offers of a trader were changed
 *
 * Handles updating of slots and denying standing accepts
 *
 */
public class OfferAction extends TraderAction {

    private boolean updateOffers = true;
    private boolean updateSlots = true;
    private boolean denyAccepts = true;

    private Map<Integer, Offer> changes;

    public OfferAction(Trader trader) {
        super(trader);
    }

    public Map<Integer, Offer> getChanges() {
        return changes;
    }

    public void setChanges(Map<Integer, Offer> changes) {
        this.changes = changes;
    }

    @Override
    public void execute() {

        if (getChanges() != null && !getChanges().isEmpty()) {

            if (isUpdateOffers()) { // Update offers
                for (Map.Entry<Integer, ? extends Offer> entry : getChanges().entrySet()) {
                    getTrader().getOffers().set(entry.getKey(), entry.getValue());
                }
            }

            if (isUpdateSlots()) {  // Update slots
                final int[] slots = toIntArray(getChanges().keySet());
                TradeSlot.updateTradeSlots(getTrader().getHolder(), true, slots);
                MirrorSlot.updateMirrors(getOtherTrader().getHolder(), true, slots);
            }

        }

        if (isDenyAccepts()) {// Deny accepts
            new DenyAction(getTrader(), DenyAction.Reason.OWN_OFFER_CHANGED).execute();
            new DenyAction(getOtherTrader(), DenyAction.Reason.OTHERS_OFFER_CHANGED).execute();
        }

    }

    public boolean isUpdateOffers() {
        return updateOffers;
    }

    public void setUpdateOffers(boolean updateOffers) {
        this.updateOffers = updateOffers;
    }

    public boolean isUpdateSlots() {
        return updateSlots;
    }

    public void setUpdateSlots(boolean updateSlots) {
        this.updateSlots = updateSlots;
    }

    public boolean isDenyAccepts() {
        return denyAccepts;
    }

    public void setDenyAccepts(boolean denyAccepts) {
        this.denyAccepts = denyAccepts;
    }

    private static int[] toIntArray(Set<Integer> integers) {
        final int[] array = new int[integers.size()];
        int i = 0;
        for(Integer integer : integers) {
            array[i] = integer;
            i++;
        }
        return array;
    }

    public static OfferAction create(Trader trader, int offerIndex, Offer offer) {
        final OfferAction offerAction = new OfferAction(trader);
        if (offer != null && offerIndex < 0) {
            offerAction.setChanges(new HashMap<Integer, Offer>());
            offerAction.getChanges().put(offerIndex, offer);
        }
        return offerAction;
    }
}
