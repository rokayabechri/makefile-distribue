package fr.ensimag.make.distrib.core.comm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
				System.out.println("Envoi de la tache et du nombre d'element qui vont etre envoyes.");
				
				int nbFiles = 2;
				String[] files = {"test.jpg","test2.jpg"};
				String cmd = "echo toto";
				String targetName = "lol.png";
				
				agent.sendToAgent.println(nbFiles + "," + cmd);
				
				String ok2 = agent.receiveFromAgent.readLine();
				if (!ok2.equals("OK2"))
					throw new Exception ("fail2");
				else
					System.out.println(ok2);
				
				for (int i = 0; i < nbFiles; i++) {
					File myFile = new File ("./" + files[i]);
					byte [] fileAsByteArray  = new byte [(int)myFile.length()];
					
					agent.sendToAgent.println(myFile.length() + "," + myFile.getName());
					String ok3 = agent.receiveFromAgent.readLine();
					if (!ok3.equals("OK3"))
						throw new Exception ("fail3");
					else
						System.out.println(ok3);
					
					FileInputStream fis = new FileInputStream(myFile);
					BufferedInputStream bis = new BufferedInputStream(fis);
					bis.read(fileAsByteArray, 0, fileAsByteArray.length);
					OutputStream os = agent.socket.getOutputStream();
					System.out.println("Sending...");
					System.out.println("1");
					os.write(fileAsByteArray, 0, fileAsByteArray.length);
					System.out.println("2");
					os.flush();
					System.out.println("3");
					//os.close();
					
					String ok4 = agent.receiveFromAgent.readLine();
					if (!ok4.equals("OK4"))
						throw new Exception ("fail4");
				}
				
				// les fichiers sont envoyés, on attend le résultat désormais
				agent.sendToAgent.println(targetName);
				String fileLine = agent.receiveFromAgent.readLine();
				int posComma = fileLine.indexOf(",");
				String fileSize = fileLine.substring(0, posComma);
				String fileName = fileLine.substring(posComma + 1, fileLine.length());
				int intFileSize = 0;
				try {
					intFileSize = Integer.valueOf(fileSize);
				} catch (NumberFormatException e) {
					throw e;
				}
				System.out.println("\tFileName = " + fileName + ", FileSize = " + fileSize + ".");
				
				
				byte [] temp  = new byte [intFileSize];
				
				System.out.println("\tReception en cours");
				agent.sendToAgent.println("OK1");
			    InputStream is = agent.socket.getInputStream();
			    FileOutputStream fos = new FileOutputStream(fileName);
			    BufferedOutputStream bos = new BufferedOutputStream(fos);
			    int bytesRead = is.read(temp, 0, temp.length);
			    int current = bytesRead;

			    do {
			       bytesRead = is.read(temp, current, (temp.length-current));
			       if(bytesRead >= 0) current += bytesRead;
			    } while(current < intFileSize);

			    bos.write(temp, 0 , current);
			    bos.flush();
			    agent.sendToAgent.println("OK2");
			    String end = agent.receiveFromAgent.readLine();
			    System.out.println("Fini ! Resultat recu par agent : " + end);
				
			}
			System.out.println("Envoi operation END");
			agent.sendToAgent.println("END");
			String ackEnd = agent.receiveFromAgent.readLine();
			System.out.println("AckEND = "+ackEnd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
