package me.josvth.trade.transaction.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TransactionLayout {

	private int rows = 3;

	public int getRows() {
		return rows;
	}

	public String getTransactionTitle(String user, String other) {
		return "YOU NEED TO FIX THIS TITLE";
	}

	public String getConfirmTitle(String user, String other) {
		return "YOU NEED TO FIX THIS TITLE";
	}

	public void fillTransactionInventory(Inventory inventory) {

	}

	public void fillConfirmInventory(Inventory inventory) {

	}

}
