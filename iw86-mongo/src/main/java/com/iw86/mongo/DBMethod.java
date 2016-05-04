/**
 * 
 */
package com.iw86.mongo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tanghuang
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBMethod {

	// 处理的字段名
	String value();

	// 编码或解码
	boolean encode();
		
}
