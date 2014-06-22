package me.josvth.trade.transaction.inventory.offer;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.offer.description.MoneyOfferDescription;
import org.bukkit.inventory.ItemStack;

public class MoneyOffer extends Offer {

    public static final String TYPE_NAME = "money";

    private double amount = 0;

    public MoneyOffer() {
        this(0);
    }

    public MoneyOffer(double amount) {
        super();
        this.amount = amount;
        setAllowedInInventory(true);
        setCanStayInInventory(false);
    }

    public static MoneyOffer create(Trader trader, double amount) {
        final MoneyOffer offer = trader.getLayout().getOfferDescription(MoneyOffer.class).createOffer();
        offer.setAmount(amount);
        return offer;
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
        return getDescription(holder.getTrader()).createItem(this, holder);
    }

    @Override
    public ItemStack createMirrorItem(TransactionHolder holder) {
        return getDescription(holder.getTrader()).createMirrorItem(this, holder);
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public int getMaxAmount() {
        return 64;
    }

    @Override
    public MoneyOffer clone() {
        return new MoneyOffer(amount);
    }

    @Override
    public boolean isSimilar(Offer offer) {
        return offer instanceof MoneyOffer;
    }

    @Override
    public void grant(Trader trader, boolean nextTick) {
        grant(trader, true, amount);
    }

    @Override
    public void grant(Trader trader, boolean nextTick, double amount) {
        Trade.getInstance().getEconomy().depositPlayer(trader.getName(), amount);
        // TODO Check if successful
    }

}
