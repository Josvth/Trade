package me.josvth.trade.transaction;

import org.bukkit.configuration.ConfigurationSection;

public class TransactionOptions {

    private boolean allowInventoryClosing = true;
    private boolean disableDragging = false;

    public void load(ConfigurationSection section) {
        setAllowInventoryClosing(section.getBoolean("allow-inventory-closing", true));
        setDisableDragging(section.getBoolean("disable-dragging", false));
    }

    public boolean allowInventoryClosing() {
        return allowInventoryClosing;
    }

    public void setAllowInventoryClosing(boolean allowInventoryClosing) {
        this.allowInventoryClosing = allowInventoryClosing;
    }

    public boolean disableDragging() {
        return disableDragging;
    }

    public void setDisableDragging(boolean disableDragging) {
        this.disableDragging = disableDragging;
    }
}
