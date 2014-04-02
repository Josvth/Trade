package me.josvth.trade.transaction.action.trader.status;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.EndAction;
import me.josvth.trade.transaction.action.trader.TraderAction;
import me.josvth.trade.transaction.inventory.slot.AcceptSlot;
import me.josvth.trade.transaction.inventory.slot.StatusSlot;

public class AcceptAction extends TraderAction {

    private final Reason reason;

    public AcceptAction(Trader trader, Reason reason) {
        super(trader);
        this.reason = reason;
    }

    @Override
    public void execute() {

        if (!getTrader().hasAccepted()) {

            getTrader().setAccepted(true);

            getTrader().getFormattedMessage(reason.messagePath).send(getTrader().getPlayer());
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

            if (getOtherTrader().hasAccepted()) {
                new EndAction(getTransaction(), EndAction.Reason.ACCEPT).execute();
            }

        }

    }

    public enum Reason {

        GENERIC("accept.generic.message", "accept.generic.mirror"),
        BUTTON("accept.generic.message", "accept.generic.mirror"),
        COMMAND("accept.generic.message", "accept.generic.mirror");

        public final String messagePath;
        public final String mirrorMessagePath;

        Reason(String messagePath, String mirrorMessagePath) {
            this.messagePath = messagePath;
            this.mirrorMessagePath = mirrorMessagePath;
        }

    }

}
