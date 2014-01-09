package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Trader;
import org.bukkit.entity.Player;

public abstract class TraderAction extends Action {

    private final Trader trader;

    public TraderAction(ActionProvoker provoker, Trader trader) {
        super(provoker);
        this.trader = trader;
    }

    public Trader getTrader() {
        return trader;
    }

    public Trader getOtherTrader() {
        return getTrader().getOtherTrader();
    }

    public Player getPlayer() {
        return getTrader().getPlayer();
    }
}
