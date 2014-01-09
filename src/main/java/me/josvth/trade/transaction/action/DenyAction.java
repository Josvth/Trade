package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.slot.AcceptSlot;
import me.josvth.trade.transaction.inventory.slot.StatusSlot;

public class DenyAction extends TraderAction {

    private final Reason reason;

    public DenyAction(Trader trader, Reason reason) {
        this(trader, trader, reason);
    }

    public DenyAction(ActionProvoker provoker, Trader trader, Reason reason) {
        super(provoker, trader);
        this.reason = reason;
    }

    @Override
    public void execute() {

        if (getTrader().hasAccepted()) {

            getTrader().setAccepted(false);

            getTrader().sendFormattedMessage(reason.messagePath, false);
            getOtherTrader().sendFormattedMessage(reason.mirrorMessagePath, false, "%player%", getTrader().getName());

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

        GENERIC ("deny.generic.message", "deny.generic.mirror"),
        BUTTON ("deny.button.message", "deny.generic.mirror"),
        OFFER_CHANGED ("deny.offer-changed.message", "deny.offer-changed.mirror"),
        COMMAND ("deny.generic.message", "deny.generic.mirror");

        public final String messagePath;
        public final String mirrorMessagePath;

        Reason(String messagePath, String mirrorMessagePath) {
            this.messagePath = messagePath;
            this.mirrorMessagePath = mirrorMessagePath;
        }

    }

}
