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

import fr.ensimag.make.distrib.parser.Parser;

public class Register extends Thread implements Runnable {
	public static List<AgentListener> agentListeners = null;
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
			while (Parser.isTasksToExec()) {
				try {
					Socket soc;
					s.setSoTimeout(5000);
					soc = s.accept();
					addAgent(soc);
					System.out.println("$THREAD REGISTER : Connexion recue, new agent id=" + (lastId-1));
					// Un BufferedReader permet de lire par ligne.
				} catch (Exception e) {
				}
			}
		}
		
		if (agentListeners != null) {
			for (AgentListener al : agentListeners)
				al.interrupt();
		}
        this.interrupt();

        
	}

	private void addAgent(Socket soc) throws IOException {
		if (agentListeners == null) {
			agentListeners = new ArrayList<AgentListener>();
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
		agentListeners.add(al);
		
	}
}
