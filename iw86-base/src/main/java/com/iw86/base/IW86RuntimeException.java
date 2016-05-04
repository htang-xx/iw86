/**
 * 
 */
package com.iw86.base;

/**
 * @author tanghuang
 *
 */
public class IW86RuntimeException extends RuntimeException {

	protected static final long serialVersionUID = 1L;

	public IW86RuntimeException(String message) {
		super(message);
	}

	public IW86RuntimeException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public IW86RuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
