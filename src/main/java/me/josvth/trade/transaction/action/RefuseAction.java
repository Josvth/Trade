package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Trader;

public class RefuseAction extends TraderAction {

    private final Method method;

    public RefuseAction(Trader trader) {
        super(trader);
        this.method = Method.GENERIC;
    }

    public RefuseAction(Trader trader, Method method) {
        super(trader);
        this.method = method;
    }

    @Override
    public void execute() {
        if (!getTrader().hasRefused()) {

            getTrader().setRefused(true);

            getTrader().getFormattedMessage(method.messagePath).send(getPlayer());
            getOtherTrader().getFormattedMessage(method.mirrorMessagePath).send(getOtherTrader().getPlayer(), "%player%", getTrader().getName());

            if (getTransaction().useLogging()) {
                getTransaction().logAction(this);
            }

            new EndAction(getTransaction(), EndAction.Reason.REFUSE).execute();

        }
    }

    public enum Method {

        GENERIC ("refused.generic.message", "refused.generic.mirror"),
        BUTTON ("refused.generic.message", "refused.generic.mirror"),
        COMMAND ("refused.generic.message", "refused.generic.mirror"),
        DISCONNECT ("refused.disconnected.message", "refused.disconnected.mirror");

        public final String messagePath;
        public final String mirrorMessagePath;

        Method(String messagePath, String mirrorMessagePath) {
            this.messagePath = messagePath;
            this.mirrorMessagePath = mirrorMessagePath;
        }

    }

}
