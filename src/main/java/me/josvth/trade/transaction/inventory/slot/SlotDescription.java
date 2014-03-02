package me.josvth.trade.transaction.inventory.slot;

import org.bukkit.configuration.ConfigurationSection;

public class SlotDescription {

    private final String type;
    private final ConfigurationSection configuration;

    public SlotDescription(String type, ConfigurationSection configuration) {
        this.type = type;
        this.configuration = configuration;
    }

    public String getType() {
        return type;
    }

    public ConfigurationSection getConfiguration() {
        return configuration;
    }

}
