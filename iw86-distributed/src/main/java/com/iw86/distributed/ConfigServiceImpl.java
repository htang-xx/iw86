package com.iw86.distributed;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.stereotype.Service;

/**
 * 配置服务实现. <br>
 * <pre>
 * 初始化：
 * 1、可以不需要参数
 * 2、重写方法{@link #initConfig(Map)}
 * 3、配置参数path和/或config，分别完成默认配置路径及配置参数的初始化.
 *   path参数对于可能存在冲突的不同配置，必须保存唯一.
 * 4、为保证配置的同步（如与数据库一致），可以指定一个{@link ConfigLoader}，当从配置加载器获得的数据与zk中不同时，
 * 以配置加载器中获得的数据为准（此配置加载器仅需要加载受到侦听的数据）。
 * 更新配置：
 * 1、{@link #save(String, String)}保存一项配置
 * 2、{@link #save(Map)}同时保存多项配置
 * 配置变化侦听：
 * 通过方法{@link #addListener(ConfigChangeListener, String...)}进行侦听，无论
 * 启动或有变化时，都会调用{@link ConfigChangeListener#configChanged(String, String)}方法.
 * </pre>
 * @author tanghuang
 * 
 */
@Service
public class ConfigServiceImpl extends AbstractDistributedService implements ConfigService {

	/**
	 * 默认配置路径
	 */
	public static final String CONFIG = "/Config";

	private static final String NULL_STRING = null;

	/**
	 * 配置路径
	 */
	private String path;

	/**
	 * 配置初始化参数
	 */
	private Map<String, String> config;

	/**
	 * 配置加载器
	 */
	private ConfigLoader configLoader;

	private Map<String, String> values = new HashMap<String, String>();

	private ConfigChangeListener defaultListener;

	/**
	 * @param configLoader
	 *            the configLoader to set
	 */
	public void setConfigLoader(ConfigLoader configLoader) {
		this.configLoader = configLoader;
	}

	/**
	 * 设置默认路径
	 * 
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 设置默认参数（此部分配置数据优先于方法{@link #initConfig(Map)}
	 * 
	 * @param config
	 *            the config to set
	 */
	public void setConfig(Map<String, String> config) {
		this.config = config;
	}

	/**
	 * 初始化路径及数据
	 */
	@Override
	protected final void onConnected() {
		if (defaultListener == null) {
			defaultListener = new DefaultConfigChangeListener();
			createPath(CONFIG);
			if (path != null) {
				path = getPath(CONFIG, path);
				createPath(path);
			} else {
				path = CONFIG;
			}
			Map<String, String> map = new HashMap<String, String>();
			initConfig(map);
			if (config != null) {
				map.putAll(config);
			}
			if (map.size() > 0) {// 保存初始化数据
				for (String key : map.keySet()) {
					try {
						setNotExists(key, map.get(key));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			Set<String> ns = values.keySet();
			this.addListen(ns.toArray(new String[ns.size()]));
		}
	}

	/**
	 * 初始化配置
	 * 
	 * @param config
	 *            配置数据需要放入此映射中
	 */
	protected void initConfig(Map<String, String> config) {
	}

	@Override
	public void save(String name, String value) {
		save(name, value, false);
	}

	@Override
	public void save(String name, String value, boolean onlyExists) {
		try {
			setData(getPath(path, name), value, !onlyExists);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void save(Map<String, String> values) {
		save(values, false);
	}

	public void save(Map<String, String> values, boolean onlyExists) {
		try {
			String value = null;
			boolean create = !onlyExists;
			for (String key : values.keySet()) {
				value = values.get(key);
				setData(getPath(path, key), value, create);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addListener(ConfigChangeListener listener, String... name) {
		Watcher watcher = new WatcherImpl(listener);
		for (String n : name) {
			String rs = addListener(getPath(path, n), watcher);
			if (configLoader != null) {
				String ov = configLoader.load(n);
				if ((rs == null && ov != null) || (rs != null && !rs.equals(ov))) {
					save(n, ov);
				} else {
					listener.configChanged(n, ov == null ? rs : ov);
				}
			} else if (rs != null) {
				listener.configChanged(n, rs);
			}
		}
	}

	/**
	 * 配置变化的侦听实现
	 * 
	 * @author lj
	 * 
	 */
	private class WatcherImpl implements Watcher {

		private ConfigChangeListener listener;

		public WatcherImpl(ConfigChangeListener listener) {
			this.listener = listener;
		}

		@Override
		public void process(WatchedEvent event) {
			switch (event.getType()) {
			case NodeCreated:
			case NodeDataChanged:
				listener.configChanged(getConfigName(event), getData(event.getPath(), this));
				break;
			case NodeDeleted:
				listener.configChanged(getConfigName(event), null);
				addListener(event.getPath(), this);
				break;
			default:
				break;
			}
		}

		private String getConfigName(WatchedEvent event) {
			String p = event.getPath();
			int k = p.lastIndexOf('/');
			return p.substring(k + 1);
		}

	}

	public static void main(String[] args) throws Exception {
		final ConfigServiceImpl c = new ConfigServiceImpl();
		c.setCluster("192.168.8.11");
		c.init();
		Thread.sleep(1000);
		c.setConfigLoader(new ConfigLoader() {

			@Override
			public String load(String name) {
				return "init value";
			}

		});
		c.addListener(new ConfigChangeListener() {

			@Override
			public void configChanged(String name, String value) {
				System.out.println(name + " : " + value);
			}

		}, "aaa");
		// Thread.sleep(30000);
		c.save("aaa", "中主");
		c.save("aaa", "222");
		c.save("aaa", "333");
		c.save("aaa", "444");
		c.save("aaa", null);
		c.save("aaa", "555");
		c.save("aaa", "666");
		c.save("aaa", "777");
		c.save("aaa", "888");
		c.addListen("bbb");
		Thread.sleep(2000);
		// c.restart();
		Thread t = new Thread() {
			public void run() {
				for (int i = 0; i < 10; i++) {
					System.out.println("bbb: " + c.get("bbb"));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
		Thread.sleep(2000);
		c.save("bbb", "111", false);
		Thread.sleep(2000);
		c.save("bbb", "222");
	}

	@Override
	public String get(String name) {
		return values.get(name);
	}

	@Override
	public Integer getInt(String name) {
		String rs = values.get(name);
		return rs == null ? null : Integer.valueOf(rs);
	}

	@Override
	public void addListen(String... name) {
		synchronized (values) {
			for (String n : name) {
				if (!values.containsKey(n)) {
					values.put(n, NULL_STRING);
				}
			}
		}
		addListener(defaultListener, name);
	}

	/**
	 * 默认侦听器实现
	 * 
	 * @author lj
	 * 
	 */
	private class DefaultConfigChangeListener implements ConfigChangeListener {

		@Override
		public void configChanged(String name, String value) {
			values.put(name, value);
		}

	}

}
