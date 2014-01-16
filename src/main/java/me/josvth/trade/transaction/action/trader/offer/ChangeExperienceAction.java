package me.josvth.trade.transaction.action.trader.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.ActionProvoker;
import me.josvth.trade.transaction.action.trader.status.DenyAction;
import me.josvth.trade.transaction.offer.ExperienceOffer;
import me.josvth.trade.transaction.offer.Offer;
import me.josvth.trade.transaction.offer.OfferResponse;
import me.josvth.trade.util.ExperienceManager;

import java.util.HashMap;
import java.util.Map;

public class ChangeExperienceAction extends OfferAction {

    private final int amount;

    private OfferResponse response;

    public ChangeExperienceAction(ActionProvoker provoker, Trader trader, int amount) {
        super(provoker, trader);
        this.amount = amount;
    }

    @Override
    public Map<Integer, ? extends Offer> getChanges() {
        return response.getChangedSlots();
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

            response = getTrader().getOffers().addOffer(offer);

            final int added = amount - offer.getAmount();

            expManager.changeExp(-1 * added);

            getTrader().getFormattedMessage("experience.added.self").send(getPlayer(), "%experience%", String.valueOf(added));
            getOtherTrader().getFormattedMessage("experience.added.other").send(getOtherPlayer(), "%experience%", String.valueOf(added));

        } else {

            final ExperienceOffer offer = getTrader().getLayout().getOfferDescription(ExperienceOffer.class).createOffer();

            getTrader().getOffers().removeOffer(offer);

            final int removed = -1 * amount - offer.getAmount();

            // Give the player experience
            new ExperienceManager(getPlayer()).changeExp(removed);

            getTrader().getFormattedMessage("experience.removed.self").send(getPlayer(), "%experience%", String.valueOf(removed));
            getOtherTrader().getFormattedMessage("experience.removed.other").send(getOtherPlayer(), "%experience%", String.valueOf(removed));

        }

        super.execute();

    }

}
