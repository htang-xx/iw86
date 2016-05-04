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
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBField {
	// 字段别名
	String value() default "";

	// 是否允许更新
	boolean insert() default true;

	// 是否允许更新
	boolean update() default true;

	// 复位空字段，设置为null或删除
	// string "", number 0, date 0, [], List, Set, Map
	boolean trim() default true;

	// @MyDocClass注解的自定义类型
	// MyClass, List<MyClass>, Set<MyClass>, Map<String,MyClass>
	Class<?> cclass() default Object.class;

	// 容器类型的内部容器成员层次，仅当多层容器类型叠加时
	// List<List<?>> : list,?
	// List<List<List<?>>: list,list,?
	// List<Map<,List<?>> : map,list,?
	DBValueType[] stype() default {};
}
