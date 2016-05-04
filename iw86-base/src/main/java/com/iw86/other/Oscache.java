/**
 * 
 */
package com.iw86.other;

import java.util.Date;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * 基于oscache的封装缓存类<br>
 * 先用Oscache构造缓存对象，然后使用put/get来添加/获取缓存数据
 * @author tanghuang
 */
public class Oscache extends GeneralCacheAdministrator {
	private static final long serialVersionUID = 4090143795837027482L;
	private String keyPrefix; // 关键字前缀字符
	private int refreshPeriod;// 过期时间(单位为秒)

	/**
	 * 默认构造器
	 * 注意：此构造方法必须要加载配置文件oscache.properties
	 */
	public Oscache(){
		super();
	}
	
    /**
     * 构造器，无需配置文件
	 * @param keyPrefix 关键字前缀
	 * @param capacity 缓存的对象数目
	 * @param refreshPeriod 过期时间,单位秒
	 */
	public Oscache(String keyPrefix, int capacity, int refreshPeriod) {
		super();
		this.keyPrefix = keyPrefix;
		this.refreshPeriod = refreshPeriod;
		this.setCacheCapacity(capacity);
	}

	/**
	 * 添加被缓存的对象
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value) {
		//调用父类putInCache（String key, Object content）方法
		this.putInCache(this.keyPrefix + "_" + key, value);
	}

	/**
	 * 删除被缓存的对象
	 * @param key
	 */
	public void remove(String key) {
		//调用父类flushEntry（String key）方法
		this.flushEntry(this.keyPrefix + "_" + key);
	}

	/**
	 * 删除所有被缓存的对象
	 * @param date
	 */
	public void removeAll(Date date) {
		//调用父类flushAll（Date date）方法
		this.flushAll(date);
	}

	/**
	 * 删除所有被缓存的对象
	 */
	public void removeAll() {
		//调用父类flushAll（）方法
		this.flushAll();
	}

	/**
	 * 获取被缓存的对象
	 * @param key
	 * @return 返回缓存数据
	 * @throws Exception
	 */
	public Object get(String key){   
		try{   
			return this.getFromCache(this.keyPrefix+"_"+key,this.refreshPeriod);             
		} catch (NeedsRefreshException e) {   
			this.cancelUpdate(this.keyPrefix+"_"+key);
			return null;
		}   
  
     }
}
