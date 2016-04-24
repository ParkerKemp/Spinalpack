package com.spinalcraft.spinalpack;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class EventListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerTeleport(PlayerTeleportEvent event){
		TeleportCause cause = event.getCause();
		if(cause == TeleportCause.CHORUS_FRUIT)
			event.setCancelled(true);
	}
}
