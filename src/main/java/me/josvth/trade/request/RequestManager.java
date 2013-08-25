package me.josvth.trade.request;

import com.avaje.ebeaninternal.server.transaction.TransactionManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RequestManager {

	private final TransactionManager transactionManager;

	private final RequestOptions options = new RequestOptions();

	private Map<String, Set<Request>> activeRequests = new HashMap<String, Set<Request>>();

	public RequestManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public RequestOptions getOptions() {
		return options;
	}

	public void submit(Request request) {
		getOrCreateRequestSet(request.getRequested()).add(request);
	}

	public Set<Request> getRequestSet(String player) {
		return activeRequests.get(player);
	}

	private Set<Request> getOrCreateRequestSet(String player) {
		Set<Request> set = activeRequests.get(player);
		if (set == null)
			set = activeRequests.put(player, new HashSet<Request>());
		return set;
	}
}
