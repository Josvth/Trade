package me.josvth.trade.transaction.action.trader.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.offer.MoneyOffer;
import me.josvth.trade.transaction.inventory.offer.OfferList;
import me.josvth.trade.transaction.inventory.slot.MoneySlot;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class ChangeMoneyAction extends ChangeOfferAction {

    public ChangeMoneyAction(Trader trader, OfferList list, double amount) {
        super(trader, list);
        setOffer(MoneyOffer.create(getTrader(), Math.abs(amount)));
        setAddition(amount > 0);
    }

    private Economy getEconomy() {
        return getTrader().getTransaction().getPlugin().getEconomy();
    }

    @Override
    public void execute() {

        // First we check if the player has the money
        if (isAdd()) {

            if (!getEconomy().has(getTrader().getName(), getInitialAmount())) {
                getTrader().getFormattedMessage("money.insufficient").send(getPlayer(), "%money%", getEconomy().format(getInitialAmount()));
                return;
            }

        }

        // Execute super
        super.execute();

        if (isAdd()) {

            final EconomyResponse response = getEconomy().withdrawPlayer(getTrader().getName(), getChangedAmount());

            // TODO Check if withdraw success

            // Send messages
            getTrader().getFormattedMessage("money.added.self").send(getPlayer(), "%money%", getEconomy().format(getChangedAmount()), "%balance%", getEconomy().format(response.balance));

            if (getChangedAmount() > 0) {

                // Only send the other trader a message if something actually was changed
                getOtherTrader().getFormattedMessage("money.added.other").send(getOtherPlayer(), "%player%", getTrader().getName(), "%money%", getEconomy().format(getChangedAmount()));

                // Update money slots
                MoneySlot.updateMoneySlots(getTrader().getHolder(), true, getCurrentAmount());

            }

        } else {

            final EconomyResponse response = getEconomy().depositPlayer(getTrader().getName(), getChangedAmount());

            // TODO Check if deposit success

            // Send messages
            getTrader().getFormattedMessage("money.removed.self").send(getPlayer(), "%money%", getEconomy().format(getChangedAmount()), "%balance%", getEconomy().format(response.balance));

            if (getChangedAmount() > 0) {

                // Only send the other trader a message if something actually was changed
                getOtherTrader().getFormattedMessage("money.removed.other").send(getOtherPlayer(), "%player%", getTrader().getName(), "%money%", getEconomy().format(getChangedAmount()));

                // Update money slots
                MoneySlot.updateMoneySlots(getTrader().getHolder(), true, getCurrentAmount());

            }

        }

    }

}
