package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.action.trader.offer.SetOfferAction;
import me.josvth.trade.transaction.inventory.offer.Offer;

public class EndAction extends Action {

    private final Reason reason;

    public EndAction(Transaction transaction) {
        super(transaction);
        this.reason = Reason.GENERIC;
    }

    public EndAction(Transaction transaction, Reason reason) {
        super(transaction);
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

        final boolean nextTick = reason != Reason.RELOAD;

        handleOffers(getTransaction().getTraderA(), nextTick);
        handleOffers(getTransaction().getTraderB(), nextTick);

        getTransaction().remove();

    }

    private void handleOffers(Trader trader, boolean nextTick) {

        final SetOfferAction offerAction = new SetOfferAction(trader, trader.getHolder().getInventoryList());

        for (int i = 0; i < trader.getHolder().getInventoryList().getContents().length; i++) {
            final Offer offer = trader.getHolder().getInventoryList().getContents()[i];
            if (offer != null && !offer.canStayInInventory()) {
                offer.grant(getTransaction().getTraderA(), nextTick);
                offerAction.setOffer(i, null);
            }
        }

        if (!offerAction.getChanges().isEmpty()) {
            offerAction.execute();
        }

        if (reason == Reason.ACCEPT) {
            trader.getOffers().grant(trader.getOtherTrader(), nextTick);
        } else {
            trader.getOffers().grant(trader, nextTick);
        }

    }

    public enum Reason {

        GENERIC(null),
        ACCEPT(null),
        REFUSE(null),
        RELOAD("cancelled.reload");

        public final String messagePath;

        Reason(String messagePath) {
            this.messagePath = messagePath;
        }
    }
}
