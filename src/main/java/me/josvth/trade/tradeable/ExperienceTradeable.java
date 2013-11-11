package me.josvth.trade.tradeable;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.util.ItemDescription;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ExperienceTradeable extends Tradeable {

	private static final ItemDescription DEFAULT_ITEM_DESCRIPTION = new ItemDescription(
			Material.EXP_BOTTLE, 0, (short)0, (byte)0,
			new FormattedMessage("You added %levels% levels."),
			Arrays.asList(new FormattedMessage[]{
					new FormattedMessage("Left click to add %small% level(s)"),
					new FormattedMessage("Right click to remove %small% level(s)"),
					new FormattedMessage("Shift left click to add %large% levels"),
					new FormattedMessage("Shift right click to remove %large% levels")})
	);

	private static final ItemDescription DEFAULT_OTHER_ITEM_DESCRIPTION  = new ItemDescription(
			Material.EXP_BOTTLE, 0, (short)0, (byte)0,
			new FormattedMessage("You added %levels% levels."),
			null
	);

	private int levels = 0;

	private final ItemDescription itemDescription;
	private final ItemDescription otherItemDescription;

	public ExperienceTradeable() {
		this(0, DEFAULT_ITEM_DESCRIPTION, DEFAULT_OTHER_ITEM_DESCRIPTION);
	}

	public ExperienceTradeable(int levels) {
		this(levels, DEFAULT_ITEM_DESCRIPTION, DEFAULT_OTHER_ITEM_DESCRIPTION);
	}

	public ExperienceTradeable(int levels, ItemDescription itemDescription, ItemDescription otherItemDescription) {
		super(TradeableType.EXPERIENCE);
		this.levels = levels;
		this.itemDescription = itemDescription;
		this.otherItemDescription = otherItemDescription;
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

		if (remaining.getLevels() > 0) {

			levels -= remaining.getLevels();

			if (levels < 0) {
				remaining.setLevels(-1 * levels);
				levels = 0;
				return (T) remaining;
			}

		}

		return null;

	}

	public int getLevels() {
		return levels;
	}

	public void setLevels(int levels) {
		this.levels = levels;
	}

	@Override
	public ItemStack getDisplayItem() {
		return itemDescription.create("%levels%", String.valueOf(levels));
	}

	@Override
	public ItemStack getOtherDisplayItem() {
		return otherItemDescription.create("%levels%", String.valueOf(levels));
	}

	@Override
	public boolean isWorthless() {
		return levels <= 0;
	}

	@Override
	public void grant(Trader trader) {
		grant(trader, levels);
	}

	public static void grant(Trader trader, int levels) {
		trader.getPlayer().setLevel(trader.getPlayer().getLevel() + levels);
	}

	public ExperienceTradeable clone() {
		return new ExperienceTradeable(levels);
	}

	@Override
	public void onClick(InventoryClickEvent event) {

	}

	@Override
	public String toString() {
		return "EXP: " + levels;
	}
}
