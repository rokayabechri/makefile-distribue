package Parser;

import java.util.ArrayList;
import java.util.List;

public class Target {

	private String name = null;
	private String cmd = null;
	private List<String> dependencies = null;

	public Target() {
		name = "n/a";
		cmd = "n/a";
		dependencies = new ArrayList<String>();
	}

	@Override
	public String toString() {
		return name + "\n" + cmd + "\n" + dependencies + "\n";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
