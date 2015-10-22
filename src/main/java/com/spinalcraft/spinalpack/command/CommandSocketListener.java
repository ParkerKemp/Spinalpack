package com.spinalcraft.spinalpack.command;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import com.spinalcraft.spinalpack.Spinalpack;

public class CommandSocketListener implements Runnable{
	private Socket socket;
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
				socket = server.accept();
				new Thread(new CommandClientHandler(socket, plugin)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

