package me.josvth.trade.transaction.action;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.offer.ExperienceOffer;
import me.josvth.trade.util.ExperienceManager;

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
                getTrader().sendFormattedMessage("experience.insufficient", false, "%experience%", String.valueOf(amount));
                return;
            }

            final ExperienceOffer offer = getTrader().getLayout().getOfferDescription(ExperienceOffer.class).createOffer();

            getTrader().getOffers().addOffer(offer);

            final int added = amount - offer.getAmount();

            expManager.changeExp(-1 * added);

            getTrader().sendFormattedMessage("experience.added.self", false, "%experience%", String.valueOf(added));
            getOtherTrader().sendFormattedMessage("experience.added.other", false, "%experience%", String.valueOf(added));

            new DenyAction(getTransaction().getTransactionProvoker(), getTrader(), DenyAction.Reason.OFFER_CHANGED).execute();

        } else {

            final ExperienceOffer offer = getTrader().getLayout().getOfferDescription(ExperienceOffer.class).createOffer();

            getTrader().getOffers().removeOffer(offer);

            final int removed = -1 * amount - offer.getAmount();

            // Give the player experience
            new ExperienceManager(getPlayer()).changeExp(removed);

            getTrader().sendFormattedMessage("experience.removed.self", false, "%experience%", String.valueOf(removed));
            getOtherTrader().sendFormattedMessage("experience.removed.other", false, "%experience%", String.valueOf(removed));

            new DenyAction(getTransaction().getTransactionProvoker(), getTrader(), DenyAction.Reason.OFFER_CHANGED).execute();

        }

    }

}
