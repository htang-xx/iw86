package com.iw86.distributed;

import java.util.List;

/**
 * 服务器发生变化
 * @author tanghuang
 *
 */
public interface ServerChangeListener {

	/**
	 * 服务器发生变化
	 * @param newServers 新服务器列表
	 * @param oldServers  旧服务器列表
	 * @throws Exception 异常
	 */
	void onChange(List<String> newServers, List<String> oldServers) throws Exception;

}
