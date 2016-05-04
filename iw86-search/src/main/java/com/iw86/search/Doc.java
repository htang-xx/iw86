package com.iw86.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

/**
 * Document封装
 * @author tanghuang
 */
public class Doc {
	
	private Document doc;

	public Doc(){
		doc = new Document();
	}

	/**
	 * 存储、索引、分词，比如文件的各种属性，比如MP3文件的歌手、专辑等
	 */
	public void keyword(String iName, String iValue){
		iValue = Utils.changeText(iValue);
		doc.add(new TextField(iName, iValue, Field.Store.YES));
	}
	
	/**
	 * 存储、索引、不分词，用于URI（比如MSN聊天记录的日期域、比如MP3文件的文件全路径等等）
	 */
	public void text(String iName, String iValue){
		doc.add(new StringField(iName, iValue, Field.Store.YES));
	}
	/**
	 * 存储、不索引、不分词，比如文件的全路径
	 */
	public void unIndexed(String iName, String iValue){
		doc.add(new StoredField(iName, iValue));
	}
	
	/**
	 * 不存储、索引、分词，比如HTML的正文、Word的内容等等，这部分内容是要被索引的，
	 * 但是由于具体内容通常很大，没有必要再进行存储，可以到时候根据URI再来挖取。
	 * 所以，这部分只分词、索引，而不存储
	 */
	public void unStored(String iName, String iValue){
		iValue = Utils.changeText(iValue);
		doc.add(new TextField(iName, iValue, Field.Store.NO));
	}
	
	public Document getDocument(){
		return doc;
	}
}
