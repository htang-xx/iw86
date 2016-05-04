/**
 * 
 */
package com.iw86.web;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

import com.iw86.io.FileUtil;

/**
 * 模板生成页面
 * @author tanghuang
 */
public class VelocityViewRender implements InitializingBean {

	private VelocityConfigurer velocityConfigurer;

	private VelocityEngine velocityEngine;

	/**
	 * @return the velocityEngine
	 */
	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	/**
	 * @param velocityEngine
	 *            the velocityEngine to set
	 */
	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	/**
	 * @return the velocityConfigurer
	 */
	public VelocityConfigurer getVelocityConfigurer() {
		return velocityConfigurer;
	}

	/**
	 * @param velocityConfigurer
	 *            the velocityConfigurer to set
	 */
	public void setVelocityConfigurer(VelocityConfigurer velocityConfigurer) {
		this.velocityConfigurer = velocityConfigurer;
	}

	public void afterPropertiesSet() throws Exception {
		setVelocityEngine(getVelocityConfigurer().getVelocityEngine());
	}

	/**
	 * 模板渲染
	 * @param tplName
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public String parser(String tplName, VelocityContext context)
			throws Exception {
		Template tpl = this.getVelocityEngine().getTemplate(tplName);
		StringWriter out = new StringWriter();
		tpl.merge(context, out);
		return out.toString();
	}

	/**
	 * @param filePath
	 *            生成的文件，包括路径和文件名
	 * @param tplName
	 *            模板名（如果不在CLASSPATH下，则包括路径）
	 * @param encode
	 * @param context
	 * @throws Exception
	 */
	public void render(String filePath, String tplName, String encode,
			VelocityContext context) throws Exception {
		FileUtil.writIn(filePath, parser(tplName, context), encode);
	}

}
