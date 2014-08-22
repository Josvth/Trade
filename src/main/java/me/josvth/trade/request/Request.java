package me.josvth.trade.request;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Request {

    private final UUID idRequester;
    private final UUID idRequested;

    private final RequestMethod method;

    private long submitDate = -1;

    public static Request createRequest(Player player, Player by, RequestMethod method){
        return new Request((player == null)? null : player.getUniqueId(), (by == null)? null : by.getUniqueId(), method);
    }

    public Request(UUID idRequested, UUID idRequester, RequestMethod method) {
        this.idRequester = idRequester;
        this.idRequested = idRequested;
        this.method = method;
    }

    public UUID getIdRequester() {
        return idRequester;
    }

    public Player getRequesterPlayer() {
        return Bukkit.getPlayer(idRequester);
    }

    public OfflinePlayer getRequesterOfflinePlayer() {return Bukkit.getOfflinePlayer(idRequester); }

    public UUID getRequestedID() {
        return idRequested;
    }

    public Player getRequestedPlayer() {
        return Bukkit.getPlayer(idRequested);
    }

    public OfflinePlayer getRequestedOfflinePlayer() {return Bukkit.getOfflinePlayer(idRequested); }

    public RequestMethod getMethod() {
        return method;
    }

    public long getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(long submitDate) {
        this.submitDate = submitDate;
    }

}
