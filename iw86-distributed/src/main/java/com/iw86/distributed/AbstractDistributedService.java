package com.iw86.distributed;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.util.StringUtils;

/**
 * 抽象的分布式服务
 * 
 * @author lj
 * 
 */
public class AbstractDistributedService implements Watcher {

	/**
	 * 客户端
	 */
	ZooKeeper zk;

	/**
	 * 集群地址
	 */
	private String cluster;

	/**
	 * 超时
	 */
	private int timeout = 2000;

	protected AtomicBoolean closing = new AtomicBoolean();
	private long lastconn;

	/**
	 * 设置ZK服务器集群
	 * 
	 * @param cluster
	 *            the cluster to set
	 */
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	/**
	 * 初始化zk
	 * 
	 * @throws Exception
	 */
	@PostConstruct
	public final void init() throws Exception {
		beforeInit();
		restart();
	}

	/**
	 * 关闭zk
	 */
	@PreDestroy
	public final void destroy() {
		beforeDestroy();
		try {
			closing.set(true);
			zk.close();
			Thread.sleep(1000);// 等一秒以保证zk关闭
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void restart() {
		if (System.currentTimeMillis() - lastconn < 1000)
			return;
		if (zk != null) {
			try {
				zk.close();
				zk = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			zk = new ZooKeeper(cluster == null ? "127.0.0.1" : cluster, timeout, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 销毁之前的操作
	 */
	protected void beforeDestroy() {
	}

	/**
	 * 初始化之前的操作
	 */
	protected void beforeInit() {
	}

	/**
	 * 处理连接连上及断开
	 */
	@Override
	public void process(WatchedEvent event) {
		KeeperState ke = event.getState();
		switch (ke) {
		case Disconnected:
			onDisconnected();
			System.out.println("Zookeeper disconnected .");
			if (!closing.get())
				restart();
			break;
		case SyncConnected:
			onConnected();
			synchronized (zk) {
				zk.notifyAll();
			}
			System.out.println("Zookeeper connected .");
			break;
		case Expired:
			onDisconnected();
			restart();
			break;
		default:
			break;
		}
	}

	/**
	 * 连上之后的处理
	 */
	protected void onConnected() {

	}

	/**
	 * 断开时的处理
	 */
	protected void onDisconnected() {

	}

	/**
	 * 获得一个路径
	 * 
	 * @param rootNode
	 *            根路径
	 * @param key
	 *            子节点
	 * @return 路径
	 */
	protected String getPath(String rootNode, String key) {
		if (!StringUtils.isEmpty(rootNode)) {
			if (key.startsWith("/")) {
				key = key.substring(1);
			}
			if (rootNode.endsWith("/")) {
				return rootNode + key;
			}
			return rootNode + "/" + key;
		}
		return key;
	}

	protected boolean checkConnected() {
		while (!closing.get() && !zk.getState().isConnected()) {
			synchronized (zk) {
				try {
					zk.wait(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return zk.getState().isConnected();
	}

	/**
	 * 在zk上创建一个路径
	 * 
	 * @param path
	 *            路径
	 */
	protected void createPath(String path) {
		while (true) {
			try {
				if (!closing.get() && zk.exists(path, false) == null) {
					int k = path.lastIndexOf('/');
					if (k > 0) {
						createPath(path.substring(0, k));
					}
					zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				}
			} catch (KeeperException e) {
				if (e.code() == Code.CONNECTIONLOSS) {
					if (checkConnected())
						continue;
				}
				if (e.code() != Code.NODEEXISTS)
					throw new RuntimeException(e);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			break;
		}
	}

	/**
	 * 获得节点的数据
	 * 
	 * @param path
	 *            路径
	 * @param watcher
	 *            观察者（可为null）
	 * @return 节点数据
	 */
	protected String getData(String path, Watcher watcher) {
		try {
			byte[] rs = zk.getData(path, watcher, null);
			return new String(rs, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 如果节点不存在，则保存数据，存在则不保存
	 * 
	 * @param path
	 *            节点路径
	 * @param value
	 *            值
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	protected void setNotExists(String path, String value) throws KeeperException, InterruptedException {
		byte[] v = value == null ? new byte[0] : value.getBytes();
		if (zk.exists(path, false) == null) {
			zk.create(path, v, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
	}

	/**
	 * 保存节点数据
	 * 
	 * @param path
	 *            路径
	 * @param value
	 *            值
	 * @throws KeeperException
	 * @throws InterruptedException
	 * @throws UnsupportedEncodingException
	 */
	protected void setData(String path, String value)
			throws KeeperException, InterruptedException, UnsupportedEncodingException {
		setData(path, value, true);
	}

	/**
	 * 保存节点数据
	 * 
	 * @param path
	 * @param value
	 * @param create
	 *            不存在节点时是否保存
	 * @throws KeeperException
	 * @throws InterruptedException
	 * @throws UnsupportedEncodingException
	 */
	protected void setData(String path, String value, boolean create)
			throws KeeperException, InterruptedException, UnsupportedEncodingException {
		if (value == null) {
			zk.delete(path, -1);
		} else {
			byte[] v = value.getBytes("UTF-8");
			if (zk.exists(path, false) == null) {
				if (create) {
					zk.create(path, v, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				}
			} else {
				zk.setData(path, v, -1);
			}
		}
	}

	/**
	 * 添加节点的侦听
	 * 
	 * @param path
	 *            路径
	 * @param watcher
	 *            侦听器
	 * @return 节点的数据
	 */
	protected String addListener(String path, Watcher watcher) {
		try {
			if (zk.exists(path, watcher) != null) {
				byte[] rs = zk.getData(path, null, null);
				return new String(rs, "UTF-8");
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
