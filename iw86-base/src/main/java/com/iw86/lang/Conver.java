/**
 * 
 */
package com.iw86.lang;

import java.math.BigDecimal;
import java.util.Set;

/**
 * 类型转换器
 * @author tanghuang
 */
public class Conver {
	/**
	 * 转换为字符串<br>
	 * 如果给定的值为null，或者转换失败，返回默认值
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static String toStr(Object value, String defaultValue) {
		if (null == value) return defaultValue;
		if (value instanceof String) return (String) value;
		return value.toString();
	}

	/**
	 * 转换为int<br>
	 * 如果给定的值为空，或者转换失败，返回默认值
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Integer toInt(Object value, Integer defaultValue) {
		if (value == null) return defaultValue;
		if (value instanceof Integer) return (Integer) value;
		final String valueStr = value.toString();
		if (StringUtil.isBlank(valueStr)) return defaultValue;
		try {
			return Integer.parseInt(valueStr);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转换为long<br>
	 * 如果给定的值为空，或者转换失败，返回默认值
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Long toLong(Object value, Long defaultValue) {
		if (value == null) return defaultValue;
		if (value instanceof Long)  return (Long) value;
		final String valueStr = value.toString();
		if (StringUtil.isBlank(valueStr)) return defaultValue;
		try {
			return new BigDecimal(valueStr).longValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转换为double<br>
	 * 如果给定的值为空，或者转换失败，返回默认值
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Double toDouble(Object value, Double defaultValue) {
		if (value == null) return defaultValue;
		if (value instanceof Double) return (Double) value;
		final String valueStr = value.toString();
		if (StringUtil.isBlank(valueStr)) return defaultValue;
		try {
			return new BigDecimal(valueStr).doubleValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转换为Float<br>
	 * 如果给定的值为空，或者转换失败，返回默认值
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Float toFloat(Object value, Float defaultValue) {
		if (value == null) return defaultValue;
		if (value instanceof Float) return (Float) value;
		final String valueStr = value.toString();
		if (StringUtil.isBlank(valueStr)) return defaultValue;
		try {
			return Float.parseFloat(valueStr);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转换为boolean<br>
	 * 如果给定的值为空，或者转换失败，返回默认值
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Boolean toBool(Object value, Boolean defaultValue) {
		if (value == null) return defaultValue;
		if (value instanceof Boolean) return (Boolean) value;
		final String valueStr = value.toString();
		if (StringUtil.isBlank(valueStr)) return defaultValue;
		try {
			return Boolean.parseBoolean(valueStr);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	// -----------------------------------------------------------------------
	// 全角半角转换
	/**
	 * 半角转全角
	 * @param input String.
	 * @return 全角字符串.
	 */
	public static String toSBC(String input) {
		return toSBC(input, null);
	}

	/**
	 * 半角转全角
	 * @param input String
	 * @param notConvertSet 不替换的字符集合
	 * @return 全角字符串.
	 */
	public static String toSBC(String input, Set<Character> notConvertSet) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (null != notConvertSet && notConvertSet.contains(c[i])) {
				// 跳过不替换的字符
				continue;
			}
			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] < '\177') {
				c[i] = (char) (c[i] + 65248);
			}
		}
		return new String(c);
	}

	/**
	 * 全角转半角
	 * @param input
	 * @return 半角字符串
	 */
	public static String toDBC(String input) {
		return toDBC(input, null);
	}

	/**
	 * 替换全角为半角
	 * @param text 文本
	 * @param notConvertSet 不替换的字符集合
	 * @return 替换后的字符
	 */
	public static String toDBC(String text, Set<Character> notConvertSet) {
		char c[] = text.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (null != notConvertSet && notConvertSet.contains(c[i])) {
				// 跳过不替换的字符
				continue;
			}
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);
			}
		}
		String returnString = new String(c);
		return returnString;
	}
	
	/**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1,double v2,int scale){
        if(scale<0) return 0;
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
