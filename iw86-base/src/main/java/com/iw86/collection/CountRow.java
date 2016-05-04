/**
 * 
 */
package com.iw86.collection;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * 计数map
 * @author tanghuang
 *
 */
public class CountRow<T> extends Row<T, Integer> {
	
	private static final long serialVersionUID = 1L;

	public CountRow() {
		super();
	}

	public Integer add(T key, Integer value) {
		Integer v = get(key);
		if (v == null)
			return put(key, value);
		return put(key, v + value);
	}

	public Integer add(T key) {
		return add(key, 1);
	}

	public Integer decr(T key) {
		return add(key, -1);
	}

	public Map.Entry<T, Integer> getMaxEntry() {
		Map.Entry<T, Integer> e = null;
		for (Map.Entry<T, Integer> _e : this.entrySet()) {
			if (e == null || e.getValue() < _e.getValue())
				e = _e;
		}
		return e;
	}

	@SuppressWarnings("unchecked")
	public Map.Entry<T, Integer>[] getDescEntries() {
		Map.Entry<T, Integer>[] e = this.entrySet().toArray(new Map.Entry[0]);
		Arrays.sort(e, new Comparator<Map.Entry<T, Integer>>() {
			public int compare(Map.Entry<T, Integer> o1, Map.Entry<T, Integer> o2) {
				return o2.getValue() - o1.getValue();
			}
		});
		return e;
	}

}
