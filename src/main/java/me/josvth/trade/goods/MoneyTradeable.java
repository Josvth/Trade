package me.josvth.trade.goods;

import me.josvth.trade.transaction.Trader;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MoneyTradeable extends Tradeable {

	private double amount = 0;

	public MoneyTradeable() {
		super(TradeableType.MONEY);
	}

	public MoneyTradeable(double amount) {
		super(TradeableType.MONEY);
		this.amount = amount;
	}

	@Override
	public <T extends Tradeable> T add(T tradeable) {

		if (!(tradeable instanceof MoneyTradeable))
			return tradeable;

		MoneyTradeable remaining = ((MoneyTradeable) tradeable).clone();

		amount += remaining.getAmount();

		return null;

	}

	@Override
	public <T extends Tradeable> T remove(T tradeable) {

		if (!(tradeable instanceof MoneyTradeable)) {
			return tradeable;
		}

		MoneyTradeable remaining = ((MoneyTradeable) tradeable).clone();

		amount -= remaining.getAmount();

		if (amount < 0) {
			amount = 0.0;
			remaining.setAmount(-1 * amount);
			return (T) remaining;
		} else {
			return null;
		}

	}

	@Override
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	protected MoneyTradeable clone() {
		return new MoneyTradeable(amount);
	}

	@Override
	public ItemStack getDisplayItem() {

		final ItemStack item = new ItemStack(Material.GOLD_INGOT, 0);

		final ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(amount + " monies");

		item.setItemMeta(meta);

		return item;

	}

	@Override
	public void grant(Trader trader) {

	}

}
