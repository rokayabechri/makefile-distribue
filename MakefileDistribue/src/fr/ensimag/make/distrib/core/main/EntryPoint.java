package fr.ensimag.make.distrib.core.main;

import fr.ensimag.make.distrib.core.comm.Register;
import fr.ensimag.make.distrib.parser.Parser;

public class EntryPoint {

	static private String aide = "Usage : java [nom appli] makefileName\n\tPrecisez le nom du makefile à executer";
	private static Register register = new Register();

	static private Boolean bStart = false;
	static private Boolean bEnd = false;
	static private Long start;
	static private Long end;
	static private String totalTime;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args == null || args.length < 1) {
			System.out.println(aide);
		} else {
			System.out
					.println("Demarrage de MakefileDistribue, makefile traite : "
							+ args[0]);
			Parser.parse(args[0]);
			register.start();
		}
	}

	public static void begin() {
		if (!bStart) {
			start = System.currentTimeMillis();
			bStart = true;
		}
	}

	public static void end() {
		if (!bEnd) {
			end = System.currentTimeMillis();
			bEnd = true;
		}
		if (end != null && start != null) {
			Long totalTimeInSeconds = (end - start) / 1000;
			Long totalMillisecTime = (end - start) % 1000;
			String milliSec = "000";

			if (totalMillisecTime < 10) {
				milliSec = "00" + totalMillisecTime.toString();
			} else if (totalMillisecTime < 100) {
				milliSec = "0" + totalMillisecTime.toString();
			} else {
				milliSec = totalMillisecTime.toString();
			}

			totalTime = totalTimeInSeconds.toString() + "." + milliSec;
		} else {
			bStart = true;
			bEnd = true;
			totalTime = "0.000";
		}

	}

	public static void printTotalTime() {
		if (bStart && bEnd) {
			System.out.println("TOTAL TIME");
			System.out.println(totalTime + " seconds");
		} else {
			System.out.println("TOTAL TIME");
			System.out.println("Global target is not over");
		}
	}
}
