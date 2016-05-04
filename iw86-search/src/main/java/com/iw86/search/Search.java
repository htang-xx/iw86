package com.iw86.search;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import com.iw86.lang.StringUtil;
/**
 * lucene搜索类
 * @author tanghuang
 */
public class Search {
	
	private Analyzer analyzer;
	private IndexReader[] indexReaders;
	private Highlighter highlighter;
	private boolean logicOR;     //是否逻辑或关系，默认逻辑与
	private boolean sortByScore; //是否按匹配度排序,默认否
	private SortField[] sortFields; //针对排序的字段
	private boolean isHighlight; //是否高亮显示关键词，默认不高亮
	private int resultSize;      //获得搜索的结果数
	
	public Search(IndexReader[] indexReaders,Analyzer analyzer) {
		this.indexReaders=indexReaders;
		this.analyzer=analyzer;
		logicOR = false;
		isHighlight = false;
		sortByScore = false;
		highlighter=null;   
		sortFields=null;
		resultSize = 0;
	}
	
	/**
	 * 单字段搜索
	 * @param indexFieldName
	 * @param keyWord
	 * @param beginNum
	 * @param endNum
	 * @param isWhole 是否完全匹配搜索
	 * @return
	 */
	public Document[] search(String indexFieldName,String keyWord,int beginNum,int endNum, boolean isWhole){
		Query qry = mkQuery(indexFieldName, keyWord, isWhole);
		if (qry == null) {
			return null;
		} else {
			if(isHighlight){
				highlighter = new Highlighter(new SimpleHTMLFormatter("<font color='red'>", "</font>"), new QueryScorer(qry));
			}
			return query( qry, beginNum, endNum);
		}
	}
	
	/**
	 * 多字段搜索
	 * @param indexFields
	 * @param keyWords
	 * @param beginNum
	 * @param endNum
	 * @return
	 */
	public Document[] search(String indexFields[], String keyWords[],int beginNum,int endNum){
		Query qry = mkQuery(indexFields, keyWords);
		if (qry == null) {
			return null;
		} else {
			if(isHighlight){
				highlighter = new Highlighter(new SimpleHTMLFormatter("<font color='red'>", "</font>"), new QueryScorer(qry));
			}
			return query(qry, beginNum, endNum);
		}
	}
	
	/**
	 * 搜索获得结果
	 * @param query
	 * @param beginNum
	 * @param endNum
	 * @return
	 */
	public Document[] query(Query query,int beginNum,int endNum){
		IndexSearcher searcher = null;
		try {
			if(indexReaders.length==1){
				searcher = new IndexSearcher(indexReaders[0]);
			}else{
				MultiReader mReader = new MultiReader(indexReaders);
				searcher = new IndexSearcher(mReader);
			}
			ScoreDoc[] hits;
			if(sortFields!=null && sortFields.length>0){
				TopFieldCollector tsc = TopFieldCollector.create(new Sort(sortFields),endNum,true,true,sortByScore);//按指定字段排序,sortByScore表示是否先按匹配度排
				searcher.search(query, tsc);
				hits = tsc.topDocs().scoreDocs;
				resultSize = tsc.getTotalHits();
			}else if(sortByScore) {
				TopScoreDocCollector tsc = TopScoreDocCollector.create(endNum); //仅按匹配度降序排序
				searcher.search(query, tsc);
				hits = tsc.topDocs().scoreDocs;
				resultSize = tsc.getTotalHits();
			}else{
				TopDocs results = searcher.search(query, endNum); //默认按建索引的顺序
				hits = results.scoreDocs;
				resultSize = results.totalHits;
			}
			if (hits != null && hits.length > 0) {
				int findNum = hits.length;
				if (beginNum >= findNum) return null;
				Document[] doc=new Document[findNum-beginNum];
				int n = 0;
				for (int i = beginNum; i < findNum; i++) {
					doc[n] = searcher.doc(hits[i].doc);
					n++;
				}
				return doc;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			searcher = null;
		}
		return null;
	}
	
	/**
	 * 单字段
	 * @param indexFieldName
	 * @param keyWord
	 * @param isWhole 是否需要完全匹配精确搜索
	 * @return
	 */
	private Query mkQuery(String indexFieldName, String keyWord, boolean isWhole) {
		try {
			if(isWhole){ //完全匹配精确搜索
				Term t=new Term(indexFieldName,keyWord); 
				return new TermQuery(t);
			}else{
				QueryParser qp = new QueryParser(indexFieldName, analyzer);
				if (logicOR) {
					qp.setDefaultOperator(QueryParser.OR_OPERATOR);
				} else {
					qp.setDefaultOperator(QueryParser.AND_OPERATOR);
				}
				keyWord = Utils.changeText(keyWord);
				return qp.parse(keyWord);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**多字段*/
	private Query mkQuery(String indexFields[], String keyWords[]) {
		if (indexFields == null || indexFields.length < 1) {
			return null;
		}
		int n=indexFields.length;
		BooleanClause.Occur[] flags = new BooleanClause.Occur[n];
		for(int i=0;i<n;i++){
			if (logicOR) {
				flags[i]=BooleanClause.Occur.SHOULD;
			}else{
				flags[i]=BooleanClause.Occur.MUST;
			}
		}
		Query query=null;
		try {
			query = MultiFieldQueryParser.parse(keyWords, indexFields, flags, analyzer);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return query;
	}
	
	/**设置逻辑或*/
	public void setLogicOR() {
		logicOR = true;
	}
	/**设置按匹配度排序*/
	public void setSortByScore() {
		sortByScore = true;
	}
	/**设置需要排序的字段*/
	public void setSortFields(SortField[] sortFields) {
		this.sortFields = sortFields;
	}

	/**得到搜索结果数目*/
	public int getResultSize() {
		return resultSize;
	}
	/**获得语法分析器*/
	public Analyzer getAnalyzer() {
		return analyzer;
	}
	/**设置关键词高亮显示*/
	public void setHighlight() {
		isHighlight = true;
	}
	
	/**
	 * 获得高亮的结果，只能高亮分词的
	 */
	public String highlight(String field,String content,int length) {
		String hcontent = null;
		if(isHighlight && highlighter!=null && content!=null){
			try {
				content = StringUtil.trimHtml(content);
				highlighter.setTextFragmenter(new SimpleFragmenter(length));
				hcontent = highlighter.getBestFragment(getAnalyzer(),field,content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(StringUtil.isEmpty(hcontent)) content=StringUtil.sub(content, length, "");
		else content=hcontent;
		return content;
	}
}