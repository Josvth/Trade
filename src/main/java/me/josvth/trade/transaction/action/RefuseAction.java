package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Trader;

public class RefuseAction extends TraderAction {

    private final Reason reason;

    public RefuseAction(Trader trader, Reason reason) {
        super(trader, trader);
        this.reason = reason;
    }

    @Override
    public void execute() {
        if (!getTrader().hasRefused()) {

            getTrader().setRefused(true);

            getTrader().sendFormattedMessage(reason.messagePath, false);
            getOtherTrader().sendFormattedMessage(reason.mirrorMessagePath, false, "%player%", getTrader().getName());

            if (getTransaction().useLogging()) {
                getTransaction().logAction(this);
            }

            new EndAction(getTransaction(), EndAction.Reason.REFUSE).execute();

        }
    }

    public enum Reason {

        GENERIC ("refused.generic.message", "refused.generic.mirror"),
        BUTTON ("refused.generic.message", "refused.generic.mirror"),
        COMMAND ("refused.generic.message", "refused.generic.mirror"),
        DISCONNECT ("refused.disconnected.message", "refused.disconnected.mirror"),
        CLOSE("refused.closed.message", "refused.closed.mirror");

        public final String messagePath;
        public final String mirrorMessagePath;

        Reason(String messagePath, String mirrorMessagePath) {
            this.messagePath = messagePath;
            this.mirrorMessagePath = mirrorMessagePath;
        }

    }

}
