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


/**
 * This class is used to report to the server side, 
 *  the cmd execution performed on the agent side.
 * <p>
 *  This object implements the JavaBean design pattern.
 *    
 */

public class ExecData {

	
	/**
	 * EXECDATA type PRE
	 */
	static public String EXECDATA_TYPE_PRECOMMAND   = "PRE";
	/**
	 * EXECDATA type PANEL
	 */
	static public String EXECDATA_TYPE_PANELCOMMAND = "PANEL";
	/**
	 * EXECDATA type UNDO
	 */
	static public String EXECDATA_TYPE_UNDOCOMMAND  = "UNDO";
	/**
	 * 
	 */
	static public String[] EXECDATA_TYPES = {
		EXECDATA_TYPE_PRECOMMAND, 
		EXECDATA_TYPE_PANELCOMMAND, 
		EXECDATA_TYPE_UNDOCOMMAND
	};
	

	/*
	 * command executed
	 */
	private String command = "";
	/*
	 * standard output
	 */
	private String stdout = "";
	/* 
	 * standard error
	 */
	private String stderr = "";
	/*
	 * exit value of the command
	 */
	private int    exitValue = 0;
	/*
	 * exceptions thrown if any during execution
	 */
	private String[] exceptions;
	//
	static public String SUP                  = ">";
	static public String GREATERTHAN          = " &gt; ";
	static public String INF                  = "<";
	static public String LESSTHAN             = " &lt; ";
	static public String SLASH                = "/";
	//
	static public String EXEC                 = "EXEC";
	static public String EXEC_START_TAG       = INF+EXEC+SUP;
	static public String EXEC_STOP_TAG        = INF+SLASH+EXEC+SUP;
	//
	static public String EXECDATA             = "EXECDATA";
	static public String EXECDATA_START_TAG   = INF+EXECDATA+SUP;
	static public String EXECDATA_START_PRE_TAG   = INF+EXECDATA+" type=\""+ExecData.EXECDATA_TYPE_PRECOMMAND+"\" "+SUP;
	static public String EXECDATA_START_PANEL_TAG = INF+EXECDATA+" type=\""+ExecData.EXECDATA_TYPE_PANELCOMMAND+"\" "+SUP;
	static public String EXECDATA_START_UNDO_TAG  = INF+EXECDATA+" type=\""+ExecData.EXECDATA_TYPE_UNDOCOMMAND+"\" "+SUP;
	static public String EXECDATA_STOP_TAG    = INF+SLASH+EXECDATA+SUP;
	//
	static public String EXECDATA_COMMAND     = "EXECDATA_COMMAND";
	static public String COMMAND_START_TAG    = INF+EXECDATA_COMMAND+SUP;
	static public String COMMAND_STOP_TAG     = INF+SLASH+EXECDATA_COMMAND+SUP;
	//
	static public String EXECDATA_STDOUT      = "EXECDATA_STDOUT";
	static public String STDOUT_START_TAG     = INF+EXECDATA_STDOUT+SUP;
	static public String STDOUT_STOP_TAG      = INF+SLASH+EXECDATA_STDOUT+SUP;
	//
	static public String EXECDATA_STDERR      = "EXECDATA_STDERR";
	static public String STDERR_START_TAG     = INF+EXECDATA_STDERR+SUP;
	static public String STDERR_STOP_TAG      = INF+SLASH+EXECDATA_STDERR+SUP;
	//
	static public String EXECDATA_EXITVALUE   = "EXECDATA_EXITVALUE";
	static public String EXITVALUE_START_TAG  = INF+EXECDATA_EXITVALUE+SUP;
	static public String EXITVALUE_STOP_TAG   = INF+SLASH+EXECDATA_EXITVALUE+SUP;
	//
	static public String EXECDATA_EXCEPTIONS  = "EXECDATA_EXCEPTIONS";
	static public String EXCEPTIONS_START_TAG = INF+EXECDATA_EXCEPTIONS+SUP;
	static public String EXCEPTIONS_STOP_TAG  = INF+SLASH+EXECDATA_EXCEPTIONS+SUP;
	//
	static public String EXECDATA_EXCEPTION   = "EXECDATA_EXCEPTION";
	static public String EXCEPTION_START_TAG  = INF+EXECDATA_EXCEPTION+SUP;
	static public String EXCEPTION_STOP_TAG   = INF+SLASH+EXECDATA_EXCEPTION+SUP;
	
