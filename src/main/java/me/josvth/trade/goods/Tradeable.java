package me.josvth.trade.goods;

public abstract class Tradeable {

	public abstract TradeableType getType();

	public enum TradeableType {
		ITEM,
		EXPERIENCE,
		MONEY;
	}

}
