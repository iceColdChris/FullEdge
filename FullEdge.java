/*
 * Chris Fahlin
 * Tcss 342
 * Graphs
 * FullEdge.java
 */


public class FullEdge {
	String start;
	String dest;
	double cost;

	FullEdge(String st, String dst, double cst) {
		start = st;
		dest = dst;
		cost = cst;
	}

	public String toString() {
		return "("+start+","+dest+","+cost+")";
	}
}