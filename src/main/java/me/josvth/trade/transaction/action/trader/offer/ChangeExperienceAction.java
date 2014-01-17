package me.josvth.trade.transaction.action.trader.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.ActionProvoker;
import me.josvth.trade.transaction.inventory.slot.ExperienceSlot;
import me.josvth.trade.transaction.offer.ExperienceOffer;
import me.josvth.trade.util.ExperienceManager;

public class ChangeExperienceAction extends ChangeOfferAction {

    public ChangeExperienceAction(Trader trader, int amount) {
        super(trader, ExperienceOffer.create(trader, Math.abs(amount)), amount > 0);
    }

    @Override
    public void execute() {

        final ExperienceManager expManager = new ExperienceManager(getPlayer());

        if (isAdd()) {

            if (!expManager.hasExp(getInitialAmount())) {
                getTrader().getFormattedMessage("experience.insufficient").send(getPlayer(), "%experience%", String.valueOf(getInitialAmount()));
                return;
            }

        }

        // Execute super
        super.execute();

        if (isAdd()) {

            // Take experience from player
            final int added = getChangedAmount();

            // Send messages
            getTrader().getFormattedMessage("experience.added.self").send(getPlayer(), "%experience%", String.valueOf(added));

            if (added > 0) {

                // Only send the other trader a message if something actually was changed
                getOtherTrader().getFormattedMessage("experience.added.other").send(getOtherPlayer(), "%experience%", String.valueOf(added));
                expManager.changeExp(-1 * added);

                // Update experience slots //TODO update amount on offers as well
                ExperienceSlot.updateExperienceSlots(getTrader().getHolder(), true, getCurrentAmount());

            }

        } else {

            // Grant experience
            final int removed = getChangedAmount();

            // Send messages
            getTrader().getFormattedMessage("experience.removed.self").send(getPlayer(), "%experience%", String.valueOf(removed));

            if (removed > 0) {

                expManager.changeExp(removed);

                // Only send the other trader a message if something actually was changed
                getOtherTrader().getFormattedMessage("experience.removed.other").send(getOtherPlayer(), "%experience%", String.valueOf(removed));

                // Update experience slots //TODO update amount on offers as well
                ExperienceSlot.updateExperienceSlots(getTrader().getHolder(), true, getCurrentAmount());

            }

        }

    }

}
