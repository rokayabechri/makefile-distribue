package fr.ensimag.make.distrib.core.comm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;

import fr.ensimag.make.distrib.core.exception.WaitOneSecException;
import fr.ensimag.make.distrib.parser.Parser;
import fr.ensimag.make.distrib.parser.Rule;

public class AgentListener extends Thread implements Runnable {

	private Agent agent;
	private Rule rule;
	private boolean deadAgent = false;

	public AgentListener(Agent agent) {
		this.agent = agent;
	}

	public void run() {
		String agentListenerPrefix = "#" + agent.id;

		boolean allGood = false;
		while (true) {
			if (!deadAgent) {
				try {
					while (true) {
						try {
							this.rule = Parser.getTask();
							break;
						} catch (WaitOneSecException wose) {
							// WAIT
							try {
								Thread.sleep(1000);
							} catch (InterruptedException ie) {
								// nothing to do :
								// if Interrupted, AgentListener goes checking
								// again as required
							}
						}
					}

					if (this.rule == null) {
						String ok = agent.receiveFromAgent.readLine();
						System.out.println("Envoi operation END");
						agent.sendToAgent.println("END");
						String ackEnd = agent.receiveFromAgent.readLine();
						System.out.println("AckEND = " + ackEnd);
						break;
					}

					System.out.println(agentListenerPrefix + " START target="
							+ rule.getTarget() + " | cmd=" + rule.getCmd());

					allGood = false;
					String ok = agent.receiveFromAgent.readLine();
					if (!"OK".equals(ok)) {
						deadAgent = true;
						throw new Exception("fail1");
					}

					int nbFiles = this.rule.getDependencies().size();
					List<String> files = this.rule.getDependencies();
					String cmd = this.rule.getCmd();
					String targetName = this.rule.getTarget();

					agent.sendToAgent.println(nbFiles + "," + cmd);

					String ok2 = agent.receiveFromAgent.readLine();
					if (!ok2.equals("OK2"))
						throw new Exception("fail2");

					for (int i = 0; i < nbFiles; i++) {
						File myFile = new File("./" + files.get(i));
						byte[] fileAsByteArray = new byte[(int) myFile.length()];

						agent.sendToAgent.println(myFile.canExecute() + "," + myFile.length() + ","
								+ myFile.getName());
						String ok3 = agent.receiveFromAgent.readLine();

						if (!ok3.equals("OK3"))
							throw new Exception("fail3");

						FileInputStream fis = new FileInputStream(myFile);
						BufferedInputStream bis = new BufferedInputStream(fis);
						bis.read(fileAsByteArray, 0, fileAsByteArray.length);
						OutputStream os = agent.socket.getOutputStream();
						os.write(fileAsByteArray, 0, fileAsByteArray.length);
						os.flush();
						fis.close();
						
						String ok4 = agent.receiveFromAgent.readLine();
						if (!ok4.equals("OK4"))
							throw new Exception("fail4");
					}

					// les fichiers sont envoy�s, on attend le r�sultat
					// d�sormais
					agent.sendToAgent.println(targetName);
					String fileLine = agent.receiveFromAgent.readLine();
					int posComma = fileLine.indexOf(",");
					String fileSize = fileLine.substring(0, posComma);
					String fileName = fileLine.substring(posComma + 1,
							fileLine.length());
					int intFileSize = 0;
					try {
						intFileSize = Integer.valueOf(fileSize);
					} catch (NumberFormatException e) {
						throw e;
					}

					if (intFileSize == -1) {
						throw new Exception();
					}

					byte[] temp = new byte[intFileSize];

					agent.sendToAgent.println("OK1");
					InputStream is = agent.socket.getInputStream();
					FileOutputStream fos = new FileOutputStream(fileName);
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
					
					Thread.sleep(100);
					agent.sendToAgent.println("OK2");
					String end = agent.receiveFromAgent.readLine();
					if (end == null) {
						throw new Exception();
					}
					System.out.println(">>>> " + agentListenerPrefix
							+ " DONE target=" + end);

					Parser.taskDone(this.rule);

					allGood = true;

				} catch (Exception e) {
					e.printStackTrace();
					if (e instanceof SocketException
							|| e instanceof IOException) {
						this.deadAgent = true;
					}
					// Traitement g�n�rique
					if (!allGood) {
						// Il faut remettre la rule dans la boite aux lettres
						Parser.failedTarget(this.rule);
					}
				}
			} else {
				break;
			}
		}

		System.out.println("### Agent" + agentListenerPrefix + " termin�");
	}

}
