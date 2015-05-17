package com.spinalcraft.spinalpack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Spinalpack extends JavaPlugin{
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost";
	
	static Connection conn = null;
	
	static ConsoleCommandSender console;
	
	@Override
	public void onEnable(){
		getDataFolder().mkdirs();
		console = Bukkit.getConsoleSender();
		
		console.sendMessage(code(Co.BLUE) + "Spinalpack online!");
		createDatabase();
		new Thread(new CommandSocketListener(this)).start();
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
			sender.sendMessage(Spinalpack.code(Co.GREEN) + "You can view Spinalcraft's rules here: " + Spinalpack.code(Co.BLUE) + "http://www.reddit.com/r/SpinalCraft/wiki/rules");
			sender.sendMessage("");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("report")){
			sender.sendMessage("");
			sender.sendMessage(Spinalpack.code(Co.GREEN) + "Use this link to send a report to the mods: " + Spinalpack.code(Co.BLUE) + "http://www.reddit.com/message/compose?to=%2Fr%2FSpinalCraft");
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
		return false;
	}
	
	public static ResultSet query(String query){
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean update(String query){
		Statement stmt;
		if(conn == null)
			System.out.println("Conn is null!");
		try {
			stmt = conn.createStatement();
			if(stmt == null)
				System.out.println("Stmt is null!");
			stmt.executeUpdate(query);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static PreparedStatement prepareStatement(String query) throws SQLException{
		return conn.prepareStatement(query);
	}
	
	public static int deleteReportedChunk(String world, int x, int z){
		String query;
		
		query = "SELECT * FROM Chunks WHERE world = '" + world + "' AND x = '" + x + "' AND z = '" + z + "'";
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if(!rs.first())
				return 0;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return 2;
		}
		
		query = "DELETE FROM Chunks WHERE world = '" + world + "' AND x = '" + x + "' AND z = '" + z + "'";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return 2;
		}
		return 1;
	}
	
	public static int insertReportedChunk(String username, String world, int x, int z){
		String query; 
		
		query = "SELECT * FROM Chunks WHERE world = '" + world + "' AND x = '" + x + "' AND z = '" + z + "' AND batchNum IS NULL";
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if(rs.first())
				return 0;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return 2;
		}
		
		query = "INSERT INTO Chunks(username, date, world, x, z) values ('" + username + "', '" + System.currentTimeMillis() / 1000 + "', '" + world + "', '" + x + "', '" + z + "')";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 2;
		}
		console.sendMessage(code(Co.GOLD) + "Chunk coords: (" + x + ", " + z + ")");
		return 1;
	}
	
	public static String petOwner(UUID uuid){
		String query = "SELECT * FROM Pets WHERE uuid = '" + uuid.toString() + "'";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if(rs.first())
				return rs.getString("currentOwner");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void insertPet(Player player, Entity entity){
		String query;
		query = "INSERT INTO Pets (currentOwner, since, petType, uuid, originalOwner) VALUES ('"
		+ player.getName() + "', '"
		+ System.currentTimeMillis() + "', '"
		+ entity.getType().toString() + "', '"
		+ entity.getUniqueId().toString() + "', '"
		+ player.getName() + "')";
		
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createChunkTable(){
		String query;
		query = "CREATE TABLE IF NOT EXISTS Chunks (ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, username VARCHAR(31), date VARCHAR(63), world VARCHAR(31), x INT, z INT)";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		query = "ALTER TABLE Chunks ADD batchNum INT";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			console.sendMessage("Chunks table already has a batchNum column. Moving on...");
		}
	}
	
	public static void createPetTable(){
		String query;
		try {
			query = "CREATE TABLE IF NOT EXISTS Pets (ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, currentOwner VARCHAR(31), since BIGINT, petType VARCHAR(31), uuid VARCHAR(36), originalOwner VARCHAR(31))";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createDatabase(){
		String query;
		String dbName = getDbName();
		try {
			conn = DriverManager.getConnection(DB_URL, "root", "password");
			Statement stmt = conn.createStatement();
			
			stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
			
			query = "USE " + dbName;
			stmt.executeQuery(query);
			
			console.sendMessage(code(Co.GREEN) + "Spinalpack loaded database!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			console.sendMessage(code(Co.DARKRED) + "Spinalpack failed to load database!");
		}
	}
	
	private String getDbName(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/plugins/Spinalpack/db.txt"));
			String ret = reader.readLine();
			reader.close();
			return ret;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String code(Co color){
		switch(color){
		case BLACK:
			return "\u00A70";
		case DARKBLUE:
			return "\u00A71";
		case DARKGREEN:
			return "\u00A72";
		case DARKAQUA:
			return "\u00A73";
		case DARKRED:
			return "\u00A74";
		case DARKPURPLE:
			return "\u00A75";
		case GOLD:
			return "\u00A76";
		case GRAY:
			return "\u00A77";
		case DARKGRAY:
			return "\u00A78";
		case BLUE:
			return "\u00A79";
		case GREEN:
			return "\u00A7a";
		case AQUA:
			return "\u00A7b";
		case RED:
			return "\u00A7c";
		case LIGHTPURPLE:
			return "\u00A7d";
		case YELLOW:
			return "\u00A7e";
		case WHITE:
			return "\u00A7f";
		}
		return "";
	}
	
	public static String parseColorTags(String original){
		return original.replace("#black", Spinalpack.code(Co.BLACK))
				.replace("#darkblue", Spinalpack.code(Co.DARKBLUE))
				.replace("#darkgreen", Spinalpack.code(Co.DARKGREEN))
				.replace("#darkaqua", Spinalpack.code(Co.DARKAQUA))
				.replace("#darkred", Spinalpack.code(Co.DARKRED))
				.replace("#darkpurple", Spinalpack.code(Co.DARKPURPLE))
				.replace("#gold", Spinalpack.code(Co.GOLD))
				.replace("#gray", Spinalpack.code(Co.GRAY))
				.replace("#darkgray", Spinalpack.code(Co.DARKGRAY))
				.replace("#blue", Spinalpack.code(Co.BLUE))
				.replace("#green", Spinalpack.code(Co.GREEN))
				.replace("#aqua", Spinalpack.code(Co.AQUA))
				.replace("#red", Spinalpack.code(Co.RED))
				.replace("#purple", Spinalpack.code(Co.LIGHTPURPLE))
				.replace("#yellow", Spinalpack.code(Co.YELLOW))
				.replace("#white", Spinalpack.code(Co.WHITE));
	}
}
