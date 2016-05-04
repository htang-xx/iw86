package com.iw86.distributed;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.springframework.stereotype.Service;

/**
 * 服务器服务. <br>
 * 不同服务器可以组成一个集群（通过group属性），同一集群下第一台启动的服务器默认为领导服务器
 * @author tanghunag
 * 
 */
@Service
public class ServerServiceImpl extends AbstractDistributedService implements ServerService {
	/**
	 * 默认配置路径
	 */
	public static final String SERVER = "/Server";
	private static final String SEPARATOR = "/instances";
	/**
	 * 服务器端口的环境变量
	 */
	public static final String SERVER_PORT = "SERVER_PORT";
	/**
	 * 服务器分组
	 */
	private String group;

	/**
	 * 服务器IP+端口
	 */
	private String ip;

	/**
	 * 当前服务器ID
	 */
	private String serverId;
	/**
	 * zk路径
	 */
	private String path;

	/**
	 * 是否领导者
	 */
	private boolean leaderServer;

	/**
	 * 服务器列表(serverId)
	 */
	private List<String> list;
	/**
	 * 当前节点序号
	 */
	private String serverNode;

	private List<ServerChangeListener> listeners = new ArrayList<ServerChangeListener>();

	/**
	 * 添加当前服务器端口
	 */
	private void addPort() {
		String s = System.getProperty(SERVER_PORT);
		if (s != null) {
			ip = ip + ":" + s;
		}
	}

	@Override
	protected void onDisconnected() {
		synchronized (this) {
			leaderServer = false;
		}
	}

	private boolean bregister = false;

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	protected void onConnected() {
		// if (ip == null) {
		if (!bregister) {
			createPath(SERVER);
			if (group != null) {
				path = getPath(SERVER, group);
				createPath(path);
			} else {
				path = SERVER;
			}
			path = path + SEPARATOR;
			// ip = getLocalAddress();
			// addPort();
			bregister = true;
		}
		addListener();
	}

	/**
	 * 注册zk子节点侦听
	 */
	private void addListener() {
		try {
			createPath(path.substring(0, path.lastIndexOf('/')));
			String node = zk.create(path, ip.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			int k = node.lastIndexOf(SEPARATOR);
			serverNode = node.substring(k + SEPARATOR.length());
			serverId = ip + ' ' + serverNode;
			onListChange(zk.getChildren(path.substring(0, path.lastIndexOf('/')), new ServerWatcher()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设定分组（同一分组名视为一个服务器集群）
	 * 
	 * @param group
	 *            the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * 判断是否IPV4
	 * 
	 * @param address
	 * @return
	 */
	private static boolean isIPv4(String address) {
		return !address.startsWith("127.") && address.split("[\\x2E]").length == 4;
	}

	/**
	 * 获得一个本机的IPV4地址
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	private static String getLocalAddress() {
		List<String> ips = listIps();
		if (ips.size() > 0) {
			for (String s : ips) {
				if (s.startsWith("192.168.") || s.startsWith("172.") || s.startsWith("10.")) {
					return s;
				}
			}
			return ips.get(0);
		}
		return "127.0.0.1";
	}

	/**
	 * 列出IP地址
	 * 
	 * @return
	 */
	private static List<String> listIps() {
		List<String> list = new ArrayList<String>();
		try {
			String ip = null;
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {// 循环所有地址以得到一个外网地址
				NetworkInterface nif = netInterfaces.nextElement();
				Enumeration<InetAddress> iparray = nif.getInetAddresses();
				while (iparray.hasMoreElements()) {
					ip = iparray.nextElement().getHostAddress();
					if (isIPv4(ip)) {
						list.add(ip);
					}
				}
			}
		} catch (Exception e) {
		}
		return list;
	}

	@Override
	public String getServerId() {
		return serverId;
	}

	@Override
	public boolean isLeaderServer() {
		return leaderServer;
	}

	@Override
	public List<String> list() {
		return list(this.group);
	}

	@Override
	public List<String> list(String group) {
		String path = getPath(SERVER, group);
		try {
			List<String> list = zk.getChildren(path, false);
			Collections.sort(list);
			List<String> rs = new ArrayList<String>();
			for (String s : list) {
				rs.add(getData(path + '/' + s, null));
			}
			return rs;
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	/**
	 * 处理所有服务器节点
	 * 
	 * @param list
	 *            节点ID
	 */
	private void onListChange(List<String> list) {
		Collections.sort(list);
		List<String> rs = new ArrayList<String>();
		int len = SEPARATOR.length() - 1;
		String seq = SEPARATOR.substring(1);
		for (String s : list) {
			if (s.startsWith(seq)) {
				rs.add(s.substring(len));
			}
		}
		List<String> old = this.list;
		synchronized (this) {// 判断当前应用服务器是否同一集群第一台服务器
			if (rs.size() > 0 && rs.get(0).equals(serverNode)) {
				if (!leaderServer) {
					this.leaderServer = true;
					System.out.println("Current server is leader of group '" + (group == null ? "default" : group) + "' .");
				}
			} else if (leaderServer) {
				leaderServer = false;
				System.out.println("Current server is not leader of group '" + (group == null ? "default" : group) + "' .");
			}
			this.list = rs;
		}
		onListChange(this.list, old == null ? new ArrayList<String>() : old);
	}

	/**
	 * 服务器列表发生改变
	 * 
	 * @param list
	 * @param old
	 */
	private void onListChange(List<String> list, List<String> old) {
		int k = list.size();
		if (k == old.size()) {
			for (int i = 0; i < k; i++) {
				if (!list.get(i).equals(old.get(i))) {
					break;
				} else if (i == k - 1) {
					return;
				}
			}
		}
		for (ServerChangeListener l : listeners) {
			try {
				l.onChange(list, old);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void addListener(ServerChangeListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	/**
	 * 对子节点的侦听
	 * 
	 * @author lj
	 * 
	 */
	private class ServerWatcher implements Watcher {

		@Override
		public void process(WatchedEvent event) {
			switch (event.getType()) {
			case NodeChildrenChanged:
				List<String> rs = null;
				String p = path.substring(0, path.lastIndexOf('/'));
				try {
					rs = zk.getChildren(p, this);
				} catch (Exception e) {
					return;
				}
				onListChange(rs);
				break;
			default:
				break;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// System.setProperty(SERVER_PORT, "80");
		ServerServiceImpl s = new ServerServiceImpl();
		s.setCluster("192.168.8.11");
		s.group = "test";
		s.addListener(new ServerChangeListener() {

			@Override
			public void onChange(List<String> newServers, List<String> oldServers) throws Exception {
				System.out.println("new :" + newServers);
				System.out.println("old :" + oldServers);
			}

		});
		s.init();
		System.out.println(s.isLeaderServer());
		System.out.println(s.getServerId());
		System.out.println(s.list());

		System.out.println(s.list(s.group));

		Thread.sleep(2000);
		// s.restart();
		Thread.sleep(2000);

		System.out.println(s.list(s.group));

	}
}
