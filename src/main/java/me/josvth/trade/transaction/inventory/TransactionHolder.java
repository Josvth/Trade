package me.josvth.trade.transaction.inventory;

import me.josvth.trade.transaction.Transaction;

import me.josvth.trade.transaction.TransactionStage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import static me.josvth.trade.transaction.TransactionStage.*;

public class TransactionHolder implements InventoryHolder {

	private final Transaction transaction;
	private final TransactionLayout transactionLayout;

	private final String user;

	private Inventory transactionInventory;
	private Inventory confirmInventory;

	public TransactionHolder(Transaction transaction, String user) {

		this.transaction = transaction;
		this.transactionLayout = transaction.getTransactionLayout();

		this.user = user;

	}

	public Inventory getTransactionInventory() {
		if (transactionInventory == null) {
			transactionInventory = Bukkit.createInventory(this, transactionLayout.getRows(), transactionLayout.getTransactionTitle(user, transaction.getOther(user)));
			transactionLayout.fillTransactionInventory(transactionInventory);
		}
		return transactionInventory;
	}

	public Inventory getConfirmInventory() {
		if (transactionInventory == null) {
			transactionInventory = Bukkit.createInventory(this, transactionLayout.getRows(), transactionLayout.getConfirmTitle(user, transaction.getOther(user)));
			transactionLayout.fillTransactionInventory(transactionInventory);
		}
		return transactionInventory;
	}

	@Override
	public Inventory getInventory() {
		switch (transaction.getStage()) {
			case PRE:
			case IN_PROGRESS:
				return getTransactionInventory();
			case POST:
				return getConfirmInventory();
		}
		return null;
	}

}
