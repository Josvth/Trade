package me.josvth.trade.goods;

import org.bukkit.inventory.ItemStack;

public class ItemTradeable extends Tradeable {


	public ItemTradeable(ItemStack item) {

	}

	@Override
	public TradeableType getType() {
		return TradeableType.ITEM;
	}
}
