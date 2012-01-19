package fr.ensimag.make.distrib.parser;

import java.util.List;
import java.util.Map;

public class Tests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// // args[0]...
		// String makefileName = "makefile_simple";
		//
		// // on parse le makefile et recupere la liste des regles
		// List<Rule> listRules = MakefileParser.parse(makefileName);
		//
		// MakefileParser.cleanTargetAll(listRules);
		//
		// // on genere le mapping (target - regle)
		// Map<String, Rule> mapTargetRule =
		// Dependency.mapTargetRule(listRules);
		//
		// // on genere le mapping (nom de la dependance - [boolean] pret a
		// l'envoi)
		// Map<String, Boolean> mapDepRdy = Dependency.mapDepRdy(mapTargetRule,
		// listRules);
		//
		// // on prend en compte les dates de creation des cibles et des
		// dependances si celles-ci existent
		// Dependency.recentDep(listRules, mapDepRdy);
		//
		// // on genere le mapping (nom de la dependance - nom des cibles qui en
		// dependent)
		// Map<String, List<String>> mapDepTarget =
		// Dependency.mapDepTarget(listRules, mapDepRdy);
		//
		// // on recupere la list des tasks faisables
		// List<Rule> listTasks = Dependency.getListTasks(listRules, mapDepRdy);

		Parser.parse("makefile_simple");

		while (Parser.isTasksToExec()) {
			System.out.println("bal :" + Dependency.getBal());
			System.out.println("mapDepRdy :" + Dependency.getMapDepRdy());
			System.out.println("--------------------------------------------");
			Rule rule = Parser.getTask();
			Parser.taskDone(rule);
		}

		// System.out.println("bal :" + Dependency.getBal());
		// System.out.println("mapDepRdy :" + Dependency.getMapDepRdy());
		// System.out.println("mapTargetRule :" +
		// Dependency.getMapTargetRule());

	}
}
