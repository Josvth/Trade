package me.josvth.trade.request;

import me.josvth.trade.transaction.Transaction;

public class RequestResponse {

	private final RequestRestriction requestRestriction;
	private final Transaction transaction;

	public RequestResponse(RequestRestriction requestRestriction, Transaction transaction) {
		this.requestRestriction = requestRestriction;
		this.transaction = transaction;
	}

	public RequestRestriction getRequestRestriction() {
		return requestRestriction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

}

