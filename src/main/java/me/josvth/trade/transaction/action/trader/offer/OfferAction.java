package me.josvth.trade.transaction.action.trader.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.ActionProvoker;
import me.josvth.trade.transaction.action.trader.status.DenyAction;
import me.josvth.trade.transaction.action.trader.TraderAction;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.transaction.offer.Offer;

import java.util.Map;
import java.util.Set;

/**
 * Represents an abstract action in which offers of a trader were changed
 *
 * Handles updating of slots and denying standing accepts
 *
 */
public abstract class OfferAction extends TraderAction {

    public OfferAction(Trader trader) {
        super(trader);
    }

    public abstract Map<Integer, ? extends Offer> getChanges();

    @Override
    public void execute() {

        // Update slots
        final int[] slots = toIntArray(getChanges().keySet());
        TradeSlot.updateTradeSlots(getTrader().getHolder(), true, slots);
        MirrorSlot.updateMirrors(getOtherTrader().getHolder(), true, slots);

        // Deny accepts
        new DenyAction(getTrader(), DenyAction.Reason.OWN_OFFER_CHANGED).execute();
        new DenyAction(getOtherTrader(), DenyAction.Reason.OTHERS_OFFER_CHANGED).execute();

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

}
