package com.iw86.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CodecReader;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import com.iw86.collection.Row;
import com.iw86.lang.StringUtil;

/**
 * Lucene索引类
 * @author tanghuang
 */
public class Index {
	private IndexWriter iw;

	/**
	 * 默认（重建索引）
	 */
	public Index(String indexPath, Analyzer analyzer) throws IOException {
		getWriter(indexPath, true, analyzer);
	}

	/**
	 * @param isNew 是否需要重建索引
	 */
	public Index(String indexPath, Boolean isNew, Analyzer analyzer) throws IOException {
		getWriter(indexPath, isNew, analyzer);
	}

	/**
	 * 取得IndexWriterr
	 * @param indexPath 为NULL表示内存索引（内存索引效果并不好，不建议使用）
	 * @param isNew 是否新建索引
	 * @return IndexWriterr
	 */
	private void getWriter(String indexPath, boolean isNew, Analyzer analyzer) throws IOException {
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		if (isNew)
			iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);// 总是重新创建
		else
			iwc.setOpenMode(IndexWriterConfig.OpenMode.APPEND); // 追加索引
		if (indexPath == null) {
			iw = new IndexWriter(new RAMDirectory(), iwc); // 内存索引
		} else {
			Path path = Paths.get(indexPath);
			iw = new IndexWriter(FSDirectory.open(path), iwc);
		}
	}

	/** 添加Document */
	public void addDocument(Doc doc) throws IOException {
		iw.addDocument(doc.getDocument());
	}

	/**
	 * 添加索引
	 * @param readers
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public void addIndexes(CodecReader... readers) throws CorruptIndexException, IOException {
		if (readers == null || iw == null) return;
		iw.addIndexes(readers);
	}

	/**
	 * 添加索引
	 * @param dirs
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public void addIndexes(Directory dirs[]) throws CorruptIndexException,IOException {
		if (dirs == null || iw == null) return;
		iw.addIndexes(dirs);
	}

	/**
	 * 删除索引
	 * @param field
	 * @param text
	 */
	public void deleteIndex(String field, String text) {
		if (StringUtil.isEmpty(field) || null == text) return;
		try {
			Term aTerm = new Term(field, text);
			iw.deleteDocuments(aTerm);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除索引
	 * @param field 指定索引字段
	 * @param texts 索引删除条件
	 */
	public void deleteIndex(String field, List<String> texts) {
		if (StringUtil.isEmpty(field) || null == texts) return;
		try {
			for (int i = 0; i < texts.size(); i++) {
				Term aTerm = new Term(field, texts.get(i));
				if (null != aTerm) {
					iw.deleteDocuments(aTerm);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除索引
	 * @param field 指定索引字段
	 * @param texts 索引删除条件Row
	 * @param rowKey Row指定的key数据
	 */
	public void deleteIndex(String field, List<Row> texts, String rowKey) {
		if (StringUtil.isEmpty(field) || null == texts) return;
		try {
			for (int i = 0; i < texts.size(); i++) {
				Term aTerm = new Term(field, texts.get(i).gets(rowKey));
				if (null != aTerm) {
					iw.deleteDocuments(aTerm);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 索引优化(因开销较大，3.5后已不建议使用optimize，现仅合并) 
	 * Optimize的过程就是要减少剩下的Segment的数量,尽量让它们处于一个文件中
	 */
	public void optimize() throws IOException {
		iw.commit();
		iw.forceMerge(1);
	}

	/**
	 * 删除后的真正删除
	 * @throws IOException
	 */
	public void optimizeDel() throws IOException {
		iw.commit();
		iw.forceMergeDeletes(); // 强制优化，真正删除
	}

	/***/
	public void close() throws IOException {
		iw.close();
	}

	/**
	 * 检查索引文件是否存在
	 * @param s
	 * @return 索引是否存在
	 */
	public static boolean indexExists(String s) {
		File file = new File(s + File.separator + "segments");
		return file.exists();
	}
}
