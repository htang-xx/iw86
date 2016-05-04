package com.iw86.distributed;

import java.util.Map;

/**
 * 配置服务. <br>
 * 
 * <pre>
 * 应用场景：
 * 1、不同应用服务器之间需要同步配置（如数据库连接配置）
 * 2、应用内的一些参数需要在通过后台界面修改后立刻在所有应用服务器生效
 * 通过方法{@link #save(String, String)}或{@link #save(Map)}保存的数据，
 * 会立刻通知到所有通过{@link #addListener(ConfigChangeListener, String...)}方法注册了侦听的所有应用服务器中。
 * </pre>
 * 
 * @author lj
 * @see {@link ConfigServiceImpl}、{@link ConfigLoader}
 */
public interface ConfigService {

	/**
	 * 获得配置值(必须通过方法{@link #addListen(String...)}加入侦听的才可以获得)
	 * 
	 * @param name
	 *            配置名
	 * @return 配置值
	 */
	String get(String name);

	/**
	 * 获得配置值(必须通过方法{@link #addListen(String...)}加入侦听的才可以获得)
	 * 
	 * @param name
	 *            配置名
	 * @return 配置值
	 */
	Integer getInt(String name);

	/**
	 * 更新配置
	 * 
	 * @param name
	 *            配置名
	 * @param value
	 *            配置值
	 */
	void save(String name, String value);

	/**
	 * 更新配置
	 * 
	 * @param name
	 *            配置名
	 * @param value
	 *            配置值
	 * @param onlyExists
	 *            只保存已经存在于分布式服务器的配置
	 */
	void save(String name, String value, boolean onlyExists);

	/**
	 * 更新配置
	 * 
	 * @param values
	 *            配置参数
	 */
	void save(Map<String, String> values);

	/**
	 * 更新配置
	 * 
	 * @param values
	 *            配置参数
	 */
	void save(Map<String, String> values, boolean onlyExists);

	/**
	 * 添加配置侦听
	 * 
	 * @param listener
	 *            侦听器
	 * @param name
	 *            配置名
	 */
	void addListener(ConfigChangeListener listener, String... name);

	/**
	 * 对指定的配置进行侦听并缓存，通过{@link #get(String)}或{@link #getInt(String)}获取相应配置.
	 * 
	 * @param name
	 *            配置名列表
	 */
	void addListen(String... name);

}
