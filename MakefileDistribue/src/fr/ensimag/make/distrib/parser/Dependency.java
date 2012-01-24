package fr.ensimag.make.distrib.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ensimag.make.distrib.core.exception.WaitOneSecException;
import fr.ensimag.make.distrib.core.mailbox.BAL;

public class Dependency {

	private static Map<String, Boolean> mapDepRdy;
	private static Map<String, List<String>> mapDepTarget;
	private static Map<String, Rule> mapTargetRule;
	private static BAL bal;

	public static Map<String, Boolean> getMapDepRdy() {
		return mapDepRdy;
	}

	public static void setMapDepRdy(Map<String, Boolean> mapDepRdy) {
		Dependency.mapDepRdy = mapDepRdy;
	}

	public static Map<String, List<String>> getMapDepTarget() {
		return mapDepTarget;
	}

	public static void setMapDepTarget(Map<String, List<String>> mapDepTarget) {
		Dependency.mapDepTarget = mapDepTarget;
	}

	public static Map<String, Rule> getMapTargetRule() {
		return mapTargetRule;
	}

	public static void setMapTargetRule(Map<String, Rule> mapTargetRule) {
		Dependency.mapTargetRule = mapTargetRule;
	}

	public static BAL getBal() {
		return bal;
	}

	public static void setBal(BAL bal) {
		Dependency.bal = bal;
	}

	public static Map<String, Rule> mapTargetRule(List<Rule> listRules) {
		Map<String, Rule> mapTargetRule = new HashMap<String, Rule>();
		for (int i = 0; i < listRules.size(); i++) {
			Rule rule = listRules.get(i);
			mapTargetRule.put(rule.getTarget(), rule);
		}
		return mapTargetRule;
	}

	public static Map<String, Boolean> mapDepRdy(
			Map<String, Rule> mapTargetRule, List<Rule> listRules) {
		Map<String, Boolean> mapDepRdy = new HashMap<String, Boolean>();
		boolean rdy = false;
		Rule rule = null;
		String dep = null;

		for (int i = 0; i < listRules.size(); i++) {
			rule = listRules.get(i);
			for (int j = 0; j < rule.getDependencies().size(); j++) {
				dep = rule.getDependencies().get(j);
				if (mapTargetRule.containsKey(dep)) {
					rdy = false;
				} else {
					rdy = true;
				}
				mapDepRdy.put(dep, rdy);
			}
		}

		Set<String> setTarget = mapTargetRule.keySet();
		for (String target : setTarget) {
			if (!mapDepRdy.containsKey(target)) {
				mapDepRdy.put(target, false);
			}
		}

		return mapDepRdy;
	}

	public static void recentDep(List<Rule> listRules,
			Map<String, Boolean> mapDepRdy) {
		for (int i = 0; i < listRules.size(); i++) {
			Rule rule = listRules.get(i);
			for (int j = 0; j < rule.getDependencies().size(); j++) {
				String depName = rule.getDependencies().get(j);
				File fileDep = new File(depName);
				if (fileDep.exists()) {
					File fileTarget = new File(rule.getTarget());
					if (fileDep.lastModified() > fileTarget.lastModified()) {
						break;
					}
				} else {
					break;
				}
				mapDepRdy.put(rule.getTarget(), true);
			}
		}

	}

	public static Map<String, List<String>> mapDepTarget(List<Rule> listRules,
			Map<String, Boolean> mapDepRdy) {
		Map<String, List<String>> mapDepTarget = new HashMap<String, List<String>>();

		Iterator<String> ite = mapDepRdy.keySet().iterator();
		while (ite.hasNext()) {
			String dep = ite.next();
			List<String> listTargets = new ArrayList<String>();
			for (int i = 0; i < listRules.size(); i++) {
				Rule rule = listRules.get(i);
				if (rule.getDependencies().contains(dep)) {
					listTargets.add(rule.getTarget());
				}
			}
			mapDepTarget.put(dep, listTargets);
		}

		return mapDepTarget;
	}

	public static List<Rule> getListTasks(List<Rule> listRules,
			Map<String, Boolean> mapDepRdy) {
		List<Rule> listTasks = new ArrayList<Rule>();
		for (int i = 0; i < listRules.size(); i++) {
			Rule rule = listRules.get(i);
			boolean task = true;
			for (int j = 0; j < rule.getDependencies().size(); j++) {
				String dep = rule.getDependencies().get(j);
				task = task && mapDepRdy.get(dep);
			}
			if (task) {
				listTasks.add(rule);
			}
		}
		for (int i = 0; i < listTasks.size(); i++) {
			if (mapDepRdy.get(listTasks.get(i).getTarget())) {
				listTasks.remove(i);
				i--;
			}
		}
		return listTasks;
	}

	public synchronized static void taskDone(Rule rule) {
		// on previent les peres de la rule finie et ils viendront peut
		// etre s'ajouter a la bal
		mapDepRdy.put(rule.getTarget(), true);
		for (int i = 0; i < mapDepTarget.get(rule.getTarget()).size(); i++) {
			Rule currentRule = mapTargetRule.get(mapDepTarget.get(
					rule.getTarget()).get(i));
			boolean task = true;
			for (int j = 0; j < currentRule.getDependencies().size(); j++) {
				String dep = currentRule.getDependencies().get(j);
				task = task && mapDepRdy.get(dep);
			}
			if (task) {
				bal.depose(currentRule);
			}
		}
	}

	public static void parse(String makefileName) {
		// on parse le makefile et recupere la liste des regles
		List<Rule> listRules = MakefileParser.parse(makefileName);

		// on supprime la cible clean qui n'est pas geree
		MakefileParser.cleanTargetAllAndClean(listRules);

		// on genere le mapping (target - regle)
		mapTargetRule = Dependency.mapTargetRule(listRules);

		// on genere le mapping (nom de la dependance - [boolean] pret a
		// l'envoi)
		mapDepRdy = Dependency.mapDepRdy(mapTargetRule, listRules);

		// on prend en compte les dates de creation des cibles et des
		// dependances si celles-ci existent
		Dependency.recentDep(listRules, mapDepRdy);

		// on genere le mapping (nom de la dependance - nom des cibles qui en
		// dependent)
		mapDepTarget = Dependency.mapDepTarget(listRules, mapDepRdy);

		List<Rule> listTasks = Dependency.getListTasks(listRules, mapDepRdy);

		bal = new BAL(listRules.size(), listTasks);
	}

	public synchronized static Rule getTask() throws WaitOneSecException {
		Rule rule = bal.retire();
		return rule;
	}

	public static boolean isTasksToExec() {
		Set<String> depSet = mapDepRdy.keySet();
		for (String dep : depSet) {
			if (!mapDepRdy.get(dep)) {
				return true;
			}
		}
		return false;
	}

	public synchronized static void failedTarget(Rule rule) {
		bal.depose(rule);
	}

}