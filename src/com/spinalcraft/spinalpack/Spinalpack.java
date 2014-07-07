package com.spinalcraft.spinalpack;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;


public class Spinalpack extends JavaPlugin{
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost";
	
	JavaPlugin Slipdisk = null, Spawnalcraft = null, Spinalvote = null;
	
	static Connection conn = null;
	//static Statement stmt = null;
	
	ConsoleCommandSender console;
	
	@Override
	public void onEnable(){
		getDataFolder().mkdirs();
		console = Bukkit.getConsoleSender();
		
		console.sendMessage(code(Co.BLUE) + "Spinalpack online!");
		createDatabase();
	}
	
	@Override
	public void onDisable(){
		try {
			if(conn != null)
				conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
		return false;
	}
	
	public void unpack(){
		reload("Spawnalcraft");
		reload("Spinalvote");
		reload("Slipdisk");
	}
	
	private void reload(String name){
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(name);
		//JavaPluginLoader loader = Bukkit.getPl;
		if(plugin != null){
			manager.disablePlugin(plugin);
		}
		
		try {
			manager.loadPlugin(new File(System.getProperty("user.dir") + "/plugins/Spinalpack/" + name + ".jar"));
		} catch (UnknownDependencyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPluginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidDescriptionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return 2;
		}
		
		query = "DELETE FROM Chunks WHERE world = '" + world + "' AND x = '" + x + "' AND z = '" + z + "'";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 2;
		}
		return 1;
	}
	
	public static int insertReportedChunk(String username, String world, int x, int z){
		String query; 
		
		query = "SELECT * FROM Chunks WHERE world = '" + world + "' AND x = '" + x + "' AND z = '" + z + "'";
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if(rs.first())
				return 0;
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
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
		return 1;
	}
	
