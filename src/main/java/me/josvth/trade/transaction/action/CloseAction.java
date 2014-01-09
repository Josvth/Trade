package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Trader;

public class CloseAction extends TraderAction {

    public CloseAction(Trader trader) {
        super(trader, trader);
    }

    public CloseAction(ActionProvoker provoker, Trader trader) {
        super(provoker, trader);
    }

    @Override
    public void execute() {

        if (getTrader().getState() != Trader.State.ROAMING) {

            getTrader().sendFormattedMessage("closed-inventory.self", false);
            getOtherTrader().sendFormattedMessage("closed-inventory.other", false, "%player%", getPlayer().getName());

            getTrader().setState(Trader.State.ROAMING);

            if (getTransaction().useLogging()) {
                getTransaction().logAction(this);
            }

        }

    }

}
