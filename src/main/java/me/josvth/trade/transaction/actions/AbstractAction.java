package me.josvth.trade.transaction.actions;

public class AbstractAction implements Action {

	protected final String name;

	protected final String messagePath;
	protected final String mirrorMessagePath;

	public AbstractAction(String name, String messagePath, String mirrorMessagePath) {
		this.name = name;
		this.messagePath = messagePath;
		this.mirrorMessagePath = mirrorMessagePath;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getMessagePath() {
		return messagePath;
	}

	@Override
	public String getMirrorMessagePath() {
		return mirrorMessagePath;
	}

}
