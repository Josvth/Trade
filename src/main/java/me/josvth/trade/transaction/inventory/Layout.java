package me.josvth.trade.transaction.inventory;

import me.josvth.bukkitformatlibrary.message.FormattedMessage;
import me.josvth.bukkitformatlibrary.message.MessageHolder;
import me.josvth.trade.offer.Offer;
import me.josvth.trade.offer.description.ExperienceOfferDescription;
import me.josvth.trade.offer.description.ItemOfferDescription;
import me.josvth.trade.offer.description.OfferDescription;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Layout extends MessageHolder {

    private static final FormattedMessage DEFAULT_TITLE = new FormattedMessage("You%spaces%%other%");
    private static final Map<Class<? extends Offer>, OfferDescription> DEFAULT_OFFER_DESCRIPTIONS = new HashMap<Class<? extends Offer>, OfferDescription>();

    static {

        final ItemOfferDescription itemOfferDescription = new ItemOfferDescription();
        DEFAULT_OFFER_DESCRIPTIONS.put(itemOfferDescription.getOfferClass(), itemOfferDescription);

        final ExperienceOfferDescription experienceOfferDescription = new ExperienceOfferDescription();
        experienceOfferDescription.setExperienceItem(ItemStackUtils.setMeta(
                new ItemStack(Material.EXP_BOTTLE),
                "You added %levels% levels.",
                Arrays.asList(
                        "Left click to add %small% level(s)",
                        "Right click to remove %small% level(s)",
                        "Shift left click to add %large% levels",
                        "Shift right click to remove %large% levels")
        )
        );

        experienceOfferDescription.setExperienceItemMirror(ItemStackUtils.setMeta(
                new ItemStack(Material.EXP_BOTTLE),
                "%player% added %levels% levels.",
                null)
        );

        experienceOfferDescription.setSmallModifier(1);
        experienceOfferDescription.setLargeModifier(5);
        DEFAULT_OFFER_DESCRIPTIONS.put(experienceOfferDescription.getOfferClass(), experienceOfferDescription);

        // TODO add money offer

    }

    private final String name;
    private final int rows;
    // Messages
    private final Map<String, FormattedMessage> messages = new HashMap<String, FormattedMessage>();
    // Offer descriptions
    private final Map<Class<? extends Offer>, OfferDescription> offerDescriptions = new HashMap<Class<? extends Offer>, OfferDescription>(DEFAULT_OFFER_DESCRIPTIONS);
    private Slot[] slots;
    private int offerSize = 4;
    private FormattedMessage title = DEFAULT_TITLE;
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
        if (slots.length != rows * 9) {
            throw new IllegalArgumentException("Array length (" + slots.length + ") does not match layout size (" + rows * 9 + ").");
        }
        this.slots = slots;
    }

    public int getInventorySize() {
        return rows * 9;
    }

    public String generateTitle(TransactionHolder holder) {

        final int maxCharacters = 32;


        final StringBuilder titleBuilder = new StringBuilder(title.get("%other%", holder.getTrader().getName()));

        Matcher matcher = Pattern.compile("%spaces%").matcher(titleBuilder);

        if (matcher.find()) {
            final StringBuilder spacesBuilder = new StringBuilder();
            for (int i = 0; i < maxCharacters - (titleBuilder.length() - (matcher.end() - matcher.start())); i++) {
                spacesBuilder.append(" ");
            }
            titleBuilder.replace(matcher.start(), matcher.end(), spacesBuilder.toString());
        }

        if (titleBuilder.length() > maxCharacters) {
            return titleBuilder.substring(0, maxCharacters - 1);
        } else {
            return titleBuilder.toString();
        }

    }

    public FormattedMessage getFormattedMessage(String key) {
        FormattedMessage message = messages.get(key);
        if (message == null) {
            message = new FormattedMessage(key);
        }
        return message;
    }

    public boolean hasFormattedMessage(String key) {
        return messages.containsKey(key);
    }

    // Offer descriptions
    public <T extends OfferDescription> T getOfferDescription(Class<? extends Offer<T>> offerClass) {
        return (T) offerDescriptions.get(offerClass);
    }

    public <T extends OfferDescription> T setOfferDescription(Class<? extends Offer<T>> offerClass, T description) {
        if (description == null) {
            return (T) offerDescriptions.put(offerClass, DEFAULT_OFFER_DESCRIPTIONS.get(offerClass));
        } else {
            return (T) offerDescriptions.put(offerClass, description);
        }
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


}
