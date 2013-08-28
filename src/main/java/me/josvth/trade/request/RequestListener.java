package me.josvth.trade.request;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import me.josvth.bukkitformatlibrary.managers.FormatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class RequestListener implements Listener {

	private final RequestManager requestManager;
   	private final FormatManager formatManager;

	public RequestListener(RequestManager requestManager, FormatManager formatManager) {
		this.requestManager = requestManager;
		this.formatManager = formatManager;
	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {

		if (!(event.getRightClicked() instanceof Player)) return;

		Player requester = event.getPlayer();
		Player requested = (Player) event.getRightClicked();

		RequestMethod method;

		if (requester.isSneaking())
			method = RequestMethod.SHIFT_RIGHT_CLICK;
		else
			method = RequestMethod.RIGHT_CLICK;

		// First we check if the requester his answering a request
		Request request = requestManager.getRequest(requester.getName(), requested.getName());

		if (request != null) {

			if (requestManager.mayUseMethod(requester, method)) {
				event.setCancelled(true);

				RequestRestriction restriction = requestManager.accept(request);

				if (restriction == RequestRestriction.ALLOW) {
					FormattedMessage message = formatManager.create("personal", "trading.started");
					message.send(requester, "%player%", requested.getName());
					message.send(requested, "%player%", requester.getName());
				} else {
					FormattedMessage message = formatManager.create("personal", restriction.tradeMessagePath);
					message.send(requester, "%player%", requested.getName());
					message.send(requested, "%player%", requester.getName());
				}

			}


		} else {

			request = new Request(requester.getName(), requested.getName(), method);

			RequestRestriction restriction = requestManager.checkRequest(request);

			if (restriction == RequestRestriction.ALLOW) {
				requestManager.submit(request);
				event.setCancelled(true);
			}

			if (restriction != RequestRestriction.METHOD && restriction != RequestRestriction.PERMISSION)
				formatManager.create("personal", restriction.requestMessagePath).send(requester, "%player%", requested.getName());

		}
	}
}
