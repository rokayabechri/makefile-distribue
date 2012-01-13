package fr.ensimag.make.distrib.core.comm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Register extends Thread implements Runnable {
	public static List<Agent> agents = null;
	// Incremental value
	private static int lastId = 0;
	private static int port = 13337;
	private static ServerSocket s = null;
	
	public void run() {
		// Accept connexions and add the agent to the agents list
		try {
			s = new ServerSocket(port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (s != null) {
			while (true) {
				try {
					
					Socket soc;
					
					System.out.println("$THREAD REGISTER : Serveur en attente de connexion...");
					soc = s.accept();
					addAgent(soc);
					System.out.println("$THREAD REGISTER : Connexion recue, new agent id=" + lastId);
					// Un BufferedReader permet de lire par ligne.
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}
        

        
	}

	private void addAgent(Socket soc) throws IOException {
		if (agents == null) {
			agents = new ArrayList<Agent>();
		}
		Agent newAgent = new Agent();
		
		newAgent.id = lastId;
		lastId++;
		newAgent.ip = soc.getInetAddress().toString();
		newAgent.port = soc.getPort();
		newAgent.socket = soc;
		newAgent.available = true;
		
		newAgent.receiveFromAgent = new BufferedReader(new InputStreamReader(
				soc.getInputStream()));

		newAgent.sendToAgent = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(soc.getOutputStream())), true);

		
		AgentListener al = new AgentListener(newAgent);
		al.start();
		agents.add(newAgent);
		
	}
}
