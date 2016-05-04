/**
 * 
 */
package com.iw86.lang;

import java.util.regex.Pattern;

/**
 * 字符串检测
 * @author tanghuang
 */
public class Validator {

	/** 英文字母 、数字和下划线 */
	public final static Pattern GENERAL = Pattern.compile("^\\w+$");
	/** 数字 */
	public final static Pattern NUMBER = Pattern.compile("\\d+");
	/** 汉字 */
	public final static Pattern CHINESS = Pattern.compile("^[\u4E00-\u9FFF]+$");
	/** 分组 */
	public final static Pattern GROUP_VAR = Pattern.compile("\\$(\\d+)");
	/** IP v4 */
	public final static Pattern IPV4 = Pattern.compile("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
	/** 货币 */
	public final static Pattern MONEY = Pattern.compile("^(\\d+(?:\\.\\d+)?)$");
	/** 邮件 */
	public final static Pattern EMAIL = Pattern.compile("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+");
	/** 移动电话 */
	public final static Pattern MOBILE = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
	/** 身份证号码 */
	public final static Pattern CITIZEN_ID = Pattern.compile("[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}(\\d|X|x)");
	/** 邮编 */
	public final static Pattern ZIP_CODE = Pattern.compile("\\d{6}");
	/** 生日 */
	public final static Pattern BIRTHDAY = Pattern.compile("(\\d{4})(/|-|\\.)(\\d{1,2})(/|-|\\.)(\\d{1,2})日?$");
	/** URL */
	public final static Pattern URL = Pattern.compile("(https://|http://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?");
	/** 中文字、英文字母、数字和下划线 */
	public final static Pattern GENERAL_WITH_CHINESE = Pattern.compile("^[\\u0391-\\uFFE5\\w]+$");
	/** UUID */
	public final static Pattern UUID = Pattern.compile("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$");

	/**
	 * 验证是否相等，当两值都为null返回true
	 * @param obj1 对象1
	 * @param obj2 对象2
	 * @return 当两值都为null或相等返回true
	 */
	public static boolean equals(Object obj1, Object obj2) {
		return (obj1 != null) ? (obj1.equals(obj2)) : (obj2 == null);
	}
	
	/**
	 * 给定内容是否匹配正则
	 * @param regex 正则
	 * @param content 内容
	 * @return 正则为null或者""则不检查，返回true，内容为null返回false
	 */
	public static boolean isMatch(String regex, String content) {
		if(content == null) return false; //提供null的字符串为不匹配
		if(StringUtil.isEmpty(regex)) return true; //正则不存在则为全匹配
		return Pattern.matches(regex, content);
	}

	/**
	 * 给定内容是否匹配正则
	 * @param pattern 模式  
	 * @param content 内容
	 * @return 正则为null或者""则不检查，返回true，内容为null返回false
	 */
	public static boolean isMatch(Pattern pattern, String content) {
		if(content == null || pattern == null) return false; //提供null的字符串为不匹配
		return pattern.matcher(content).matches();
	}

	/**
	 * 验证是否为英文字母 、数字和下划线
	 * @param value 值
	 * @return 是否为英文字母 、数字和下划线
	 */
	public static boolean isGeneral(String value) {
		return isMatch(GENERAL, value);
	}

	/**
	 * 验证是否为给定长度范围的英文字母 、数字和下划线
	 * @param value 值
	 * @param min 最小长度，负数自动识别为0
	 * @param max 最大长度，0或负数表示不限制最大长度
	 * @return 是否为给定长度范围的英文字母 、数字和下划线
	 */
	public static boolean isGeneral(String value, int min, int max) {
		String reg = "^\\w{" + min + "," + max + "}$";
		if (min < 0) {
			min = 0;
		}
		if (max <= 0) {
			reg = "^\\w{" + min + ",}$";
		}
		return isMatch(reg, value);
	}

	/**
	 * 验证是否为给定最小长度的英文字母 、数字和下划线
	 * @param value 值
	 * @param min 最小长度，负数自动识别为0
	 * @return 是否为给定最小长度的英文字母 、数字和下划线
	 */
	public static boolean isGeneral(String value, int min) {
		return isGeneral(value, min, 0);
	}

	/**
	 * 验证该字符串是否是数字
	 * @param value 字符串内容
	 * @return 是否是数字
	 */
	public static boolean isNumber(String value) {
		if (StringUtil.isBlank(value)) {
			return false;
		}
		return isMatch(NUMBER, value);
	}

	/**
	 * 验证是否为货币
	 * @param value 值
	 * @return 是否为货币
	 */
	public static boolean isMoney(String value) {
		return isMatch(MONEY, value);
	}

	/**
	 * 验证是否为邮政编码（中国）
	 * @param value 值
	 * @return 是否为邮政编码（中国）
	 */
	public static boolean isZipCode(String value) {
		return isMatch(ZIP_CODE, value);
	}

	/**
	 * 验证是否为可用邮箱地址
	 * @param value 值
	 * @return 否为可用邮箱地址
	 */
	public static boolean isEmail(String value) {
		return isMatch(EMAIL, value);
	}

	/**
	 * 验证是否为手机号码（中国）
	 * @param value 值
	 * @return 是否为手机号码（中国）
	 */
	public static boolean isMobile(String value) {
		return isMatch(MOBILE, value);
	}

	/**
	 * 验证是否为身份证号码（18位中国）<br>
	 * 出生日期只支持到到2999年
	 * @param value 值
	 * @return 是否为身份证号码（18位中国）
	 */
	public static boolean isCitizenId(String value) {
		return isMatch(CITIZEN_ID, value);
	}

	/**
	 * 验证是否为生日
	 * @param value 值
	 * @return 是否为生日
	 */
	public static boolean isBirthday(String value) {
		if(StringUtil.isEmpty(value) || value.length()!=10) return false;
		if (isMatch(BIRTHDAY, value)) {
			int year = Integer.parseInt(value.substring(0, 4));
			int month = Integer.parseInt(value.substring(5, 7));
			int day = Integer.parseInt(value.substring(8, 10));

			if (month < 1 || month > 12) {
				return false;
			}
			if (day < 1 || day > 31) {
				return false;
			}
			if ((month == 4 || month == 6 || month == 9 || month == 11) && day == 31) {
				return false;
			}
			if (month == 2) {
				boolean isleap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
				if (day > 29 || (day == 29 && !isleap)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 验证是否为IPV4地址
	 * @param value 值
	 * @return 是否为IPV4地址
	 */
	public static boolean isIpv4(String value) {
		if(StringUtil.isEmpty(value) || value.length()<7 || value.length()>15) return false;
		return isMatch(IPV4, value);
	}

	/**
	 * 验证是否为URL
	 * @param value 值
	 * @return 是否为URL
	 */
	public static boolean isUrl(String value) {
		return isMatch(URL, value);
	}

	/**
	 * 验证是否为汉字
	 * @param value 值
	 * @return 是否为汉字
	 */
	public static boolean isChinese(String value) {
		return isMatch(CHINESS, value);
	}

	/**
	 * 验证是否为中文字、英文字母、数字和下划线
	 * @param value 值
	 * @return 是否为中文字、英文字母、数字和下划线
	 */
	public static boolean isGeneralWithChinese(String value) {
		return isMatch(GENERAL_WITH_CHINESE, value);
	}

	/**
	 * 验证是否为UUID
	 * @param value 值
	 * @return 是否为UUID
	 */
	public static boolean isUUID(String value) {
		return isMatch(UUID, value);
	}

}
