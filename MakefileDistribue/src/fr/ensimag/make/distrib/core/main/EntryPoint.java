package fr.ensimag.make.distrib.core.main;

import fr.ensimag.make.distrib.core.comm.Register;

public class EntryPoint {

	private static Register register = new Register();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		register.start();
	}

}
