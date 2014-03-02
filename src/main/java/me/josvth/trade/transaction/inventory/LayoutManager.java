package me.josvth.trade.transaction.inventory;

import me.josvth.bukkitformatlibrary.message.FormattedMessage;
import me.josvth.bukkitformatlibrary.message.managers.MessageManager;
import me.josvth.trade.Trade;
import me.josvth.trade.transaction.inventory.slot.*;
import me.josvth.trade.transaction.offer.Offer;
import me.josvth.trade.transaction.offer.description.ExperienceOfferDescription;
import me.josvth.trade.transaction.offer.description.ItemOfferDescription;
import me.josvth.trade.transaction.offer.description.MoneyOfferDescription;
import me.josvth.trade.transaction.offer.description.OfferDescription;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class LayoutManager {

    public static final int PLAYER_INVENTORY_SIZE = InventoryType.PLAYER.getDefaultSize();

    private final Trade plugin;
    private final MessageManager messageManager;

    private final Map<String, Class<? extends Slot>> registeredSlots = new HashMap<String, Class<? extends Slot>>();

    private final Map<String, Layout> layouts = new HashMap<String, Layout>();

    public LayoutManager(Trade plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
    }

    public void load(ConfigurationSection layoutSection, ConfigurationSection messageSection, ConfigurationSection offerSection) {

        final Map<String, FormattedMessage> defaultMessages = getMessagesFromSection(messageSection);
        final Map<Class<? extends Offer>, OfferDescription> defaultOfferDescriptions = getOfferDescriptionsFromSection(offerSection);

        // First we load our default default layout
        final Layout defaultLayout = new Layout("default", this);
        loadLayout(defaultLayout, layoutSection.getDefaultSection().getConfigurationSection("default"), defaultMessages, defaultOfferDescriptions);

        // Then we loud the other layouts in the section. Overriding our default if necessary
        for (String key : layoutSection.getKeys(false)) {

            try {
                if (layoutSection.isConfigurationSection(key)) {
                    Layout layout = layouts.get(key);
                    if (layout == null) {
                        layout = new Layout(key, this);
                    }

                    loadLayout(layout, layoutSection.getConfigurationSection(key), defaultMessages, defaultOfferDescriptions);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        }

    }

    private void loadLayout(Layout layout, ConfigurationSection section, Map<String, FormattedMessage> defaultMessages, Map<Class<? extends Offer>, OfferDescription> defaultOfferDescriptions) throws IllegalArgumentException {

        if (section == null) {
            throw new IllegalArgumentException("Section is null.");
        }

        layout.getMessages().putAll(defaultMessages);
        layout.getOfferDescriptions().putAll(defaultOfferDescriptions);

        layout.setGuiRows(section.getInt("rows"));

        layout.setOfferSize(section.getInt("offer-size", 4));

        layout.setTitle(new FormattedMessage(messageManager.preformatMessage(section.getString("title"))));

        layout.setPermission(section.getString("permission"));
        layout.setPriority(section.getInt("priority", 0));
        layout.setShared(section.getBoolean("shared", true));

        final int slotSize = layout.getGuiSize() + PLAYER_INVENTORY_SIZE;

        // Load slots
        if (section.isConfigurationSection("slots")) {

            for (String slotKey : section.getConfigurationSection("slots").getKeys(false)) {

                if (section.getConfigurationSection("slots").isConfigurationSection(slotKey)) {

                    try {

                        final int slotID = Integer.parseInt(slotKey);

                        if (slotID >= 0 && slotID < slotSize) {

                            final ConfigurationSection slotSection = section.getConfigurationSection("slots." + slotKey);

                            if (slotSection != null && slotSection.isString("type")) {
                                layout.getSlotDescriptions().put(slotID, new SlotDescription(slotSection.getString("type"), slotSection));
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

            // Set inventory slots
            for (int i = layout.getGuiSize(); i < layout.getGuiSize() + PLAYER_INVENTORY_SIZE; i++) {
                layout.getSlotDescriptions().put(i, new SlotDescription("inventory", null));
            }

            // Load offer descriptions
            layout.getOfferDescriptions().putAll(getOfferDescriptionsFromSection(section.getConfigurationSection("offers")));

            // Load messages
            layout.setKeyWhenMissing(plugin.getGeneralConfiguration().getBoolean("debug-mode", false));
            layout.getMessages().putAll(getMessagesFromSection(section.getConfigurationSection("messages")));

            layouts.put(layout.getName(), layout);

        }
    }

    private Map<String, FormattedMessage> getMessagesFromSection(ConfigurationSection section) {

        final Map<String, FormattedMessage> messages = new HashMap<String, FormattedMessage>();

        if (section == null) {
            return messages;
        }

        for (Map.Entry<String, Object> entry : section.getValues(true).entrySet()) {
            if (entry.getValue() instanceof String) {
                messages.put(entry.getKey(), new FormattedMessage(messageManager.preformatMessage((String) entry.getValue())));
            }
        }

        return messages;

    }

    private Map<Class<? extends Offer>, OfferDescription> getOfferDescriptionsFromSection(ConfigurationSection section) {

        final Map<Class<? extends Offer>, OfferDescription> offerDescriptions = new HashMap<Class<? extends Offer>, OfferDescription>();

        if (section == null) {
            return offerDescriptions;
        }

        for (String offerKey : section.getKeys(false)) {

            if (section.isConfigurationSection(offerKey)) {

                final ConfigurationSection offerSection = section.getConfigurationSection(offerKey);

                if ("item".equalsIgnoreCase(offerKey)) {
                    final ItemOfferDescription description = new ItemOfferDescription();
                    offerDescriptions.put(description.getOfferClass(), description);
                } else if ("experience".equalsIgnoreCase(offerKey)) {
                    final ExperienceOfferDescription description = new ExperienceOfferDescription();
                    description.setSmallModifier(offerSection.getInt("small-modifier"));
                    description.setLargeModifier(offerSection.getInt("large-modifier"));
                    description.setExperienceItem(ItemStackUtils.fromSection(offerSection.getConfigurationSection("experience-item"), messageManager));
                    description.setExperienceItemMirror(ItemStackUtils.fromSection(offerSection.getConfigurationSection("experience-item-mirror"), messageManager));
                    offerDescriptions.put(description.getOfferClass(), description);
                } else if ("money".equalsIgnoreCase(offerKey)) {
                    final MoneyOfferDescription description = new MoneyOfferDescription();
                    description.setSmallModifier(offerSection.getInt("small-modifier"));
                    description.setLargeModifier(offerSection.getInt("large-modifier"));
                    description.setMoneyItem(ItemStackUtils.fromSection(offerSection.getConfigurationSection("money-item"), messageManager));
                    description.setMoneyItemMirror(ItemStackUtils.fromSection(offerSection.getConfigurationSection("money-item-mirror"), messageManager));
                    offerDescriptions.put(description.getOfferClass(), description);
                }

            }

        }

        return offerDescriptions;

    }

    public void unload() {
        layouts.clear();
    }

    public Layout getLayout(String playerNameA, String playerNameB) {

        Layout found = getDefaultLayout();

        final Player playerA = Bukkit.getPlayerExact(playerNameA);
        final Player playerB = Bukkit.getPlayerExact(playerNameB);

        for (Layout layout : layouts.values()) {
            if ((found != null && layout.getPriority() > found.getPriority())) {

                if (layout.getPermission() != null) {

                    final boolean AHasPermission = playerA != null && playerA.hasPermission(layout.getPermission());
                    final boolean BHasPermission = playerB != null && playerB.hasPermission(layout.getPermission());

                    if (AHasPermission && BHasPermission || (layout.isShared() && (AHasPermission || BHasPermission))) {
                        found = layout;
                    }

                } else {
                    found = layout;
                }

            }
        }

        return found;

    }

    public Layout getDefaultLayout() {
        return layouts.get("default");
    }

    public Map<String, Class<? extends Slot>> getRegisteredSlots() {
        return registeredSlots;
    }
}
