package me.josvth.trade.transaction;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import me.josvth.bukkitformatlibrary.managers.FormatManager;
import me.josvth.trade.Trade;
import me.josvth.trade.request.Request;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class TransactionManager {

	private final Trade plugin;

	private final FormatManager formatManager;
	private final TransactionListener listener;

	private Map<String, Transaction> transactions = new HashMap<String, Transaction>();

	public TransactionManager(Trade plugin, FormatManager formatManager) {
		this.plugin = plugin;
		this.formatManager = formatManager;
		this.listener = new TransactionListener(this, this.formatManager);
	}

	public void initialize() {
		Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
	}

	public void load(ConfigurationSection section) {
		// TODO load transaction configuration
	}

	public void unload() {
		for (Transaction transaction : new LinkedHashSet<Transaction>(transactions.values())) {
			transaction.stop(false);
			final FormattedMessage message = formatManager.getMessage("trading.reload");
			message.send(transaction.getTraderA().getPlayer());
			message.send(transaction.getTraderB().getPlayer());
		}
		transactions.clear();
	}

	public Trade getPlugin() {
		return plugin;
	}

	public Transaction createTransaction(String playerA, String playerB) {

		Transaction transaction = removeTransaction(playerA);
		if (transaction != null) transaction.cancel();

		transaction = removeTransaction(playerB);
		if (transaction != null) transaction.cancel();

		transaction = new Transaction(this, plugin.getLayoutManager().getLayout(playerA, playerB),  playerA, playerB);

		return transaction;

	}

	public boolean isInTransaction(String player) {
		return transactions.containsKey(player);
	}

	public Transaction getTransaction(String player) {
		return transactions.get(player);
	}

	public void addTransaction(Transaction transaction) {
		transactions.put(transaction.getTraderA().getName(), transaction);
		transactions.put(transaction.getTraderB().getName(), transaction);
	}

	public void removeTransaction(Transaction transaction) {
		removeTransaction(transaction.getTraderA().getName());
		removeTransaction(transaction.getTraderB().getName());
	}

	private Transaction removeTransaction(String player) {
		return transactions.remove(player);
	}

}