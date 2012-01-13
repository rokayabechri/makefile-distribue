/*
 * Licensed Materials - Property of IBM
 *
 * OCO Source Materials
 *
 * (C) Copyright IBM Corp. 2010, 2011 All Rights Reserved
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package fr.ensimag.make.distrib.agent;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 */
public class ExecDataReader 
extends Thread {
	InputStream stream;
	String      stringRead;
	
	/**
	 * 
	 * @param stream
	 */
	public ExecDataReader(InputStream stream) {
		this.stream     = stream;
		this.stringRead = "";
	}
	
	/**
	 * 
	 */
	public void run() {
		int c;
		try {
			while ((c = this.stream.read()) != -1) {
				this.stringRead = this.stringRead + ((char)c);
			}
			this.stream.close();
		} catch (IOException ioe) {
		}
	}
	
	/**
	 * 
	 * @return String
	 */
	public String getStringRead() {
		return this.stringRead;
	}
	
	/**
	 * 
	 *
	 */
	public void clean() {
		try {
			this.stream.close();
		} catch (IOException ioe) {
		}
	}
}
