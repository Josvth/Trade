package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.slot.AcceptSlot;
import me.josvth.trade.transaction.inventory.slot.StatusSlot;

public class DenyAction extends TraderAction {

    private final Method method;

    public DenyAction(Trader trader) {
        super(trader);
        method = Method.GENERIC;
    }

    public DenyAction(Trader trader, Method method) {
        super(trader);
        this.method = method;
    }

    @Override
    public void execute() {

        if (getTrader().hasAccepted()) {

            getTrader().setAccepted(false);

            getTrader().getFormattedMessage(method.messagePath).send(getTrader().getPlayer());
            getTrader().getOtherTrader().getFormattedMessage(method.mirrorMessagePath).send(getTrader().getOtherTrader().getPlayer(), "%player%", getTrader().getName());

            if (getTrader().getHolder().hasViewers()) {
                AcceptSlot.updateAcceptSlots(getTrader().getHolder(), true);
                StatusSlot.updateStatusSlots(getTrader().getHolder().getOtherHolder(), true);
            }

            if (getTransaction().useLogging()) {
                getTransaction().logAction(this);
            }

        }

    }

    public enum Method {

        GENERIC ("deny.generic.message", "deny.generic.mirror"),
        BUTTON ("deny.generic.message", "deny.generic.mirror"),
        COMMAND ("deny.generic.message", "deny.generic.mirror");

        public final String messagePath;
        public final String mirrorMessagePath;

        Method(String messagePath, String mirrorMessagePath) {
            this.messagePath = messagePath;
            this.mirrorMessagePath = mirrorMessagePath;
        }

    }

}
