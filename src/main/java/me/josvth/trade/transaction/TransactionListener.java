package me.josvth.trade.transaction;

import me.josvth.bukkitformatlibrary.managers.FormatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class TransactionListener implements Listener {

	private final TransactionManager transactionManager;
	private final FormatManager formatManager;

	public TransactionListener (TransactionManager transactionManager, FormatManager formatManager) {
		this.transactionManager = transactionManager;
		this.formatManager = formatManager;
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {

		Transaction transaction = transactionManager.getTransaction(event.getPlayer().getName());

		if (transaction == null) return;

		Trader trader = transaction.getTrader(event.getPlayer().getName());

		transaction.cancel();

//		formatManager.create("personal", "trade.cancelled.disconnect").send(trader.getPlayer());
		formatManager.create("personal", "trade.cancelled.other-disconnect").send(trader.getOther().getPlayer(), "%player%", event.getPlayer().getName());

	}

}
