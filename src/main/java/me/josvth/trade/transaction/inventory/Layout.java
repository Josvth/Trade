package me.josvth.trade.transaction.inventory;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import me.josvth.trade.offer.*;
import me.josvth.trade.offer.ExperienceOffer;
import me.josvth.trade.offer.Offer;
import me.josvth.trade.transaction.inventory.slot.*;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Layout {

	private static final ItemStack DEFAULT_EXPERIENCE_ITEM = ItemStackUtils.setMeta(
			new ItemStack(Material.EXP_BOTTLE),
			"You added %levels% levels.",
			Arrays.asList(new String[]{
					"Left click to add %small% level(s)",
					"Right click to remove %small% level(s)",
					"Shift left click to add %large% levels",
					"Shift right click to remove %large% levels"})
	);

	private static final ItemStack DEFAULT_EXPERIENCE_ITEM_MIRROR = ItemStackUtils.setMeta(
			new ItemStack(Material.EXP_BOTTLE),
			"%player% added %levels% levels.",
			null);

	private static final FormattedMessage DEFAULT_TITLE = new FormattedMessage("You%spaces%%other%");

	private final String name;
	private final int rows;

	private Slot[] slots;
	private int offerSize = 4;

	private FormattedMessage title = DEFAULT_TITLE;

	// Offer properties
	private int experienceSmallModifier = 1;
	private int experienceLargeModifier = 5;
   	private ItemStack experienceItem = DEFAULT_EXPERIENCE_ITEM;
	private ItemStack experienceItemMirror = DEFAULT_EXPERIENCE_ITEM_MIRROR;

	// Layout options
	private int priority = -1;
	private String permission = null;
	private boolean shared = true;

	public Layout(String name, int rows, int offerSize) {
		this.name = name;
		this.rows = rows;
		this.offerSize = offerSize;
		this.slots = new Slot[rows * 9];
	}

	public String getName() {
		return name;
	}

	public int getRows() {
		return rows;
	}

	public int getOfferSize() {
		return offerSize;
	}

	public void setOfferSize(int size) {
		this.offerSize = size;
	}

	public Slot[] getSlots() {
		return slots;
	}

	public void setSlots(Slot[] slots) {
		if (slots.length != rows*9) {
			throw new IllegalArgumentException("Array length ("+ slots.length+") does not match layout size ("+rows*9+").");
		}
		this.slots = slots;
	}
	public int getInventorySize() {
		return rows * 9;
	}

	public String generateTitle(TransactionHolder holder) {

		final int maxCharacters = 64;

		final StringBuilder titleBuilder = new StringBuilder(title.get("%other%", holder.getTrader().getName()));

		Matcher matcher = Pattern.compile("%spaces%").matcher(titleBuilder);

		if (matcher.find()) {
			final StringBuilder spacesBuilder = new StringBuilder();
			for (int i = 0; i < maxCharacters - (titleBuilder.length() - (matcher.end() - matcher.start())); i++) {
				spacesBuilder.append(" ");
			}
			titleBuilder.replace(matcher.start(), matcher.end(), spacesBuilder.toString());
		}

		if (titleBuilder.)
		return titleBuilder.substring(0, maxCharacters - 1);

	}

	// Offer properties
	public int getExperienceSmallModifier() {
		return experienceSmallModifier;
	}

	public void setExperienceSmallModifier(int experienceSmallModifier) {
		this.experienceSmallModifier = experienceSmallModifier;
	}

	public int getExperienceLargeModifier() {
		return experienceLargeModifier;
	}

	public void setExperienceLargeModifier(int experienceLargeModifier) {
		this.experienceLargeModifier = experienceLargeModifier;
	}

	public ItemStack getExperienceItem() {
		return experienceItem;
	}

	public void setExperienceItem(ItemStack experienceItem) {
		this.experienceItem = (experienceItem == null)? DEFAULT_EXPERIENCE_ITEM : experienceItem;
	}

	public ItemStack getExperienceItemMirror() {
		return experienceItemMirror;
	}

	public void setExperienceItemMirror(ItemStack experienceItemMirror) {
		this.experienceItemMirror = (experienceItemMirror == null)? DEFAULT_EXPERIENCE_ITEM_MIRROR : experienceItem;
	}

	// Layout options
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public <T extends Slot> Set<T> getSlotsOfType(Class<T> clazz) {

		final Set<T> set = new HashSet<T>();

		for (Slot slot : slots) {
			if (clazz.isInstance(slot)) {
				set.add((T) slot);
			}
		}

		return set;

	}

    public <T extends Offer> T createOffer(Class<T> clazz, OfferList offerList, int offerIndex) {

        if (ItemOffer.class.isAssignableFrom(clazz)) {
            return (T) new ItemOffer(offerList, offerIndex);
        } else if (ExperienceOffer.class.isAssignableFrom(clazz)) {
            return (T) new ExperienceOffer(offerList, offerIndex, experienceSmallModifier, experienceLargeModifier, experienceItem, experienceItemMirror);
        } else if (MoneyOffer.class.isAssignableFrom(clazz)) {
            return (T) new MoneyOffer(offerList, offerIndex);
        }

        throw new IllegalArgumentException("Class is not supported.");

    }
}
