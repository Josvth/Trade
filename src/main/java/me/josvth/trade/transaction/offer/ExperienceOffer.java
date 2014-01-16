package me.josvth.trade.transaction.offer;

import me.josvth.trade.transaction.action.trader.offer.ChangeExperienceAction;
import me.josvth.trade.transaction.offer.description.ExperienceOfferDescription;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.util.ExperienceManager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ExperienceOffer extends StackableOffer {

    private int experience = 0;

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
        return getDescription(holder.getTrader()).createItem(this);
    }

    @Override
    public ItemStack createMirror(TransactionHolder holder) {
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
    public void grant(Trader trader) {
        grant(trader, experience);
    }

    public static void grant(Trader trader, int experience) {
        new ExperienceManager(trader.getPlayer()).changeExp(experience);
    }

    @Override
    public void onClick(InventoryClickEvent event, int offerIndex) {

		// We always cancel the event.
		event.setCancelled(true);

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

        if (event.isLeftClick()) {
            new ChangeExperienceAction(holder.getTrader(), holder.getTrader(), event.isShiftClick() ? getDescription(holder.getTrader()).getLargeModifier() : getDescription(holder.getTrader()).getSmallModifier()).execute();
        } else if (event.isRightClick()) {
            new ChangeExperienceAction(holder.getTrader(), holder.getTrader(), -1*(event.isShiftClick() ? getDescription(holder.getTrader()).getLargeModifier() : getDescription(holder.getTrader()).getSmallModifier())).execute();
        }

    }

    @Override
    public String toString() {
        return "EXP: " + experience;
    }

}
