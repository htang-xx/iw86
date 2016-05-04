package com.iw86.distributed;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;

/**
 * 分布式锁的实现. <br>
 * 此类通过ReentrantLock完成当前进程内的锁处理，通过zk完成不同进程间的分布式锁.
 * @author tanghunag
 * 
 */
public class DistributedLockServiceImpl extends AbstractDistributedService implements DistributedLockService {

	/**
	 * 默认配置路径
	 */
	public static final String LOCK = "/Lock";

	/**
	 * 子节点前缀
	 */
	private static final String SEQ = "lock-";

	/**
	 * 线程池服务
	 */
	private ExecutorService es;

	/**
	 * 全部锁是否停止标志
	 */
	private AtomicBoolean totalFlag = new AtomicBoolean(true);

	/**
	 * 锁的缓存
	 */
	private Map<String, Lock> locks = new ConcurrentHashMap<String, Lock>();

	/**
	 * 初始化路径及数据
	 */
	@Override
	protected final void onConnected() {
		createPath(LOCK);
	}

	@Override
	protected void beforeInit() {
		if (es == null)
			es = Executors.newCachedThreadPool();
	}

	@Override
	protected void beforeDestroy() {
		totalFlag.set(false);
		if (es != null)
			es.shutdown();
	}

	/**
	 * 构造新的锁时总是优先从取出已经有的锁
	 */
	@Override
	public Lock newLock(String name) {
		Lock l = locks.get(name);
		if (l == null) {
			l = new LockImpl(name);
			locks.put(name, l);
		}
		return l;
	}

	/**
	 * 锁实现
	 * 
	 * @author lj
	 * 
	 */
	private class LockImpl implements Lock, Runnable, Watcher {

		/**
		 * zk路径
		 */
		private String path;
		/**
		 * 当前应用锁
		 */
		private Lock lock = new ReentrantLock();
		/**
		 * 计数
		 */
		private CountDownLatch latch;
		/**
		 * 当前节点ID
		 */
		private String node;

		/**
		 * 运行标志位
		 */
		private AtomicBoolean flag = new AtomicBoolean();

		/**
		 * lock是否成功
		 */
		private boolean locked;

		/**
		 * 构造锁
		 * 
		 * @param name
		 */
		public LockImpl(String name) {
			path = getPath(LOCK, name);
			createPath(path);
		}

		/**
		 * 锁定前先计数，再创建节点，如果是第一个子节点则锁成功，否则等待前一节点被删除
		 */
		@Override
		public void lock() {
			lock.lock();
			latch = new CountDownLatch(1);
			try {
				flag.set(true);
				es.execute(this);
				latch.await();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * 提供给线程池以完成锁的创建
		 */
		@Override
		public void run() {
			try {
				while (totalFlag.get() && flag.get()) {
					Boolean c = createNode();
					if (c != null) {
						if (c) {
							locked = true;
							latch.countDown();
							return;
						}
						return;
					}
					Thread.sleep(100);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				flag.set(false);
			}
		}

		/**
		 * 创建节点
		 * 
		 * @return 是否第一个子节点
		 */
		private Boolean createNode() {
			try {
				this.node = zk.create(path + '/' + SEQ, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
						CreateMode.EPHEMERAL_SEQUENTIAL);
				node = node.substring(node.lastIndexOf('/') + 1);
				List<String> childs = zk.getChildren(path, this);
				// 注意需要排序
				Collections.sort(childs);
				int k = childs.indexOf(node);
				if (k <= 0) {
					return true;
				} else {
					zk.exists(path + '/' + childs.get(k - 1), this);
				}
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		/**
		 * 中断锁的处理，视为锁失败
		 */
		@Override
		public void lockInterruptibly() throws InterruptedException {
			flag.set(false);
			locked = false;
			latch.countDown();
			lock.lockInterruptibly();
		}

		/**
		 * 创建子节点时是第一个，直接返回true，否则返回false;
		 */
		@Override
		public boolean tryLock() {
			if (lock.tryLock()) {
				return createNode();
			}
			return false;
		}

		/**
		 * 等待加锁，如果超时返回false
		 */
		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			if (lock.tryLock(time, unit)) {
				locked = false;
				latch = new CountDownLatch(1);
				try {
					flag.set(true);
					es.execute(this);
					latch.await(time, unit);
					return locked;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			return false;
		}

		/**
		 * 释放锁时删除当前节点
		 */
		@Override
		public void unlock() {
			try {
				zk.delete(path + '/' + node, -1);
			} catch (Exception e) {
				if (!(e instanceof InterruptedException))
					e.printStackTrace();
			}
			lock.unlock();
		}

		/**
		 * DO NOTHING
		 */
		@Override
		public Condition newCondition() {
			return null;
		}

		/**
		 * 前一节点被删除时计数减一，完成锁的获取
		 */
		@Override
		public void process(WatchedEvent event) {
			switch (event.getType()) {
			case NodeDeleted:
				locked = true;
				latch.countDown();
				break;
			case NodeChildrenChanged:
				while (true) {
					try {// 防止在加入对前一子节点的侦听前前一子节点已经被删除之
						List<String> childs = zk.getChildren(path, false);
						if (childs.size() > 0) {// 注意需要排序
							Collections.sort(childs);
							if (node.equals(childs.get(0))) {
								locked = true;
								latch.countDown();
							}
						}
					} catch (KeeperException e) {
						if (e.code() == Code.CONNECTIONLOSS) {
							if (checkConnected())
								continue;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
				break;
			default:
			}
		}

	}

	public static void main(String[] args) throws Exception {
		DistributedLockServiceImpl ds = new DistributedLockServiceImpl();
		ds.init();
		Lock l = ds.newLock("test");
		try {
			l.lock();
			// /
			System.out.println("ok");
		} finally {
			l.unlock();
		}
		ds.destroy();
	}

}
