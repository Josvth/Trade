package me.josvth.trade.transaction.inventory;

import me.josvth.bukkitformatlibrary.message.FormattedMessage;
import me.josvth.bukkitformatlibrary.message.MessageHolder;
import me.josvth.trade.transaction.inventory.offer.Offer;
import me.josvth.trade.transaction.inventory.offer.description.OfferDescription;
import me.josvth.trade.transaction.inventory.slot.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Layout extends MessageHolder {

    private final String name;

    private final LayoutManager manager;
    // Offer descriptions
    private final Map<Class<? extends Offer>, OfferDescription> offerDescriptions = new HashMap<Class<? extends Offer>, OfferDescription>();
    // Slot descriptions
    private final Map<Integer, SlotDescription> slotDescriptions = new HashMap<Integer, SlotDescription>();
    private int guiRows;
    private int offerSize = 4;
    private FormattedMessage title = new FormattedMessage("");
    // Layout options
    private int priority = -1;
    private String permission = null;
    private boolean shared = true;

    public Layout(String name, LayoutManager manager) {
        this.name = name;
        this.manager = manager;
    }

    public String getName() {
        return name;
    }

    public int getGuiRows() {
        return guiRows;
    }

    public void setGuiRows(int rows) {
        this.guiRows = rows;
    }

    public int getOfferSize() {
        return offerSize;
    }

    public void setOfferSize(int size) {
        this.offerSize = size;
    }

    public int getGuiSize() {
        return getGuiRows() * 9;
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
    public <T> OfferDescription<T> getOfferDescription(Class<T> offerClass) {
        return offerDescriptions.get(offerClass);
    }

    public Map<Class<? extends Offer>, OfferDescription> getOfferDescriptions() {
        return offerDescriptions;
    }

    // Slot descriptions
    public Map<Integer, SlotDescription> getSlotDescriptions() {
        return slotDescriptions;
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

    public void setTitle(FormattedMessage title) {
        this.title = title;
    }

    public Slot[] createSlots(TransactionHolder holder) {

        final Slot[] slots = new Slot[getGuiSize() + LayoutManager.PLAYER_INVENTORY_SIZE];

        for (Map.Entry<Integer, SlotDescription> entry : getSlotDescriptions().entrySet()) {

            // If the type is money we check first if economy is enabled
            if (MoneySlot.TYPE_NAME.equalsIgnoreCase(entry.getValue().getType()) && !manager.getPlugin().useEconomy()) {

                slots[entry.getKey()] = null;

            } else {

                final Class<? extends Slot> slotClass = manager.getRegisteredSlots().get(entry.getValue().getType());

                Slot slot = null;

                try {
                    slot = (Slot) slotClass.getMethod("deserialize", int.class, TransactionHolder.class, SlotDescription.class).invoke(null, entry.getKey(), holder, entry.getValue());
                } catch (Exception e) {
                    try {
                        slot = slotClass.getConstructor(int.class).newInstance(entry.getKey(), holder);
                    } catch (Exception ignored) {

                    }
                }

                slots[entry.getKey()] = slot;

            }

        }

        // Set upper slots of player inventory
        for (int i = 9; i < LayoutManager.PLAYER_INVENTORY_SIZE; i++) {
            final InventorySlot inventorySlot = new InventorySlot(i - 9 + getGuiSize(), holder);
            inventorySlot.setInventorySlot(i);
            slots[inventorySlot.getSlot()] = inventorySlot;
        }

        // Set lower slots of player inventory (hotbar)
        for (int i = 0; i < 9; i++) {
            final InventorySlot inventorySlot = new InventorySlot(i + getGuiSize() + LayoutManager.PLAYER_INVENTORY_SIZE - 9, holder);
            inventorySlot.setInventorySlot(i);
            slots[inventorySlot.getSlot()] = inventorySlot;
        }

        return slots;

    }

    public OutsideSlot getOutsideSlot(TransactionHolder holder) {
        return null;
    }
}