	/**
	 * Constructor
	 * <p>
	 */
	public ExecData() {
		super();
	}


	/**
	 * Constructor
	 * <p>
	 * @param command
	 * @param stdout
	 * @param stderr
	 * @param exitValue
	 * @param exceptions
	 */
	public ExecData(String command, 
			String stdout, 
			String stderr, 
			int exitValue, 
			String[] exceptions) {
		this();
		this.command    = command;
		this.stdout     = stdout;
		this.stderr     = stderr;
		this.exitValue  = exitValue;
		this.exceptions = exceptions;
	}

	/**
	 * 
	 * <p>
	 * @return String 
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * 
	 * <p>
	 * @param bXML boolean if xml format is wanted
	 * @return String 
	 */
	public String getCommand(boolean bXML) {
		String returned = "";
		if (!bXML) returned = this.getCommand();
		else { 
			returned = returned + COMMAND_START_TAG + "\n";
			returned = returned + this.getCommand();
			returned = returned + "\n" + COMMAND_STOP_TAG;
		}
		return returned;
	}

	/**
	 * 
	 * <p>
	 * @param command
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	/**
	 * 
	 * <p>
	 * @return String 
	 */
	public String getStdout() {
		return stdout;
	}
	/**
	 * 
	 * <p>
	 * @param bXML boolean if xml format is wanted
	 * @return String 
	 */
	public String getStdout(boolean bXML) {
		String returned = "";
		if (!bXML) returned = this.getStdout();
		else { 
			returned = returned + STDOUT_START_TAG + "\n";
			String stdout = ExecData.protectXML(this.getStdout());
			returned = returned + stdout;
			returned = returned  + "\n" + STDOUT_STOP_TAG;
		}
		return returned;
	}
	/**
	 * 
	 * <p>
	 * @param stdout
	 */
	public void setStdout(String stdout) {
		this.stdout = stdout.replaceAll("[^\\p{Print}]\\n\\t", " ");
	}
	/**
	 * 
	 * <p>
	 * @return String 
	 */
	public String getStderr() {
		return stderr;
	}
	/**
	 * 
	 * <p>
	 * @param bXML boolean if xml format is wanted
	 * @return String 
	 */
	public String getStderr(boolean bXML) {
		String returned = "";
		if (!bXML) returned = this.getStderr();
		else { 
			returned = returned + STDERR_START_TAG + "\n";
			String stderr = ExecData.protectXML(this.getStderr());
			returned = returned + stderr;
			returned = returned  + "\n" + STDERR_STOP_TAG;
		}
		return returned;
	}
	/**
	 * 
	 * <p>
	 * @param stderr
	 */
	public void setStderr(String stderr) {
		this.stderr = stderr.replaceAll("[^\\p{Print}]\\n\\t", " ");
	}
	/**
	 * 
	 * <p>
	 * @return int
	 */
	public int getExitValue() {
		return exitValue;
	}
	/**
	 * 
	 * <p>
	 * @param bXML boolean if xml format is wanted
	 * @return int
	 */
	public String getExitValue(boolean bXML) {
		String returned = "";
		if (!bXML) returned = Integer.toString(this.getExitValue());
		else { 
			returned = returned + EXITVALUE_START_TAG + "\n";
			returned = returned + this.getExitValue();
			returned = returned  + "\n" + EXITVALUE_STOP_TAG;
		}
		return returned;
	}
	/**
	 * 
	 * <p>
	 * @param exitValue
	 */
	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}
	/**
	 * 
	 * <p>
	 * @return String 
	 */
	public String[] getExceptions() {
		return exceptions;
	}
	/**
	 * 
	 * <p>
	 * @param bXML boolean if xml format is wanted
	 * @return String 
	 */
	public String getExceptions(boolean bXML) {
		String returned = "";
		if (!bXML) {
			if (exceptions!=null) {
				for (String exception : exceptions) {
					returned = returned  + "\n" + exception;
				}
			}
		}
		else { 
			returned = returned + EXCEPTIONS_START_TAG + "\n";
			if (exceptions!=null) {
				for (String exception : exceptions) {
					returned = returned + EXCEPTION_START_TAG + "\n";
					returned = returned + ExecData.protectXML(exception);
					returned = returned  + "\n" + EXCEPTION_STOP_TAG;
				}
			}
			returned = returned  + "\n" + EXCEPTIONS_STOP_TAG;
		}
		return returned;

	}
	/**
	 * 
	 * <p>
	 * @param exceptions
	 */
	public void setExceptions(String[] exceptions) {
		this.exceptions = exceptions;
	}
	/**
	 * 
	 * <p>
	 * @param exception
	 */
	public void addException(String exception) {
		int nb = 0;
		if (this.exceptions!=null) {
			nb = exceptions.length;
		}
		String[] newExceptions = new String[nb+1];
		for (int i=0; i<nb; i++) {
			newExceptions[i] = this.exceptions[i];
		}

		// Before adding exception, remove unicode character which can not go through the line
		char c = 0;
		exception = exception.replace(c, ' ');
		/*
		for (int i=0; i<exception.length(); i++) {
			char c = exception.charAt(i);
			int u  = exception.charAt(i);
			PowerHACommonPlugin.getLogger().log(Level.FINEST, "exception[i]="+c+ " unicode="+u);
			if (u==0) {
				exception = exception.replace(c, ' ');
			}
		}
		 */

		newExceptions[nb] = exception;
		this.exceptions = newExceptions; 
	}

	/**
	 * <p>
	 */
	public String toString() {
		String returned = "\nExecData:\n";
		returned = returned + " command="+this.command + "\n";
		returned = returned + " stdout=\n"+this.stdout + "\n";
		returned = returned + " stderr=\n"+this.stderr + "\n";
		returned = returned + " exitvalue="+this.exitValue + "\n";
		if (exceptions!=null) {
			for (int i=0; i<exceptions.length; i++) {
				returned = returned + " exception["+i+"]="+this.exceptions[i] + "\n";
			}
		}
		return returned;
	}

	/**
	 * @param bXML boolean if xml format is wanted
	 * @param type String to type the EXECDATA, either 
	 *  <p> ExecData.EXECDATA_TYPE_PRECOMMAND
	 *  <p> ExecData.EXECDATA_TYPE_PANELCOMMAND
	 *  <p> ExecData.EXECDATA_TYPE_UNDOCOMMAND 
	 * <p>
	 */
	public String toString(boolean bXML, String type) {
		String returned = "";
		if (!bXML) returned = this.toString();
		else {
			returned = returned + "\n" + INF+EXECDATA+" type=\""+type+"\" "+ SUP + "\n";
			returned = returned + "\n" + this.getCommand(true);
			if (type.equals(ExecData.EXECDATA_TYPE_PANELCOMMAND)) {
				returned = returned + "\n" + this.getStdout();
			} else {
				returned = returned + "\n" + this.getStdout(true);
			}
			returned = returned + "\n" + this.getStderr(true);
			returned = returned + "\n" + this.getExitValue(true);
			returned = returned + "\n" + this.getExceptions(true);
			returned = returned + "\n" + EXECDATA_STOP_TAG + "\n";
		}
		return returned;
	}
	
	
	/**
	 * Protect xml String from '<' and '>' by replacing them with ' &lt; ' and ' &gt; '  Otherwise the XDParser gets lost. 
	 * <p>
	 * @param xml String 
	 * @return xml 
	 */
	static public String protectXML(String xml) {
		xml = xml.replaceAll(ExecData.INF, ExecData.LESSTHAN);
		xml = xml.replaceAll(ExecData.SUP, ExecData.GREATERTHAN);
		return xml;
	}
	/**
	 * Restore xml String from ' &lt; 'and ' &gt; ' by replacing them with '<' and '>'. 
	 * <p>
	 * @param xml String 
	 * @return xml 
	 */
	static public String restoreXML(String xml) {
		xml = xml.replaceAll(ExecData.LESSTHAN, ExecData.INF);
		xml = xml.replaceAll(ExecData.GREATERTHAN, ExecData.SUP);
		return xml;
	}
	
}
