package com.iw86.distributed;

import java.util.List;

/**
 * 服务器服务
 * @author tanghuang
 * 
 */
public interface ServerService {

	/**
	 * 获得服务器（运行实例）唯一标识
	 * @return 服务器唯一标识
	 */
	String getServerId();

	/**
	 * 是否整个系统领导服务器(按JAVA实例)
	 * @return 是否领导服务器
	 */
	boolean isLeaderServer();

	/**
	 * 列出所有服务器(仅当前群组)
	 * @return 所有服务器
	 */
	List<String> list();

	/**
	 * 列出某一群组下的所有服务器
	 * @param group 群组名
	 * @return 群组下的所有服务器
	 */
	List<String> list(String group);

	/**
	 * 添加侦听器
	 * @param listener 侦听器
	 */
	void addListener(ServerChangeListener listener);

}
