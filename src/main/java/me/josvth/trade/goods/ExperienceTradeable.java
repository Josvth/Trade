package me.josvth.trade.goods;

import me.josvth.trade.transaction.Trader;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ExperienceTradeable extends Tradeable {

	private int levels = 0;

	public ExperienceTradeable() {
		this(0);
	}

	public ExperienceTradeable(int levels) {
		super(TradeableType.EXPERIENCE);
		this.levels = levels;
	}

	@Override
	public <T extends Tradeable> T add(T tradeable) {

		if (!(tradeable instanceof ExperienceTradeable)) {
			return tradeable;
		}

		ExperienceTradeable remaining = ((ExperienceTradeable) tradeable).clone();

		int newLevels = levels + remaining.getLevels();

		final int overflow = newLevels - 64;

		if (overflow > 0) {
			newLevels -= overflow;
			levels = newLevels;
			remaining.setLevels(overflow);
			return (T) remaining;
		} else {
			setLevels(newLevels);
			return null;
		}

	}

	@Override
	public <T extends Tradeable> T remove(T tradeable) {

		if (!(tradeable instanceof ExperienceTradeable)) {
			return tradeable;
		}

		ExperienceTradeable remaining = ((ExperienceTradeable) tradeable).clone();

		levels -= remaining.getLevels();

		if (levels < 0) {
			levels = 0;
			remaining.setLevels(-1 * levels);
			return (T) remaining;
		} else {
			return null;
		}

	}

	@Override
	public ItemStack getDisplayItem() {
		return new ItemStack(Material.EXP_BOTTLE, levels);
	}

	@Override
	public boolean isWorthless() {
		return levels <= 0;
	}

	@Override
	public void grant(Trader trader) {
		grant(trader, levels);
	}

	public ExperienceTradeable clone() {
		return new ExperienceTradeable(levels);
	}

	public static void grant(Trader trader, int levels) {
		trader.getPlayer().setLevel(trader.getPlayer().getLevel() + levels);
	}

	public int getLevels() {
		return levels;
	}

	public void setLevels(int levels) {
		this.levels = levels;
	}

	@Override
	public String toString() {
		return "EXP: " + levels;
	}
}
