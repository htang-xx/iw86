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
public @interface DBValueMethod {

	// 处理的值类型
	DBValueType[] type();

	// 编码或解码
	boolean encode();
	
}
