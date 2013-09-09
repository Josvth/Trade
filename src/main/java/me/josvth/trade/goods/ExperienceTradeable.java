package me.josvth.trade.goods;

import org.bukkit.inventory.ItemStack;

public class ExperienceTradeable extends Tradeable {

	public ExperienceTradeable() {
		super(TradeableType.EXPERIENCE);
	}


	@Override
	public <T extends Tradeable> T add(T tradeable) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ItemStack getDisplayItem() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isWorthless() {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
