package me.josvth.trade.request;

public enum RequestMethod {

    COMMAND("trade.request.command", "requesting.restriction.method.command"),
    RIGHT_CLICK("trade.request.right-click", "requesting.restriction.method.right-click"),
    SHIFT_RIGHT_CLICK("trade.request.shift-right-click", "requesting.restriction.method.shift-right-click"),
    LEFT_CLICK("trade.request.left-click", "requesting.restriction.method.left-click"),
    SHIFT_LEFT_CLICK("trade.request.shift-left-click", "requesting.restriction.method.shift-left-click");

    public final String permission;
    public final String messagePath;

    private RequestMethod(String permission, String messagePath) {
        this.permission = permission;
        this.messagePath = messagePath;
    }

}
