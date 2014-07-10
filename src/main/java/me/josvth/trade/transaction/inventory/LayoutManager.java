package me.josvth.trade.transaction.inventory;

import me.josvth.bukkitformatlibrary.message.FormattedMessage;
import me.josvth.bukkitformatlibrary.message.managers.MessageManager;
import me.josvth.trade.Trade;
import me.josvth.trade.transaction.inventory.offer.ExperienceOffer;
import me.josvth.trade.transaction.inventory.offer.ItemOffer;
import me.josvth.trade.transaction.inventory.offer.MoneyOffer;
import me.josvth.trade.transaction.inventory.offer.Offer;
import me.josvth.trade.transaction.inventory.offer.description.ExperienceOfferDescription;
import me.josvth.trade.transaction.inventory.offer.description.ItemOfferDescription;
import me.josvth.trade.transaction.inventory.offer.description.MoneyOfferDescription;
import me.josvth.trade.transaction.inventory.offer.description.OfferDescription;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.inventory.slot.SlotDescription;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.Map;

public class LayoutManager {

    private static final Map<Class<? extends Offer>, OfferDescription> DEFAULT_OFFER_DESCRIPTIONS = new HashMap<Class<? extends Offer>, OfferDescription>();

    static {
        DEFAULT_OFFER_DESCRIPTIONS.put(ItemOffer.class, new ItemOfferDescription());
        DEFAULT_OFFER_DESCRIPTIONS.put(ExperienceOffer.class, new ExperienceOfferDescription());
        DEFAULT_OFFER_DESCRIPTIONS.put(MoneyOffer.class, new MoneyOfferDescription());
    }

    public static final int PLAYER_INVENTORY_SIZE = InventoryType.PLAYER.getDefaultSize();

    private final Trade plugin;
    private final MessageManager messageManager;

    private final Map<String, Class<? extends Slot>> registeredSlots = new HashMap<String, Class<? extends Slot>>();

    private final Map<String, Layout> layouts = new HashMap<String, Layout>();

    public LayoutManager(Trade plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
    }


    public Trade getPlugin() {
        return plugin;
    }

    public void load(ConfigurationSection layoutSection, ConfigurationSection messageSection, ConfigurationSection globalOfferSection) {

        final Map<String, FormattedMessage> defaultMessages = getMessagesFromSection(messageSection);

        final Map<Class<? extends Offer>, OfferDescription> globalOfferDescriptions = new HashMap<Class<? extends Offer>, OfferDescription>();
        globalOfferDescriptions.putAll(DEFAULT_OFFER_DESCRIPTIONS);
        globalOfferDescriptions.putAll(getOfferDescriptionsFromSection(globalOfferSection));

        // First we load our default default layout
        final Layout defaultLayout = new Layout("default", this);
        loadLayout(defaultLayout, layoutSection.getDefaultSection().getConfigurationSection("default"), defaultMessages, globalOfferDescriptions);

        // Then we loud the other layouts in the section. Overriding our default if necessary
        for (String key : layoutSection.getKeys(false)) {

            try {
                if (layoutSection.isConfigurationSection(key)) {
                    final Layout layout = new Layout(key, this);
                    layouts.put(key, layout);
                    loadLayout(layout, layoutSection.getConfigurationSection(key), defaultMessages, globalOfferDescriptions);
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

        if (layout.getGuiRows() > 6 || layout.getGuiRows() <= 0) {
            throw new IllegalArgumentException("Layout rows must be greater than 0 and smaller than 7");
        }

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
            layout.setKeyWhenMissing(plugin.isDebugMode());
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

                if (ItemOffer.TYPE_NAME.equalsIgnoreCase(offerKey)) {
                    final ItemOfferDescription description = new ItemOfferDescription();
                    offerDescriptions.put(description.getOfferClass(), description);
                } else if (ExperienceOffer.TYPE_NAME.equalsIgnoreCase(offerKey)) {
                    final ExperienceOfferDescription description = new ExperienceOfferDescription();
                    description.setSmallModifier(offerSection.getInt("small-modifier"));
                    description.setLargeModifier(offerSection.getInt("large-modifier"));
                    description.setExperienceItem(ItemStackUtils.fromSection(offerSection.getConfigurationSection("experience-item"), messageManager));
                    description.setExperienceItemMirror(ItemStackUtils.fromSection(offerSection.getConfigurationSection("experience-item-mirror"), messageManager));
                    offerDescriptions.put(description.getOfferClass(), description);
                } else if (MoneyOffer.TYPE_NAME.equalsIgnoreCase(offerKey)) {
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

    public Map<String, Layout> getLayouts() {
        return layouts;
    }

    public Layout getLayout(Player playerA, Player playerB) {

        Layout found = getDefaultLayout();

        for (Layout layout : layouts.values()) {
            if ((found != null && layout.getPriority() > found.getPriority())) {

                if (layout.getPermission() != null) {

                    final boolean AHasPermission = playerA.hasPermission(layout.getPermission());
                    final boolean BHasPermission = playerB.hasPermission(layout.getPermission());

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
        final Layout defaultLayout = layouts.get(plugin.getTransactionManager().getOptions().getDefaultLayoutName());
        if (defaultLayout == null) {
            if (getPlugin().isDebugMode()) {
                getPlugin().getLogger().info("[DEBUG] Could not find set default layout: " + plugin.getTransactionManager().getOptions().getDefaultLayoutName() + ". Falling back to 'default'.");
                return layouts.get("default");
            }
        }
        return defaultLayout;
    }

    public Map<String, Class<? extends Slot>> getRegisteredSlots() {
        return registeredSlots;
    }
}
