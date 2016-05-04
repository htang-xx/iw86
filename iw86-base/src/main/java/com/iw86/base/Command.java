/**
 * 
 */
package com.iw86.base;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 命令行执行 ,调用系统命令
 * @author tanghuang
 */
public class Command{

	/**
	 * 默认,打出屏幕显示 
     * @param cmd
     * @return
     */
    public static String exec(String cmd){
        return exec(cmd, true);
    }
    
    /**
     * @param cmd
     * @return
     */
    public static boolean process(String cmd){
    	return exec(cmd,false)!=null;
    }
    
    /**
     * 命令行执行指令
     * @param cmd
     * @param getStr
     * @return
     */
    public static String exec(String cmd, boolean getStr){
        StringBuilder result = new StringBuilder();
        BufferedReader stdout = null;
        try{
            Process process = Runtime.getRuntime().exec(cmd);
            if(getStr){
                stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while((line = stdout.readLine()) != null) {
                    result.append(line).append("\n");
                }
            }
        }catch(Exception e){
            result.append(e.getMessage());
            e.printStackTrace();
        }
        return result.toString();
    }
    
    /**
     * 命令行执行指令(将命令和参数分开，避免exec自己以空格去划分)
     * @param cmd
     * @param getStr
     * @param wait 慎用，有可能造成程序阻塞
     * @return
     */
    public static String exec(String[] cmd, boolean getStr, boolean wait){
        StringBuilder result = new StringBuilder();
        BufferedReader stdout = null;
        try{
        	ProcessBuilder builder = new ProcessBuilder(cmd);   
        	builder.redirectErrorStream(true); //让ErrorStream和InputStream在同一进程使用，以保证不被阻塞
        	Process process = builder.start();   
            if(getStr){
                stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while((line = stdout.readLine()) != null) {
                    result.append(line).append("\n");
                }
            }
            if(wait) process.waitFor();
        }catch(Exception e){
            result.append(e.getMessage());
            e.printStackTrace();
        }
        return result.toString();
    }
    
    /**
	 * 休眠
	 * @param mills 休眠时间，单位毫秒
	 */
	public static void sleep(long mills) {
		try {
			Thread.sleep(mills);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
