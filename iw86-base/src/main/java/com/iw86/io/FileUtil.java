/**
 * 
 */
package com.iw86.io;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.iw86.base.Constant;
import com.iw86.lang.StringUtil;

/**
 * 文件操作
 * @author tanghuang
 */
public class FileUtil {
	
	/**
	 * @param fileName
	 * @param mkdir
	 * @return
	 * @throws IOException
	 */
	public static File getFile(String fileName, boolean mkdir){
		File file = new File(fileName);
		if (mkdir && !file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		return file;
	}
	
	/**
	 * 获得文件
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static File getFile(String fileName){
		return getFile(fileName,false);
	}
	
	/**
	 * 遍历文件夹中文件
	 * @param filepath 文件路径
	 * @return 返回file［］ 数组
	 */
	public static File[] getFileList(String filepath) {
		File d = null;
		File list[] = null;
		/** 建立当前目录中文件的File对象 **/
		try {
			d = getFile(filepath);
			if (d.exists()) {
				list = d.listFiles();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/** 取得代表目录中所有文件的File对象数组 **/
		return list;
	}
	
	/**
	 * 创建文件的父目录
	 * @param file
	 * @throws IOException
	 */
	public static void mkParentDirs(File file) throws IOException {
		if(file==null) return;
		File parent = file.getCanonicalFile().getParentFile();
		if (parent == null) return;
		parent.mkdirs();
		if (!parent.isDirectory()) {
			throw new IOException("Unable to create parent directories of " + file);
		}
	}
	
	/**
	 * 创建目录
	 * @param dir
	 * @return
	 */
	public static boolean mkDirs(String dir) {
		boolean bRet = false;
		try {
			File file = getFile(dir);
			if (!file.exists() && file.mkdirs()) {
				bRet = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}
	
	/**
	 * 写入文件（不考虑编码问题）
	 * @param in
	 * @param fileName
	 */
	public static void writIn(InputStream in, String fileName) throws IOException{
		DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName));
		byte[] buffer = new byte[4096];
		int count = 0;
		while ((count = in.read(buffer)) > 0) {
			out.write(buffer, 0, count);
		}
		out.close();
		in.close();
	}
	
	/**
	 * 写入文件
	 * @param fileName
	 * @param str
	 * @param encoding
	 */
	public static void writIn(String fileName, String str, String encoding){
		OutputStreamWriter ow = null;
		BufferedWriter bw = null;
		try {
			File newFile = getFile(fileName,true);
			ow = new OutputStreamWriter(new FileOutputStream(newFile),encoding);
			bw = new BufferedWriter(ow);
			bw.write(str);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (ow != null) ow.close();
			} catch (Exception e) {}
		}
	}

	/**
	 * UTF-8编码写入文件
	 * @param fileName
	 * @param str
	 * @return
	 */
	public static void writIn(String fileName, String str) {
		writIn(fileName, str, Constant.UTF_8);
	}
	
	/**
	 * @param in
	 * @param encoding
	 * @return
	 */
	public static String readStream(InputStream in, String encoding) throws IOException{
		if (in == null) return Constant.EMPTY;
		InputStreamReader inReader= null;
		if (encoding == null){
			inReader= new InputStreamReader(in);
		}else{
			inReader= new InputStreamReader(in, encoding);
		}
		char[] buffer= new char[4096];
		int readLen= 0;
		StringBuilder sb= new StringBuilder();
		while((readLen = inReader.read(buffer))!=-1){
			sb.append(buffer, 0, readLen);
		}
		inReader.close();
		return sb.toString();
	}
	
	/**
	 * 读取文件
	 * @param fileName
	 * @param isSet 是否需要加上换行（\r\n）
	 * @param encoding 编码，如GBK
	 * @return
	 */
	public static String read(String fileName, boolean isSet, String encoding) {
		File file = getFile(fileName);
		try {
			InputStream in = new FileInputStream(file);
			return readStream(in,encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * UTF-8编码读取文件
	 * @param fileName
	 * @return
	 */
	public static String read(String fileName) {
		return read(fileName, false, Constant.UTF_8);
	}

	/**
	 * 拷贝文件
	 * @param fromFile
	 * @param toFile
	 * @param overwrite 是否覆盖
	 * @return
	 */
	public static void copy(String fromFile, String toFile, boolean overwrite) {
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			File oldFile = getFile(fromFile);
			if(!oldFile.exists()) return;
			File newFile = getFile(toFile, true);
			if(!overwrite && newFile.exists()) return;
			input = new FileInputStream(oldFile);
			output = new FileOutputStream(newFile);
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = input.read(b)) != -1) {
				output.write(b, 0, len);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (output != null) output.close();
				if (input != null) input.close();
			} catch (Exception e2) {}
		}
	}
	
	/**
	 * 删除文件
	 * @param filePathAndName 文本文件完整绝对路径及文件名
	 * @return Boolean 成功删除返回true遭遇异常返回false
	 */
	public static boolean delFile(String filePathAndName) {
		boolean bea = false;
		try {
			String filePath = filePathAndName;
			File myDelFile = getFile(filePath);
			if (myDelFile.exists()) {
				myDelFile.delete();
				bea = true;
			} else {
				bea = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bea;
	}

	/**
	 * 删除文件夹
	 * @param folderPath 文件夹完整绝对路径
	 * @return
	 */
	public static void delFolder(String folderPath) {
		try {
			/** 删除完里面所有内容 **/
			delAllFile(folderPath);
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = getFile(filePath);
			/** 删除空文件夹 **/
			myFilePath.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除指定文件夹下所有文件
	 * @param path 文件夹完整绝对路径
	 * @return
	 */
	public static boolean delAllFile(String path) {
		boolean bea = false;
		File file = getFile(path);
		if (!file.exists()) return bea;
		if (!file.isDirectory()) return bea;
		
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = getFile(path + tempList[i]);
			} else {
				temp = getFile(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				/** 先删除文件夹里面的文件 **/
				delAllFile(path + "/" + tempList[i]);
				/** 再删除空文件 **/
				delFolder(path + "/" + tempList[i]);
				bea = true;
			}
		}
		return bea;
	}
	
	/**
	 * 从文件名得到文件绝对路径。
	 * @param fileName 文件名
	 * @return 对应的文件路径
	 */
	public static String getFilePath(String fileName) {
		return getFile(fileName).getAbsolutePath();
	}

	/**
	 * 得到文件的类型。 实际上就是得到文件名中最后一个“.”后面的部分。
	 * @param fileName 文件名
	 * @return 文件名中的类型部分
	 */
	public static String getTypePart(String fileName) {
		if(!StringUtil.isEmpty(fileName)){
			int point = fileName.lastIndexOf('.');
			if (point != -1 && point < fileName.length() - 1) {
				return fileName.substring(point + 1);
			}
		}
		return "";
	}

	/**
	 * 得到文件的类型。 实际上就是得到文件名中最后一个“.”后面的部分。
	 * @param file 文件
	 * @return 文件名中的类型部分
	 */
	public static String getFileType(java.io.File file) {
		return getTypePart(file.getName());
	}
	
}
