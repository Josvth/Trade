package me.josvth.trade.transaction.action.trader.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.slot.ExperienceSlot;
import me.josvth.trade.transaction.offer.MoneyOffer;
import net.milkbowl.vault.economy.Economy;

public class ChangeMoneyAction extends ChangeOfferAction {

    public ChangeMoneyAction(Trader trader, int amount) {
        super(trader, MoneyOffer.create(trader, Math.abs(amount)), amount > 0);
    }

    private Economy getEconomy() {
        return getTrader().getTransaction().getPlugin().getEconomy();
    }

    @Override
    public void execute() {

        if (isAdd()) {

            if (!getEconomy().has(getTrader().getName(), getInitialAmount() / 100)) {
                getTrader().getFormattedMessage("money.insufficient").send(getPlayer(), "%money%", getEconomy().format(getInitialAmount()/100));
                return;
            }

        }

        // Execute super
        super.execute();

        if (isAdd()) {

            // Take money from player
            final int added = getChangedAmount();

            // Send messages
            getTrader().getFormattedMessage("money.added.self").send(getPlayer(), "%money%", getEconomy().format(added/100));

            if (added > 0) {

                // Only send the other trader a message if something actually was changed
                getOtherTrader().getFormattedMessage("money.added.other").send(getOtherPlayer(), "%player%", getTrader().getName(), "%money%", String.valueOf(added));
                getEconomy().withdrawPlayer(getTrader().getName(), added);

            }

        } else {

            // Deposit money
            final int removed = getChangedAmount();

            // Send messages
            getTrader().getFormattedMessage("money.removed.self").send(getPlayer(), "%money%", String.valueOf(removed));

            if (removed > 0) {

                getEconomy().depositPlayer(getTrader().getName(), removed);

                // Only send the other trader a message if something actually was changed
                getOtherTrader().getFormattedMessage("money.removed.other").send(getOtherPlayer(), "%player%", getTrader().getName(), "%money%", String.valueOf(removed));

            }

        }

    }

}
