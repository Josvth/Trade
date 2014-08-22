package me.josvth.trade.request;

import me.josvth.bukkitformatlibrary.message.MessageHolder;
import me.josvth.trade.Trade;
import me.josvth.trade.tasks.RequestTimeOutTask;
import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.TransactionManager;
import me.josvth.trade.transaction.action.StartAction;
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

    private final Set<UUID> ignoring = new HashSet<UUID>();

    private final Map<UUID, List<Request>> activeRequests = new HashMap<UUID, List<Request>>();

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
    public boolean toggleIgnoring(Player player) {
        if (isIgnoring(player)) {
            ignoring.remove(player.getUniqueId());
            return false;
        } else {
            ignoring.add(player.getUniqueId());
            return true;
        }
    }

    public boolean isIgnoring(Player player) {
        return ignoring.contains(player.getUniqueId());
    }

    // Restriction handling
    public RequestRestriction mayRequest(Player requester, Player requested, RequestMethod method) {

        if (requester == null || requested == null) {
            return RequestRestriction.OFFLINE;
        }

        // We check if the entity is a NPC
        if (requested.hasMetadata("NPC")) {
            return RequestRestriction.NPC;
        }

        if (requested == requester) {
            return RequestRestriction.SELF;
        }

        RequestRestriction restriction = RequestRestriction.ALLOW;

        // We check method first
        if (!mayUseMethod(requester, method)) {
            restriction = RequestRestriction.METHOD;

            // We directly check for exclusion
            if (!hasExclusion(requested, restriction)) {
                return restriction;
            }

        }

//        if (!hasExclusion(requester, RequestRestriction.PERMISSION)) {
//            return RequestRestriction.PERMISSION;
//        }

        if (isIgnoring(requested)) {
            return RequestRestriction.IGNORING;
        }

        if (transactionManager.isInTransaction(requested)) {
            return RequestRestriction.BUSY;
        }

        if (isRequested(requested, requester)) {
            return RequestRestriction.PENDING;
        }

        if (!hasExclusion(requester, RequestRestriction.FLOOD) && countRequestsBy(requester) >= options.getMaxRequests()) {
            return RequestRestriction.FLOOD;
        }

        if (!requester.getWorld().equals(requested.getWorld()) && !options.allowCrossWorld()) {
            restriction = RequestRestriction.CROSS_WORLD;
        } else if (!requester.getGameMode().equals(requested.getGameMode()) && !options.allowCrossGameMode()) {
            restriction = RequestRestriction.CROSS_GAME_MODE;
        } else if (!requester.canSee(requested) && !options.mustSee()) {
            restriction = RequestRestriction.VISION;
        } else if (requester.getLocation().distance(requested.getPlayer().getLocation()) > options.getMaxDistance() && options.getMaxDistance() != -1) {
            restriction = RequestRestriction.DISTANCE;
        } else if (options.getDisabledWorlds() != null && options.getDisabledWorlds().contains(requester.getWorld().getName())) {
            restriction = RequestRestriction.WORLD;
        }// TODO ADD REGION CHECK

        if (restriction != RequestRestriction.ALLOW && hasExclusion(requested, restriction)) {
            return RequestRestriction.ALLOW;
        }

        return restriction;

    }

    private boolean hasExclusion(Player player, RequestRestriction restriction) {
        return player.hasPermission(restriction.excludePermission) || player.isOp();
    }

    private boolean mayUseMethod(Player requester, RequestMethod method) {

        // First we check permissions
        if (!requester.hasPermission(method.permission)) {
            return false;
        }

        // Next we check if the request option is enabled
        switch (method) {
            case COMMAND:
                return getOptions().allowCommandRequest();
            case LEFT_CLICK:
                return getOptions().allowLeftClickRequest();
            case SHIFT_LEFT_CLICK:
                return getOptions().allowLeftShiftClickRequest();
            case RIGHT_CLICK:
                return getOptions().allowRightClickRequest();
            case SHIFT_RIGHT_CLICK:
                return options.allowRightShiftClickRequest();
        }
        return true;
    }



    private boolean isRequested(Player player, Player by) {

        final List<Request> list = getActiveRequests(player);

        if (list == null) return false;

        for (Request request : list) {
            if (request.getIdRequester().equals(by.getUniqueId())) {
                return true;
            }
        }

        return false;

    }

    private int countRequestsBy(Player player) {
        int amount = 0;
        for (List<Request> requests : activeRequests.values()) {
            for (Request request : requests) {
                if (request.getIdRequester() == player.getUniqueId()) {
                    amount++;
                }
            }
        }
        return amount;
    }

    // Current request methods
    private Request getRequest(Player player, Player by) {

        final List<Request> set = getActiveRequests(player);

        if (set == null) return null;

        for (Request request : set) {
            if (request.getIdRequester() == by.getUniqueId()) {
                return request;
            }
        }

        return null;

    }

    private boolean addRequest(Request request) {

        List<Request> list = getActiveRequests(request.getRequestedPlayer());

        if (list == null) {
            list = new LinkedList<Request>();
            setActiveRequests(request.getRequestedPlayer(), list);
        }

        return list.add(request);

    }

    public boolean removeRequest(Request request) {

        final List<Request> list = getActiveRequests(request.getRequestedPlayer());

        if (list != null) {

            final boolean removed = list.remove(request);

            if (list.isEmpty()) {
                setActiveRequests(request.getRequestedPlayer(), null);
            }

            return removed;

        }

        return false;

    }

    public List<Request> getActiveRequests(Player player) {
        return activeRequests.get(player.getUniqueId());
    }

    private List<Request> setActiveRequests(Player player, List<Request> list) {
        if (list == null) {
            return activeRequests.remove(player.getUniqueId());
        }
        return activeRequests.put(player.getUniqueId(), list);
    }

    public RequestResponse submit(Request request) {

        final RequestRestriction restriction = mayRequest(request.getRequesterPlayer(), request.getRequestedPlayer(), request.getMethod());

        if (restriction == RequestRestriction.ALLOW) {

            // We check if there is a counter request
            final Request counterRequest = getRequest(request.getRequesterPlayer(), request.getRequestedPlayer());

            // If so we start a transaction
            if (counterRequest != null) {

                // We remove the counter request from the active requests
                removeRequest(counterRequest);

                final Transaction transaction = transactionManager.createTransaction(counterRequest.getRequesterPlayer(), counterRequest.getRequestedPlayer());

                new StartAction(transaction).execute();

                return new RequestResponse(request, restriction, transaction);

            }

            // If not we add this request to the active requests
            addRequest(request);
            request.setSubmitDate(System.currentTimeMillis());
            Bukkit.getScheduler().runTaskLater(plugin, new RequestTimeOutTask(this, request), options.getTimeoutMillis() / 50);

            // And send a message to the requested
            messageHolder.getMessage("requesting.requested-by").send(request.getRequestedPlayer(), "%player%", request.getRequesterPlayer().getName());

        }

        // Send message to requester
        if (restriction == RequestRestriction.METHOD) {
            if (messageHolder.hasMessage(request.getMethod().messagePath)) {
                messageHolder.getMessage(request.getMethod().messagePath).send(request.getRequesterPlayer());
            }
        } else {
            messageHolder.getMessage(restriction.requestMessagePath).send(request.getRequesterPlayer(), "%player%", (request.getRequestedPlayer() != null) ? request.getRequestedPlayer().getName() : "unknown");
        }

        // In all other cases we return only the restriction
        return new RequestResponse(request, restriction, null);

    }

}
