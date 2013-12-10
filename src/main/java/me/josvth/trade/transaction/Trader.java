package me.josvth.trade.transaction;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import me.josvth.bukkitformatlibrary.managers.FormatManager;
import me.josvth.trade.offer.OfferList;
import me.josvth.trade.transaction.inventory.Layout;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Trader {

	private final Transaction transaction;
	private final String name;

	private final FormatManager formatManager;

	private final OfferList offers;
    private final Layout layout;
    private final TransactionHolder holder;

	private Trader other;

	private boolean accepted = false;
	private boolean refused = false;

	public Trader(Transaction transaction, String name, int offerSize) {

        this.transaction = transaction;
		this.name = name;

		this.formatManager = transaction.getPlugin().getFormatManager();

		this.offers = new OfferList(this, offerSize);
        this.layout = transaction.getLayout();  //TODO Trader specific layouts?
        this.holder = new TransactionHolder(transaction.getPlugin(), this);

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

	public void accept(AcceptReason reason) {

		if (!accepted) {

			accepted = true;

			if (reason.messagePath != null) {
				formatManager.getMessage(reason.messagePath).send(getPlayer());
			}

			if (reason.mirrorMessagePath != null) {
				formatManager.getMessage(reason.mirrorMessagePath).send(getOther().getPlayer(), "%player%", name);
			}

		}

	}

	public void deny(DenyReason reason) {

		if (accepted) {

			accepted = true;

			if (reason.messagePath != null) {
				formatManager.getMessage(reason.messagePath).send(getPlayer());
			}

			if (reason.mirrorMessagePath != null) {
				formatManager.getMessage(reason.mirrorMessagePath).send(getOther().getPlayer(), "%player%", name);
			}

		}

	}

	public boolean hasRefused() {
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
		getPlayer().closeInventory();
	}

    public Layout getLayout() {
        return layout;
    }

    public enum AcceptReason {

		SELF ("trading.accepted.self", "trading.accepted.other");

		public final String messagePath;
		public final String mirrorMessagePath;

		private AcceptReason(String messagePath, String mirrorMessagePath) {
			this.messagePath = messagePath;
			this.mirrorMessagePath = mirrorMessagePath;
		}

	}

	public enum DenyReason {

		SELF ("trading.denied.self", "trading.denied.other"),
		CHANGED_OFFER ("trading.offer-changed", null), // When the other changed their offer
		FORCED (null, null);

		public final String messagePath;
		public final String mirrorMessagePath;

		private DenyReason(String messagePath, String mirrorMessagePath) {
			this.messagePath = messagePath;
			this.mirrorMessagePath = mirrorMessagePath;
		}

	}
}
