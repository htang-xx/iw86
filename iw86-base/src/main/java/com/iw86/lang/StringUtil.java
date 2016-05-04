/**
 * 
 */
package com.iw86.lang;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.iw86.base.Constant;

/**
 * 字符串常用处理
 * @author tanghuang
 */
public class StringUtil {
	private static final String HexString = "0123456789ABCDEF"; //十六进制字符串
	
	/**
	 * 判断一个字符串是否空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
	/** 
	 * 判断指定的字符串是否是空串
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		if (isEmpty(str))
			return true;
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断一个字符串是否为整数(包括负数)
	 * @param str
	 * @return
	 */
	public static boolean isInt(String str) {
		return str.matches("^-?\\d+");
	}
	
	/**
	 * 删掉字符串中所有的空格，包括全角空格
	 * @param str
	 * @return
	 */
	public static String trim(String str){
		if(isEmpty(str)) return Constant.EMPTY;
		return str.replaceAll(" ", Constant.EMPTY).replaceAll("　", Constant.EMPTY);
	}
	
	/**
	 * 判断2个字符串是否相等 (字符串equals方法，主要供velocity使用)
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean equals(String str1, String str2) {
		if(str1==null){
			if(str2==null) return true;
			else return false;
		}
		return str1.equals(str2);
	}
	
	/**
	 * 取数组的元素（可弥补velocity中不能取数组指定元素的问题）
	 * @param objs
	 * @param n
	 * @return
	 */
	public static String getOne(String[] objs, int n){
		if(objs==null || objs.length<=n) return Constant.EMPTY;
		return objs[n];
	}
	
	/**
	 * 将数组合并为字符串
	 * @param separator 分隔符
	 * @param args 待合并的数组
	 * @return 合并后的字符串
	 */
	public static String arr2str(String separator,boolean skipNull,Object[] args) {
		if(separator==null) separator = Constant.EMPTY;
		StringBuilder buf = new StringBuilder();
		for (Object arg : args) {
			if(arg!=null){
				if(buf.length()>0) buf.append(separator);
				buf.append(arg.toString());
			}else if(!skipNull){
				if(buf.length()>0) buf.append(separator);
				buf.append(Constant.NULL);
			}
		}
		return buf.toString();
	}

	/**
	 * 字符串拼接
	 * @param separator 分隔符
	 * @param args 待连接的字符串
	 * @return
	 */
	public static String join(String separator,boolean skipNull,Object... args) {
		if(separator==null) separator = Constant.EMPTY;
		StringBuilder buf = new StringBuilder();
		for (Object arg : args) {
			if(arg!=null){
				if(buf.length()>0) buf.append(separator);
				buf.append(arg.toString());
			}else if(!skipNull){
				if(buf.length()>0) buf.append(separator);
				buf.append(Constant.NULL);
			}
		}
		return buf.toString();
	}
	
	/**
	 * 字符串拼接，join(null,true,args)
	 * @param args
	 * @return
	 */
	public static String str(Object... args) {
		return join(null,true,args);
	}
	
