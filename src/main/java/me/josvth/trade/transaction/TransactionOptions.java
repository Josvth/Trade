package me.josvth.trade.transaction;

import org.bukkit.configuration.ConfigurationSection;

public class TransactionOptions {

    private final static String ALLOW_INVENTORY_CLOSING_KEY = "allow-inventory-closing";
    private final static String ALLOW_DRAGGING_KEY = "allow-dragging";
    private final static String USE_ECONOMY_KEY = "use-economy";
    private final static String DEFAULT_LAYOUT_KEY = "default-layout";

    private boolean allowInventoryClosing = false;
    private boolean allowDragging = true;
    private boolean useEconomy = true;
    private String defaultLayoutName = "default";

    public void load(ConfigurationSection section) {
        setAllowInventoryClosing(section.getBoolean(ALLOW_INVENTORY_CLOSING_KEY, false));
        setAllowDragging(section.getBoolean(ALLOW_DRAGGING_KEY, true));
        setUseEconomy(section.getBoolean(USE_ECONOMY_KEY, false));
        setDefaultLayoutName(section.getString(DEFAULT_LAYOUT_KEY, "default"));
    }

    public void store(ConfigurationSection section) {
        section.set(ALLOW_INVENTORY_CLOSING_KEY, getAllowInventoryClosing());
        section.set(ALLOW_DRAGGING_KEY, getAllowDragging());
        section.set(USE_ECONOMY_KEY, getUseEconomy());
        section.set(DEFAULT_LAYOUT_KEY, getDefaultLayoutName());
    }

    public boolean getAllowInventoryClosing() {
        return allowInventoryClosing;
    }

    public void setAllowInventoryClosing(boolean allowInventoryClosing) {
        this.allowInventoryClosing = allowInventoryClosing;
    }

    public boolean getAllowDragging() {
        return allowDragging;
    }

    public void setAllowDragging(boolean allowDragging) {
        this.allowDragging = allowDragging;
    }

    public boolean getUseEconomy() {
        return useEconomy;
    }

    public void setUseEconomy(boolean useEconomy) {
        this.useEconomy = useEconomy;
    }

    public void setDefaultLayoutName(String defaultLayoutName) {
        this.defaultLayoutName = defaultLayoutName;
    }

    public String getDefaultLayoutName() {
        return defaultLayoutName;
    }

}
