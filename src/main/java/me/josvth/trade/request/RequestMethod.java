package me.josvth.trade.request;

public enum RequestMethod {

	COMMAND ("trade.request.command"),
	RIGHT_CLICK ("trade.request.right-click"),
	SHIFT_RIGHT_CLICK ("trade.request.shift-right-click"),
	LEFT_CLICK ("trade.request.left-click"),
	SHIFT_LEFT_CLICK ("trade.request.shift-left-click");

	public final String permission;

	private RequestMethod(String permission) {
		this.permission = permission;
	}

}
