/**
 * 
 */
package com.iw86.base;

/**
 * @author tanghuang
 *
 */
public class IW86Exception extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	private int code;

	public IW86Exception() {
		super();
	}

	public IW86Exception(String message) {
		super(message);
	}
	
	public IW86Exception(int code){
		super();
		this.code = code;
	}
	
	public IW86Exception(int code, String message){
		super(message);
		this.code = code;
	}

	public IW86Exception(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
