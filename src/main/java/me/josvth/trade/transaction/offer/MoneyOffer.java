package me.josvth.trade.transaction.offer;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.action.trader.offer.ChangeMoneyAction;
import me.josvth.trade.transaction.offer.description.MoneyOfferDescription;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MoneyOffer extends StackableOffer {

    private int amount = 0;

    public MoneyOffer() {
        this(0);
    }

    public MoneyOffer(int amount) {
        this.amount = amount;
    }

    @Override
    public String getType() {
        return "money";
    }

    @Override
    public MoneyOfferDescription getDescription(Trader trader) {
        return (MoneyOfferDescription) super.getDescription(trader);
    }

    @Override
    public ItemStack createItem(TransactionHolder holder) {
        return getDescription(holder.getTrader()).createItem(this);
    }

    @Override
    public ItemStack createMirror(TransactionHolder holder) {
        return getDescription(holder.getTrader()).createMirrorItem(this, holder);
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int getMaxAmount() {
        return 64;
    }

    public double getDoubleAmount() {
        return amount/100;
    }

	@Override
	public MoneyOffer clone() {
		return new MoneyOffer(amount);
	}

	@Override
	public void grant(Trader trader) {
        Trade.getInstance().getEconomy().depositPlayer(trader.getName(), amount/100);
    }

    @Override
    public void onClick(InventoryClickEvent event, int offerIndex) {

        // We always cancel the event.
        event.setCancelled(true);

        final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

        final int amount = event.isShiftClick() ? getDescription(holder.getTrader()).getLargeModifier() : getDescription(holder.getTrader()).getSmallModifier();

        if (amount <= 0) {  // If amount is smaller or equal to 0 we do nothing to allow disabling of shift clicking
            return;
        }

        if (event.isLeftClick()) {
            new ChangeMoneyAction(holder.getTrader(), amount).execute();
        } else if (event.isRightClick()) {
            new ChangeMoneyAction(holder.getTrader(), -1*amount).execute();
        }

    }

    public static MoneyOffer create(Trader trader, int amount) {
        final MoneyOffer offer = trader.getLayout().getOfferDescription(MoneyOffer.class).createOffer();
        offer.setAmount(amount);
        return offer;
    }
}
