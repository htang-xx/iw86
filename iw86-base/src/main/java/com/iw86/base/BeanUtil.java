/**
 * 
 */
package com.iw86.base;

import java.lang.reflect.Field;

/**
 * @author tanghuang
 *
 */
public class BeanUtil {
	
	/**
	 * 判断一个对象是否为null
	 * 
	 * @param bean 对象
	 * @return 如果对象为null，返回true
	 */
	public static boolean isNull(Object bean) {
		return bean == null;
	}

	/**
	 * 判断一个对象是否非空
	 * 
	 * @param bean 对象
	 * @return 如果对象非空，返回true
	 */
	public static boolean isNotNull(Object bean) {
		return bean != null;
	}
	
	/**
	 * 获取对象指定字段的Field对象
	 * 
	 * @param bean 对象
	 * @param fieldName 字段名
	 * @return 字段对应的Field对象
	 */
	public static Field getField(Object bean, String fieldName) {
		if(bean == null) return null;
		Class<?> clazz = bean.getClass();
		while (true) {
			try {
				return clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				if (clazz.getSuperclass() == null) {
					String className = bean.getClass().getSimpleName();
					throw new IW86RuntimeException("clazz:" + className + " " + e.getMessage(), e);
				}
				clazz = clazz.getSuperclass();
			}
		}
	}
	
	/**
	 * 获取一个对象指定字段上的值
	 * 
	 * @param model 对象
	 * @param fieldName 指定的字段
	 * @return 字段对应的值，若对象为空则返回null
	 */
	public static Object getProperty(Object model, String fieldName) {
		if (model == null) {
			return null;
		}

		Field field = getField(model, fieldName);
		field.setAccessible(true);
		try {
			return field.get(model);
		} catch (Exception e) {
			throw new IW86RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * 设置对象指定字段的值
	 * @param bean
	 * @param fieldName
	 * @param value
	 */
	public static void setProperty(Object bean, String fieldName, Object value) {
		try {
			Field field = bean.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(bean, value);
		} catch (Exception e) {
			throw new IW86RuntimeException(e.getMessage(), e);
		}

	}

}
