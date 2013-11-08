package me.josvth.trade.request;

import me.josvth.bukkitformatlibrary.managers.FormatManager;
import me.josvth.trade.Trade;
import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.TransactionManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RequestManager {

	private final Trade plugin;

	private final FormatManager formatManager;
	private final TransactionManager transactionManager;

	private final RequestListener listener;

	private final RequestOptions options = new RequestOptions();

	private final Set<String> ignoring = new HashSet<String>();

	private final Map<String, Set<Request>> activeRequests = new HashMap<String, Set<Request>>();

	public RequestManager(Trade plugin, FormatManager formatManager, TransactionManager transactionManager) {
		this.plugin = plugin;
		this.formatManager = formatManager;
		this.transactionManager = transactionManager;
		this.listener = new RequestListener(this, this.formatManager);
	}

	public void load(ConfigurationSection section) {
		Bukkit.getPluginManager().registerEvents(listener, plugin);
		options.load(section);
	}

	public void unload() {
		ignoring.clear();
		activeRequests.clear();
	}

	public RequestOptions getOptions() {
		return options;
	}

	// Ignoring handling
	public boolean toggleIgnoring(String player) {
		if (isIgnoring(player)) {
			ignoring.remove(player.toLowerCase());
			return false;
		} else {
			ignoring.add(player.toLowerCase());
			return true;
		}
	}

	public boolean isIgnoring(String player) {
		return ignoring.contains(player.toLowerCase());
	}

	// Restriction handling
	public RequestRestriction checkRequest(Request request) {

		Player requester = request.getRequesterPlayer();
		Player requested = request.getRequesterPlayer();

		if (requester == null || requested == null)
			return RequestRestriction.OFFLINE;

		if (hasExclusion(requester, RequestRestriction.PERMISSION))
			return RequestRestriction.PERMISSION;

		if (isIgnoring(request.getRequested()))
			return RequestRestriction.IGNORING;

		if (transactionManager.isInTransaction(request.getRequested()))
			return RequestRestriction.BUSY;

		if (isRequested(request.getRequester(), request.getRequested()))
			return RequestRestriction.PENDING;

		RequestRestriction restriction = RequestRestriction.ALLOW;

		if (!requester.getWorld().equals(requested.getWorld()) && !options.allowCrossWorld())
			restriction = RequestRestriction.CROSS_WORLD;
		else if (!requester.getGameMode().equals(requested.getGameMode()) && !options.allowCrossGameMode())
			restriction = RequestRestriction.CROSS_GAME_MODE;
		else if (!requester.canSee(requested) && !options.mustSee())
			restriction = RequestRestriction.VISION;
		else if (requester.getLocation().distance(requested.getPlayer().getLocation()) > options.getMaxDistance())
			restriction = RequestRestriction.DISTANCE;
		else if (options.getDisabledWorlds() != null && options.getDisabledWorlds().contains(requester.getWorld().getName()))
			restriction = RequestRestriction.WORLD;
		// TODO ADD REGION CHECK

		if (!mayUseMethod(requester, request.getMethod()))
			restriction = RequestRestriction.METHOD;

		if (restriction != RequestRestriction.ALLOW && hasExclusion(requested, restriction))
			return RequestRestriction.ALLOW;
		else
			return restriction;

	}

	public boolean mayUseMethod(Player player, RequestMethod method) {
		if (options.usePermisions())
			return player.hasPermission(method.permission);
		switch (method) {
			case COMMAND: return options.allowCommandRequest();
			case LEFT_CLICK: return options.allowLeftClickRequest();
			case SHIFT_LEFT_CLICK: return options.allowLeftShiftClickRequest();
			case RIGHT_CLICK: return options.allowRightClickRequest();
			case SHIFT_RIGHT_CLICK: return options.allowRightShiftClickRequest();
		}
		return true;
	}

	private boolean hasExclusion(Player player, RequestRestriction restriction) {
		if (!options.usePermisions()) return false;
		return player.hasPermission(restriction.excludePermission);
	}

	public boolean hasRequests(String requested) {
		return isRequested(requested, null);
	}

	public boolean isRequested(String requested, String requester) {

		Set<Request> set = getRequestSet(requested);

		if (set == null) return false;

		if (requester == null) return true;

		for (Request request : set)
			if (request.getRequester().equalsIgnoreCase(requested)) return true;

		return false;

	}

	// Current request methods
	public Request getRequest(String requested, String requester) {

		Set<Request> set = getRequestSet(requested);

		if (set == null) return null;

		for (Request request : set)
			if (request.getRequester().equalsIgnoreCase(requested)) return request;

		return null;

	}

	public Set<Request> getRequestSet(String player) {
		return activeRequests.get(player.toLowerCase());
	}

	public Set<Request> getOrCreateRequestSet(String player) {
		Set<Request> set = activeRequests.get(player.toLowerCase());
		if (set == null)
			set = activeRequests.put(player, new HashSet<Request>());
		return set;
	}

	// Handling request objects
	public void submit(Request request) {
		getOrCreateRequestSet(request.getRequested()).add(request);
		request.setSubmitDate(System.currentTimeMillis());
	}

	public RequestRestriction accept(Request request) {

		RequestRestriction restriction = checkRequest(request);

		if (restriction == RequestRestriction.ALLOW) {

			handleRequest(request);

			activeRequests.remove(request.getRequested().toLowerCase());

		}

		return restriction;

	}

	public Transaction handleRequest(Request request) {
		final Transaction transaction = transactionManager.createTransaction(request.getRequester(), request.getRequested());
		transaction.start();
		return transaction;
	}

}
