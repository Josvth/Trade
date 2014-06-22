package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.TransactionManager;

public class StartAction extends Action {

    public StartAction(Transaction transaction) {
        super(transaction);
    }

    private TransactionManager getManager() {
        return getTransaction().getManager();
    }

    @Override
    public void execute() {

        if (getTransaction().isStarted()) {
            throw new IllegalArgumentException("Cannot start an already started transaction");
        }

        if (getTransaction().hasEnded()) {
            throw new IllegalArgumentException("Cannot start an ended transaction");
        }

        if (getTransaction().getLayout() == null) {
            throw new IllegalStateException("Cannot start transaction without an layout.");
        }

        // TODO Add method like isInTransaction(Trader)
        if (getManager().isInTransaction(getTransaction().getTraderA().getPlayer()) || getManager().isInTransaction(getTransaction().getTraderB().getPlayer())) {
            throw new IllegalArgumentException("One of the traders is already trading!");
        }

        getTransaction().setStage(Transaction.Stage.IN_PROGRESS);

        getTransaction().getTraderA().getHolder().updateInventoryList();
        getTransaction().getTraderB().getHolder().updateInventoryList();

        getTransaction().getTraderA().getHolder().updateAllSlots();
        getTransaction().getTraderB().getHolder().updateAllSlots();

        getTransaction().getTraderA().openInventory();
        getTransaction().getTraderB().openInventory();

        getManager().addTransaction(getTransaction());

        if (getTransaction().useLogging()) {
            getTransaction().logAction(this);
        }


    }


}
