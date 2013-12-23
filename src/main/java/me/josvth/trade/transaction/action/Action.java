package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Transaction;

public abstract class Action {

    private final Transaction transaction;
    private final ActionExecutor executor;

    public Action(Transaction transaction, ActionExecutor executor) {
        this.transaction = transaction;
        this.executor = executor;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public ActionExecutor getExecutor() {
        return executor;
    }

    public abstract void execute();

}
