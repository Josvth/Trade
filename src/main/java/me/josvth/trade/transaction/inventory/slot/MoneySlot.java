package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.action.trader.offer.ChangeMoneyAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.offer.MoneyOffer;
import me.josvth.trade.transaction.offer.OfferList;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MoneySlot extends Slot {

	private final ItemStack moneyItem;

	private final int smallModifier;
	private final int largeModifier;

	public MoneySlot(int slot, ItemStack moneyItem, int smallModifier, int largeModifier) {
		super(slot);
		this.moneyItem = moneyItem;
		this.smallModifier = smallModifier;
		this.largeModifier = largeModifier;
	}

    @Override
    public void onClick(InventoryClickEvent event) {

        // We always cancel the event.
        event.setCancelled(true);

        final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

        final int amount = event.isShiftClick() ? largeModifier : smallModifier;

        if (amount <= 0) { // If amount is smaller or equal to 0 we do nothing to allow disabling of shift clicking
            return;
        }

        if (event.isLeftClick()) {
            new ChangeMoneyAction(holder.getTrader(), amount).execute();
        } else if (event.isRightClick()) {
            new ChangeMoneyAction(holder.getTrader(), -1*amount).execute();
        }

    }


    @Override
    public void update(TransactionHolder holder) {
        update(holder, getMoney(holder.getOffers()));
    }

    private static int getMoney(OfferList offers) {
        int money = 0;
        for (MoneyOffer tradeable : offers.getOfClass(MoneyOffer.class).values()) {
            money += tradeable.getAmount();
        }
        return money;
    }

    public void update(TransactionHolder holder, int money) {
        final double divider = Math.pow(10, holder.getEconomy().fractionalDigits());
        setSlot(
                holder,
                ItemStackUtils.argument(
                        moneyItem.clone(),
                        "%money%", holder.getEconomy().format(money / divider),
                        "%small%", holder.getEconomy().format(smallModifier / divider),
                        "%large%", holder.getEconomy().format(largeModifier / divider)
                )
        );
    }

}
