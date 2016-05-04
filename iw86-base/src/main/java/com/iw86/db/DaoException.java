/**
 * 
 */
package com.iw86.db;

/**
 * @author tanghuang
 *
 */
public class DaoException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public static final int NOKEY 			= -1000;
	public static final int DUPLICATE 		= -1001;
	public static final int WRITECONCERN 	= -1002;
	public static final int SERVICECONFIG 	= -1003;
	public static final int FIELDITYPE	 	= -1004;
	public static final int DATACORRUPT	 	= -1005;
		
	private int code;

	public DaoException() {
		super();
	}

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public DaoException(String message) {
		super(message);
	}

	public DaoException(Throwable cause) {
		super(cause);
	}

	public DaoException(int code) {
		this();
		this.code = code;
	}

	public DaoException(int code, String message) {
		this(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
