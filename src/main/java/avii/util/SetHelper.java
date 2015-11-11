package main.java.avii.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetHelper {
	public static ArrayList<String> getSortedListFromSet(Set<String> theSet) {
		ArrayList<String> sortedParamKeySet = new ArrayList<String>();
		for (String key : theSet) {
			sortedParamKeySet.add(key);
		}
		Collections.sort(sortedParamKeySet);
		return sortedParamKeySet;
	}

	public static ArrayList<Integer> getSortedIntegerListFromSet(Set<Integer> theSet) {
		ArrayList<Integer> sortedParamKeySet = new ArrayList<Integer>();
		for (Integer key : theSet) {
			sortedParamKeySet.add(key);
		}
		Collections.sort(sortedParamKeySet);
		return sortedParamKeySet;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Collection<? extends Object> difference(Collection<? extends Object> a, Collection<? extends Object> b) {
		Set<? extends Object> set = new HashSet(a);
		set.removeAll(b);
		return set;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Collection<? extends Object> intersection(Collection<? extends Object> a, Collection<? extends Object> b) {
		Set<? extends Object> set = new HashSet(a);
		set.retainAll(b);
		return set;
	}
}
