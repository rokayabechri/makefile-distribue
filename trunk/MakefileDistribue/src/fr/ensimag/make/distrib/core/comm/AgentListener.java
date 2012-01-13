package fr.ensimag.make.distrib.core.comm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

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
			if (ok.equals("OK")) {
				System.out.println("Envoi de la commande : dir");
				agent.sendToAgent.println("dir");
				
				
				// apres envoi commande, envoi des fichiers
				File myFile = new File ("./data/test.jpg");
				byte [] fileAsByteArray  = new byte [(int)myFile.length()];
				System.out.println("SIZE = "+(int)myFile.length());
				
				FileInputStream fis = new FileInputStream(myFile);
				BufferedInputStream bis = new BufferedInputStream(fis);
				bis.read(fileAsByteArray, 0, fileAsByteArray.length);
				OutputStream os = agent.socket.getOutputStream();
				System.out.println("Sending...");
				os.write(fileAsByteArray, 0, fileAsByteArray.length);
				os.flush();
				os.close();
				
				String retour = agent.receiveFromAgent.readLine();
				System.out.println("Retour recu = " + retour);
			}
			//System.out.println("Envoi operation END");
			//agent.sendToAgent.println("END");
			//String ackEnd = agent.receiveFromAgent.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
	}
	
	
}
