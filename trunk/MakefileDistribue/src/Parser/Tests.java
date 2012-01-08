package Parser;

import java.util.List;

public class Tests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// args[0]...
		String makefileName = "Makefile_matrix_2";
		List<Target> listTarget = MakefileParser.parse(makefileName);
		for (int i = 0; i < listTarget.size(); i++) {
			System.out.println(listTarget.get(i).toString());
		}

	}
}
