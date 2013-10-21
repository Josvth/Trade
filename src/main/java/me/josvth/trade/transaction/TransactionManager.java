package me.josvth.trade.transaction;

import me.josvth.trade.Trade;
import me.josvth.trade.request.Request;

import java.util.HashMap;
import java.util.Map;

public class TransactionManager {

	private final Trade plugin;

	private Map<String, Transaction> transactions = new HashMap<String, Transaction>();

	public TransactionManager(Trade plugin) {
		this.plugin = plugin;
	}

	public Trade getPlugin() {
		return plugin;
	}

	public Transaction handleRequest(Request request) {
		return createTransaction(request.getRequester(), request.getRequested());
	}

	public Transaction createTransaction(String playerA, String playerB) {

		Transaction transaction = removeTransaction(playerA);
		if (transaction != null) transaction.cancel();

		transaction = removeTransaction(playerB);
		if (transaction != null) transaction.cancel();

		transaction = new Transaction(this, playerA, playerB);

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
