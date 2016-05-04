package com.iw86.distributed;

/**
 * 序列号服务
 * @author tanghuang
 * 
 */
public interface SequenceService {

	/**
	 * 下一个序列号
	 * @param name
	 * @return
	 */
	long next(String name);

	/**
	 * 一下个固定长度序列号
	 * @param name 名字
	 * @param length 长度
	 * @return 固定长度序列号
	 */
	String next(String name, int length);

	/**
	 * 将一个全局的值增加指定的值
	 * @param name 全局关键字
	 * @param delta 指定的值
	 * @return 最终结果
	 */
	int addAndGet(String name, int delta);

	/**
	 * 将一个全局的值增加1
	 * @param name 全局关键字
	 * @return 最终结果
	 */
	int incrementAndGet(String name);

	/**
	 * 获得全局的值(初始值为0)
	 * @param name 全局关键字
	 * @return 全局的值
	 */
	int intValue(String name);
}
