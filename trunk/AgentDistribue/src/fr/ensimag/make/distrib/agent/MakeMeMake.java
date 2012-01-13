package fr.ensimag.make.distrib.agent;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MakeMeMake {
	static private String aide = "Usage : java MakeMeMake @serveur portServeur\n\tPr�cisez l'adresse de la machine distante";
	static private Socket socket = null;
	static private BufferedReader receiveFromServer;
	static private PrintWriter sendToServer;
	
	static public void main(String[] args) throws IOException, InterruptedException {
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
						System.out.println("La connexion avec le serveur a �t� interrompue... Tentative de reconnexion.");
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
			System.out.println("Erreur : serveur injoignable...\t\nV�rifiez la configuration");
		} catch (IOException e) {
			System.out.println("Erreur : serveur injoignable...\t\nV�rifiez la configuration");
		}
	}
	
	static private boolean waitForWork() throws IOException {
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
				System.out.println("Travail recu du serveur, action... Taille = " + action);
				// ExecCommand.execute(operation);
				System.out.println("Attente fichier...");
				
				int filesize = 2238520;
				
				// receive file
				
			    byte [] mybytearray  = new byte [filesize];
			    InputStream is = socket.getInputStream();
			    FileOutputStream fos = new FileOutputStream("lol.jpg");
			    BufferedOutputStream bos = new BufferedOutputStream(fos);
			    int bytesRead = is.read(mybytearray,0,mybytearray.length);
			    int current = bytesRead;

			    // thanks to A. C�diz for the bug fix
			    do {
			       bytesRead =
			          is.read(mybytearray, current, (mybytearray.length-current));
			       if(bytesRead >= 0) current += bytesRead;
			    } while(bytesRead > -1);

			    bos.write(mybytearray, 0 , current);
			    bos.flush();
				
				
		        sendToServer.println("Travail fini !");
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
