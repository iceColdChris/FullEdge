/*
 * Chris Fahlin
 * Tcss 342
 * Graphs
 * WeightComparator.java
 */

import java.util.Comparator;


public class WeightComparator implements Comparator<Graph>
{

	@Override
	public int compare(Graph a, Graph b) {
		return (int) (a.totalEdgeCost() - b.totalEdgeCost());
	}
}
