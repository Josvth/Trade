package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.TransactionManager;

public class StartAction extends Action {

    public StartAction(Transaction transaction) {
        super(transaction.getTransactionProvoker());
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

        if (getManager().isInTransaction(getTransaction().getTraderA().getName()) || getManager().isInTransaction(getTransaction().getTraderB().getName())) {
            throw new IllegalArgumentException("One of the traders is already trading!");
        }

        getManager().addTransaction(getTransaction());

        getTransaction().setStage(Transaction.Stage.IN_PROGRESS);

        getTransaction().getTraderA().getHolder().updateInventoryList();
        getTransaction().getTraderB().getHolder().updateInventoryList();

        getTransaction().getTraderA().getHolder().updateAllSlots();
        getTransaction().getTraderB().getHolder().updateAllSlots();

        getTransaction().getTraderA().openInventory();
        getTransaction().getTraderB().openInventory();

        if(getTransaction().useLogging()) {
            getTransaction().logAction(this);
        }

    }


}
