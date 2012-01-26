package fr.ensimag.make.distrib.agent;

import java.io.File;
import java.io.IOException;

import com.developpez.adiguba.shell.ProcessConsumer;
import com.developpez.adiguba.shell.Shell;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
		String operation = "touch LOOL";
		Shell sh = new Shell();
		sh.setDirectory(new File("/tmp"));
		try {
			ProcessConsumer result = sh.command(operation);
			System.out.println(result.consumeAsString());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
