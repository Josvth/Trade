package me.josvth.trade.goods;

import org.bukkit.inventory.ItemStack;

public class MoneyTradeable extends Tradeable {

	public MoneyTradeable() {
		super(TradeableType.MONEY);
	}

	@Override
	public Tradeable add(Tradeable tradeable) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ItemStack getItem() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isEmpty() {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

}
