package me.josvth.trade.request;

import me.josvth.bukkitformatlibrary.message.MessageHolder;
import me.josvth.trade.Trade;
import me.josvth.trade.tasks.RequestTimeOutTask;
import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.TransactionManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class RequestManager {

    private final Trade plugin;

    private final MessageHolder messageHolder;
    private final TransactionManager transactionManager;

    private final RequestListener listener;

    private final RequestOptions options = new RequestOptions();

    private final Set<String> ignoring = new HashSet<String>();

    private final Map<String, List<Request>> activeRequests = new HashMap<String, List<Request>>();

    public RequestManager(Trade plugin, MessageHolder messageHolder, TransactionManager transactionManager) {
        this.plugin = plugin;
        this.messageHolder = messageHolder;
        this.transactionManager = transactionManager;
        this.listener = new RequestListener(this);
    }

    public void load(ConfigurationSection section) {
        options.load(section);
    }

    public void initialize() {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public void unload() {
        ignoring.clear();
        activeRequests.clear();
    }

    public MessageHolder getMessageHolder() {
        return messageHolder;
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
    public RequestRestriction mayRequest(String player, String by, RequestMethod method) {

        final Player requesterPlayer = Bukkit.getPlayerExact(player);
        final Player requestedPlayer = Bukkit.getPlayerExact(by);

        if (requesterPlayer == null || requestedPlayer == null) {
            return RequestRestriction.OFFLINE;
        }

        if (!hasExclusion(requesterPlayer, RequestRestriction.PERMISSION) && options.usePermissions()) {
            return RequestRestriction.PERMISSION;
        }

        if (isIgnoring(player)) {
            return RequestRestriction.IGNORING;
        }

        if (transactionManager.isInTransaction(by)) {
            return RequestRestriction.BUSY;
        }

        if (isRequested(player, by)) {
            return RequestRestriction.PENDING;
        }

        if (!hasExclusion(requesterPlayer, RequestRestriction.FLOOD) && countRequestsBy(requesterPlayer.getName()) >= options.getMaxRequests()) {
            return RequestRestriction.FLOOD;
        }

        RequestRestriction restriction = RequestRestriction.ALLOW;

        if (!requesterPlayer.getWorld().equals(requestedPlayer.getWorld()) && !options.allowCrossWorld())
            restriction = RequestRestriction.CROSS_WORLD;
        else if (!requesterPlayer.getGameMode().equals(requestedPlayer.getGameMode()) && !options.allowCrossGameMode())
            restriction = RequestRestriction.CROSS_GAME_MODE;
        else if (!requesterPlayer.canSee(requestedPlayer) && !options.mustSee())
            restriction = RequestRestriction.VISION;
        else if (requesterPlayer.getLocation().distance(requestedPlayer.getPlayer().getLocation()) > options.getMaxDistance())
            restriction = RequestRestriction.DISTANCE;
        else if (options.getDisabledWorlds() != null && options.getDisabledWorlds().contains(requesterPlayer.getWorld().getName()))
            restriction = RequestRestriction.WORLD;
        // TODO ADD REGION CHECK

        if (!mayUseMethod(requesterPlayer, method))
            restriction = RequestRestriction.METHOD;

        if (restriction != RequestRestriction.ALLOW && hasExclusion(requestedPlayer, restriction))
            return RequestRestriction.ALLOW;
        else
            return restriction;

    }

    private boolean mayUseMethod(Player player, RequestMethod method) {
        if (options.usePermissions())
            return player.hasPermission(method.permission);
        switch (method) {
            case COMMAND:
                return options.allowCommandRequest();
            case LEFT_CLICK:
                return options.allowLeftClickRequest();
            case SHIFT_LEFT_CLICK:
                return options.allowLeftShiftClickRequest();
            case RIGHT_CLICK:
                return options.allowRightClickRequest();
            case SHIFT_RIGHT_CLICK:
                return options.allowRightShiftClickRequest();
        }
        return true;
    }

    private boolean hasExclusion(Player player, RequestRestriction restriction) {
        if (!options.usePermissions()) return false;
        return player.hasPermission(restriction.excludePermission);
    }

    private boolean isRequested(String player, String by) {

        final List<Request> list = getActiveRequests(player);

        if (list == null) return false;

        for (Request request : list) {
            if (request.getRequester().equalsIgnoreCase(by)) {
                return true;
            }
        }

        return false;

    }

    private int countRequestsBy(String player) {
        int amount = 0;
        for (List<Request> requests : activeRequests.values()) {
            for (Request request : requests) {
                if (player.equalsIgnoreCase(request.getRequester())) {
                    amount++;
                }
            }
        }
        return amount;
    }

    // Current request methods
    private Request getRequest(String player, String by) {

        final List<Request> set = getActiveRequests(player);

        if (set == null) return null;

        for (Request request : set) {
            if (request.getRequester().equalsIgnoreCase(by)) {
                return request;
            }
        }

        return null;

    }

    private boolean addRequest(Request request) {

        List<Request> list = getActiveRequests(request.getRequested());

        if (list == null) {
            list = new LinkedList<Request>();
            setActiveRequests(request.getRequested(), list);
        }

        return list.add(request);

    }

    public boolean removeRequest(Request request) {

        final List<Request> list = getActiveRequests(request.getRequested());

        if (list != null) {

            final boolean removed = list.remove(request);

            if (list.isEmpty()) {
                setActiveRequests(request.getRequested(), null);
            }

            return removed;

        }

        return false;

    }

    private List<Request> getActiveRequests(String player) {
        return activeRequests.get(player.toLowerCase());
    }

    private List<Request> setActiveRequests(String requested, List<Request> list) {
        if (list == null) {
            return activeRequests.remove(requested.toLowerCase());
        }
        return activeRequests.put(requested.toLowerCase(), list);
    }

    public RequestResponse submit(Request request) {

        final RequestRestriction restriction = mayRequest(request.getRequested(), request.getRequester(), request.getMethod());

        if (restriction == RequestRestriction.ALLOW) {

            // We check if there is a counter request
            final Request counterRequest = getRequest(request.getRequester(), request.getRequested());

            // If so we start a transaction
            if (counterRequest != null) {

                // We remove the counter request from the active requests
                removeRequest(counterRequest);

                final Transaction transaction = transactionManager.createTransaction(counterRequest.getRequester(), counterRequest.getRequested());

                return new RequestResponse(restriction, transaction);

            }

            // If not we add this request to the active requests
            addRequest(request);
            request.setSubmitDate(System.currentTimeMillis());
            Bukkit.getScheduler().runTaskLater(plugin, new RequestTimeOutTask(this, request), options.getTimeoutMillis() / 50);

        }

        // In all other cases we return only the restriction
        return new RequestResponse(restriction, null);

    }

}
