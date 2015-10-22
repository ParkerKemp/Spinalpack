package com.spinalcraft.spinalpack.command;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandExecutor extends BukkitRunnable{
	
	private String input;
	
	public CommandExecutor(String input){
		this.input = input;
	}
	
	@Override
	public void run(){
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), input);
	}
}