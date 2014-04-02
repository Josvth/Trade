package me.josvth.trade.transaction.inventory.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.click.ClickBehaviour;
import me.josvth.trade.transaction.inventory.click.ClickContext;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.ContentSlot;
import me.josvth.trade.transaction.inventory.slot.ExperienceSlot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.transaction.inventory.offer.description.ExperienceOfferDescription;
import me.josvth.trade.util.ExperienceManager;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        super();
        this.experience = experience;
        setAllowedInInventory(true);
        setCanStayInInventory(false);
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
    public void grant(Trader trader, boolean nextTick) {
        grant(trader, true, experience);
    }

    public void grant(Trader trader, boolean nextTick, int experience) {
        new ExperienceManager(trader.getPlayer()).changeExp(experience);
    }

    @Override
    public String toString() {
        return "EXP: " + experience;
    }

}
