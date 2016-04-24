package com.spinalcraft.spinalpack;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerTeleport(PlayerTeleportEvent event){
		TeleportCause cause = event.getCause();
		if(cause != TeleportCause.CHORUS_FRUIT)
			return;
		event.setCancelled(true);
		Player player = event.getPlayer();
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 10, 6));
	}
}
