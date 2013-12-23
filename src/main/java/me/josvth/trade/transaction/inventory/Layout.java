package me.josvth.trade.transaction.inventory;

import me.josvth.bukkitformatlibrary.message.FormattedMessage;
import me.josvth.bukkitformatlibrary.message.MessageHolder;
import me.josvth.trade.transaction.offer.Offer;
import me.josvth.trade.transaction.offer.description.OfferDescription;
import me.josvth.trade.transaction.inventory.slot.Slot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Layout extends MessageHolder {

    private final String name;

    private int rows;
    private Slot[] slots;
    private int offerSize = 4;
    private FormattedMessage title = new FormattedMessage("");

    // Offer descriptions
    private final Map<Class<? extends Offer>, OfferDescription> offerDescriptions = new HashMap<Class<? extends Offer>, OfferDescription>();

    // Layout options
    private int priority = -1;
    private String permission = null;
    private boolean shared = true;

    public Layout(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
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

        final StringBuilder titleBuilder = new StringBuilder(title.get("%other%", holder.getOtherTrader().getName()));

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

    // Offer descriptions
    public <T extends Offer> OfferDescription<T> getOfferDescription(Class<T> offerClass) {
        return offerDescriptions.get(offerClass);
    }

    public Map<Class<? extends Offer>, OfferDescription> getOfferDescriptions() {
        return offerDescriptions;
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

    public void setTitle(FormattedMessage title) {
        this.title = title;
    }
}
