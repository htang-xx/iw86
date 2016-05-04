package com.iw86.distributed;

/**
 * 配置加载器
 * 
 * @author lj
 * 
 */
public interface ConfigLoader {

	/**
	 * 加载指定的配置
	 * 
	 * @param name
	 *            配置名
	 * @return 配置值
	 */
	public String load(String name);
}
