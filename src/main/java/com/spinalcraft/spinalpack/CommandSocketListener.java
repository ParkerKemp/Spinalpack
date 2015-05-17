package com.spinalcraft.spinalpack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

public class CommandSocketListener implements Runnable{
	private Socket sock;
	private Spinalpack plugin;
	
	
	
	public CommandSocketListener(Spinalpack plugin){
		this.plugin = plugin;
	}
	
	@Override
	public void run(){
		File socketFile = new File(System.getProperty("user.dir") + "/plugins/Spinalpack/sockets/command.sock");
		socketFile.delete();
		AFUNIXServerSocket server;
		try {
			server = AFUNIXServerSocket.newInstance();
			server.bind(new AFUNIXSocketAddress(socketFile));
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		while (!Thread.interrupted()) {
			try {
				sock = server.accept();
				InputStream is = sock.getInputStream();
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
}

