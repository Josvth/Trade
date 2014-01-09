package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.offer.ExperienceOffer;
import me.josvth.trade.util.ExperienceManager;
import org.bukkit.entity.Player;

public class ChangeExperienceAction extends TraderAction {

    private final int amount;

    public ChangeExperienceAction(ActionProvoker provoker, Trader trader, int amount) {
        super(provoker, trader);
        this.amount = amount;
    }

    @Override
    public void execute() {

        if (amount > 0) {

            final ExperienceManager expManager = new ExperienceManager(getPlayer());

            if (!expManager.hasExp(amount)) {
                getTrader().getFormattedMessage("experience.insufficient").send(getPlayer(), "%experience%", String.valueOf(amount));
                return;
            }

            final ExperienceOffer offer = getTrader().getLayout().getOfferDescription(ExperienceOffer.class).createOffer();

            getTrader().getOffers().addOffer(offer);

            final int added = amount - offer.getAmount();

            expManager.changeExp(-1 * added);

            getTrader().getFormattedMessage("experience.added.self").send(getPlayer(), "%experience%", String.valueOf(added));
            if (getOtherTrader().hasFormattedMessage("experience.added.other")) {
                getOtherTrader().getFormattedMessage("experience.added.other").send(getOtherTrader().getPlayer(), "%player%", getTrader().getName(), "%experience%", String.valueOf(added));
            }

            getOtherTrader().cancelAccept();

        } else {

            final ExperienceOffer offer = getTrader().getLayout().getOfferDescription(ExperienceOffer.class).createOffer();

            getTrader().getOffers().removeOffer(offer);

            final int removed = -1 * amount - offer.getAmount();

            // Give the player experience
            new ExperienceManager(getPlayer()).changeExp(removed);

            if (getOtherTrader().hasFormattedMessage("experience.removed.other") && removed > 0) {
                getOtherTrader().getFormattedMessage("experience.removed.other").send(getOtherTrader().getPlayer(), "%player%", getTrader().getName(), "%experience%", String.valueOf(removed));
            }

            getOtherTrader().cancelAccept();

        }

    }

    @Override
    public String getLogMessage() {
        return null;
    }
}
