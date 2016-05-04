package com.iw86.distributed;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import javax.annotation.Resource;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.stereotype.Service;

/**
 * 序列号服务实现. <br>
 * @author tanghuang
 * 
 */
@Service
public class SequenceServiceImpl extends AbstractDistributedService implements SequenceService {
	/**
	 * 默认序列号路径
	 */
	public static final String SEQUENCE = "/Sequence";
	/**
	 * 默认数值路径
	 */
	public static final String NUMBER = "/Number";
	/**
	 * 分隔符
	 */
	private static final String SEPARATOR = "-";

	/**
	 * 分布式锁
	 */
	@Resource
	private DistributedLockService ls;

	/**
	 * 创建路径
	 */
	@Override
	protected void onConnected() {
		this.createPath(SEQUENCE);
		this.createPath(NUMBER);
	}

	/**
	 * 针对不同的名字，在相应路径下创建节点后获取序列号后删除之
	 */
	@Override
	public long next(String name) {
		String path = getPath(SEQUENCE, name + "/seq") + SEPARATOR;
		try {
			createPath(path.substring(0, path.lastIndexOf('/')));
			String node = zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			int k = node.lastIndexOf(SEPARATOR);
			long rs = Long.parseLong(node.substring(k + SEPARATOR.length()));
			zk.delete(node, -1);
			return rs;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 如果长度太短，在前面补0
	 */
	@Override
	public String next(String name, int length) {
		String s = Long.toString(next(name));
		int len = s.length();
		if (len < length) {
			int k = length - len;
			char[] c = new char[k];
			for (int i = 0; i < k; i++) {
				c[i] = '0';
			}
			return new String(c) + s;
		}
		return s.substring(len - length);
	}

	public static void main(String[] args) throws Exception {
		DistributedLockServiceImpl ls = new DistributedLockServiceImpl();
		ls.init();
		final SequenceServiceImpl s = new SequenceServiceImpl();
		s.ls = ls;
		s.init();
		List<Thread> list = new ArrayList<Thread>();
		final List<Integer> rs = new ArrayList<Integer>();
		long time = System.currentTimeMillis();
		for (int i = 0; i < 969; i++) {
			Thread t = new Thread() {
				public void run() {
					int v = s.incrementAndGet("test");
					synchronized (rs) {
						if (rs.size() == 0) {
							rs.add(v);
						} else if (v > rs.get(0)) {
							rs.clear();
							rs.add(v);
						}
					}
				}
			};
			list.add(t);
			t.start();
		}
		for (Thread t : list) {
			t.join();
		}
		System.out.println("use time : " + (System.currentTimeMillis() - time));
		System.out.println("max---" + rs.get(0));
		ls.destroy();
	}

	@Override
	public int addAndGet(String name, int delta) {
		Lock lock = ls.newLock(NUMBER + '/' + name);
		lock.lock();
		try {
			String path = getPath(NUMBER, name);
			if (zk.exists(path, false) == null) {
				zk.create(path, Integer.toString(delta).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				return delta;
			} else {
				byte[] b = zk.getData(path, false, null);
				int v = Integer.parseInt(new String(b)) + delta;
				if (delta != 0) {
					zk.setData(path, Integer.toString(v).getBytes(), -1);
				}
				return v;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public int incrementAndGet(String name) {
		return addAndGet(name, 1);
	}

	@Override
	public int intValue(String name) {
		return addAndGet(name, 0);
	}

}
