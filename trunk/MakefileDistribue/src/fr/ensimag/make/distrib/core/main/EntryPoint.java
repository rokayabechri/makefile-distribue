package fr.ensimag.make.distrib.core.main;

import fr.ensimag.make.distrib.core.comm.Register;
import fr.ensimag.make.distrib.parser.Parser;

public class EntryPoint {

	static private String aide = "Usage : java [nom appli] makefileName\n\tPr�cisez le nom du makefile à ex�cuter";
	private static Register register = new Register();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean next = true;
		if (args == null || args.length < 1) {
			System.out.println(aide);
			next = false;
		}
		if (next) {
			Parser.parse(args[0]);
			register.start();
		}
	}
}
