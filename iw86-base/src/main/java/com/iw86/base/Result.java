/**
 * 
 */
package com.iw86.base;

import com.iw86.lang.JsonUtil;

/**
 * @author tanghuang
 *
 */
public class Result implements java.io.Serializable {
	
	private static final long serialVersionUID = -6033781489662535829L;
	
	private static final String[] excludeProperty = new String[]{"data"};

	private int code;
	
	private String msg;
	
	private Object data;
	
	public Result(){}
	
	public Result(int code, String msg, Object data){
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String toString(){
		if(this.data == null) return JsonUtil.jsonExclude(this, excludeProperty);
		return JsonUtil.objToStr(this);
	}
}
