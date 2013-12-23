package me.josvth.trade.transaction.offer;

import me.josvth.trade.transaction.offer.description.MoneyOfferDescription;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.inventory.ItemStack;

public class MoneyOffer extends Offer {

	private double amount = 0;

	public MoneyOffer(OfferList list, int offerID) {
		this(list, offerID, 0);
	}

	public MoneyOffer(OfferList list, int offerID, double amount) {
		super(list, offerID);
		this.amount = amount;
	}

    @Override
    public MoneyOfferDescription getDescription() {
        return (MoneyOfferDescription) super.getDescription();
    }

    @Override
    public ItemStack createItem() {
        return getDescription().createItem(this);
    }

    @Override
    public ItemStack createMirror(TransactionHolder holder) {
        return getDescription().createMirrorItem(this, holder);
    }

	@Override
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	protected MoneyOffer clone() {
		return new MoneyOffer(list, offerIndex, amount);
	}

	@Override
	public void grant(Trader trader) {

	}

}
