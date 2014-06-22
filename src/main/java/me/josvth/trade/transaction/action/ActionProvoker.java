package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Transaction;

import java.util.UUID;

public interface ActionProvoker {

    Transaction getTransaction();

    String getName();

}
