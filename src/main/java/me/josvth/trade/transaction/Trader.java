package me.josvth.trade.transaction;

import me.josvth.trade.goods.Tradeable;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.AcceptSlot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Trader {

	private final Transaction transaction;
	private final String name;

	private final OfferList offers;
	private final TransactionHolder holder;

	private Trader other;

    private boolean accepted = false;
	private boolean refused = false;

	public Trader(Transaction transaction, String name, int offerSize) {
		this.transaction = transaction;
		this.name = name;
		this.offers = new OfferList(this, offerSize);
		this.holder = new TransactionHolder(transaction.getPlugin(), this, transaction.getLayout());
	}

	public String getName() {
		return name;
	}

	public Player getPlayer() {
		return Bukkit.getPlayerExact(name);
	}

	public Trader getOther() {
		return other;
	}

	public void setOther(Trader other) {
		this.other = other;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public TransactionHolder getHolder() {
		return holder;
	}

	public boolean hasAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public void accept() {
		if (!accepted) {
			accepted = true;
			// TODO handle accepting
		}
	}

	public void deny() {
		if (accepted) {
			accepted = false;
			// TODO handle denying
		}
	}
	public boolean isRefused() {
		return refused;
	}

	public void setRefused(boolean refused) {
		this.refused = refused;
	}

	public void refuse() {
		if (!refused) {
			refused = true;
			transaction.stop();
			// TODO handle refusal
		}
	}

	public OfferList getOffers() {
		return offers;
	}

	public void openInventory() {
		getPlayer().openInventory(holder.getInventory());
	}

	public void closeInventory() {
		getPlayer().closeInventory(); // TODO Check for Transaction Inventory?
	}

}
