package fr.ensimag.make.distrib.core.mailbox;

import java.util.List;

import fr.ensimag.make.distrib.core.exception.WaitOneSecException;
import fr.ensimag.make.distrib.parser.Parser;
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

	public boolean isEmpty() {
		return listTasks.isEmpty();
	}

	synchronized public Rule retire() throws WaitOneSecException {
		if (listTasks.isEmpty() && Parser.isTasksToExec()) {
			// exception blocante dans le cas o� il reste des t�ches � accomplir
			// mais qu'aucune n'est encore faisable
			throw new WaitOneSecException();
		}

		if (!listTasks.isEmpty()) {
			return listTasks.remove(0);
		}
		return null;
	}

	public String toString() {
		return listTasks.toString();
	}

}
