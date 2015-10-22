package com.spinalcraft.spinalpack.command;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.bukkit.plugin.java.JavaPlugin;

public class CommandClientHandler implements Runnable{
	
	private Socket socket;
	private JavaPlugin plugin;
	
	public CommandClientHandler(Socket socket, JavaPlugin plugin) {
		this.socket = socket;
		this.plugin = plugin;
	}

	@Override
	public void run() {
		InputStream is;
		try {
			is = socket.getInputStream();
			byte[] buffer = new byte[128];
			int read;
			while((read = is.read(buffer)) != -1){
				final String input = new String(buffer, 0, read);
				
				new CommandExecutor(input).runTask(plugin);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
