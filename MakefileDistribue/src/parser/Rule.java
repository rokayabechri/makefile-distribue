package parser;

import java.util.ArrayList;
import java.util.List;

public class Rule {

	private String target = null;
	private String cmd = null;
	private List<String> dependencies = null;

	public Rule() {
		target = "n/a";
		cmd = "n/a";
		dependencies = new ArrayList<String>();
	}

	public Rule(String target) {
		this();
		this.target = target;
	}

	public Rule(String target, String cmd, List<String> dependencies) {
		this.target = target;
		this.cmd = cmd;
		this.dependencies = dependencies;
	}

	@Override
	public String toString() {
		return target + "\n";
		// return target + "\n" + cmd + "\n" + dependencies + "\n";
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public List<String> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}

}
