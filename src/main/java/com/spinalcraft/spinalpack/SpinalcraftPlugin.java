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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class SpinalcraftPlugin extends JavaPlugin {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost";
	
	static Connection conn = null;
	
	static ConsoleCommandSender console;
	
	@Override
	public void onEnable(){
		getDataFolder().mkdirs();
		console = Bukkit.getConsoleSender();
		
		console.sendMessage(ChatColor.BLUE + this.getName() + " online!");
		createDatabase();
	}
	
	@Override
	public void onDisable(){
		try {
			if(conn != null){
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void broadcastMessage(String message){
		new BroadcastTask(message).runTask(this);
	}
		
	private void createDatabase(){
		String query;
		String dbName = getDbName();
		
		if(conn == null){
			try {
				conn = DriverManager.getConnection(DB_URL, "root", "password");
				Statement stmt = conn.createStatement();
				
				stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
				
				query = "USE " + dbName;
				stmt.executeQuery(query);
				
				console.sendMessage(ChatColor.GREEN + "Loaded Spinalcraft database!");
			} catch (SQLException e) {
				e.printStackTrace();
				console.sendMessage(ChatColor.DARK_RED + "Failed to load Spinalcraft database!");
			}
		}
	}
	
	protected static boolean update(String query){
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
	
	public static PreparedStatement prepareStatement(String query) throws SQLException{
		return conn.prepareStatement(query);
	}
	
	
	private String getDbName(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/plugins/Spinalpack/db.txt"));
			String ret = reader.readLine();
			reader.close();
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String parseColorTags(String original){
		return original.replace("#black", ChatColor.BLACK.toString())
				.replace("#darkblue", ChatColor.DARK_BLUE.toString())
				.replace("#darkgreen", ChatColor.DARK_GREEN.toString())
				.replace("#darkaqua", ChatColor.DARK_AQUA.toString())
				.replace("#darkred", ChatColor.DARK_RED.toString())
				.replace("#darkpurple", ChatColor.DARK_PURPLE.toString())
				.replace("#gold", ChatColor.GOLD.toString())
				.replace("#gray", ChatColor.GRAY.toString())
				.replace("#darkgray", ChatColor.DARK_GRAY.toString())
				.replace("#blue", ChatColor.BLUE.toString())
				.replace("#green", ChatColor.GREEN.toString())
				.replace("#aqua", ChatColor.AQUA.toString())
				.replace("#red", ChatColor.RED.toString())
				.replace("#purple", ChatColor.LIGHT_PURPLE.toString())
				.replace("#yellow", ChatColor.YELLOW.toString())
				.replace("#white", ChatColor.WHITE.toString());
	}
}
