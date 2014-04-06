package me.josvth.trade.transaction.action.trader.status;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.EndAction;
import me.josvth.trade.transaction.action.trader.TraderAction;

public class RefuseAction extends TraderAction {

    private final Reason reason;

    public RefuseAction(Trader trader, Reason reason) {
        super(trader);
        this.reason = reason;
    }

    @Override
    public void execute() {
        if (!getTrader().hasRefused()) {

            getTrader().setRefused(true);

            getTrader().getFormattedMessage(reason.messagePath).send(getPlayer());
            getOtherTrader().getFormattedMessage(reason.mirrorMessagePath).send(getOtherPlayer(), "%player%", getTrader().getName());

            if (getTransaction().useLogging()) {
                getTransaction().logAction(this);
            }

            new EndAction(getTransaction(), EndAction.Reason.REFUSE).execute();

        }
    }

    public enum Reason {

        GENERIC("refuse.generic.message", "refuse.generic.mirror"),
        BUTTON("refuse.generic.message", "refuse.generic.mirror"),
        COMMAND("refuse.generic.message", "refuse.generic.mirror"),
        DISCONNECT("refuse.disconnected.message", "refuse.disconnected.mirror"),
        CLOSE("refuse.close.message", "refuse.close.mirror");

        public final String messagePath;
        public final String mirrorMessagePath;

        Reason(String messagePath, String mirrorMessagePath) {
            this.messagePath = messagePath;
            this.mirrorMessagePath = mirrorMessagePath;
        }

    }

}
