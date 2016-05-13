/**
 * 
 */
package com.iw86.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iw86.base.Constant;
import com.iw86.collection.Row;
import com.iw86.lang.Conver;
import com.iw86.lang.StringUtil;
import com.iw86.lang.Validator;

/**
 * request处理
 * @author tanghuang
 */
public class RequestUtil {
    
    /**手机浏览器的User-Agent里的关键词*/
    private static String[] mobileUserAgents=new String[]{
	    "Windows Phone",
	    "iPhone",
	    "iPad",
	    "Android",
	    "micromessenger"
    };
    
	/**
	 * 根据当前请求的特征，判断该请求是否来自手机终端，主要检测特殊的头信息，以及user-Agent这个header
	 * @param request http请求
	 * @return 如果命中手机特征规则，则返回对应的特征字符串
	 */
    public static boolean isMobileDevice(HttpServletRequest request){
        String userAgent = request.getHeader("user-agent");
        if(userAgent!=null && userAgent.trim().length()>0){
	        for (int i = 0;i < mobileUserAgents.length; i++) {
	            if(userAgent.contains(mobileUserAgents[i])){
	                return true;
	            }
	        }
        }
        return false;
    }
 
    /**
     * 判断是否为搜索引擎
     * @param req
     * @return
     */
    public static boolean isRobot(HttpServletRequest req){
        String ua = req.getHeader("user-agent");
        if(StringUtil.isBlank(ua)) return false;
        return (ua != null  && (ua.indexOf("Baiduspider") != -1 
        		|| ua.indexOf("Googlebot") != -1
                || ua.indexOf("sogou") != -1
                || ua.indexOf("sina") != -1
                || ua.indexOf("iaskspider") != -1
                || ua.indexOf("ia_archiver") != -1
                || ua.indexOf("Sosospider") != -1
                || ua.indexOf("YoudaoBot") != -1
                || ua.indexOf("yahoo") != -1
                || ua.indexOf("yodao") != -1
                || ua.indexOf("MSNBot") != -1
                || ua.indexOf("spider") != -1
                || ua.indexOf("Twiceler") != -1
                || ua.indexOf("Sosoimagespider") != -1
                || ua.indexOf("naver.com/robots") != -1
                || ua.indexOf("Nutch") != -1
                || ua.indexOf("spider") != -1));   
    }

	/**
     * 获取客户端IP地址，此方法用在proxy环境中
     * @param request
     * @return
     */
    public static String getRemoteAddr(HttpServletRequest request) {
    	String ip = request.getHeader("X-Forwarded-For");
		if(StringUtil.isEmpty(ip) || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtil.isEmpty(ip) || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtil.isEmpty(ip) || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("X-Real-IP");
		}
		if (StringUtil.isEmpty(ip) || ip.equalsIgnoreCase("unknown")) {
			ip = request.getRemoteAddr();
		}
		// 多级反向代理检测
		if (ip != null && ip.indexOf(",") > 0) {
			ip = ip.trim().split(",")[0];
		}
		if(!Validator.isIpv4(ip)) ip = "127.0.0.1";
		return ip;
    }
	
	/**
	 * 获得参数值
	 * @param request
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static String get(HttpServletRequest request, String name,
			String defaultValue) {
		return request.getParameter(name) != null ? request.getParameter(name) : defaultValue;
	}

	public static String get(HttpServletRequest request, String name) {
		return get(request, name, "");
	}

	/** 获得整数值 */
	public static int getInt(HttpServletRequest request, String name,
			int defaultValue) {
		return StringUtil.getInt(request.getParameter(name), defaultValue);
	}

	public static int getInt(HttpServletRequest request, String name) {
		return getInt(request, name, 0);
	}
	
	public static float getFloat(HttpServletRequest request, String name,
			float defaultValue) {
		return Conver.toFloat(request.getParameter(name), defaultValue);
	}
	
	public static double getDouble(HttpServletRequest request, String name,
			double defaultValue) {
		return Conver.toDouble(request.getParameter(name), defaultValue);
	}
	
	public static long getLong(HttpServletRequest request, String name,
			long defaultValue) {
		return Conver.toLong(request.getParameter(name), defaultValue);
	}

	/** 设置request值 */
	public static void setRequest(HttpServletRequest request,
			String requsetName, Object requsetObj) {
		request.setAttribute(requsetName, requsetObj);
	}

	/** 获得request值 */
	public static Object getReqest(HttpServletRequest request,
			String requsetName) {
		return request.getAttribute(requsetName);
	}

	/**
	 * @param paramName
	 * @return
	 */
	public static String[] getValues(HttpServletRequest request, String paramName) {
		return request.getParameterValues(paramName);
	}

	/**
	 * 获取参数的数字值组合成一个字符串
	 * @param request
	 * @param paramName
	 * @param separator
	 * @return
	 */
	public static String getValues(HttpServletRequest request, String paramName, String separator) {
		String[] values = request.getParameterValues(paramName);
		return StringUtil.arr2str(separator, true, values);
	}

