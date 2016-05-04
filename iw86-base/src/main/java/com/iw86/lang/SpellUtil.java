/**
 * 
 */
package com.iw86.lang;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author tanghuang
 *
 */
public class SpellUtil {
	// 创建汉语拼音处理类
    private static HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
    static{
    	// 输出设置，大小写，音标方式
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    /**
     * 取首字母，即声母
     * @param str
     * @return
     */
    public static String getFirstSpell(String str) {
    	if(StringUtil.isEmpty(str)) return "";
    	return String.valueOf(firstSpell(str.charAt(0)));
    }
    
    /**
     * 返回字符串的首字母缩写,是汉字转化为首字母缩写,其它字符不进行转换
     * @param str String 字符串
     * @return String 转换成缩写的字符串
     */
    public static String getAcronym(String str) {
    	if(StringUtil.isEmpty(str)) return "";
    	char[] chars = str.toCharArray();
    	StringBuilder retuBuf = new StringBuilder();
        for (char c:chars) {
        	retuBuf.append(firstSpell(c));
        }
        return retuBuf.toString();
    }
	/**
     * 将字符串转换成拼音数组
     * @param src
     * @return
     */
    public static String[] strToSpell(String src) {
        return strToSpell(src, false, null);
    }

    /**
     * 将字符串转换成拼音数组
     * @param src
     * @param isPolyphone 是否查出多音字的所有拼音
     * @param separator 多音字拼音之间的分隔符
     * @return
     */
    public static String[] strToSpell(String src, boolean isPolyphone,
            String separator) {
        // 判断字符串是否为空
        if ("".equals(src) || null == src) {
            return null;
        }
        char[] srcChar = src.toCharArray();
        int srcCount = srcChar.length;
        String[] srcStr = new String[srcCount];

        for (int i = 0; i < srcCount; i++) {
            srcStr[i] = charToSpell(srcChar[i], isPolyphone, separator);
        }
        return srcStr;
    }

    /** 
     * 将单个字符转换成拼音
     * @param src
     * @param isPolyphone
     * @param separator
     * @return
     */
    public static String charToSpell(char src, boolean isPolyphone,
            String separator) {
    	StringBuilder tempPinying = new StringBuilder();
        // 如果是中文
        if (src > 128) {
            try {
                // 转换得出结果
                String[] strs = PinyinHelper.toHanyuPinyinStringArray(src,defaultFormat);     
                // 是否查出多音字，默认是查出多音字的第一个字符
                if (isPolyphone && null != separator) {
                    for (int i = 0; i < strs.length; i++) {
                        tempPinying.append(strs[i]);
                        if (strs.length != (i + 1)) {
                            // 多音字之间用特殊符号间隔起来
                            tempPinying.append(separator);
                        }
                    }
                } else {
                    tempPinying.append(strs[0]);
                }

            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        } else {
            tempPinying.append(src);
        }
        return tempPinying.toString();
    }
    
    /**
     * 将单个字符取首字母
     * @param src
     * @return
     */
    public static char firstSpell(char src) {
        // 如果是中文
        if (src > 128) {
            try {
                // 转换得出结果
                String[] strs = PinyinHelper.toHanyuPinyinStringArray(src,defaultFormat);
                return strs[0].charAt(0); //取首字母
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        }
        return src;
    }
}
