package me.josvth.trade.transaction.action.trader;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.Action;
import org.bukkit.entity.Player;

public abstract class TraderAction extends Action {

    private final Trader trader;

    public TraderAction(Trader trader) {
        this.trader = trader;
    }

    public Trader getTrader() {
        return (Trader) getProvoker();
    }

    public Player getPlayer() {
        return getTrader().getPlayer();
    }

    public Trader getOtherTrader() {
        return getTrader().getOtherTrader();
    }

    public Player getOtherPlayer() {
        return getOtherTrader().getPlayer();
    }

}
