package fr.ensimag.make.distrib.parser;

import java.util.List;
import java.util.Map;



public class Tests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// args[0]...
		String makefileName = "Makefile_blender";
		
		// on parse le makefile et recupere la liste des regles
		List<Rule> listRules = MakefileParser.parse(makefileName);
		
		// on genere le mapping (target - regle)
		Map<String, Rule> mapTargetRule = Dependency.mapTargetRule(listRules);
		
		// on genere le mapping (nom de la dependance - [boolean] pret a l'envoi)
		Map<String, Boolean> mapDepRdy = Dependency.mapDepRdy(mapTargetRule, listRules);
				
		// on prend en compte les dates de creation des cibles et des dependances si celles-ci existent
		Dependency.recentDep(listRules, mapDepRdy);
		
		// on genere le mapping (nom de la dependance - nom des cibles qui en dependent)
		Map<String, List<String>> mapDepTarget = Dependency.mapDepTarget(listRules, mapDepRdy);
		
		// on recupere la list des tasks faisables (ca vide la liste des rules au fur et a mesure)
		List<Rule> listTasks = Dependency.getListTasks(listRules, mapDepRdy);
		
		// on upgrade la listTasks, une fois qu'une task est finie
		Dependency.taskDone(listRules.get(5), mapDepRdy, mapDepTarget, listTasks, mapTargetRule);
		
		System.out.println(listTasks.toString());
	}
}