	/**
	 * 字符串byte长度截取
	 * @param len 需要显示的长度
	 * (<font color="red">注意：长度是以byte为单位的，一个汉字是2个byte</font>)
	 * @param symbol 用于表示省略的信息的字符，如“...”,“>>>”等。
	 * @return 返回处理后的字符串
	 */
	public static String sub(String str, int len, String symbol) {
		if (isEmpty(str)) return Constant.EMPTY;
		try {
			int counterOfDoubleByte = 0;
			byte[] b = str.getBytes(Constant.GBK);
			if (b.length <= len) return str;
			
			for (int i = 0; i < len; i++) {
				// 通过判断字符的类型来进行截取
				if (b[i] < 0) counterOfDoubleByte++;
			}
			if (counterOfDoubleByte % 2 == 0)
				str = new String(b, 0, len, Constant.GBK) + symbol;
			else
				str = new String(b, 0, len - 1, Constant.GBK) + symbol;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 改进JDK subString
	 * index从0开始计算，最后一个字符为-1
	 * @param str
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	public static String subString(String str, int fromIndex, int toIndex) {
		if (isEmpty(str)) return Constant.EMPTY;
		int len = str.length();
		if (fromIndex < 0) {
			fromIndex = len + fromIndex;
			if (toIndex == 0) toIndex = len;
		}
		if (toIndex < 0) toIndex = len + toIndex;
		if (toIndex < fromIndex) {
			int tmp = fromIndex;
			fromIndex = toIndex;
			toIndex = tmp;
		}
		if (fromIndex == toIndex) return Constant.EMPTY;
		char[] strArray = str.toCharArray();
		char[] newStrArray = Arrays.copyOfRange(strArray, fromIndex, toIndex);
		return new String(newStrArray);
	}

	
	/**
	 * 把字符型数字转换成整型.
	 * @param str 字符型数字
	 * @return int 返回整型值。如果不能转换则返回默认值defaultValue.
	 */
	public static int getInt(String str, int defaultValue) {
		return Conver.toInt(str, defaultValue);
	}
	
	/**
	 * 判断一个字符串是否为纯数字
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		return str.matches("\\d+");
	}
	
	/**
	 * 针对字符串为NULL的处理
	 * @param str
	 * @return
	 */
	public static String notNull(String str) {
		if (str == null)  str = Constant.EMPTY;
		return str;
	}
	
	/**
	 * 取值
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static String gets(String str,String defaultValue){
		if(isEmpty(str)) return defaultValue;
		return str;
	}
	
	/**
	 * 使HTML的标签失去作用
	 * @param input 被操作的字符串
	 * @return String
	 */
	public static final String escapeHTMLTag(String input) {
		if (input == null)
			return "";
		input = input.trim().replaceAll("&", "&amp;");
		input = input.replaceAll("<", "&lt;");
		input = input.replaceAll(">", "&gt;");
		input = input.replaceAll("\n", "<br>");
		input = input.replaceAll("'", "&#39;");
		input = input.replaceAll("\"", "&quot;");
		input = input.replaceAll("\\\\", "&#92;");
		return input;
	}
	
	/**
	 * 去掉html代码
	 * @param html
	 * @return
	 */
	public static String trimHtml(String html) {
		if (isEmpty(html)) return Constant.EMPTY;
		return html.replaceAll("<.*?>", Constant.EMPTY);
	}
	
	/**
	 * 去掉空格、换行、制表符
	 * @param input
	 * @return
	 */
	public static String trimSpace(String input){
		if (isEmpty(input)) return Constant.EMPTY;
		return input.replaceAll("\\s*|\t|\r|\n", Constant.EMPTY);
	}
	
	/**
	 * 通配符（*和?）匹配是否包含
	 * @param regx
	 * @param str
	 * @return
	 */
	public static boolean isMatcher(String regx,String str){
		if(regx.indexOf(str)!=-1) return true;
		if(StringUtil.isEmpty(str) || StringUtil.isEmpty(regx)) return false;
		regx = regx.replace('.', '#').replaceAll("#", "\\\\.");
		regx = regx.replace('*', '#').replaceAll("#", ".*");
		regx = regx.replace('?', '#').replaceAll("#", ".?");
		regx = "^" + regx + "$";
		return Validator.isMatch(regx, str);
	}
	
	/**
	 * 是否匹配上
	 * @param alist
	 * @param str
	 * @return
	 */
	public static boolean hasMatcher(List<String> alist,String str){
		if(alist!=null){
			for(String regx:alist){
				if(isMatcher(regx,str)) return true;
			}
		}
		return false;
	}
	
	/**
	 * 格式化文本
	 * @param template 文本模板，被替换的部分用 {key} 表示
	 * @param map 参数值对
	 * @return 格式化后的文本
	 */
	public static String format(String template, Map<?, ?> map) {
		if (null == map || map.isEmpty()) {
			return template;
		}
		for (Entry<?, ?> entry : map.entrySet()) {
			template = template.replace("{" + entry.getKey() + "}", entry.getValue().toString());
		}
		return template;
	}
	
	/**
	 * urlencode
	 * @param str
	 * @param coding
	 * @return
	 */
	public static String encode(String str,String coding) {
		if (isEmpty(str))  return Constant.EMPTY;
		try {
			return java.net.URLEncoder.encode(str, coding);
		} catch (Exception e) {
			return str;
		}
	}
	
	/**
	 * utf8 urlencode
	 * @param str
	 * @return
	 */
	public static String encode(String str) {
		return encode(str,Constant.UTF_8);
	}
	
	/**
	 * 字节数组转哈希(16进制)
	 * @param msg_byte
	 * @return
	 */
	public static String bytesToHexStr(byte[] msg_byte) {
		StringBuilder sb = new StringBuilder(msg_byte.length * 2);
		for (int i = 0; i < msg_byte.length; i++) {
			sb.append(HexString.charAt(0xf & msg_byte[i] >> 4)).append(
					HexString.charAt(msg_byte[i] & 0xf));
		}
		return sb.toString();
	}
	
	/**
	 * 16进制哈希转字节数组
	 * @param s
	 * @return
	 */
	public static byte[] hexStrToBytes(String s) {
		try {
			int i = s.length();
			byte result[] = new byte[i >> 1];
			for (int k = 0; k < i;)
				result[k >> 1] = (byte) (Integer.parseInt(s.substring(k, k += 2), 16));
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
