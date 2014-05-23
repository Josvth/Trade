package me.josvth.trade.request;

public enum RequestRestriction {

    ALLOW("", "requesting.restriction.allow"),
    OFFLINE("", "requesting.restriction.offline"),
    BUSY("", "requesting.restriction.busy"),
    IGNORING("", "requesting.restriction.ignoring"),
    PENDING("", "requesting.restriction.already-requested"),
    FLOOD("", "requesting.restriction.flood"),
    PERMISSION("trade.use", "requesting.restriction.permission"),
    CROSS_GAME_MODE("trade.allow.cross-game-mode", "requesting.restriction.cross-game-mode"),
    CROSS_WORLD("trade.allow.cross-world", "requesting.restriction.cross-world"),
    VISION("trade.allow.vision", "requesting.restriction.vision"),
    DISTANCE("trade.allow.distance", "requesting.restriction.distance"),
    WORLD("trade.allow.world", "requesting.restriction.world"),
    REGION("trade.allow.region", "requesting.restriction.region"),
    METHOD("trade.allow.*", "requesting.restriction.method"),
    SELF("", "requesting.restriction.self"),
    NPC("", "");

    public final String excludePermission;
    public final String requestMessagePath;

    private RequestRestriction(String excludePermission, String requestMessagePath) {
        this.excludePermission = excludePermission;
        this.requestMessagePath = requestMessagePath;
    }

}
