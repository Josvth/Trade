package me.josvth.trade.tasks;

import me.josvth.trade.request.Request;
import me.josvth.trade.request.RequestManager;
import org.bukkit.entity.Player;

public class RequestTimeOutTask implements Runnable {

    private final RequestManager manager;
    private final Request request;

    public RequestTimeOutTask(RequestManager manager, Request request) {
        this.manager = manager;
        this.request = request;
    }

    @Override
    public void run() {
        if (manager.removeRequest(request)) {
            final Player player = request.getRequesterPlayer();
            if (player != null) {
                manager.getMessageHolder().getMessage("requesting.timeout").send(player, "%player%", request.getRequestedPlayer().getName());
            }
        }
    }
}
