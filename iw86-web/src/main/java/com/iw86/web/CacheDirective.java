/**
 * 
 */
package com.iw86.web;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Hashtable;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import com.iw86.lang.StringUtil;
import com.iw86.other.Oscache;

/**
 * velocity局部缓存
 * @author tanghuang
 */
public class CacheDirective extends Directive {
	private final static Hashtable<String,String> body_tpls = new Hashtable<String, String>();
	private static Oscache VM_CACHE_2H = new Oscache("VM_CACHE_2H", 100, 7200); //2小时缓存，100个
	
	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.directive.Directive#getName()
	 */
	@Override
	public String getName() {
		return "cache"; //指定指令的名称
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.directive.Directive#getType()
	 */
	@Override
	public int getType() {
		return BLOCK; //指定指令类型为块指令
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.directive.Directive#render(org.apache.velocity.context.InternalContextAdapter, java.io.Writer, org.apache.velocity.runtime.parser.node.Node)
	 */
	@Override
	public boolean render(InternalContextAdapter context, Writer writer, Node node)
			throws IOException, ResourceNotFoundException, ParseErrorException,
			MethodInvocationException {
		//获得缓存信息
        SimpleNode sn_region = (SimpleNode) node.jjtGetChild(0);
        String region = (String)sn_region.value(context);
        SimpleNode sn_key = (SimpleNode) node.jjtGetChild(1);
        Serializable key = (Serializable)sn_key.value(context);
     
        Node body = node.jjtGetChild(2);
        //检查内容是否有变化
        String tpl_key = key+"@"+region;
        String body_tpl = body.literal();
        String old_body_tpl = body_tpls.get(tpl_key);
        String cache_html = (String)VM_CACHE_2H.get(tpl_key);
        if(cache_html == null || !StringUtil.equals(body_tpl, old_body_tpl)){
            StringWriter sw = new StringWriter();
            body.render(context, sw);
            cache_html = sw.toString();
            VM_CACHE_2H.put(tpl_key, cache_html);
            body_tpls.put(tpl_key, body_tpl);
        }
        writer.write(cache_html);
        return true;
	}

}
