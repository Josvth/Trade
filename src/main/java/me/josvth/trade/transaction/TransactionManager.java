package me.josvth.trade.transaction;

import me.josvth.trade.request.Request;

import java.util.HashMap;
import java.util.Map;

public class TransactionManager {

	Map<String, Transaction> transactions = new HashMap<String, Transaction>();

	public void handleRequest(Request request) {

	}

	public boolean isInTransaction(String player) {
		return transactions.containsKey(player.toLowerCase());
	}
}
