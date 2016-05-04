package com.iw86.distributed;

import java.util.concurrent.locks.Lock;

/**
 * 分布式锁服务
 * @author tanghuang
 *
 */
public interface DistributedLockService {

	/**
	 * 新的分布式锁
	 * @param name 锁名
	 * @return 分布式锁
	 */
	Lock newLock(String name);

}
