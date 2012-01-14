package fr.ensimag.make.distrib.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Dependency {

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
				// listRules.remove(rule);
				listTasks.add(rule);
			}
		}
		return listTasks;
	}

	public static void taskDone(Rule rule, Map<String, Boolean> mapDepRdy,
			Map<String, List<String>> mapDepTarget, List<Rule> listTasks,
			Map<String, Rule> mapTargetRule) {
		// 1 - soit on previent les peres de la rule finie et ils viendront peut
		// etre s'ajouter a la listTasks
		// 2 - soit on re-get une nouvelle listTasks
		mapDepRdy.put(rule.getTarget(), true);
		// 1
		for (int i = 0; i < mapDepTarget.get(rule.getTarget()).size(); i++) {
			Rule currentRule = mapTargetRule.get(mapDepTarget.get(
					rule.getTarget()).get(i));
			boolean task = true;
			for (int j = 0; j < currentRule.getDependencies().size(); j++) {
				String dep = currentRule.getDependencies().get(j);
				task = task && mapDepRdy.get(dep);
			}
			if (task) {
				listTasks.add(rule);
			}
		}
	}
}