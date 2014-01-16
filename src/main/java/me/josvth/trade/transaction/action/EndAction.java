package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Transaction;

public class EndAction extends Action {

    private final Reason reason;

    public EndAction(Transaction transaction) {
        super(transaction.getTransactionProvoker());
        this.reason = Reason.GENERIC;
    }

    public EndAction(Transaction transaction, Reason reason) {
        super(transaction.getTransactionProvoker());
        this.reason = reason;
    }

    @Override
    public void execute() {

        if (!getTransaction().isStarted()) {
            throw new IllegalStateException("Cannot stop a non started transaction");
        }

        if (getTransaction().hasEnded()) {
            throw new IllegalStateException("Cannot stop an ended transaction");
        }

        getTransaction().stop();

        if (reason.messagePath != null) {
            getTransaction().getTraderA().getFormattedMessage(reason.messagePath).send(getTransaction().getTraderA().getPlayer());
            getTransaction().getTraderB().getFormattedMessage(reason.messagePath).send(getTransaction().getTraderB().getPlayer());
        }

        getTransaction().getTraderA().closeInventory();
        getTransaction().getTraderB().closeInventory();

        if (reason == Reason.ACCEPT) {
            getTransaction().getTraderA().getOffers().grant(getTransaction().getTraderB());
            getTransaction().getTraderB().getOffers().grant(getTransaction().getTraderA());
        } else {
            getTransaction().getTraderA().getOffers().grant(getTransaction().getTraderA());
            getTransaction().getTraderB().getOffers().grant(getTransaction().getTraderB());
        }

        getTransaction().remove();

    }

    public enum Reason {

        GENERIC (null),
        ACCEPT (null),
        REFUSE (null),
        RELOAD ("cancelled.reload");

        public final String messagePath;

        Reason(String messagePath) {
            this.messagePath = messagePath;
        }
    }
}
