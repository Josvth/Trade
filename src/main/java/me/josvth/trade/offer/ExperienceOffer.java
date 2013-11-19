package me.josvth.trade.offer;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.util.ItemDescription;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ExperienceOffer extends Offer {

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

	public ExperienceOffer(OfferList list, int offerID) {
		this(list, offerID, 0, DEFAULT_ITEM_DESCRIPTION, DEFAULT_OTHER_ITEM_DESCRIPTION);
	}

	public ExperienceOffer(OfferList list, int offerID, int levels) {
		this(list, offerID, levels, DEFAULT_ITEM_DESCRIPTION, DEFAULT_OTHER_ITEM_DESCRIPTION);
	}

	public ExperienceOffer(OfferList list, int offerID, int levels, ItemDescription itemDescription, ItemDescription otherItemDescription) {
		super(list, offerID);
		this.levels = levels;
		this.itemDescription = itemDescription;
		this.otherItemDescription = otherItemDescription;
	}

    public int add(int amount) {
        final int remainder = levels + amount - 64; // TODO Remove hard coded 64
        if (remainder > 0) {
            levels = 64;
            return remainder;
        } else {
            levels = levels + amount;
            return 0;
        }
    }

    public int remove(int amount) {
        final int remainder = levels - amount;
        if (remainder > 0) {
            levels = remainder;
            return 0;
        } else {
            levels = 0;
            return -1 * remainder;
        }
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

	public ExperienceOffer clone() {
		return new ExperienceOffer(list, offerIndex, levels, itemDescription, otherItemDescription);
	}

	@Override
	public void onClick(InventoryClickEvent event) {

	}

	@Override
	public String toString() {
		return "EXP: " + levels;
	}


}