	public static void insertVoteRecord(String username, String timestamp, String service){
		String query;
		query = "INSERT INTO Votes(username, date, service) values('" + username + "', '" + timestamp + "', '" + service + "')";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void createSlipTable(){
		String query;
		try {
			query = "CREATE TABLE IF NOT EXISTS Slips (username VARCHAR(31) PRIMARY KEY, timeCreated INT, cooldown INT, w1 VARCHAR(31), sx1 FLOAT, sy1 FLOAT, sz1 FLOAT, x1 FLOAT, y1 FLOAT, z1 FLOAT, pitch1 FLOAT, yaw1 FLOAT, w2 VARCHAR(31), sx2 FLOAT, sy2 FLOAT, sz2 FLOAT, x2 FLOAT, y2 FLOAT, z2 FLOAT, pitch2 FLOAT, yaw2 FLOAT)";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void insertSlipNode(String username, Location sLocation, Location pLocation, int slipno){
		String query;
		if(slipno == 1)
			query = "INSERT INTO Slips (username, timeCreated, cooldown, w1, sx1, sy1, sz1, x1, y1, z1, pitch1, yaw1) VALUES ('"
				+ username
				+ "', '"
				+ System.currentTimeMillis() / 1000
				+ "', '"
				+ -300
				+ "', '"
				+ sLocation.getWorld().getName()
				+ "', '"
				+ sLocation.getBlockX()
				+ "', '"
				+ sLocation.getBlockY()
				+ "', '"
				+ sLocation.getBlockZ()
				+ "', '"
				+ pLocation.getX()
				+ "', '"
				+ pLocation.getY()
				+ "', '"
				+ pLocation.getZ()
				+ "', '"
				+ pLocation.getPitch()
				+ "', '"
				+ pLocation.getYaw()
				+ "') ON DUPLICATE KEY UPDATE "
				+ "timeCreated = '"
				+ System.currentTimeMillis() / 1000
				+ "', cooldown = (cooldown + 30), w1 = '"
				+ sLocation.getWorld().getName()
				+ "', sx1 = '"
				+ sLocation.getBlockX()
				+ "', sy1 = '"
				+ sLocation.getBlockY()
				+ "', sz1 = '"
				+ sLocation.getBlockZ()
				+ "', x1 = '"
				+ pLocation.getX()
				+ "', y1 = '"
				+ pLocation.getY()
				+ "', z1 = '"
				+ pLocation.getZ()
				+ "', pitch1 = '"
				+ pLocation.getPitch()
				+ "', yaw1 = '"
				+ pLocation.getYaw()
				+ "'";
		else
			query = "INSERT INTO Slips (username, timeCreated, cooldown, w2, sx2, sy2, sz2, x2, y2, z2, pitch2, yaw2) VALUES ('"
				+ username
				+ "', '"
				+ System.currentTimeMillis() / 1000
				+ "', '"
				+ -300
				+ "', '"
				+ sLocation.getWorld().getName()
				+ "', '"
				+ sLocation.getBlockX()
				+ "', '"
				+ sLocation.getBlockY()
				+ "', '"
				+ sLocation.getBlockZ()
				+ "', '"
				+ pLocation.getX()
				+ "', '"
				+ pLocation.getY()
				+ "', '"
				+ pLocation.getZ()
				+ "', '"
				+ pLocation.getPitch()
				+ "', '"
				+ pLocation.getYaw()
				+ "') ON DUPLICATE KEY UPDATE "
				+ "timeCreated = '"
				+ System.currentTimeMillis() / 1000
				+ "', cooldown = (cooldown + 30), w2 = '"
				+ sLocation.getWorld().getName()
				+ "', sx2 = '"
				+ sLocation.getBlockX()
				+ "', sy2 = '"
				+ sLocation.getBlockY()
				+ "', sz2 = '"
				+ sLocation.getBlockZ()
				+ "', x2 = '"
				+ pLocation.getX()
				+ "', y2 = '"
				+ pLocation.getY()
				+ "', z2 = '"
				+ pLocation.getZ()
				+ "', pitch2 = '"
				+ pLocation.getPitch()
				+ "', yaw2 = '"
				+ pLocation.getYaw()
				+ "'";		
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean deleteSlip(String username){
		String query;
		query = "DELETE FROM Slips WHERE username = '" + username + "'";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean unlinkSign(String username, int slipno){
		String query;
		if(slipno == 1)
			query = "UPDATE Slips SET w1 = NULL WHERE username = '" + username + "'";
		else
			query = "UPDATE Slips SET w2 = NULL WHERE username = '" + username + "'";
		
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static Slip slip(String username){
		Slip ret = new Slip();
		float x, y, z, pitch, yaw;
		String world;
		String query = "SELECT * FROM Slips WHERE username = '" + username + "'";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeQuery(query);
			
			ResultSet res = stmt.getResultSet();
			if(!res.first())
				return ret;
			
			ret.timeCreated = res.getInt("timeCreated");
			ret.cooldown = res.getInt("cooldown");
			world = res.getString("w1");
			if(world != null){
				x = res.getFloat("sx1");
				y = res.getFloat("sy1");
				z = res.getFloat("sz1");
				ret.sign1 = new Location(Bukkit.getWorld(world), x, y, z);
			
				x = res.getFloat("x1");
				y = res.getFloat("y1");
				z = res.getFloat("z1");
				pitch = res.getFloat("pitch1");
				yaw = res.getFloat("yaw1");
				ret.slip1 = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
			}
				
			world = res.getString("w2");
			if(world != null){
				x = res.getFloat("sx2");
				y = res.getFloat("sy2");
				z = res.getFloat("sz2");
				ret.sign2 = new Location(Bukkit.getWorld(world), x, y, z);
				
				x = res.getFloat("x2");
				y = res.getFloat("y2");
				z = res.getFloat("z2");
				pitch = res.getFloat("pitch2");
				yaw = res.getFloat("yaw2");
				ret.slip2 = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void createChunkTable(){
		String query;
		query = "CREATE TABLE IF NOT EXISTS Chunks (ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, username VARCHAR(31), date VARCHAR(63), world VARCHAR(31), x INT, z INT)";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void createVoteTable(){
		String query;
		try {
			query = "CREATE TABLE IF NOT EXISTS Votes (ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, username VARCHAR(31), date VARCHAR(63), service VARCHAR(63))";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createDatabase(){
		String query;
		try {
			conn = DriverManager.getConnection(DB_URL, "root", "password");
			Statement stmt = conn.createStatement();
			stmt = conn.createStatement();
			
			stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS Spinalcraft");
			
			query = "USE Spinalcraft";
			stmt.executeQuery(query);
			
			console.sendMessage(code(Co.GREEN) + "Spinalpack loaded database!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			console.sendMessage(code(Co.DARKRED) + "Spinalpack failed to load database!");
		}
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
