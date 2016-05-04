/**
 * 
 */
package com.iw86.lang;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.iw86.collection.Row;

/**
 * Json处理类
 * @author tanghuang
 */
@SuppressWarnings("rawtypes")
public class JsonUtil {

	// 定制化值序列化处理
	private static SerializeConfig config = new SerializeConfig();
	static {
		config.put(java.sql.Timestamp.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
		config.put(java.sql.Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
		config.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
	}
	
	/**
	 * 把json格式的字符串转化成Row
	 * @param json
	 * @return
	 */
	public static Row<?, ?> strToRow(String json) {
		if (json == null || json.length() == 0)
			return new Row();
		return JSON.parseObject(json, Row.class);   
	}
	
	/**
	 * 把Row转化成json格式的字符串(注意：会将row的键值remove掉)
	 * @param row  要去掉的属性
	 * @return
	 */
	public static String rowToStr(Map row, String[] excludeProperty) {
		if (row == null || row.size() == 0)
			return "{}";
		if(excludeProperty!=null && excludeProperty.length>0){
			for(String key:excludeProperty){
				row.remove(key);
			}
		}
		return JSON.toJSONString(row,config,SerializerFeature.DisableCircularReferenceDetect);
	}
	
	/**
	 * 把Row转化成json格式的字符串
	 * @param row
	 * @return
	 */
	public static String rowToStr(Map row) {
		return rowToStr(row,null);
	}
	
	/**
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static Object strToObj(String json,Class<Object> clazz) {
		return JSON.parseObject(json, clazz);   
	}
	
	/**
	 * 把Object转化成json格式的字符串
	 * @param obj
	 * @return
	 */
	public static String objToStr(Object obj) {
		if (obj == null)
			return "{}";
		return JSON.toJSONString(obj,config,SerializerFeature.DisableCircularReferenceDetect);
	}
	
	/**
	 * 把row里的json的数据解析后添加到row中
	 * @param row
	 * @param field
	 * @return
	 */
	public static Row readDataInfo(Row<Object, Object> row,String field) {
		if (row == null)
			return new Row();
		Row jsonRow = strToRow(row.gets(field));
		for (Object key : jsonRow.keySet()) {
			if (!row.containsKey(key)) {
				row.put(key, jsonRow.get(key));
			}
		}
		if (row.containsKey(field)) {
			row.remove(field);
		}
		return row;
	}
	
	/**
	 * 把指定的属性存入datainfo字段中去,返回row可进行链式编程
	 * @param row
	 * @param field
	 * @param props
	 * @return
	 */
	public static Row<Object, Object> writeDataInfo(Row<Object, Object> row, String field, String[] props) {
		Row<String, Object> dataInfo = new Row<String, Object>();
		// 把指定的属性读出来，并存入dataInfo
		for (String prop : props) {
			if (row.get(prop) != null) {
				dataInfo.put(prop, row.get(prop));
				row.remove(prop);
			}
		}
		row.put(field, rowToStr(dataInfo));
		return row;
	}
	
	/**
	 * 把一个JSON对象数组描述字符转化为List
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Row> strToList(String json) {
		return (List<Row>)strToObjList(json,Row.class);
	}
	
	/**
	 * 把一个JSON对象数组描述字符转化为List
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static List<?> strToObjList(String json, Class<?> clazz) {
		return (List<?>)JSON.parseArray(json,clazz);
	}
	
	/**
	 * 把list转化为JSON数组字符串描述
	 * 
	 * @param list
	 * @return
	 */
	public static String listToStr(List<?> list) {
		if (list == null || list.size() == 0)
			return "[]";
		return jsonExclude(list,null);
	}
	
	/**
	 * 转换对象到json
	 * @param o
	 * @param excludeProperty 要排除的属性
	 * @return
	 */
	public static String jsonExclude(Object o, String[] excludeProperty) {
		return baseJson(o,excludeProperty,true);
	}
	
	/**
	 * 转换对象到json
	 * @param o
	 * @param includeProperty 要包含的属性
	 * @return
	 */
	public static String jsonInclude(Object o, String[] includeProperty) {
		return baseJson(o,includeProperty,false);
	}
	
	/**
	 * 按指定方式过滤JSON数据字段
	 * 包括对字段的过滤、日期值的过滤处理
	 * @param o
	 * @param excludeProperty
	 * @param exclude
	 * @return
	 */
	private static String baseJson(Object o, String[] propertys, boolean exclude) {
		NamedPropertyFilter filter = new NamedPropertyFilter(propertys,exclude);
		
		SerializeWriter out = new SerializeWriter();
		JSONSerializer serializer = new JSONSerializer(out,config);
		serializer.getPropertyFilters().add(filter);
		serializer.write(o);
		return out.toString();
	}
	
	// 按名称过滤属性
	private static class NamedPropertyFilter implements PropertyFilter {
		private String[] names;
		private boolean exclude;

		public NamedPropertyFilter(String[] names, boolean exclude) {
			this.names = names;
			this.exclude = exclude;
		}

		public boolean apply(Object source, String property, Object value) {
			if (names == null || names.length < 1) {
				return exclude;
			}
			for (String name : names) {
				if (name.equals(property)) {
					return !exclude;
				}
			}
			return exclude;
		}
	}
	
}
