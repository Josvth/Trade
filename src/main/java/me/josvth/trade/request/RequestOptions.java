package me.josvth.trade.request;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class RequestOptions {

    // Player options
    private boolean usePermissions = false;
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

        usePermissions = section.getBoolean("use-permissions", usePermissions);
        allowCrossGameMode = section.getBoolean("allow-cross-game-mode", allowCrossGameMode);
        allowCrossWorld = section.getBoolean("allow-cross-world", allowCrossWorld);
        mustSee = section.getBoolean("must-see", mustSee);
        maxDistance = section.getInt("max-distance", maxDistance);
        disabledWorlds = section.getStringList("disabled-worlds");
        disabledRegions = section.getStringList("disabled-regions");

        timeoutMillis = section.getLong("timeout", timeoutMillis);
        maxRequests = section.getInt("max-requests", maxRequests);

        allowCommandRequest = section.getBoolean("method-allow.command", allowCommandRequest);
        allowRightClickRequest = section.getBoolean("method-allow.right-click", allowRightClickRequest);
        allowShiftRightClickRequest = section.getBoolean("method-allow.shift-right-click", allowShiftRightClickRequest);
        allowLeftClickRequest = section.getBoolean("method-allow.left-click", allowLeftClickRequest);
        allowShiftLeftClickRequest = section.getBoolean("method-allow.shift-left-click", allowShiftLeftClickRequest);

    }

    public boolean usePermissions() {
        return usePermissions;
    }

    public void setUsePermissions(boolean usePermissions) {
        this.usePermissions = usePermissions;
    }

    public boolean allowCrossGameMode() {
        return allowCrossGameMode;
    }

    public void setAllowCrossGameMode(boolean allowCrossGameMode) {
        this.allowCrossGameMode = allowCrossGameMode;
    }

    public boolean allowCrossWorld() {
        return allowCrossWorld;
    }

    public void setAllowCrossWorld(boolean crossWorld) {
        this.allowCrossWorld = crossWorld;
    }

    public boolean mustSee() {
        return mustSee;
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
        return allowCommandRequest;
    }

    public void setAllowCommandRequest(boolean allowCommandRequest) {
        this.allowCommandRequest = allowCommandRequest;
    }

    public boolean allowRightClickRequest() {
        return allowRightClickRequest;
    }

    public void setAllowRightClickRequest(boolean allowRightClickRequest) {
        this.allowRightClickRequest = allowRightClickRequest;
    }

    public boolean allowRightShiftClickRequest() {
        return allowShiftRightClickRequest;
    }

    public void setAllowShiftRightClickRequest(boolean allowShiftRightClickRequest) {
        this.allowShiftRightClickRequest = allowShiftRightClickRequest;
    }

    public boolean allowLeftClickRequest() {
        return allowLeftClickRequest;
    }

    public void setAllowLeftClickRequest(boolean allowLeftClickRequest) {
        this.allowLeftClickRequest = allowLeftClickRequest;
    }

    public boolean allowLeftShiftClickRequest() {
        return allowShiftLeftClickRequest;
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
}
