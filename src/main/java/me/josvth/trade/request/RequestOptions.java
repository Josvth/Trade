package me.josvth.trade.request;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class RequestOptions {

	// Player options
	private boolean usePermissions = true;
	private boolean crossGameMode = false;
	private boolean crossWorld = false;
	private boolean mustSee = false;
	private int maxDistance = 10;
	private List<String> disabledWorlds;
	private List<String> disabledRegions;

	// Request method options
	private long timeoutMillis = 10000;
	private boolean rightClickRequest = true;
	private boolean rightShiftClickRequest = true;
	private boolean leftClickRequest = false;
	private boolean leftShiftClickRequest = false;

	public void load(ConfigurationSection section) {

		usePermissions = section.getBoolean("use-permissions", usePermissions);
		crossGameMode = section.getBoolean("allow-cross-gamemode", crossGameMode);
		crossWorld = section.getBoolean("allow-cross-world", crossWorld);
		mustSee = section.getBoolean("must-see", mustSee);
		maxDistance = section.getInt("max-distance", maxDistance);
		disabledWorlds = section.getStringList("disabled-worlds");
		disabledRegions = section.getStringList("disabled-regions");

		timeoutMillis = section.getLong("timeout", timeoutMillis);
		rightClickRequest = section.getBoolean("allow-right-click-request", rightClickRequest);
		rightShiftClickRequest = section.getBoolean("allow-shift-right-click-request", rightShiftClickRequest);
		leftClickRequest = section.getBoolean("allow-left-click-request", leftClickRequest);
		leftShiftClickRequest = section.getBoolean("allow-shift-left-click-request", leftShiftClickRequest);

	}

	public boolean isUsePermissions() {
		return usePermissions;
	}

	public void setUsePermissions(boolean usePermissions) {
		this.usePermissions = usePermissions;
	}

	public boolean isCrossGameMode() {
		return crossGameMode;
	}

	public void setCrossGameMode(boolean crossGameMode) {
		this.crossGameMode = crossGameMode;
	}

	public boolean isCrossWorld() {
		return crossWorld;
	}

	public void setCrossWorld(boolean crossWorld) {
		this.crossWorld = crossWorld;
	}

	public boolean isMustSee() {
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
}
