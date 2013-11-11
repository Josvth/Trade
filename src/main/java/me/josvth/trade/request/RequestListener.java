package me.josvth.trade.request;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import me.josvth.bukkitformatlibrary.managers.FormatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class RequestListener implements Listener {

	private final RequestManager requestManager;
   	private final FormatManager formatManager;

	public RequestListener(RequestManager requestManager) {
		this.requestManager = requestManager;
		this.formatManager = requestManager.getFormatManager();
	}

	@EventHandler
	public void onRightClick(PlayerInteractEntityEvent event) {

		if (!(event.getRightClicked() instanceof Player)) return;

		Player requester = event.getPlayer();
		Player requested = (Player) event.getRightClicked();

		RequestMethod method;

		if (requester.isSneaking())
			method = RequestMethod.SHIFT_RIGHT_CLICK;
		else
			method = RequestMethod.RIGHT_CLICK;

		handleEvent(event, requester, requested, method);

	}

	@EventHandler
	public void onLeftClick(EntityDamageByEntityEvent event) {

		if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;

		Player requester = (Player) event.getDamager();
		Player requested = (Player) event.getEntity();

		RequestMethod method;

		if (requester.isSneaking())
			method = RequestMethod.SHIFT_LEFT_CLICK;
		else
			method = RequestMethod.LEFT_CLICK;

		handleEvent(event, requester, requested, method);

	}

	private void handleEvent(Cancellable event, Player requester, Player requested, RequestMethod method) {

		final RequestResponse response = requestManager.submit(new Request(requester.getName(), requested.getName(), method));

		final RequestRestriction restriction = response.getRequestRestriction();

		if (restriction == RequestRestriction.ALLOW) {
			event.setCancelled(true);
		}

		if (response.getTransaction() != null) {
			response.getTransaction().start();
			// TODO add messages

		} else {

			if (restriction == RequestRestriction.METHOD) {
				formatManager.getMessage(RequestMethod.COMMAND.messagePath).send(requester);
			} else {
				formatManager.getMessage(restriction.requestMessagePath).send(requester, "%player%", requested.getName());
				formatManager.getMessage("requesting.requested-by").send(requested, "%player%", requester.getName());
			}

		}

	}
}
