package me.josvth.trade.request;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class RequestOptions {

    // Player options
    private boolean allowCrossGameMode = false;
    private boolean allowCrossWorld = false;
    private boolean mustSee = false;
    private int maxDistance = 10;
    private List<String> disabledWorlds;
    private List<String> disabledRegions;

    // Request method options
    private long timeoutMillis = 10000;
    private int maxRequests = 5;

    private boolean allowCommandRequest = true;
    private boolean allowRightClickRequest = true;
    private boolean allowShiftRightClickRequest = true;
    private boolean allowLeftClickRequest = false;
    private boolean allowShiftLeftClickRequest = false;

    public void load(ConfigurationSection section) {

        setAllowCrossGameMode(section.getBoolean("allow-cross-game-mode", isAllowCrossGameMode()));
        setAllowCrossWorld(section.getBoolean("allow-cross-world", isAllowCrossWorld()));
        setMustSee(section.getBoolean("must-see", isMustSee()));
        setMaxDistance(section.getInt("max-distance", getMaxDistance()));
        setDisabledWorlds(section.getStringList("disabled-worlds"));
        setDisabledRegions(section.getStringList("disabled-regions"));

        setTimeoutMillis(section.getLong("timeout", getTimeoutMillis()));
        setMaxRequests(section.getInt("max-requests", getMaxRequests()));

        setAllowCommandRequest(section.getBoolean("method-allow.command", isAllowCommandRequest()));
        setAllowRightClickRequest(section.getBoolean("method-allow.right-click", isAllowRightClickRequest()));
        setAllowShiftRightClickRequest(section.getBoolean("method-allow.shift-right-click", isAllowShiftRightClickRequest()));
        setAllowLeftClickRequest(section.getBoolean("method-allow.left-click", isAllowLeftClickRequest()));
        setAllowShiftLeftClickRequest(section.getBoolean("method-allow.shift-left-click", isAllowShiftLeftClickRequest()));

    }

    public boolean allowCrossGameMode() {
        return isAllowCrossGameMode();
    }

    public void setAllowCrossGameMode(boolean allowCrossGameMode) {
        this.allowCrossGameMode = allowCrossGameMode;
    }

    public boolean allowCrossWorld() {
        return isAllowCrossWorld();
    }

    public void setAllowCrossWorld(boolean crossWorld) {
        this.allowCrossWorld = crossWorld;
    }

    public boolean mustSee() {
        return isMustSee();
    }

    public void setMustSee(boolean mustSee) {
        this.mustSee = mustSee;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public void setDisabledWorlds(List<String> disabledWorlds) {
        this.disabledWorlds = disabledWorlds;
    }

    public List<String> getDisabledRegions() {
        return disabledRegions;
    }

    public void setDisabledRegions(List<String> disabledRegions) {
        this.disabledRegions = disabledRegions;
    }

    public boolean allowCommandRequest() {
        return isAllowCommandRequest();
    }

    public void setAllowCommandRequest(boolean allowCommandRequest) {
        this.allowCommandRequest = allowCommandRequest;
    }

    public boolean allowRightClickRequest() {
        return isAllowRightClickRequest();
    }

    public void setAllowRightClickRequest(boolean allowRightClickRequest) {
        this.allowRightClickRequest = allowRightClickRequest;
    }

    public boolean allowRightShiftClickRequest() {
        return isAllowShiftRightClickRequest();
    }

    public void setAllowShiftRightClickRequest(boolean allowShiftRightClickRequest) {
        this.allowShiftRightClickRequest = allowShiftRightClickRequest;
    }

    public boolean allowLeftClickRequest() {
        return isAllowLeftClickRequest();
    }

    public void setAllowLeftClickRequest(boolean allowLeftClickRequest) {
        this.allowLeftClickRequest = allowLeftClickRequest;
    }

    public boolean allowLeftShiftClickRequest() {
        return isAllowShiftLeftClickRequest();
    }

    public void setAllowShiftLeftClickRequest(boolean allowShiftLeftClickRequest) {
        this.allowShiftLeftClickRequest = allowShiftLeftClickRequest;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public void setMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
    }

    public boolean isAllowCrossGameMode() {
        return allowCrossGameMode;
    }

    public boolean isAllowCrossWorld() {
        return allowCrossWorld;
    }

    public boolean isMustSee() {
        return mustSee;
    }

    public boolean isAllowCommandRequest() {
        return allowCommandRequest;
    }

    public boolean isAllowRightClickRequest() {
        return allowRightClickRequest;
    }

    public boolean isAllowShiftRightClickRequest() {
        return allowShiftRightClickRequest;
    }

    public boolean isAllowLeftClickRequest() {
        return allowLeftClickRequest;
    }

    public boolean isAllowShiftLeftClickRequest() {
        return allowShiftLeftClickRequest;
    }
}
