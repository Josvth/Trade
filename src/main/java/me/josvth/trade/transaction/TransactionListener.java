package me.josvth.trade.transaction;

import me.josvth.bukkitformatlibrary.managers.FormatManager;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TransactionListener implements Listener {

	private final TransactionManager transactionManager;
	private final FormatManager formatManager;

	public TransactionListener(TransactionManager transactionManager, FormatManager formatManager) {
		this.transactionManager = transactionManager;
		this.formatManager = formatManager;
	}

	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		if (event.getInventory().getHolder() instanceof TransactionHolder) {
			((TransactionHolder) event.getInventory().getHolder()).onDrag(event);
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event){
		if (event.getInventory().getHolder() instanceof TransactionHolder) {
			((TransactionHolder) event.getInventory().getHolder()).onClick(event);
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() instanceof TransactionHolder) {
			((TransactionHolder) event.getInventory().getHolder()).onClose(event);
		}
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {

		final Transaction transaction = transactionManager.getTransaction(event.getPlayer().getName());

		if (transaction == null) {
			return;
		}

		final Trader trader = transaction.getTrader(event.getPlayer().getName());

		transaction.stop(false);

		formatManager.getMessage("trading.disconnect").send(trader.getPlayer());
		formatManager.getMessage("trading.disconnect-other").send(trader.getOther().getPlayer(), "%player%", event.getPlayer().getName());

	}

}
