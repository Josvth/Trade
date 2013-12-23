package me.josvth.trade.offer;

import me.josvth.trade.offer.description.ExperienceOfferDescription;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.util.ExperienceManager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ExperienceOffer extends Offer {

    private int experience = 0;

    public ExperienceOffer(OfferList list, int offerID) {
        super(list, offerID);
    }

    @Override
    public ExperienceOfferDescription getDescription() {
       return (ExperienceOfferDescription) super.getDescription();
    }

    @Override
    public ItemStack createItem() {
        return getDescription().createItem(this);
    }

    @Override
    public ItemStack createMirror(TransactionHolder holder) {
        return getDescription().createMirrorItem(this, holder);
    }

    public int add(int amount) {
        final int remainder = experience + amount - 64; // TODO Remove hard coded 64
        if (remainder > 0) {
            experience = 64;
            return remainder;
        } else {
            experience = experience + amount;
            return 0;
        }
    }

    public int remove(int amount) {
        final int remainder = experience - amount;
        if (remainder > 0) {
            experience = remainder;
            return 0;
        } else {
            experience = 0;
            return -1 * remainder;
        }
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    @Override
    public boolean isWorthless() {
        return experience <= 0;
    }

    @Override
    public void grant(Trader trader) {
        grant(trader, experience);
    }

    public static void grant(Trader trader, int experience) {
        new ExperienceManager(trader.getPlayer()).changeExp(experience);
    }

    @Override
    public void onClick(InventoryClickEvent event) {

		// We always cancel the event.
		event.setCancelled(true);

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

        if (event.isLeftClick()) {

            holder.getOffers().addExperience(event.isShiftClick()?  getDescription().getLargeModifier() : getDescription().getSmallModifier());

        } else if (event.isRightClick()) {

            holder.getOffers().removeExperience(event.isShiftClick()?  getDescription().getLargeModifier() : getDescription().getSmallModifier());

        }

    }

    @Override
    public String toString() {
        return "EXP: " + experience;
    }

}
