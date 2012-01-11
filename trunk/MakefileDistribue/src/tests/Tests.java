package tests;

import java.util.List;

import dependency.graphs.DependencyGraph;
import dependency.graphs.Tree;
import parser.MakefileParser;
import parser.Rule;

public class Tests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// args[0]...
		String makefileName = "Makefile_matrix";

		// on parse le makefile et recupere la liste des regles
		List<Rule> listRules = MakefileParser.parse(makefileName);
		
		// on genere le graphe de dependances
		DependencyGraph depGraph = new DependencyGraph(listRules);
		Tree<Rule> dg = depGraph.generate();
		
		System.out.println(dg.toString());
		
	}
}
