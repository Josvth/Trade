package me.josvth.trade.offer;

import me.josvth.trade.offer.description.ExperienceOfferDescription;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ExperienceOffer extends Offer {

    private int levels = 0;

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
        final int remainder = levels + amount - 64; // TODO Remove hard coded 64
        if (remainder > 0) {
            levels = 64;
            return remainder;
        } else {
            levels = levels + amount;
            return 0;
        }
    }

    public int remove(int amount) {
        final int remainder = levels - amount;
        if (remainder > 0) {
            levels = remainder;
            return 0;
        } else {
            levels = 0;
            return -1 * remainder;
        }
    }

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    @Override
    public boolean isWorthless() {
        return levels <= 0;
    }

    @Override
    public void grant(Trader trader) {
        grant(trader, levels);
    }

    public static void grant(Trader trader, int levels) {
        trader.getPlayer().setLevel(trader.getPlayer().getLevel() + levels);
    }

    @Override
    public void onClick(InventoryClickEvent event) {

		// We always cancel the event.
		event.setCancelled(true);

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

        final Trader trader = holder.getTrader();

        final Player player = (Player) event.getWhoClicked();

		if (event.isLeftClick()) {

			final int levelsToAdd = event.isShiftClick()? getDescription().getLargeModifier() : getDescription().getSmallModifier();

			if (player.getLevel() < levelsToAdd) {
                trader.getFormattedMessage("experience.insufficient").send(player, "%levels%", String.valueOf(levelsToAdd));
				return;
			}

			final int remainder = holder.getOffers().removeExperience(levelsToAdd);

			player.setLevel(player.getLevel() - levelsToAdd);

            trader.getFormattedMessage("experience.added.self").send(player, "%levels%", String.valueOf(levelsToAdd - remainder));
            if (trader.getOtherTrader().hasFormattedMessage("experience.added.other")) {
                trader.getOtherTrader().getFormattedMessage("experience.added.other").send(trader.getPlayer(), "%player%", player.getName(), "%levels%", String.valueOf(levelsToAdd - remainder));
            }

		} else if (event.isRightClick()) {

			final int levelsToRemove = event.isShiftClick()? getDescription().getLargeModifier() : getDescription().getSmallModifier();

			final int remainder = holder.getOffers().removeExperience(levelsToRemove);

			player.setLevel(player.getLevel() + levelsToRemove - remainder);

            trader.getFormattedMessage("experience.removed.self").send(player, "%levels%", String.valueOf(levelsToRemove - remainder));
            if (trader.getOtherTrader().hasFormattedMessage("experience.removed.other")) {
                trader.getOtherTrader().getFormattedMessage("experience.removed.other").send(trader.getPlayer(), "%player%", player.getName(), "%levels%", String.valueOf(levelsToRemove - remainder));
            }

		}

    }

    @Override
    public String toString() {
        return "EXP: " + levels;
    }

}
