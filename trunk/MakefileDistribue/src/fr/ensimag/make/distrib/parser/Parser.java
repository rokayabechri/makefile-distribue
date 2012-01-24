package fr.ensimag.make.distrib.parser;

import fr.ensimag.make.distrib.core.exception.WaitOneSecException;

public class Parser {

	public static void taskDone(Rule rule) {
		Dependency.taskDone(rule);
	}

	public static void parse(String makefileName) {
		Dependency.parse(makefileName);
	}

	public static Rule getTask() throws WaitOneSecException {
		return Dependency.getTask();
	}
	
	public static boolean isTasksToExec() {
		return Dependency.isTasksToExec();
	}

	public static void failedTarget(Rule rule) {
		Dependency.failedTarget(rule);
	}

}
