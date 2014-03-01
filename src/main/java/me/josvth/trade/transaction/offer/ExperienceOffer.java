package me.josvth.trade.transaction.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.offer.description.ExperienceOfferDescription;
import me.josvth.trade.util.ExperienceManager;
import org.bukkit.inventory.ItemStack;

public class ExperienceOffer extends StackableOffer {

    private int experience = 0;

    public static ExperienceOffer create(Trader trader, int amount) {
        final ExperienceOffer offer = trader.getLayout().getOfferDescription(ExperienceOffer.class).createOffer();
        offer.setAmount(amount);
        return offer;
    }

    public ExperienceOffer() {
        this(0);
    }

    public ExperienceOffer(int experience) {
        this.experience = experience;
    }

    @Override
    public String getType() {
        return null;
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
    public int getAmount() {
        return experience;
    }

    @Override
    public void setAmount(int amount) {
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
    public boolean isSimilar(StackableOffer contents) {
        return contents instanceof ExperienceOffer;
    }

    @Override
    public void grant(Trader trader) {
        grant(trader, experience);
    }

    public void grant(Trader trader, int experience) {
        new ExperienceManager(trader.getPlayer()).changeExp(experience);
    }

    @Override
    public String toString() {
        return "EXP: " + experience;
    }

}
