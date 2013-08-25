package me.josvth.trade.request;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Request {

	private final String requester;
	private final String requested;

	private long submitDate = -1;

	public Request(String requester, String requested) {
		this.requester = requester;
		this.requested = requested;
	}

	public String getRequester() {
		return requester;
	}

	public Player getRequesterPlayer() {
		return Bukkit.getPlayerExact(requester);
	}

	public String getRequested() {
		return requested;
	}

	public Player getRequestedPlayer() {
		return Bukkit.getPlayerExact(requested);
	}

	public long getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(long submitDate) {
		this.submitDate = submitDate;
	}

}
