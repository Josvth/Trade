package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.action.trader.offer.ChangeMoneyAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
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
		setSlot(holder, moneyItem);
	}

}
