package com.spinalcraft.spinalpack;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BroadcastTask extends BukkitRunnable{
	private String message;
	
	public BroadcastTask(String message){
		this.message = message;
	}

	@Override
	public void run() {
		Bukkit.broadcastMessage(message);
	}
}
