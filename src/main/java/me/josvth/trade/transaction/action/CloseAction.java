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

            getTrader().getFormattedMessage("closed-inventory.self").send(getPlayer());
            getOtherTrader().getFormattedMessage("closed-inventory.other").send(getOtherPlayer(), "%player%", getTrader().getName());

            getTrader().setState(Trader.State.ROAMING);

            if (getTransaction().useLogging()) {
                getTransaction().logAction(this);
            }

        }

    }

}
