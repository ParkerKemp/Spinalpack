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
