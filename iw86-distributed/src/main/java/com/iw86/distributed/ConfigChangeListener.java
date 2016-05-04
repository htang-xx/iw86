package com.iw86.distributed;

/**
 * 配置修改侦听. <br>
 * 
 * @author lj
 * 
 */
public interface ConfigChangeListener {
	/**
	 * 配置发生变化
	 * 
	 * @param name
	 *            参数名
	 * @param value
	 *            参数值(可能为null)
	 */
	public void configChanged(String name, String value);
}
