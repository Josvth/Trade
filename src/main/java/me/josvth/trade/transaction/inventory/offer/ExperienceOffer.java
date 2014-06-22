package me.josvth.trade.transaction.inventory.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.offer.description.ExperienceOfferDescription;
import me.josvth.trade.util.ExperienceManager;
import org.bukkit.inventory.ItemStack;

public class ExperienceOffer extends Offer {

    public static final String TYPE_NAME = "experience";

    private double experience = 0;

    public ExperienceOffer() {
        this(0);
    }

    public ExperienceOffer(double experience) {
        super();
        this.experience = experience;
        setAllowedInInventory(true);
        setCanStayInInventory(false);
    }

    public static ExperienceOffer create(Trader trader, int amount) {
        final ExperienceOffer offer = trader.getLayout().getOfferDescription(ExperienceOffer.class).createOffer();
        offer.setAmount(amount);
        return offer;
    }

    @Override
    public String getType() {
        return "experience";
    }

    @Override
    public ExperienceOfferDescription getDescription(Trader trader) {
        return (ExperienceOfferDescription) super.getDescription(trader);
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
        return experience;
    }

    @Override
    public void setAmount(double amount) {
        this.experience = amount;
    }

    @Override
    public int getMaxAmount() {
        return 64;
    }

    @Override
    public boolean isWorthless() {
        return experience <= 0;
    }

    @Override
    public ExperienceOffer clone() {
        return new ExperienceOffer(experience);
    }

    @Override
    public boolean isSimilar(Offer offer) {
        return offer instanceof ExperienceOffer;
    }

    @Override
    public void grant(Trader trader, boolean nextTick) {
        grant(trader, true, experience);
    }

    public void grant(Trader trader, boolean nextTick, double experience) {
        new ExperienceManager(trader.getPlayer()).changeExp(experience);
    }

}
