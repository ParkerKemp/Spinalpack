package com.spinalcraft.spinalpack;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import com.spinalcraft.spinalpack.command.CommandSocketListener;

public class Spinalpack extends SpinalcraftPlugin{
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost";
	
	static Connection conn = null;
	
	static ConsoleCommandSender console;
	
	@Override
	public void onEnable(){
		new Thread(new CommandSocketListener(this)).start();
		
		getServer().getPluginManager().registerEvents(new EventListener(Bukkit.getPluginManager().isPluginEnabled("WorldGuard")),  this);
		
		//Load CommandExecutor and CommandClientHandler preemptively, so it can still be used after overwriting .jar file
		Bukkit.getServicesManager().load(com.spinalcraft.spinalpack.command.CommandExecutor.class);
		Bukkit.getServicesManager().load(com.spinalcraft.spinalpack.command.CommandClientHandler.class);
	}
	
	@Override
	public void onDisable(){
		try {
			if(conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("rules")){
			sender.sendMessage("");
			sender.sendMessage(ChatColor.GREEN + "You can view Spinalcraft's rules here: " + ChatColor.BLUE + "http://www.reddit.com/r/SpinalCraft/wiki/rules");
			sender.sendMessage("");
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("report")){
			sender.sendMessage("");
			sender.sendMessage(ChatColor.GREEN + "Use this link to send a report to the mods: " + ChatColor.BLUE + "http://www.reddit.com/message/compose?to=%2Fr%2FSpinalCraft");
			sender.sendMessage("");
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("setdonor")){
			if(args.length == 0){
				return false;
			}
			Bukkit.getServer().dispatchCommand(sender, "pex user " + args[0] + " group add trusted");
			Bukkit.getServer().dispatchCommand(sender, "elevate " + args[0] + " donor");
			return true;
		}
		
	    if(cmd.getName().equalsIgnoreCase("donate")){
	    	sender.sendMessage(ChatColor.GREEN + "Donating money helps Spinalcraft keep running! Donate at least $5 and you will get a green name in chat, as well as a third slip sign!");
	    	sender.sendMessage(ChatColor.GREEN + "Use this link to make a donation via PayPal (remember to add your IGN as a note so we know who you are!):");
	    	sender.sendMessage(ChatColor.BLUE + "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=DU7SSYS25BSCA");
	    	return true;
	    }
	    
	    if(cmd.getName().equalsIgnoreCase("teamspeak")){
	    	sender.sendMessage(ChatColor.GREEN + "Spinalcraft has its own dedicated TeamSpeak 3 server!");
	    	sender.sendMessage(ChatColor.GREEN + "Address: " + ChatColor.AQUA + "ts.spinalcraft.com");
	    	sender.sendMessage(ChatColor.GREEN + "Password: " + ChatColor.AQUA + "spinal");
	    	return true;
	    }
	    
		return false;
	}
}
