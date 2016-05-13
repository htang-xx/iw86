/**
 * 
 */
package com.iw86.web;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.iw86.base.Constant;
import com.iw86.base.DateUtil;
import com.iw86.base.Result;
import com.iw86.collection.Row;
import com.iw86.io.FileUtil;
import com.iw86.lang.StringUtil;

/**
 * 基于spring mvc的Controller类
 * @author tanghuang
 */
public abstract class BaseController extends MultiActionController {
	
	/**
	 * @param response
	 */
	protected void setHeader(HttpServletResponse response) {
		response.setContentType("text/html;charset=UTF-8");
	}

	/**
	 * @param request
	 * @param response
	 */
	protected void noCache(HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
	}

	/**
	 * 获得前一页面
	 * 
	 * @return
	 */
	protected String getReferer(HttpServletRequest request) {
		return request.getHeader("REFERER");
	}

	/** 页面重定向 */
	protected String sendRedirect(HttpServletResponse response, String url) {
		try {
			response.sendRedirect(url);
		} catch (Exception e) {
			logger.error("页面重定向出现异常：", e);
		}
		return null;
	}
	
	/**
	 * @param request
	 * @param response
	 * @param text
	 */
	protected String renderText(HttpServletRequest request,
			HttpServletResponse response, String text) {
		noCache(request, response);// 这里不加的话，AJAX会缓存每次取的数据，从而造成数据不能更新
		setHeader(response);
		try {
			response.getWriter().write(text);
		} catch (Exception e) {
			logger.error("异常", e);
		}
		return null;
	}
	
	/**
	 * @param request
	 * @param response
	 * @param text
	 */
	protected String renderXml(HttpServletRequest request,
			HttpServletResponse response, String text) {
		noCache(request, response); // 这里不加的话，AJAX会缓存每次取的数据，从而造成数据不能更新
		response.setContentType("text/xml;charset=UTF-8");
		try {
			response.getWriter().write(text);
		} catch (Exception e) {
			logger.error("异常", e);
		}
		return null;
	}
	
	/**
	 * @param request
	 * @param response
	 * @param json
	 * @return
	 */
	protected String renderJson(HttpServletRequest request,
			HttpServletResponse response, String json) {
		noCache(request, response); // 这里不加的话，AJAX会缓存每次取的数据，从而造成数据不能更新
		response.setContentType("application/json;charset=UTF-8");
		try {
			response.getWriter().write(json);
		} catch (Exception e) {
			logger.error("异常", e);
		}
		return null;
	}
	
	protected String renderJsonByResult(HttpServletRequest request,
			HttpServletResponse response, Result result) {
		return renderJson(request, response, result.toString());
	}
	
	/**
	 * @param request
	 * @param response
	 * @param bytes
	 * @return
	 */
	protected String renderStream(HttpServletRequest request,
			HttpServletResponse response, byte[] bytes){
		noCache(request, response);
		response.setContentType("application/octet-stream;charset=UTF-8");
		try {
			OutputStream out = response.getOutputStream();
			out.write(bytes);
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.error("异常", e);
		}
		return null;
	}

	/**
	 * 输出JS代码
	 * 
	 * @param response
	 * @param content
	 * @param url
	 * @throws Exception
	 */
	protected String renderScript(HttpServletRequest request,
			HttpServletResponse response, String jscode) {
		String text = StringUtil.str("<script>", jscode, "</script>");
		return renderText(request, response, text);
	}
	
	/**
	 * 弹窗
	 * 
	 * @param response
	 * @param content
	 * @param url
	 * @throws Exception
	 */
	protected String alertToPage(HttpServletRequest request,
			HttpServletResponse response, String content, String url) {
		String text = StringUtil.str("<script>alert('", content, "');window.location='", url, "';</script>");
		return renderText(request, response, text);
	}
	
	/**
	 * 跳转页面并刷新
	 * @param response
	 * @param content
	 * @param url
	 * @throws Exception
	 */
	protected String toPage(HttpServletRequest request,
			HttpServletResponse response, String url) {
		String text = StringUtil.str("<script>window.location='", url, "';</script>");
		return renderText(request, response, text);
	}
	
	/**
	 * 子窗口返回数据，把值传给父窗口后，关闭子窗口
	 * @param response
	 * @param content
	 * @param url
	 * @throws Exception
	 */
	protected String dialogText(HttpServletRequest request,
			HttpServletResponse response, String content) {
		if(content!=null && content.length()>0){
			content = StringUtil.str("<script>document.write('<div style=\"font-size:12px;text-align:center;color:red\">",
						content,
						"<br><input type=\"button\" value=\"确定并关闭窗口\" onclick=\"parent.location.href=parent.location.href;parent.myDialog.close();\"></div>');</script>");
		}else{
			content = "<script>parent.location.href=parent.location.href;parent.myDialog.close();</script>";
		}
		return renderText(request, response, content);
	}

	/**
	 * 弹出提示框后返回上一页面并刷新（保证了参数与第一次进入上一页面相同）
	 * @param request
	 * @param response
	 * @param content 提示框内容，为空不会弹出提示框
	 * @return
	 */
	protected String alertBack(HttpServletRequest request,
			HttpServletResponse response, String content) {
		StringBuilder text = new StringBuilder("<script>");
		if(!StringUtil.isEmpty(content)) text.append("alert('").append(content).append("';");
		text.append("window.history.back();location.reload();</script>");
		return renderText(request, response, text.toString());
	}
	
	/**
	 * 文件上传
	 * @param request
	 * @param uploadPath
	 * @param suffix 允许上传的文件后缀，可多个如"jpg|jpeg|gif|bmp|png"，为null表示不限
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected ArrayList<Row<?,?>> upload(HttpServletRequest request, String uploadPath, String suffix) {
		ArrayList<Row<?,?>> list = new ArrayList<Row<?,?>>();
		try {
			FileUtil.mkDirs(uploadPath);
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(1024*1024);  // 设置缓冲区大小，这里是1M
			factory.setRepository(FileUtil.getFile(uploadPath)); // 设置缓冲区目录,大于上面的值才存储，否则直接存入内存
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(10 * 1024 * 1024); // 设置最大文件大小,10M
			List<FileItem> items = upload.parseRequest(request);// 得到所有的文件
			for (FileItem fi : items) {
				if(fi.isFormField()){ //文本域
					String fieldName = fi.getFieldName();   //获得简单域的名字
					String fieldValue = fi.getString(Constant.UTF_8); //获得简单域的值
					request.setAttribute(fieldName, fieldValue); //以便后面能从request获得
				}
			}
			for (FileItem fi : items) {
				if(!fi.isFormField()){ //文件域
					String fileName = fi.getName();
					if (!StringUtil.isEmpty(fileName)) {
						String ext = FileUtil.getTypePart(fileName).toLowerCase();
						if(StringUtil.isEmpty(suffix) || suffix.indexOf(ext)!=-1){ //允许上传的文件
							String dstr=DateUtil.getCurrentStr();
							fileName = dstr + "." + ext;
							File savedFile = FileUtil.getFile(uploadPath+fileName);
							fi.write(savedFile);
							
							Row map = new Row();
							map.put("name", fileName);
							map.put("size", fi.getSize());
							list.add(map);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("上传文件失败:", e);
		}
		return list;
	}
}
