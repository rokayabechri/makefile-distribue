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

import com.developpez.adiguba.shell.Shell;

public class MakeMeMake {
	static private String aide = "Usage : java MakeMeMake @serveur portServeur\n\tPr�cisez l'adresse de la machine distante";
	static private String jarId = "LastJar rev 89";
	static private Socket socket = null;
	static private BufferedReader receiveFromServer;
	static private PrintWriter sendToServer;

	static public void main(String[] args) throws Exception {
		System.out.println("JarID = "+ jarId);
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

		// First step : communicate to the core server that this node is
		// available
		while (true) {
			if (!avoid) {
				System.out.println("Connexion en cours...");
				System.out.println("JarID = "+ jarId);
				connect(args[0], port);

				if (socket != null) {
					System.out
							.println("Connexion correctement mise en place : "
									+ socket);

					try {
						while (true) {
							if (receiveFromServer == null
									&& sendToServer == null) {
								receiveFromServer = new BufferedReader(
										new InputStreamReader(
												socket.getInputStream()));
								sendToServer = new PrintWriter(
										new BufferedWriter(
												new OutputStreamWriter(socket
														.getOutputStream())),
										true);
							}
							try {
								if (!waitForWork())
									break;
							} catch (Error e) {
							}
						}
						leave();
					} catch (IOException e) {
						System.out
								.println("La CONNEXION avec le serveur a �t� interrompue... Tentative de reconnexion.");
						e.printStackTrace();
					}
				}
			}
			Thread.sleep(100);
		}
	}

	static private void connect(String inetAddress, Integer port) {
		try {
			socket = new Socket(inetAddress, port);
		} catch (UnknownHostException uhe) {
			System.out
					.println("Erreur : serveur injoignable...\t\nV�rifiez la configuration");
		} catch (IOException e) {
			System.out
					.println("Erreur : serveur injoignable...\t\nV�rifiez la configuration");
		}
	}

	static private boolean waitForWork() throws Exception, Error {
		if (sendToServer != null && receiveFromServer != null) {
			System.out.println("Envoie \"OK\"");
			sendToServer.println("OK");

			System.out.println("En attente de travail...");
			String action = receiveFromServer.readLine();

			if (action.equals("END")) {
				System.out
						.println("Ordre de fin de travail recu, fin du processus.");
				sendToServer.println("ACK END");
				return false;
				// receiveFromServer.close();
				// sendToServer.close();
			} else {
				System.out.println("Action recue = " + action);
				int posComma = action.indexOf(",");
				String nbFiles = action.substring(0, posComma);
				String operation = action.substring(posComma + 1,
						action.length());
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
					System.out.println("Reception fichier #" + (i + 1) + "/"
							+ nbFiles);
					String fileLine = receiveFromServer.readLine();
					posComma = fileLine.indexOf(",");
					boolean isExecutable = Boolean.parseBoolean(fileLine.substring(0, posComma));
					String subFileLine = fileLine.substring(posComma + 1, fileLine.length());
					posComma = subFileLine.indexOf(",");
					String fileSize = subFileLine.substring(0, posComma);
					String fileName = subFileLine.substring(posComma + 1,
							subFileLine.length());
					int intFileSize = 0;
					intFileSize = Integer.valueOf(fileSize);
					
					byte[] temp = new byte[intFileSize];

					System.out.println("\tReception en cours");
					sendToServer.println("OK3");
					InputStream is = socket.getInputStream();

					FileOutputStream fos = new FileOutputStream("/tmp/"+fileName);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					int bytesRead = is.read(temp, 0, temp.length);
					int current = bytesRead;
					do {
						bytesRead = is.read(temp, current,
								(temp.length - current));
						if (bytesRead >= 0)
							current += bytesRead;
					} while (current < intFileSize);
					bos.write(temp, 0, current);
					bos.flush();
					bos.close();
					fos.close();
					
					if (isExecutable) {
						File f = new File(fileName);
						f.setExecutable(true);
					}
					sendToServer.println("OK4");
					System.out.println("\tReception terminee, fichier recu.");
				}

//				Thread.sleep(500);

				// ex�cution de l'op�ration
				System.out.println("Execution commande");
				System.out.println("<<<<>>>>");
				Shell sh = new Shell();
				sh.setDirectory(new File("/tmp"));
				int retVal = sh.command(operation).error().consume();
				System.out.println(retVal);
				if (retVal != 0) {
					System.out.println("Erreur lors de l'exec commande");
					String targetNameError = receiveFromServer.readLine();
					sendToServer.println(-1 + ", ");
					throw new Error();
				}

				System.out.println("<<<<>>>>");
				// r�cup�ration nom cible
				String targetName = receiveFromServer.readLine();

				File myFile = new File("/tmp/" + targetName);
				byte[] fileAsByteArray = new byte[(int) myFile.length()];

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
				
				bis.close();
				fis.close();
				
				String ok2 = receiveFromServer.readLine();
				if (!ok2.equals("OK2"))
					throw new Exception("Not OK2");
				sendToServer.println(targetName);
				return true;
			}
		} else {
			System.out
					.println("Les flux d'entree-sortie sont corrompus, tentative de recuperation de la liaison avec le serveur...");
			return true;
		}
	}

	static private void leave() throws IOException {
		socket.close();
	}
}
