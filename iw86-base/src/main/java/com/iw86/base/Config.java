/**
 * 
 */
package com.iw86.base;

import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import com.iw86.collection.Row;
import com.iw86.lang.StringUtil;

/**
 * <p>获取配置文件属性</p>
 * <p>配置文件必须在ClassPath下</p>
 * <p>除getConfig需要指定配置文件，其他均通过config.properties获得相应属性</p>
 * @author tanghuang
 */
@SuppressWarnings("rawtypes")
public class Config {
	
	private static final String CONFIG_FILE = "config.properties"; //默认config

	private static HashMap<String,Row> configMap = new HashMap<String,Row>(); //用于保存不同文件的config

	/**
	 * 获得指定配置文件的所有属性
	 * @param configname
	 * @return
	 */
	public static Row getConfig(String configname) {
		return loadConfig(configname);
	}

	/**
	 * 获取config.properties的属性
	 * @param configKey
	 * @return
	 */
	public static String gets(String configKey) {
		return gets(null, configKey, null);
	}
	
	/**
	 * 获取config.properties的属性
	 * @param configKey
	 * @return
	 */
	public static int geti(String configKey) {
		return geti(null, configKey, 0);
	}
	
	/**
	 * @param configname
	 * @param configKey
	 * @return
	 */
	public static String gets(String configname,String configKey,String defaultVal) {
		if(configname==null) configname = CONFIG_FILE;
		String value = loadConfig(configname).gets(configKey, defaultVal);
		if (!StringUtil.isEmpty(value)) {
			try {
				return new String(value.getBytes(Constant.ISO_8859_1), Constant.UTF_8); //解决中文乱码问题
			} catch (Exception e) {
				return value;
			}
		}
		return "";
	}

	/**
	 * @param configname
	 * @param configKey
	 * @return
	 */
	public static int geti(String configname, String configKey, int defaultVal) {
		if(configname==null) configname = CONFIG_FILE;
		return loadConfig(configname).getInt(configKey, defaultVal);
	}

	/**
	 * 加载(缓存)
	 */
	@SuppressWarnings("unchecked")
	private static Row loadConfig(String configname){
		Row row = configMap.get(configname);
		if(row==null && !StringUtil.isEmpty(configname)){
			try {
				Properties properties = new Properties();
				URL url = Config.class.getClassLoader().getResource(configname);
				properties.load(url.openStream());
				if (properties.size() != 0) {
					row = new Row(properties);
					configMap.put(configname, row);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(row==null) row = new Row();
		return row;
	}
}
