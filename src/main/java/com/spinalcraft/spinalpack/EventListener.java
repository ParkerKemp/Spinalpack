package com.spinalcraft.spinalpack;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class EventListener implements Listener {
	private boolean worldGuardOn = false;
	private WorldGuardPlugin inst;
	private RegionQuery regionQuery;
	
	public EventListener(boolean worldGuardOn){
		this.worldGuardOn = worldGuardOn;
		if (worldGuardOn){
			inst = WorldGuardPlugin.inst();
			regionQuery = inst.getRegionContainer().createQuery();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerTeleport(PlayerTeleportEvent event){
		TeleportCause cause = event.getCause();
		if(cause != TeleportCause.CHORUS_FRUIT)
			return;
		event.setCancelled(true);
		Player player = event.getPlayer();
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 10, 6));
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerBlockPlace(BlockPlaceEvent event){
		if ((event.getBlock().getType() != Material.ENDER_CHEST) || !worldGuardOn){
			return;
		}
		ApplicableRegionSet set = regionQuery.getApplicableRegions(event.getBlock().getLocation());
		if (set == null) return;
		Boolean regionFlag = set.queryValue(null, DefaultFlag.BUYABLE);
		if (regionFlag == null) return;
		if (regionFlag){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You can't place Ender Chests in this area!");
		}
	}
	
}
