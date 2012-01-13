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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Locale;




public class ExecCommand 
extends Thread {

	/**
	 *  Return code when command succeeds 
	 */
	static public final int RC_SUCCESS            = 0;
	/**
	 *  Return code when command fails, general case 
	 */
	static public final int RC_ERROR              = 1;
	/**
	 *  Return code when command fails, something is not found  
	 */
	static public final int RC_NOT_FOUND          = 2;
	/**
	 *  Return code when command fails, something is missing in input   
	 */
	static public final int RC_MISSING_INPUT      = 3;
	/**
	 *  Return code when command fails, something is incorrect in input   
	 */
	static public final int RC_INCORRECT_INPUT    = 4;
	/**
	 *  Return code when command fails, missing dependency   
	 */
	static public final int RC_MISSING_DEPENDENCY = 5;
	/**
 	 *  Return code when command fails, a search has failed    
	 */
	static public final int RC_SEARCH_FAILED      = 6;
	/**
	 *  Return code when command fails, something is not found  
	 */
	static public final int NON_EXISTING_OBJECT = RC_NOT_FOUND;

	
	static private final boolean bDebug   = false;  
	static private final String  CLASS    = ExecCommand.class.getName();   


	private String         command;
	private String[]       commandArray;
	private Process        process;
	private ExecDataReader stdoutReader;
	private ExecDataReader stderrReader;
	private int            exitValue; 

	static private final int TIMEOUT = 600000; //TODO 10 minutes; should  be user configurable

	/**
	 * Ctor
	 * <p>
	 * @param command
	 * 
	 * @throws IOException
	 */
	public ExecCommand(String command) 
	throws IOException {
		final String METHOD = "ExecCommand";
		this.command = command;

		/**
		 * 
		 */
		try {
			this.process = Runtime.getRuntime().exec(this.command);
		} catch (IOException ioe) {
			throw ioe;
		}


		/**
		 * 
		 */
		if (this.process!=null) {
			//
			this.stdoutReader = new ExecDataReader(this.process.getInputStream());  
			this.stdoutReader.start();
			//
			this.stderrReader = new ExecDataReader(this.process.getErrorStream());  
			this.stderrReader.start();
		}
	}

	/**
	 * Ctor
	 * <p>
	 * @param commandArray
	 * 
	 * @throws IOException
	 */
	public ExecCommand(String[] commandArray) 
	throws IOException {
		final String METHOD = "ExecCommand";

		this.commandArray = commandArray;

		/**
		 * 
		 */
		try {
			this.process = Runtime.getRuntime().exec(this.commandArray);
		} catch (IOException ioe) {
			throw ioe;
		}


		/**
		 * 
		 */
		if (this.process!=null) {
			//
			this.stdoutReader = new ExecDataReader(this.process.getInputStream());  
			this.stdoutReader.start();
			//
			this.stderrReader = new ExecDataReader(this.process.getErrorStream());  
			this.stderrReader.start();
		}
	}


	/**
	 * 
	 */
	public void run() {
		final String METHOD = "run";
		try {
			this.process.waitFor();
		} catch (InterruptedException ie) {
		}
	}

	/**
	 * @return String
	 */
	public String getStdout() {
		return this.stdoutReader.getStringRead(); 
	}

	/**
	 * @return String
	 */
	public String getStderr() {
		return this.stderrReader.getStringRead(); 
	}

	/**
	 * 
	 * @return ExecDataReader
	 */
	public ExecDataReader getStderrReader() {
		return stderrReader;
	}

	/**
	 * 
	 * @return ExecDataReader
	 */
	public ExecDataReader getStdoutReader() {
		return stdoutReader;
	}

	/**
	 * 
	 * @return int
	 */
	public int getExitValue() {
		this.exitValue = this.process.exitValue();
		return this.exitValue;
	}

	/**
	 *
	 */
	public void clean() {
		final String METHOD = "clean";
		//
		//this.stdoutReader.clean();
		this.stdoutReader = null;
		//	
		//this.stderrReader.clean();
		this.stderrReader = null;
		//
		//this.process.destroy();  
		this.process      = null;
	}


	/**
	 * To execute a command
	 * <p>
	 * @param command
	 * @return ExecData
	 */
	static public ExecData execute(String command) {
		return ExecCommand.execute(command, ExecCommand.TIMEOUT);
	}


	/**
	 * To execute a command
	 * <p>
	 * @param command
	 * @param timeout
	 * @return ExecData
	 */
	static public ExecData execute(String command, int timeout) {
		final String METHOD = "execute";

		ExecData execData = new ExecData();
		// Executes command
		if ((command==null)            || 
				(command.trim().equals(""))) { 
			command = "";
		}
		ExecCommand execCommand = null;

		long start = System.currentTimeMillis();

		try {
			execCommand = new ExecCommand(command);
			execData.setCommand(command);
			execCommand.start();
		} catch (IOException ioe) {
			execData.addException(ioe.toString());
		}

		if (execCommand!=null) {
			//
			try {
				execCommand.join(timeout);
				if (execCommand.isAlive()) {
					Exception TimeOutException = new Exception(CLASS+"."+METHOD+"(String "+command+") timeout!");
					execData.addException(TimeOutException.toString());
				}
			} catch (InterruptedException ie) {
				execData.addException(ie.toString());
			}

			//
			try {
				execCommand.getStdoutReader().join(timeout);
				if (execCommand.getStdoutReader().isAlive()) {
					Exception TimeOutException = new Exception(CLASS+"."+METHOD+"(String "+command+") timeout "+timeout+" stdoutReader!");
					execData.addException(TimeOutException.toString());
				} else {
					execData.setStdout(execCommand.getStdout());
				}

				

			} catch (InterruptedException ie) {
				execData.addException(ie.toString());
			}

			//
			try {
				execCommand.getStderrReader().join(timeout);
				if (execCommand.getStderrReader().isAlive()) {
					Exception TimeOutException = new Exception(CLASS+"."+METHOD+"(String "+command+") ExecCommand.TIMEOUT "+timeout+" stderrReader!");
					execData.addException(TimeOutException.toString());
				} else {
					execData.setStderr(execCommand.getStderr());
				}
			} catch (InterruptedException ie) {
				execData.addException(ie.toString());
			}

			//
			try {
				execData.setExitValue(execCommand.getExitValue());
			} catch (IllegalThreadStateException itse) {
				execData.setExitValue(-1);
			} finally {
				execCommand.clean();
			}
		} else {
			execData.setExitValue(-1);// not found
		}
		return execData;
	}
}
