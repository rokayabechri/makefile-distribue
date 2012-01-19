package fr.ensimag.make.distrib.parser;

public class Parser {

	public static void taskDone(Rule rule) {
		Dependency.taskDone(rule);
	}

	public static void parse(String makefileName) {
		Dependency.parse(makefileName);
	}

	public static Rule getTask() {
		return Dependency.getTask();
	}
	
	public static boolean isTasksToExec() {
		return Dependency.isTasksToExec();
	}

}
