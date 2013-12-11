package me.josvth.trade.transaction;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import me.josvth.trade.offer.OfferList;
import me.josvth.trade.transaction.inventory.Layout;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Trader {

	private final Transaction transaction;
	private final String name;

	private final OfferList offers;
    private final Layout layout;
    private final TransactionHolder holder;

	private Trader other;

	private boolean accepted = false;
	private boolean refused = false;

	public Trader(Transaction transaction, String name, int offerSize) {

        this.transaction = transaction;
		this.name = name;

		this.offers = new OfferList(this, offerSize);
        this.layout = transaction.getLayout();  //TODO Trader specific layouts?
        this.holder = new TransactionHolder(transaction.getPlugin(), this);

	}

    public Transaction getTransaction() {
        return transaction;
    }

    public String getName() {
        return name;
    }


    public OfferList getOffers() {
        return offers;
    }

    public Layout getLayout() {
        return layout;
    }

    public TransactionHolder getHolder() {
        return holder;
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

	public boolean hasAccepted() {
		return accepted;
	}

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

	public boolean hasRefused() {
		return refused;
	}

	public void setRefused(boolean refused) {
		this.refused = refused;
	}

	public void openInventory() {
		getPlayer().openInventory(holder.getInventory());
	}

	public void closeInventory() {
		getPlayer().closeInventory();
	}

    public FormattedMessage getFormattedMessage(String key) {
        return getLayout().getFormattedMessage(key);
    }

    public boolean hasFormattedMessage(String key) {
        return getLayout().hasFormattedMessage(key);
    }
}
