package fr.ensimag.make.distrib.agent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MakeMeMake {
	static private String aide = "Usage : java MakeMeMake @serveur portServeur\n\tPrécisez l'adresse de la machine distante";
	static private Socket socket = null;
	static private BufferedReader receiveFromServer;
	static private PrintWriter sendToServer;
	
	static public void main(String[] args) throws Exception {
		Boolean avoid = false;
		Integer port = 0;
		if (args == null || args.length < 1) {
				System.out.println(aide);
				avoid = true;
		}
		if (!avoid) {
			try {
				port = Integer.valueOf(args[1]);
			} catch (NumberFormatException nfe) {
				System.out.println(aide);
				avoid = true;
			}
		}
		
		// First step : communicate to the core server that this node is available
		while (true) {
			if (!avoid) {
				System.out.println("Connexion en cours...");
				connect(args[0], port);
				
				if (socket != null) {
			        System.out.println("Connexion correctement mise en place : " + socket);
			
					try {
						while (true) { 
							if (receiveFromServer == null && sendToServer == null) { 
								receiveFromServer = new BufferedReader(
										new InputStreamReader(
												socket.getInputStream()));
								sendToServer = new PrintWriter(new BufferedWriter(
										new OutputStreamWriter(
												socket.getOutputStream())), true);
							}
							if (!waitForWork()) break;
						}
				        leave();
					} catch (IOException e) {
						System.out.println("La connexion avec le serveur a été interrompue... Tentative de reconnexion.");
					}
				}
			}
			break;
		}
	}
	
	static private void connect(String inetAddress, Integer port) {
		try {
			socket = new Socket(inetAddress, port);
		} catch (UnknownHostException uhe) {
			System.out.println("Erreur : serveur injoignable...\t\nVérifiez la configuration");
		} catch (IOException e) {
			System.out.println("Erreur : serveur injoignable...\t\nVérifiez la configuration");
		}
	}
	
	static private boolean waitForWork() throws Exception {
		if (sendToServer != null && receiveFromServer != null) {
			System.out.println("Envoie \"OK\"");
			sendToServer.println("OK");
			
			System.out.println("En attente de travail...");
			String action = receiveFromServer.readLine();
			
			
			if (action.equals("END")) {
		        sendToServer.println("ACK END") ;
		        return false;
		       // receiveFromServer.close();
		        //sendToServer.close();
			} else {
				System.out.println("Action recue = " + action);
				int posComma = action.indexOf(",");
				String nbFiles = action.substring(0, posComma);
				String operation = action.substring(posComma + 1, action.length());
				int intNbFiles = 0;
				try {
					intNbFiles = Integer.valueOf(nbFiles);
				} catch (NumberFormatException e) {
					throw e;
				}
				
				System.out.println("Travail recu du serveur, action...");
				System.out.println("\toperation = " + operation);
				System.out.println("\tnbFiles = " + nbFiles);
				sendToServer.println("OK2");
				
				System.out.println("Recuperation des fichiers requis...");
				
				for (int i = 0; i < intNbFiles; i++) {
					System.out.println("Reception fichier #" + (i+1) + "/" + nbFiles);
					String fileLine = receiveFromServer.readLine();
					posComma = fileLine.indexOf(",");
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
					sendToServer.println("OK3");
				    InputStream is = socket.getInputStream();

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
				    sendToServer.println("OK4");
				    System.out.println("\tReception terminee, fichier recu.");
				}
				
				// exécution de l'opération
				System.out.println("Execution commande");
				System.out.println("<<<<>>>>");
				ExecCommand.execute(operation);
				System.out.println("<<<<>>>>");
				// récupération nom cible
				String targetName = receiveFromServer.readLine();
			    
			    File myFile = new File ("./" + targetName);
				byte [] fileAsByteArray  = new byte [(int)myFile.length()];
				
				sendToServer.println(myFile.length() + "," + myFile.getName());
				String ok1 = receiveFromServer.readLine();
				if (!ok1.equals("OK1"))
					throw new Exception("Not OK1");
				
				FileInputStream fis = new FileInputStream(myFile);
				BufferedInputStream bis = new BufferedInputStream(fis);
				bis.read(fileAsByteArray, 0, fileAsByteArray.length);
				OutputStream os = socket.getOutputStream();
				System.out.println("Envoie resultat...");
				os.write(fileAsByteArray, 0, fileAsByteArray.length);
				os.flush();
				//os.close();
				String ok2 = receiveFromServer.readLine();
				if (!ok2.equals("OK2"))
					throw new Exception("Not OK2");
		        sendToServer.println("Fichier " + targetName + "realise !");
		        return true;
			}
		} else {
			System.out.println("Les flux d'entree-sortie sont corrompus, tentative de recuperation de la liaison avec le serveur...");
			return true;
		}
	}
	
	
	static private void leave() throws IOException {
		socket.close();
	}
}
