package fr.ensimag.make.distrib.core.comm;

import java.io.IOException;

public class AgentListener extends Thread implements Runnable {

	private Agent agent;
	
	public AgentListener(Agent agent) {
		this.agent = agent;
	}
	
	public void run() {
		System.out.println("$THREAD AgentListener #" + agent.id + " entry");
		try {
			String ok = agent.receiveFromAgent.readLine();
			System.out.println("OK ? = " + ok);
			System.out.println("Envoi operation DesertStorm");
			agent.sendToAgent.println("DesertStorm");
			String retour = agent.receiveFromAgent.readLine();
			System.out.println("Retour recu = " + retour);
			while (true)
				;
			//System.out.println("Envoi operation END");
			//agent.sendToAgent.println("END");
			//String ackEnd = agent.receiveFromAgent.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
