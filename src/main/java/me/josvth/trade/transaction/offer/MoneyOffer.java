package me.josvth.trade.transaction.offer;

import me.josvth.trade.transaction.offer.description.MoneyOfferDescription;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.inventory.ItemStack;

public class MoneyOffer extends StackableOffer {

	private double amount = 0;

	public MoneyOffer() {
		this(0.0);
	}

	public MoneyOffer(double amount) {
		this.amount = amount;
	}

    @Override
    public String getType() {
        return "money";
    }

    @Override
    public MoneyOfferDescription getDescription(Trader trader) {
        return (MoneyOfferDescription) super.getDescription(trader);
    }

    @Override
    public ItemStack createItem(TransactionHolder holder) {
        return getDescription(holder.getTrader()).createItem(this);
    }

    @Override
    public ItemStack createMirror(TransactionHolder holder) {
        return getDescription(holder.getTrader()).createMirrorItem(this, holder);
    }

	@Override
	public int getAmount() {
		return (int) (getDoubleAmount() * 100);
	}

    @Override
    public void setAmount(int amount) {
        setDoubleAmount(amount / 100);
    }

    @Override
    public int getMaxAmount() {
        return -1;
    }

    public void setDoubleAmount(double amount) {
		this.amount = amount;
	}

    public double getDoubleAmount() {
        return amount;
    }

	@Override
	public MoneyOffer clone() {
		return new MoneyOffer(amount);
	}

	@Override
	public void grant(Trader trader) {

	}

}
