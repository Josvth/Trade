package me.josvth.trade.transaction;

import me.josvth.trade.goods.Tradeable;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Trader {

	private final Transaction transaction;
	private final String name;

	private Trader other;

	private TransactionHolder holder;

	private boolean accepted = false;

	private final OfferList offers;

	public Trader(Transaction transaction, String name, int offerSize) {
		this.transaction = transaction;
		this.name = name;
		this.holder = null;
		this.offers = new OfferList(offerSize);
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

	public void setHolder(TransactionHolder holder) {
		this.holder = holder;
	}

	public boolean hasAccepted() {
		return accepted;
	}

	public void accept() {
		accepted = true;
	}

	public void deny() {
		accepted = false;
	}

	public OfferList getOffers() {
		return offers;
	}
}
