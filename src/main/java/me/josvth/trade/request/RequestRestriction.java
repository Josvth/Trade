package me.josvth.trade.request;

public enum RequestRestriction {

	ALLOW ("", "requesting.restriction.allow", "trading.restriction.allow"),
	OFFLINE("", "requesting.restriction.offline", "trading.restriction.offline"),
	BUSY ("", "requesting.restriction.busy", "trading.restriction.busy"),
	IGNORING("", "requesting.restriction.ignoring", "trading.restriction.ignoring"),
	PENDING ("", "requesting.restriction.already-requested", "trading.restriction.already-requested"),
	FLOOD ("", "requesting.restriction.flood", "trading.restriction.flood"),
	PERMISSION ("trade.use", "requesting.restriction.permission", "trading.restriction.permission"),
	CROSS_GAME_MODE("trade.allow.cross-game-mode", "requesting.restriction.cross-game-mode", "trading.restriction.cross-game-mode"),
	CROSS_WORLD("trade.allow.cross-world", "requesting.restriction.cross-world", "trading.restriction.cross-world"),
	VISION ("trade.allow.vision", "requesting.restriction.vision", "trading.restriction.vision"),
	DISTANCE ("trade.allow.distance", "requesting.restriction.distance", "trading.restriction.distance"),
	WORLD ("trade.allow.world", "requesting.restriction.world", "trading.restriction.world"),
	REGION ("trade.allow.region", "requesting.restriction.region", "trading.restriction.region"),
	METHOD ("trade.allow.*", "requesting.restriction.method", "trading.restriction.method"), ;

	public final String excludePermission;
	public final String requestMessagePath;
	public final String tradeMessagePath;

	private RequestRestriction(String excludePermission, String requestMessagePath, String tradeMessagePath) {
		this.excludePermission = excludePermission;
		this.requestMessagePath = requestMessagePath;
		this.tradeMessagePath = tradeMessagePath;
	}

}
