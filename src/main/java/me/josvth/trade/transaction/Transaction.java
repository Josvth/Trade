package me.josvth.trade.transaction;

import me.josvth.trade.goods.Tradeable;
import me.josvth.trade.transaction.inventory.TransactionLayout;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Transaction {

	private final String playerA;
	private final String playerB;

	private TransactionLayout transactionLayout = null;

	private List<Tradeable> itemsA = new ArrayList<Tradeable>();
	private List<Tradeable> itemsB = new ArrayList<Tradeable>();

	private TransactionStage stage = TransactionStage.PRE;

	public Transaction(String playerA, String playerB) {
		this.playerA = playerA;
		this.playerB = playerB;
	}

	public TransactionLayout getTransactionLayout() {
		return transactionLayout;
	}

	public Player getPlayer(String playerName) {
		Player player = null;
		if (playerA.equals(playerName))
			player = Bukkit.getPlayer(playerName);
		if (playerB.equals(playerName))
			player = Bukkit.getPlayer(playerName);
		if (player == null)
			throw new IllegalArgumentException("Player " + playerName + " is not participating in this trade or went offline.");
		return player;
	}

	public String getOther(String playerName) {
		if (playerA.equals(playerName))
			return playerB;
		if (playerB.equals(playerName))
			return playerA;
		throw new IllegalArgumentException("Player " + playerName + " is not participating in this trade.");
	}

	public Player getOtherPlayer(String playerName) {
		if (playerA.equals(playerName))
			return getPlayer(playerB);
		if (playerB.equals(playerName))
			return getPlayer(playerA);
		throw new IllegalArgumentException("Player " + playerName + " is not participating in this trade.");
	}

	public TransactionStage getStage() {
		return stage;
	}
}
