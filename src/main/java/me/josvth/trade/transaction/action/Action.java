package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Transaction;

public abstract class Action {

    private ActionProvoker provoker;

    public Transaction getTransaction() {
        return provoker.getTransaction();
    }

    public void setProvoker(ActionProvoker provoker) {
        this.provoker = provoker;
    }
    public ActionProvoker getProvoker() {
        return provoker;
    }

    public abstract void execute();

}
