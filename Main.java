/*
 * Chris Fahlin
 * Tcss 342
 * Graphs
 * Main.java
 */

public class Main {

	public static void main( String [ ] args ) {
		Graph g = new Graph("graph1.txt");
		Graph w = g.rcSubgraph();
		System.out.println(w.toString());
		System.out.println(w.totalEdgeCost());
		
	}
}