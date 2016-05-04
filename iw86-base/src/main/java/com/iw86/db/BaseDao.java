/**
 * 
 */
package com.iw86.db;

import java.util.List;
import java.util.Map;

import com.iw86.collection.Row;

/**
 * 数据库操作CRUD功能基类接口
 * @author tanghuang
 */
@SuppressWarnings("rawtypes")
public interface BaseDao {
	
	/**
	 * @param obj
	 * @return
	 */
	public int insert(Object obj);
	
	/**
	 * @param primaryKey
	 * @return
	 */
	public Object select(Object primaryKey);
	
	/**
	 * @param primaryKey
	 * @return
	 */
	public Row selectRow(Object primaryKey);
	
	/**
	 * @param newObject
	 * @return
	 */
	public int update(Object newObject);
	
	/**
	 * @param obj
	 * @return
	 */
	public int delete(Object obj);
	
	/**
	 * @param map
	 * @return
	 */
	public int count(Map map);
	
	/**
	 * @param map
	 * @return
	 */
	public List list(Map map);
	
	/**
	 * @param map
	 * @return
	 */
	public List<Row> listRow(Map map);

}
