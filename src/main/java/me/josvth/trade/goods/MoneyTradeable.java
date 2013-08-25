package me.josvth.trade.goods;

public class MoneyTradeable extends Tradeable {

	@Override
	public TradeableType getType() {
		return TradeableType.MONEY;
	}

}
