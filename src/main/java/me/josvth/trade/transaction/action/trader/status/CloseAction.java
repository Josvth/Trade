package me.josvth.trade.transaction.action.trader.status;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.trader.TraderAction;

public class CloseAction extends TraderAction {

    public CloseAction(Trader trader) {
        super(trader);
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
