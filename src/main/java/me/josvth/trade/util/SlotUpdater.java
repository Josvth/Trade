package me.josvth.trade.util;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.inventory.slot.Slot;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class SlotUpdater implements Runnable {

	private final Trade plugin;

	private Queue<Slot> queue = new LinkedBlockingDeque<Slot>();

	private BukkitTask task = null;

	public SlotUpdater(Trade plugin) {
	 	this.plugin = plugin;
	}

	public void addSlot(Slot slot) {
		queue.add(slot);
		schedule();
	}

	private void schedule() {
		if (task == null)
			task = Bukkit.getScheduler().runTask(plugin, this);
	}

	@Override
	public void run() {

		Slot slot = queue.poll();

		while (slot != null) {
            slot.update();
			slot = queue.poll();
		}

		task = null;
	}

	public void stop() {

	}
}
