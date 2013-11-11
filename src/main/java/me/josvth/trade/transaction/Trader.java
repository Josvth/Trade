package me.josvth.trade.transaction;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import me.josvth.bukkitformatlibrary.managers.FormatManager;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Trader {

	private final Transaction transaction;
	private final String name;

	private final FormatManager formatManager;

	private final OfferList offers;
	private final TransactionHolder holder;

	private Trader other;

    private boolean accepted = false;
	private boolean refused = false;

	public Trader(Transaction transaction, String name, int offerSize) {
		this.transaction = transaction;
		this.name = name;

		this.formatManager = transaction.getPlugin().getFormatManager();

		this.offers = new OfferList(offerSize);
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
		// Only do something if status changes
		if (this.accepted != accepted) {
			this.accepted = accepted;

			if (accepted) {
				formatManager.getMessage("trading.accepted").send(getPlayer());
				formatManager.getMessage("trading.accepted-other").send(getOther().getPlayer(), "%player%", name);

				// If both have accepted finish the trade
				if (getOther().hasAccepted()) {
					final FormattedMessage message = formatManager.getMessage("trading.success");
					message.send(getPlayer(), "%player%", getOther().getName());
					message.send(getOther().getPlayer(), "%player%", name);
					transaction.stop(true);
				}
			} else {
				formatManager.getMessage("trading.denied").send(getPlayer());
				formatManager.getMessage("trading.denied-other").send(getOther().getPlayer(), "%player%", name);
			}

		}

    }

	public boolean isRefused() {
		return refused;
	}

	public void setRefused(boolean refused) {
		this.refused = refused;
		if (refused) {
			formatManager.getMessage("trading.refused").send(getPlayer());
			formatManager.getMessage("trading.refused-other").send(getOther().getPlayer(), "%player%", name);
			transaction.stop(false);
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
