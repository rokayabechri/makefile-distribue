package fr.ensimag.make.distrib.core.comm;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Agent {
	public Integer id;
	public String ip;
	public Integer port;
	public Boolean available;
	public Socket socket;
	public BufferedReader receiveFromAgent;
	public PrintWriter sendToAgent;
	
	
	
}
