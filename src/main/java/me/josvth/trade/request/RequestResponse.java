package me.josvth.trade.request;

import me.josvth.trade.transaction.Transaction;

public class RequestResponse {

    private final Request request;
    private final RequestRestriction requestRestriction;
    private final Transaction transaction;

    public RequestResponse(Request request, RequestRestriction requestRestriction, Transaction transaction) {
        this.request = request;
        this.requestRestriction = requestRestriction;
        this.transaction = transaction;
    }

    public Request getRequest() {
        return request;
    }

    public RequestRestriction getRequestRestriction() {
        return requestRestriction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

}

