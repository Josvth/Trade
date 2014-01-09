package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Transaction;

public abstract class Action {

    private final ActionProvoker provoker;

    public Action(ActionProvoker provoker) {
        this.provoker = provoker;
    }

    public Transaction getTransaction() {
        return provoker.getTransaction();
    }

    public ActionProvoker getProvoker() {
        return provoker;
    }

    public abstract void execute();

    public abstract String getLogMessage();

    public int getAmount() {
        return 0;
    }

}
