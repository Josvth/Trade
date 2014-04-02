package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Transaction;

public abstract class Action {

    private final Transaction transaction;

    private ActionProvoker provoker;

    protected Action(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public ActionProvoker getProvoker() {
        return provoker;
    }

    public void setProvoker(ActionProvoker provoker) {
        this.provoker = provoker;
    }

    public abstract void execute();

}
