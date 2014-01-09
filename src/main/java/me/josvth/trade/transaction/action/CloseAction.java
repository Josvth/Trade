package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Trader;

public class CloseAction extends TraderAction {

    public CloseAction(Trader trader) {
        super(trader);
    }

    @Override
    public void execute() {

        if (getTrader().getState() != Trader.State.ROAMING) {

            getTrader().getFormattedMessage("closed-inventory.self").send(getPlayer());
            getOtherTrader().getFormattedMessage("closed-inventory.other").send(getOtherTrader().getPlayer(), "%player%", getPlayer().getName());

            getTrader().setState(Trader.State.ROAMING);

            if (getTransaction().useLogging()) {
                getTransaction().logAction(this);
            }

        }

    }

}
