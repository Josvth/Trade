package me.josvth.trade.transaction.action.trader.status;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.trader.TraderAction;
import me.josvth.trade.transaction.inventory.slot.AcceptSlot;
import me.josvth.trade.transaction.inventory.slot.StatusSlot;

public class DenyAction extends TraderAction {

    private final Reason reason;

    public DenyAction(Trader trader, Reason reason) {
        super(trader);
        this.reason = reason;
    }

    @Override
    public void execute() {

        if (getTrader().hasAccepted()) {

            getTrader().setAccepted(false);

            getTrader().getFormattedMessage(reason.messagePath).send(getPlayer(), "%player%", getOtherTrader().getName());
            getOtherTrader().getFormattedMessage(reason.mirrorMessagePath).send(getOtherPlayer(), "%player%", getTrader().getName());

            if (getTrader().getHolder().hasViewers()) {
                AcceptSlot.updateAcceptSlots(getTrader().getHolder(), true);
            }

            if (getOtherTrader().getHolder().hasViewers()) {
                StatusSlot.updateStatusSlots(getOtherTrader().getHolder(), true);
            }

            if (getTransaction().useLogging()) {
                getTransaction().logAction(this);
            }

        }

    }

    public enum Reason {

        GENERIC("deny.generic.message", "deny.generic.mirror"),
        BUTTON("deny.generic.message", "deny.generic.mirror"),
        OWN_OFFER_CHANGED("deny.own-offer-changed.message", "deny.own-offer-changed.mirror"),
        OTHERS_OFFER_CHANGED("deny.others-offer-changed.message", "deny.others-offer-changed.mirror"),
        COMMAND("deny.generic.message", "deny.generic.mirror");

        public final String messagePath;
        public final String mirrorMessagePath;

        Reason(String messagePath, String mirrorMessagePath) {
            this.messagePath = messagePath;
            this.mirrorMessagePath = mirrorMessagePath;
        }

    }

}
