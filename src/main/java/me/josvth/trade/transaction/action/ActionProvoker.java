package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Transaction;

public interface ActionProvoker {

    Transaction getTransaction();

    String getName();

}
