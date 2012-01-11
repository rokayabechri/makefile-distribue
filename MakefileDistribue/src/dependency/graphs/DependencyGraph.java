package dependency.graphs;

import java.util.ArrayList;
import java.util.List;
import parser.Rule;

public class DependencyGraph {

	private List<Rule> listRules;
	private List<String> listTargets;
	private Tree<Rule> dg;

	public List<Rule> getListRules() {
		return listRules;
	}

	public void setListRules(List<Rule> listRules) {
		this.listRules = listRules;
	}

	public List<String> getListTargets() {
		return listTargets;
	}

	public void setListTargets(List<String> listTargets) {
		this.listTargets = listTargets;
	}

	public DependencyGraph(List<Rule> listRules) {
		this.listRules = listRules;
		this.listTargets = new ArrayList<String>();
		for (int i = 0; i < listRules.size(); i++) {
			listTargets.add(listRules.get(i).getTarget());
		}
		this.dg = new Tree<Rule>(new Rule("root"));
	}

	public Tree<Rule> generate() {

		generateTree(listRules.get(0));

		return dg;
	}

	private Rule getRule(String depName) {
		Rule rule;
		for (int i = 0; i < listRules.size(); i++) {
			rule = listRules.get(i);
			if (rule.getTarget().equals(depName)) {
				return new Rule(depName, rule.getCmd(), rule.getDependencies());
			}
		}
		return null;
	}

	private boolean isTarget(String depName) {
		if (this.getListTargets().contains(depName)) {
			return true;
		}
		return false;
	}

	private void generateTree(Rule rule) {
		if (rule.getDependencies().isEmpty()) {
			dg.addLeaf(rule);
		}
		if (!haveTargets(rule)) {
			dg.addLeaf(rule);
		}
		for (int i = 0; i < rule.getDependencies().size(); i++) {
			String depName = rule.getDependencies().get(i);
			if (this.isTarget(depName)) {
				generateTree(this.getRule(depName));
			}
		}
	}

	private boolean haveTargets(Rule rule) {
		for (int i = 0; i < rule.getDependencies().size(); i++) {
			String depName = rule.getDependencies().get(i);
			if (this.isTarget(depName)) {
				return true;
			}
		}
		return false;
	}
}