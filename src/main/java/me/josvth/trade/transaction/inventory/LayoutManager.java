package me.josvth.trade.transaction.inventory;

import me.josvth.bukkitformatlibrary.message.FormattedMessage;
import me.josvth.bukkitformatlibrary.message.managers.MessageManager;
import me.josvth.trade.Trade;
import me.josvth.trade.offer.description.ExperienceOfferDescription;
import me.josvth.trade.offer.description.ItemOfferDescription;
import me.josvth.trade.transaction.inventory.slot.*;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class LayoutManager {

    private final Trade plugin;
    private final MessageManager messageManager;

    private final Map<String, Layout> layouts = new HashMap<String, Layout>();

    public LayoutManager(Trade plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
    }

    public void load(ConfigurationSection configuration) {

        try {
            loadLayout(configuration.getConfigurationSection("default"));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            loadLayout(plugin.getMessageConfiguration().getDefaultSection().getConfigurationSection("default"));
        }

        // Add messages.yml trading messages into default layout
        for (Map.Entry<String, FormattedMessage> entry : messageManager.getMessageHolder().getMessages().entrySet()) {
            if (entry.getKey().startsWith("trading.")) {
                getDefaultLayout().addMessage(entry.getKey().replaceFirst("trading.", ""), entry.getValue());
            }
        }

        for (String key : configuration.getKeys(false)) {

            if (key.equalsIgnoreCase("default")) {
                continue;
            }

            try {
                loadLayout(configuration.getConfigurationSection(key));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        }

    }

    private void loadLayout(ConfigurationSection section) throws IllegalArgumentException {

        if (section == null) {
            throw new IllegalArgumentException("Section is null.");
        }

        final String name = section.getName();

        final int size = section.getInt("size");

        if (size == 0 || size % 9 != 0) {
            throw new IllegalArgumentException("Section does not have a slot section.");
        }

        final int offerSize = section.getInt("offer-size", 4);

        final Layout layout = new Layout(name, size / 9, offerSize);

        // Load slots
        if (!section.isConfigurationSection("slots")) {
            throw new IllegalArgumentException("Section does not have a slot section.");
        }

        final Slot[] slots = new Slot[size];

        for (String slotKey : section.getConfigurationSection("slots").getKeys(false)) {

            int slotID = -1;

            try {
                slotID = Integer.parseInt(slotKey);
            } catch (NumberFormatException e) {
                // TODO add message
                continue;
            }

            if (slotID < 0 || slotID >= size) {
                // TODO add message
                continue;
            }

            final ConfigurationSection slotSection = section.getConfigurationSection("slots." + slotKey);

            final String type = slotSection.getString("type");

            Slot slot = null;

            if ("accept".equalsIgnoreCase(type)) {
                slot = new AcceptSlot(
                        slotID,
                        ItemStackUtils.fromSection(slotSection.getConfigurationSection("accept-item"), messageManager),
                        ItemStackUtils.fromSection(slotSection.getConfigurationSection("accepted-item"), messageManager)
                );
            } else if ("refuse".equalsIgnoreCase(type)) {
                slot = new RefuseSlot(
                        slotID,
                        ItemStackUtils.fromSection(slotSection.getConfigurationSection("refuse-item"), messageManager)
                );
            } else if ("close".equalsIgnoreCase(type)) {
                slot = new CloseSlot(
                        slotID,
                        ItemStackUtils.fromSection(slotSection.getConfigurationSection("close-item"), messageManager)
                );
            } else if ("status".equalsIgnoreCase(type)) {
                slot = new StatusSlot(
                        slotID,
                        ItemStackUtils.fromSection(slotSection.getConfigurationSection("considering-item"), messageManager),
                        ItemStackUtils.fromSection(slotSection.getConfigurationSection("accepted-item"), messageManager)
                );
            } else if ("trade".equalsIgnoreCase(type)) {
                slot = new TradeSlot(
                        slotID,
                        slotSection.getInt("slot", 0)
                );
            } else if ("mirror".equalsIgnoreCase(type)) {
                slot = new MirrorSlot(
                        slotID,
                        slotSection.getInt("slot", 0)
                );
            } else if ("money".equalsIgnoreCase(type)) {
                slot = new MoneySlot(
                        slotID,
                        ItemStackUtils.fromSection(slotSection.getConfigurationSection("money-item"), messageManager),
                        slotSection.getInt("small-modifier", 5),
                        slotSection.getInt("large-modifier", 10)
                );
            } else if ("experience".equalsIgnoreCase(type)) {
                slot = new ExperienceSlot(
                        slotID,
                        ItemStackUtils.fromSection(slotSection.getConfigurationSection("experience-item"), messageManager),
                        slotSection.getInt("small-modifier", 1),
                        slotSection.getInt("large-modifier", 5)
                );
            }

            if (slot == null) {
                // TODO add message
                continue;
            }

            slots[slotID] = slot;

        }

        layout.setSlots(slots);

        // Load offer descriptions
        if (section.isConfigurationSection("offers")) {

            for (String offerKey : section.getConfigurationSection("offers").getKeys(false)) {

                final ConfigurationSection offerSection = section.getConfigurationSection("offers." + offerKey);

                if ("item".equalsIgnoreCase(offerKey)) {
                    final ItemOfferDescription description = new ItemOfferDescription();
                    layout.setOfferDescription(description.getOfferClass(), new ItemOfferDescription());
                } else if ("experience".equalsIgnoreCase(offerKey)) {
                    final ExperienceOfferDescription description = new ExperienceOfferDescription();
                    description.setSmallModifier(offerSection.getInt("small-modifier", 1));
                    description.setLargeModifier(offerSection.getInt("large-modifier", 5));
                    description.setExperienceItem(ItemStackUtils.fromSection(offerSection.getConfigurationSection("item"), messageManager));
                    description.setExperienceItemMirror(ItemStackUtils.fromSection(offerSection.getConfigurationSection("item-mirror"), messageManager));
                    layout.setOfferDescription(description.getOfferClass(), new ExperienceOfferDescription());
                } else if ("money".equalsIgnoreCase(offerKey)) {
                    // TODO This
                }

            }

        }

        // Load messages
        layout.putMessages(getDefaultLayout().getMessages());

        if (section.isConfigurationSection("messages")) {

            for (Map.Entry<String, Object> entry : section.getConfigurationSection("messages").getValues(false).entrySet()) {
                layout.addMessage(entry.getKey(), messageManager.preformatMessage((String) entry.getValue()));
            }

        }

        layouts.put(layout.getName(), layout);

    }

    public void unload() {
        layouts.clear();
    }

    public Layout getLayout(String playerNameA, String playerNameB) {

        // TODO fix this
//		Layout found = DEFAULT_LAYOUT;
//		boolean permissionBased = false;
//
//		final Player playerA = Bukkit.getPlayerExact(playerNameA);
//		final Player playerB = Bukkit.getPlayerExact(playerNameB);
//
//		for (Layout layout : layouts.values()) {
//			if ((found != null && layout.getPriority() > found.getPriority()) || (layout.getPermission() != null && permissionBased == false)) {
//
//				if (layout.getPermission() != null) {
//
//					final boolean AHasPermission = playerA != null && playerA.hasPermission(layout.getPermission());
//					final boolean BHasPermission = playerB != null && playerB.hasPermission(layout.getPermission());
//
//					if (AHasPermission && BHasPermission || (layout.isShared() && (AHasPermission || BHasPermission))) {
//						found = layout;
//						permissionBased = false;
//					}
//
//				} else {
//					found = layout;
//					permissionBased = false;
//				}
//
//			}
//		}

        return layouts.get("default");

    }

    public Layout getDefaultLayout() {
        return layouts.get("default");
    }
}
