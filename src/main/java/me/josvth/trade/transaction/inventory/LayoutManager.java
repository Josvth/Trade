package me.josvth.trade.transaction.inventory;

import me.josvth.bukkitformatlibrary.managers.FormatManager;
import me.josvth.trade.Trade;
import me.josvth.trade.transaction.inventory.slot.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LayoutManager {

	private final Trade plugin;
	private final FormatManager formatManager;

	private static final Layout DEFAULT_LAYOUT = null;

//	static {
//
//		final Slot[] slots = new Slot[18];
//
//		slots[0] = new TradeSlot(0,0);
//		slots[1] = new TradeSlot(1,1);
//		slots[2] = new TradeSlot(2,2);
//
//		slots[3] = new AcceptSlot(
//				3,
//				new ItemDescription(Material.STAINED_CLAY, 0, (short) 0, (byte) 5, "§2Click to accept!", null, false),
//				new ItemDescription(Material.STAINED_CLAY, 0, (short) 0, (byte) 3, "§9You accepted the trade.", null, false));
//		slots[4] = new RefuseSlot(4, new ItemDescription(Material.STAINED_CLAY, 0, (short) 0, (byte) 6, "§4Click to refuse the trade!", null, false));
//		slots[5] = new StatusSlot(
//				5,
//				new ItemDescription(Material.STAINED_CLAY, 0, (short) 0, (byte) 4, "§9The other player is considering the trade.", null, false),
//				new ItemDescription(Material.STAINED_CLAY, 0, (short) 0, (byte) 5, "§2The other player accepted the trade.", null, false));
//
//		slots[6] = new MirrorSlot(6,0);
//		slots[7] = new MirrorSlot(7,1);
//		slots[8] = new MirrorSlot(8,2);
//
//		slots[9] = new TradeSlot(9,0);
//
//		//slots[11] = new MoneySlot(11, new ItemDescription(Material.GOLD_INGOT, 0), 1, 5);
//		slots[12] = new ExperienceSlot(
//				12,
//				new ItemDescription(
//						Material.EXP_BOTTLE,
//						0,
//						(short) 0,
//						(byte) 4,
//						"§1TODO DISPLAY LEVELS",
//						Arrays.asList(new String[]{"Left click to add 1 level.", "Shift left click to add 5 levels", "Right click to remove 1 level", "Shift right click to remove 5 levels."}),
//						false),
//				1,
//				5);
//		slots[13] = new CloseSlot(13, new ItemDescription(Material.BONE, 0));
//
//		DEFAULT_LAYOUT = new Layout("default", 2, 3);
//		DEFAULT_LAYOUT.setSlots(slots);
//
//	}

	private final Map<String, Layout> layouts = new HashMap<String, Layout>();

	public LayoutManager(Trade plugin, FormatManager formatManager) {
		this.plugin = plugin;
		this.formatManager = formatManager;
	}

	public void load(ConfigurationSection configuration) {

		layouts.put("default", DEFAULT_LAYOUT);

		for (String key : configuration.getKeys(false)) {

			final int size 		= configuration.getInt(key + ".size");

			if (size == 0 || size % 9 != 0) {
				// TODO add message
				continue;
			}

			final int offerSize = configuration.getInt(key + ".offer-size", 4);

			final Layout layout = new Layout(key, size / 9, offerSize);

			if (!configuration.isConfigurationSection(key + ".slots")) {
				// TODO add message
				continue;
			}

			final Slot[] slots = new Slot[size];

			for (String slotKey : configuration.getConfigurationSection(key + ".slots").getKeys(false)) {

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

				final ConfigurationSection slotSection = configuration.getConfigurationSection(key + ".slots." + slotKey);

				final String type = slotSection.getString("type");

				Slot slot = null;

				if ("accept".equalsIgnoreCase(type)) {
					slot = new AcceptSlot(
							slotID,
							ItemDescription.fromSection(slotSection.getConfigurationSection("accept-item"), formatManager),
							ItemDescription.fromSection(slotSection.getConfigurationSection("accepted-item"), formatManager)
						);
				} else if ("refuse".equalsIgnoreCase(type)) {
					slot = new RefuseSlot(
							slotID,
							ItemDescription.fromSection(slotSection.getConfigurationSection("refuse-item"), formatManager)
					);
				} else if ("close".equalsIgnoreCase(type)) {
					slot = new CloseSlot(
							slotID,
							ItemDescription.fromSection(slotSection.getConfigurationSection("close-item"), formatManager)
					);
				} else if ("status".equalsIgnoreCase(type)) {
					slot = new StatusSlot(
							slotID,
							ItemDescription.fromSection(slotSection.getConfigurationSection("considering-item"), formatManager),
							ItemDescription.fromSection(slotSection.getConfigurationSection("accepted-item"), formatManager)
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
							ItemDescription.fromSection(slotSection.getConfigurationSection("money-item"), formatManager),
							slotSection.getInt("small-modifier", 5),
							slotSection.getInt("large-modifier", 10)
					);
				} else if ("experience".equalsIgnoreCase(type)) {
					slot = new ExperienceSlot(
							slotID,
							ItemDescription.fromSection(slotSection.getConfigurationSection("experience-item"), formatManager),
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

			layouts.put(layout.getName(), layout);

		}

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

}
