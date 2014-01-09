package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.slot.AcceptSlot;
import me.josvth.trade.transaction.inventory.slot.StatusSlot;

public class AcceptAction extends TraderAction {

    private final Method method;

    public AcceptAction(ActionProvoker provoker, Trader trader) {
        super(provoker, trader);
        method = Method.GENERIC;
    }

    public AcceptAction(Trader trader, Method method) {
        super(trader, trader);  // TODO Correctly handle provoker
        this.method = method;
    }

    @Override
    public void execute() {

        if (!getTrader().hasAccepted()) {

            getTrader().setAccepted(true);

            getTrader().getFormattedMessage(method.messagePath).send(getPlayer());
            getOtherTrader().getFormattedMessage(method.mirrorMessagePath).send(getOtherTrader().getPlayer(), "%player%", getTrader().getName());

            if (getTrader().getHolder().hasViewers()) {
                AcceptSlot.updateAcceptSlots(getTrader().getHolder(), true);
                StatusSlot.updateStatusSlots(getTrader().getHolder().getOtherHolder(), true);
            }

            if (getTransaction().useLogging()) {
                getTransaction().logAction(this);
            }

            if (getOtherTrader().hasAccepted()) {
                new EndAction(getTransaction(), EndAction.Reason.ACCEPT).execute();
            }

        }

    }

    @Override
    public String getLogMessage() {
        return null;
    }


    public enum Method {

        GENERIC ("accept.message", "accept.mirror"),
        BUTTON ("accept.message", "accept.mirror"),
        COMMAND ("accept.message", "accept.mirror");

        public final String messagePath;
        public final String mirrorMessagePath;

        Method(String messagePath, String mirrorMessagePath) {
            this.messagePath = messagePath;
            this.mirrorMessagePath = mirrorMessagePath;
        }

    }

}
