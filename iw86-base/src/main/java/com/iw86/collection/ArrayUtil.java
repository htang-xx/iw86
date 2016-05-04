package com.iw86.collection;

import java.lang.reflect.Array;

import com.iw86.base.Constant;

/**
 * 数组操作工具类
 * @author tanghuang
 *
 */
@SuppressWarnings({"unchecked"})
public class ArrayUtil {

	/**
	 * 生成数组<br>
	 * 格式为：perfix+i<br> 
	 * 例如：prefix传入leopard,数量传入3;<br> 
	 * 返回的数组为：["leopard0","leopard1","leopard2"]
	 * 
	 * @param prefix 数组内容前缀
	 * @param count 数量
	 * @return 生成的数组
	 */
	public static String[] make(String prefix, int count) {
		String[] members = new String[count];
		for (int i = 0; i < members.length; i++) {
			members[i] = prefix + i;
		}
		return members;
	}
	
	/**
	 * 查找元素在数组中的位置
	 * @param array
	 * @param value
	 * @return
	 */
	public static int indexOf(Object[] array, Object value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value || array[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 检查数组是否含有某元素
	 * @param array
	 * @param value
	 * @return
	 */
	public static boolean contains(Object[] array, Object value) {
		return indexOf(array, value) != -1;
	}
	
	/**
	 * @param array
	 * @return
	 */
	public static String toString(Object[] array) {
		if (array == null) return Constant.EMPTY;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}
	
	/**
	 * 组合元素到数组
	 * @param elements
	 * @return
	 */
	public static <T> T[] array(T... elements) {
		return elements;
	}
	
	/**
	 * @param componentType
	 * @param arrays
	 * @return
	 */
	public static <T> T[] join(Class<T> componentType, T[][] arrays) {
		if (arrays.length == 1) {
			return arrays[0];
		}
		int length = 0;
		for (T[] array : arrays) {
			length += array.length;
		}
		T[] result = (T[]) Array.newInstance(componentType, length);

		length = 0;
		for (T[] array : arrays) {
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}
	
	/**
	 * @param arrays
	 * @return
	 */
	public static <T> T[] join(T[]... arrays) {
		Class<T> componentType = (Class<T>) arrays.getClass().getComponentType().getComponentType();
		return join(componentType, arrays);
	}
	
	/**
	 * @param buffer
	 * @param newSize
	 * @return
	 */
	public static <T> T[] resize(T[] buffer, int newSize) {
		Class<T> componentType = (Class<T>) buffer.getClass().getComponentType();
		T[] temp = (T[]) Array.newInstance(componentType, newSize);
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}
	
	/**
	 * 往数组里追加元素
	 * @param buffer
	 * @param newElement
	 * @return
	 */
	public static <T> T[] append(T[] buffer, T newElement) {
		T[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}
	
	/**
	 * 移除数组中的元素
	 * @param buffer
	 * @param offset
	 * @param length
	 * @param componentType
	 * @return
	 */
	public static <T> T[] remove(T[] buffer, int offset, int length, Class<T> componentType) {
		int len2 = buffer.length - length;
		T[] temp = (T[]) Array.newInstance(componentType, len2);
		System.arraycopy(buffer, 0, temp, 0, offset);
		System.arraycopy(buffer, offset + length, temp, offset, len2 - offset);
		return temp;
	}
	
	public static <T> T[] remove(T[] buffer, int offset, int length) {
		Class<T> componentType = (Class<T>) buffer.getClass().getComponentType();
		return remove(buffer, offset, length, componentType);
	}
	
	/**
	 * 截取数组
	 * @param buffer
	 * @param offset
	 * @param length
	 * @param componentType
	 * @return
	 */
	public static <T> T[] subarray(T[] buffer, int offset, int length, Class<T> componentType) {
		T[] temp = (T[]) Array.newInstance(componentType, length);
		System.arraycopy(buffer, offset, temp, 0, length);
		return temp;
	}
	
	public static <T> T[] subarray(T[] buffer, int offset, int length) {
		Class<T> componentType = (Class<T>) buffer.getClass().getComponentType();
		return subarray(buffer, offset, length, componentType);
	}
	
	/**
	 * 插入元素
	 * @param dest
	 * @param src
	 * @param offset
	 * @param componentType
	 * @return
	 */
	public static <T> T[] insert(T[] dest, T[] src, int offset, Class<?> componentType) {
		T[] temp = (T[]) Array.newInstance(componentType, dest.length + src.length);
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}
	
	public static <T> T[] insert(T[] dest, T[] src, int offset) {
		Class<T> componentType = (Class<T>) dest.getClass().getComponentType();
		return insert(dest, src, offset, componentType);
	}
	
	public static <T> T[] insert(T[] dest, T src, int offset, Class<?> componentType) {
		T[] temp = (T[]) Array.newInstance(componentType, dest.length + 1);
		System.arraycopy(dest, 0, temp, 0, offset);
		temp[offset] = src;
		System.arraycopy(dest, offset, temp, offset + 1, dest.length - offset);
		return temp;
	}
	
	public static <T> T[] insert(T[] dest, T src, int offset) {
		Class<T> componentType = (Class<T>) dest.getClass().getComponentType();
		return insert(dest, src, offset, componentType);
	}
	
}
