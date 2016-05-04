/**
 * 
 */
package com.iw86.mongo;

import java.io.InputStream;
import java.util.Date;

import com.mongodb.gridfs.GridFSDBFile;

/**
 * @author tanghuang
 *
 */
public class DBFile {

	private GridFSDBFile dbFile;

	public DBFile(GridFSDBFile dbFile) {
		this.dbFile = dbFile;
	}
	
	public String getFilename() {
		return dbFile.getFilename();
	}

	public String getContentType() {
		return dbFile.getContentType();
	}

	public InputStream getInputStream() {
		return dbFile.getInputStream();
	}

	public Long getLength() {
		return dbFile.getLength();
	}

	public Date getUploadDate() {
		return dbFile.getUploadDate();
	}
	
}
