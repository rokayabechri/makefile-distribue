package fr.ensimag.make.distrib.core.mailbox;

import java.util.List;

import fr.ensimag.make.distrib.parser.Rule;

public class BAL {
	private List<Rule> listTasks;
	private Semaphore semaphoreDepot, semaphoreRetrait;

	public BAL(int listTasksMaxSize, List<Rule> listTasks) {
		this.listTasks = listTasks;
		semaphoreDepot = new Semaphore(listTasksMaxSize, listTasksMaxSize);
		semaphoreRetrait = new Semaphore(0, listTasksMaxSize);
	}

	public void deposeP() {
		semaphoreDepot.P();
	}

	public void deposeV() {
		semaphoreDepot.V();
	}

	public void retireP() {
		semaphoreRetrait.P();
	}

	public void retireV() {
		semaphoreRetrait.V();
	}

	synchronized public void depose(Rule rule) {
		if (!listTasks.contains(rule)) {
			listTasks.add(rule);
		}
	}

	synchronized public Rule retire() {
		// pour le moment, on recupere le premier element de la liste a chaque
		// fois
		if (!listTasks.isEmpty()) {
			return listTasks.remove(0);
		}
		return null;
	}

	public String toString() {
		return listTasks.toString();
	}

}
