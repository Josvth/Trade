package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Trader;

public abstract class TraderAction extends Action {

    public TraderAction(Trader trader) {
        super(trader.getTransaction(), trader);
    }

    public Trader getTrader() {
        return (Trader) getExecutor();
    }

}
