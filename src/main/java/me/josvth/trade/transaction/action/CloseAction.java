package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Trader;

public class CloseAction extends TraderAction {

    public CloseAction(Trader trader) {
        super(trader);
    }

    @Override
    public void execute() {

        if (getTrader().getState() != Trader.State.ROAMING) {

            getTrader().getFormattedMessage("closed-inventory.self").send(getTrader().getPlayer());
            getTrader().getOtherTrader().getFormattedMessage("closed-inventory.other").send(getTrader().getOtherTrader().getPlayer(), "%player%", getTrader().getPlayer().getName());

            getTrader().setState(Trader.State.ROAMING);

            if (getTransaction().useLogging()) {
                getTransaction().logAction(this);
            }

        }

    }

}
