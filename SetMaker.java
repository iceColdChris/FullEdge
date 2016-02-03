/*
 * Chris Fahlin
 * Tcss 342
 * Graphs
 * SetMaker.java
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetMaker {

	// http://stackoverflow.com/questions/1670862/obtaining-powerset-of-a-set-in-java

	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
		Set<Set<T>> sets = new HashSet<Set<T>>();

		if (originalSet.isEmpty()) {    // an empty set has only one subset
			sets.add(new HashSet<T>()); // and that's the empty set
			return sets;   // sets contains only the empty set
		}

		List<T> list = new ArrayList<T>(originalSet);
		T head = list.get(0);   // first element
		Set<T> rest = new HashSet<T>(list.subList(1, list.size())); // all the rest

		for (Set<T> set : powerSet(rest)) {
			Set<T> newSet = new HashSet<T>();
			newSet.add(head);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set);
		}      
		return sets;
	}
}