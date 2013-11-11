package me.josvth.trade.request;

public enum RequestMethod {

	COMMAND ("trade.request.command", "requesting.restriction.method.command"),
	RIGHT_CLICK ("trade.request.right-click", null),
	SHIFT_RIGHT_CLICK ("trade.request.shift-right-click", null),
	LEFT_CLICK ("trade.request.left-click", null),
	SHIFT_LEFT_CLICK ("trade.request.shift-left-click", null);

	public final String permission;
	public final String messagePath;

	private RequestMethod(String permission, String messagePath) {
		this.permission = permission;
		this.messagePath = messagePath;
	}

}
