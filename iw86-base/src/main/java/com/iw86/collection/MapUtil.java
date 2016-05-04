/**
 * 
 */
package com.iw86.collection;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.iw86.base.BeanUtil;
import com.iw86.lang.Conver;
import com.iw86.lang.StringUtil;

/**
 * Map处理类
 * @author tanghuang
 */
public class MapUtil {
	public static String XMLHEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	
	/**
	 * 转成value为数字类型的map,value为null时自动转换0
	 * @param map Map
	 * @return 转换后的Map
	 */
	public static Map<String, Integer> toDefaultIntMap(Map<String, String> map) {
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (Entry<String, String> entry : map.entrySet()) {
			String gameKey = entry.getKey();
			result.put(gameKey, Conver.toInt(entry.getValue(), 0));
		}
		return result;
	}

	/**
	 * 判断Map是否为空
	 * @param map Map
	 * @return Map对象为null或包含零个元素则返回true
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		if (map == null || map.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 根据字段名返回字符串值,若为null侧返回默认值
	 * @param map
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static String gets(Map<?,?> map, Object name, String defaultValue) {
		if (map!=null && map.get(name) != null){
			return map.get(name).toString();
		}
		return defaultValue;
	}
	
	/**
	 * @param map
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static int getInt(Map<?,?> map, Object name, int defaultValue) {
		if (map!=null){
			Object o = map.get(name);
			if (o != null){
				return StringUtil.getInt(o.toString(), defaultValue);
			}
		}
		return defaultValue;
	}
	
	/**
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static float getFloat(Map<?,?> map, Object name, float defaultValue) {
		if (map!=null){
			Object o = map.get(name);
			if (o != null){
				try {
					return Float.valueOf(o.toString()).floatValue();
				} catch (Exception e) {}
			}
		}
		return defaultValue;
	}
	
	/**
	 * @param map
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static long getLong(Map<?,?> map, Object name, long defaultValue) {
		if (map!=null){
			Object o = map.get(name);
			if (o != null){
				try {
					return Long.valueOf(o.toString()).longValue();
				} catch (Exception e) {}
			}
		}
		return defaultValue;
	}
	
	/**
	 * 通过存在的Row来构造Bean对象
	 * 要求字段必须一致（建议数据库字段不要含下划线等符号，不要中间夹带大小写）
	 * @param map
	 * @param perFillObject
	 * @return
	 */
	public static Object toValueObject(Map<?,?> map, Object perFillObject) {
		try {
			if (map != null){
				Object key = null;
				Iterator<?> i = map.keySet().iterator();
				while (i.hasNext()) {
					key = i.next();
					BeanUtil.setProperty(perFillObject, (String) key, map.get(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return perFillObject;
	}

	/**
	 * 根据传过来的Bean对象转换到Map中
	 * @param vo
	 * @return
	 */
	public static HashMap<String,Object> fromValueObject(Object vo){
		HashMap<String, Object> row = new HashMap<String, Object>();
		if(vo!=null){
			try{
				Class<?> type = vo.getClass(); // 得到Class用于进行反射处理
				Field[] fields = type.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					String name = fields[i].getName();
					if(!name.equals("serialVersionUID")){
						row.put(name, BeanUtil.getProperty(vo, name));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return row;
	}

	/**
	 * 根据传过来的Bean对象转换到Row中，排除不需要或包含需要的属性
	 * @param vo
	 * @param properties
	 * @param exclude ture排除，false包含
	 * @return
	 */
	public static HashMap<String,Object> fromValueObject(Object vo, String[] properties, boolean exclude) {
		HashMap<String,Object> row = new HashMap<String,Object>();
		if(vo!=null){
			Class<?> type = vo.getClass(); // 得到Class用于进行反射处理
			try{
				Field[] fields = type.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					String name = fields[i].getName();
					if(exclude && !Arrays.asList(properties).contains(name)){
						row.put(name, BeanUtil.getProperty(vo, name));
					}else if(!exclude && Arrays.asList(properties).contains(name)){
						row.put(name, BeanUtil.getProperty(vo, name));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return row;
	}
	
	/**
	 * 根据传过来的Bean对象转换到Row中，排除不需要的属性
	 * @param vo
	 * @param excludeProperties
	 * @return
	 */
	public static HashMap<String,Object> fromValueObject(Object vo, String[] excludeProperties){
		return fromValueObject(vo, excludeProperties, true);
	}
	
	/**
	 * 检验ROW里面是否包念指定的字段值
	 * @param includStr
	 */
	public static boolean validInclude(Map<?,?> map, String[] includStr) {
		for (String key : includStr) {
			String value = gets(map, key, null);
			//是否不存在或为空
			boolean validate = value != null && !"".equals(value.trim());
			if (!validate)
				return false;
		}
		return true;
	}

	/**
	 * 获取指定字段的ROW,excludeProperties以逗号分开的字段集
	 * @param excludeProperties
	 */
	public static Map<?,?> excludeMap(Map<?,?> oldRow, String excludeProperties) {
		String[] exclude = excludeProperties.split(",");
		return excludeMap(oldRow, exclude);
	}

	/**
	 * 获取指定字段的Map,excludeProperties为字段集数组
	 * @param excludeProperties
	 */
	public static Map<Object,Object> excludeMap(Map<?,?> oldRow, String[] excludeProperties) {
		HashMap<Object,Object> newRow = new HashMap<Object,Object>();
		for (String pro : excludeProperties) {
			Object o = oldRow.get(pro);
			if (o != null)
				newRow.put(pro, o);
		}
		return newRow;
	}
	
	/**
	 * 实现对Map按照value倒序排序, 要求value为数字
	 * @param map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map.Entry[] sortedMapByValue(Map<?,?> map) {
		Set<?> set = map.entrySet();
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);
		Arrays.sort(entries, new Comparator<Object>() {
			public int compare(Object arg0, Object arg1) {
				String key1 = ((Map.Entry) arg0).getValue().toString();
				String key2 = ((Map.Entry) arg1).getValue().toString();
				Long l1 = Conver.toLong(key1,-1L);
				Long l2 = Conver.toLong(key2,-1L);
				if(l1!=-1L && l2!=-1L)
					return l2.compareTo(l1);
				else
					return key2.compareTo(key1);
					
			}
		});
		return entries;
	}
	
	/**
	 * 把XML字符串转成Row
	 * @param xml
	 * @param multi 是否多层
	 * @return
	 */
	public static Map<Object,Object> fromXml(String xml, boolean multi){
		Map<Object,Object> row = new HashMap<Object,Object>();
		if (!StringUtil.isEmpty(xml)){
			try{
				Document document = DocumentHelper.parseText(xml);
				Element rootElement = document.getRootElement();
				if(multi){
					row = nextNode(null,rootElement,row);
				}else{
					Iterator<?> elementIterator = rootElement.elementIterator();
					while (elementIterator.hasNext()){
						Element nodeElement = (Element) elementIterator.next();
						row.put(nodeElement.getName(), nodeElement.getText());
					}
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return row;
	}

	/**
	 * 转换成xml字符串
	 * @param row
	 * @param xmlHeader xml头,可使用RowUtil.XMLHEADER,为空时不加入
	 * @param rootElementName 根节点名，为空时不加入
	 * @return
	 */
	public static String toXml(Map<?,?> map,String xmlHeader,String rootElementName){
		StringBuilder xmlb = new StringBuilder();
		if (map  != null && map.size() > 0){
			if(!StringUtil.isEmpty(xmlHeader)) xmlb.append(xmlHeader);
			if(!StringUtil.isEmpty(rootElementName)) xmlb.append("<").append(rootElementName).append(">");
			for (Object key : map.keySet()){
				xmlb.append("<");
				xmlb.append(key);
				xmlb.append("><![CDATA[");
				xmlb.append(gets(map, key, ""));
				xmlb.append("]]></");
				xmlb.append(key);
				xmlb.append(">");
			}
			if(!StringUtil.isEmpty(rootElementName)) xmlb.append("</").append(rootElementName).append(">");
		}
		return xmlb.toString();
	}
	
	/**
	 * 递归方法获取子节点
	 * @param nodeName
	 * @param nodeElement
	 * @param map
	 * @return
	 */
	private static Map<Object,Object> nextNode(String parentNodeName,Element nodeElement, Map<Object,Object> row){
		Iterator<?> elementIterator = nodeElement.elementIterator();
		String key=nodeElement.getName();
		if(parentNodeName!=null) key=parentNodeName+"."+nodeElement.getName();
		if(elementIterator.hasNext()){
			while (elementIterator.hasNext()){
				row=nextNode(key,(Element) elementIterator.next(),row);
			}
		}else{
			row.put(key, nodeElement.getText());
		}
		return row;
	}
	
	/**
	 * 输出，测试用
	 */
	public static void dump(Map<?,?> map) {
		for (Iterator<?> e = map.keySet().iterator(); e.hasNext();) {
			String name = String.valueOf(e.next());
			System.out.println(name + "=" + map.get(name) + ", ");
		}
	}

	/**
	 * 导出值到字符串
	 * @return
	 */
	public static String toString(Map<?,?> map) {
		StringBuilder sb = new StringBuilder();
		for (Iterator<?> e = map.keySet().iterator(); e.hasNext();) {
			String name = String.valueOf(e.next());
			if(sb.length()>0) sb.append(",");
			sb.append(name).append(":").append(map.get(name));
		}
		return sb.toString();
	}

}
