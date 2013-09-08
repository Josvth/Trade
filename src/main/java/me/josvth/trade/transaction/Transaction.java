package me.josvth.trade.transaction;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.inventory.TransactionLayout;

public class Transaction {

	private final TransactionManager manager;

	private TransactionLayout layout = null;

	private final Trader traderA;
	private final Trader traderB;

	private TransactionStage stage = TransactionStage.PRE;

	public Transaction(TransactionManager manager, String playerA, String playerB) {
		this.manager = manager;

		traderA = new Trader(this, playerA, layout.getOfferSize());
		traderB = new Trader(this, playerB, layout.getOfferSize());

		traderA.setOther(traderB);
		traderB.setOther(traderA);

	}

	public Trade getPlugin() {
		return manager.getPlugin();
	}

	public Trader getTraderA() {
		return traderA;
	}

	public Trader getTraderB() {
		return traderB;
	}

	public Trader getTrader(String playerName) {
		if (traderA.getName().equals(playerName))
			return traderA;
		if (traderB.getName().equals(playerName))
			return traderB;
		throw new IllegalArgumentException("Player " + playerName + " is not participating in this trade or went offline.");
	}

	public TransactionLayout getLayout() {
		return layout;
	}

	public void setLayout(TransactionLayout layout) {
		this.layout = layout;
	}

	public TransactionStage getStage() {
		return stage;
	}

	public boolean isStarted() {
		return stage == TransactionStage.IN_PROGRESS;
	}

	public void start() {

		if (isStarted()) throw new IllegalArgumentException("Cannot start an already started transaction");

		if (layout == null) throw new IllegalStateException("Cannot start transaction without an layout.");


	}

	public void stop() {

	}

	public void cancel() {

	}

}