	/**
	 * UTF-8编码的值
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getUTF8(HttpServletRequest request, String name) {
		try {
			return new String(get(request, name).getBytes(Constant.UTF_8), Constant.UTF_8);
		} catch (Exception e) {
			return name;
		}
	}

	/**
	 * 取得表单里面所有的参数/值,并保存在Row里面返回(不上传图片) 多个值的默认以逗号隔开保存
	 * @param request
	 * @return row
	 */
	public static Row<String,Object> getParam(HttpServletRequest request, String notmath) {
		return getParam(request, notmath, Constant.COMMA);
	}

	/**
	 * 取得表单里面所有的参数/值,并保存在Row里面返回(不上传图片),多个值的以符号隔开保存
	 * @param request
	 * @param notmath
	 * @param separator
	 * @return
	 */
	public static Row<String,Object> getParam(HttpServletRequest request, String notmath, String separator) {
		Row<String,Object> row = new Row<String,Object>();
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement().toString();
			if (!paramName.startsWith(notmath)) {
				String[] paramValues = request.getParameterValues(paramName);
				String paramValue = StringUtil.arr2str(separator, true, paramValues);
				row.put(paramName, paramValue);
			}
		}
		return row;
	}

	/**
	 * 获取请求的所有数据
	 * 
	 * @param request
	 * @param notmath
	 * @return
	 */
	public static Row<String,Object> getAttribute(HttpServletRequest request, String notmath) {
		Row<String,Object> row = new Row<String,Object>();
		Enumeration attrNames = request.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			String attrName = attrNames.nextElement().toString();
			if (!attrName.startsWith(notmath)) {
				Object o = (Object) request.getAttribute(attrName);
				if (o != null) {
					row.put(attrName, o);
				}
			}
		}
		return row;
	}
 
    /**
     * 获取COOKIE
     * @param name
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if(name == null || cookies == null) return null;
        for (Cookie ck : cookies) {
            if (name.equalsIgnoreCase(ck.getName()))
                return ck;         
        }
        return null;
    }
 
    /**
     * 获取COOKIE
     * @param name
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if(name == null || cookies == null) return null;
        for (Cookie ck : cookies) {
        	if (name.equalsIgnoreCase(ck.getName()))
                return ck.getValue();          
        }
        return null;
    }
 
    /**
     * 设置COOKIE，关闭浏览器就失效
     * @param name
     * @param value
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value) {
        setCookie(request,response,name,value,-1,true);
    }
    
    /**
     * 设置COOKIE，有效期8小时
     * @param name
     * @param value
     */
    public static void setCookie8(HttpServletRequest request, HttpServletResponse response, String name, String value) {
        setCookie(request,response,name,value,3600*8,false);
    }
 
    /**
     * 设置COOKIE
     * @param name
     * @param value
     * @param maxAge
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name,
            String value, int maxAge, boolean all_sub_domain) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        if(all_sub_domain){
            String serverName = request.getServerName();
            String domain = getDomainOfServerName(serverName);
            if(domain!=null && domain.indexOf('.')!=-1){
                cookie.setDomain('.' + domain);
            }
        }
        cookie.setPath("/");
        response.addCookie(cookie);
    }
     
    /**
     * @param request
     * @param response
     * @param name
     * @param all_sub_domain
     */
    public static void deleteCookie(HttpServletRequest request,
            HttpServletResponse response, String name, boolean all_sub_domain) {
        setCookie(request,response,name,"",0,all_sub_domain);
    }
 
    /**
     * 获取用户访问URL中的根域名
     * 例如: www.163.com -> 163.com
     * @param host
     * @return
     */
    public static String getDomainOfServerName(String host){
        if(Validator.isIpv4(host)) return null;
        String[] names = host.split("\\.");
        int len = names.length;
        if(len==1) return null;
        else if(len==3) return StringUtil.join(".",true,names[len-2],names[len-1]);
        else if(len>3){
            String dp = names[len-2];
            if(dp.equalsIgnoreCase("com")||dp.equalsIgnoreCase("gov")||dp.equalsIgnoreCase("net")||dp.equalsIgnoreCase("edu")||dp.equalsIgnoreCase("org"))
                return StringUtil.join(".",true,names[len-3],names[len-2],names[len-1]);
            else
                return StringUtil.join(".",true,names[len-2],names[len-1]);
        }
        return host;
    }
 
    /**
     * 获取HTTP端口
     * @param req
     * @return
     * @throws MalformedURLException
     */
    public static int getHttpPort(HttpServletRequest req) {
        try {
            return new URL(req.getRequestURL().toString()).getPort();
        } catch (MalformedURLException excp) {
            return 80;
        }
    }

	/** 获得请求的绝对路径(不带参数),如：http://riji.163.com/weblog/abc.do */
	public static String getUrl(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}

	/** 获得请求的相对路径(不带参数),如：/weblog/abc.do */
	public static String getPath(HttpServletRequest request) {
		return request.getRequestURI();
	}

	/** 获得请求的参数 */
	public static String getQuery(HttpServletRequest request) {
		return request.getQueryString()!=null?request.getQueryString():"";
	}

}
