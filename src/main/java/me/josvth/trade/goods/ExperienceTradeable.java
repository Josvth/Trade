package me.josvth.trade.goods;

import org.bukkit.inventory.ItemStack;

public class ExperienceTradeable extends Tradeable {

	public ExperienceTradeable() {
		super(TradeableType.EXPERIENCE);
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
