package fr.ensimag.make.distrib.agent;

import java.io.IOException;

import com.developpez.adiguba.shell.ProcessConsumer;
import com.developpez.adiguba.shell.Shell;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String operation = "gcc -o hello.o -c hello.c -W -Wall -ansi -pedantic";
		Shell sh = new Shell();
		try {
			ProcessConsumer result = sh.command(operation);
			System.out.println(result.error().consume());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
