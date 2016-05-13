/**
 * 
 */
package com.iw86.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <字段,值> 增强版HashMap
 * @author tanghuang
 *
 */
public class Row<K,V> extends HashMap<K,V> {
	
	private static final long serialVersionUID = 1L;
	
	public Row() {
	}
	
	/**
	 * 直接拿一个Map过来当作一个Row使用
	 * @param map
	 */
	public Row(Map<K,V> map) {
		super(map);
	}
	
	/**
	 * 根据字段名返回字符串值,若为null侧返回默认值;
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public String gets(Object name, String defaultValue) {
		return MapUtil.gets(this, name, defaultValue);
	}
	
	/**
	 * @param name
	 * @return
	 */
	public String gets(Object name){
		return gets(name, null);
	}
	
	/**
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public int getInt(Object name, int defaultValue) {
		return MapUtil.getInt(this, name, defaultValue);
	}
	
	/**
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public float getFloat(Object name, float defaultValue) {
		return MapUtil.getFloat(this, name, defaultValue);
	}
	
	/**
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public long getLong(Object name, long defaultValue) {
		return MapUtil.getLong(this, name, defaultValue);
	}
	
	/**
	 * 得到所有key的字符串数组
	 * @return
	 */
	public String[] getKeys() {
		Set<K> keys = this.keySet();
		Iterator<K> iter = keys.iterator();
		String[] strs = new String[keys.size()];
		int i = 0;
		while (iter.hasNext()) {
			strs[i] = iter.next().toString();
			i++;
		}
		return strs;
	}
	
	/**
	 * 得到所有value的字符串数组
	 * @return
	 */
	public String[] getValues(){
		Collection<V> objs = this.values();
		String[] strs = new String[objs.size()];
		int i = 0;
		for(Object obj:objs){
			strs[i] = obj.toString();
			i++;
		}
		return strs;
	}
	
	/**
	 * @param perFillObject
	 * @return
	 */
	public Object toValueObject(Object perFillObject) {
		return MapUtil.toValueObject(this, perFillObject);
	}
	
	
	/* (non-Javadoc)
	 * @see java.util.AbstractMap#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<K> e = keySet().iterator(); e.hasNext();) {
			String name = String.valueOf(e.next());
			if(sb.length()>0) sb.append(",");
			sb.append(name).append(":").append(get(name));
		}
		return sb.toString();
	}

}
