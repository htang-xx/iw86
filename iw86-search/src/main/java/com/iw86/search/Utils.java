package com.iw86.search;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.FSDirectory;

import com.iw86.collection.MapUtil;
import com.iw86.lang.StringUtil;
import com.iw86.other.Oscache;

/**
 * 相关处理
 * @author tanghuang
 */
public class Utils {

	public static Oscache IR_CACHE=new Oscache("ir_cache",10,7200); //IndexReader,10个，2小时
	
	public static final char illegal[] = {
		'!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', 
		'+', ',', '-', '.', '/', ':', ';', '<', '=', '>', 
		'?', '@', '\\', '^', '_', '`', '~', '|', '[', ']'
	};
	
	public static IndexReader getIndexReader(String indexPath){
		Object obj = IR_CACHE.get(indexPath);
		if (obj != null && obj instanceof IndexReader){
			return (IndexReader)obj;
		}else {
			IndexReader ir=null;
			try {
				ir=DirectoryReader.open(FSDirectory.open(Paths.get(indexPath))); //只读模式
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(ir!=null) Utils.IR_CACHE.put(indexPath,ir);
			return ir;
		}
	}
	
	/**统一小写，过滤*/
	public static String changeText(String arg){
		arg = StringUtil.notNull(arg);
		if(arg.length() <= 1){
			return arg;
		}
		arg = arg.toLowerCase(Locale.SIMPLIFIED_CHINESE);
		return filterIllegalString(arg);
	}
	
	/**过滤影响搜索的特殊字符*/
	public static String filterIllegalString(String inStr){
		inStr = StringUtil.notNull(inStr);
		if(inStr.equals("")){
			return "";
		}
		char inch[] = inStr.toCharArray();
		for(int j = 0,m=inch.length; j <m ; j++){
			for(int i = 0,n=illegal.length; i < n; i++){
				if(inch[j] == illegal[i]){
					inch[j] = ' ';
				}
			}

		}
		return new String(inch);
	}
	
	/**单字符切分*/
	public static String getK(String str) {
		if (str == null) return "";

		StringBuilder buf=new StringBuilder();
		for(int i=0,n=str.length();i<n;i++){
			if(i!=0) buf.append(" ");
			buf.append(str.substring(i,i+1));
		}
		return buf.toString();
	}

	/**
	 * 从字符串中找出出现次数比较多的热门词
	 * @param str
	 * @param analyzer
	 * @param num
	 * @param hasTimes 是否包含次数，包含则数据为“出现次数##词”
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<String> getHotWords(String str, Analyzer analyzer, int num, boolean hasTimes) throws Exception{
		return getHotWordsBySeg(segWords(str, analyzer), num, hasTimes);
	}
	
	/**
	 * 分词
	 * @param str
	 * @param analyzer
	 * @return
	 */
	public static String segWords(String str, Analyzer analyzer){
		if (StringUtil.isEmpty(str)) return "";
		QueryParser queryParser = new QueryParser("field", analyzer);
		try {
			Query query = queryParser.parse(str);
			return query.toString("field");
		} catch (Exception e) {
			return "";
		}		
	}
	
	/**
	 * 根据切分的结果取热门词
	 * @param segStr
	 * @param num
	 * @param hasTimes 是否包含次数，包含则数据为“出现次数##词”
	 * @return
	 */
	public static ArrayList<String> getHotWordsBySeg(String segStr, int num, boolean hasTimes){
		if (StringUtil.isEmpty(segStr)) return null;
		String[] ss=segStr.replaceAll("\"", "").split(" ");
		int n=ss.length;
		if(n>0){
			HashMap<String,Integer> map = new HashMap<String,Integer> ();
			for(int i=0;i<n;i++){
				if(ss[i].length()>1){
					if(map.containsKey(ss[i])){
						map.put(ss[i], map.get(ss[i])+1);
					}else{
						map.put(ss[i],1);
					}
				}
			}
			Map.Entry[] es=MapUtil.sortedMapByValue(map);
			ArrayList<String> list=new ArrayList<String>();
			if(num==-1 || num>es.length) num=es.length;
			for(int i=0;i<num;i++){
				String word=(String)es[i].getKey();
				if(hasTimes)
					list.add(map.get(word)+"##"+word);
				else
					list.add(word);
			}
			return list;
		}
		return null;
	}
	
	/**
	 * 高亮字符串中的词
	 * @param str
	 * @param analyzer
	 * @param key
	 * @param shf
	 * @param length
	 * @return
	 */
	public static String getHighlights(String str, Analyzer analyzer,  String key,SimpleHTMLFormatter shf, int length){
		QueryParser queryParser = new QueryParser("field", analyzer);
		try {
			Query query = queryParser.parse(key);
			Highlighter highlighter = new Highlighter(shf, new QueryScorer(query));
			highlighter.setTextFragmenter(new SimpleFragmenter(length));
			return highlighter.getBestFragment(analyzer, "field", str);
		} catch (Exception e) {
			return "";
		}		
	}
}
