/**
 * 
 */
package com.iw86.db;

import java.util.List;

import javax.sql.DataSource;

import com.iw86.base.Constant;
import com.iw86.collection.Row;
import com.iw86.io.FileUtil;
import com.iw86.lang.StringUtil;

/**
 * 自动生产器，目前主要生成MyBatis DAO相关代码，根据数据库表结构生成Bean和iBatis用的sqlMap文件
 * @author tanghuang
 */
@SuppressWarnings("rawtypes")
public class AutoGenerator {

	private static String[] intType={"int","tinyint","smallint"};
	
	private DataSource dataSource;
	
	public AutoGenerator(DataSource dataSource){
		this.dataSource=dataSource;
	}
	
	/**
	 * 生成Bean和iBatis用的sqlMap文件<br>
	 * 已包含createBean、createDao、createSqlMap三个动作
	 * @param dbTable 表名
	 * @param dbBean 数据库表对应对象名
	 * @param packagePrefix 包前缀,如：com.iw86.xxx
	 * @param pgDir 包分目录，如abc，那么dao包前缀就为com.iw86.xxx.dao.abc
	 * @param mapDir sqlMap目录，为null时默认java同一目录，可设置如/resource/
	 */
	public void create(String dbTable,String dbBean,String packagePrefix,String pgDir,String mapDir){
		String mainPath = System.getProperty("user.dir").replace('\\', '/')+"/src/main/";
		String srcPath = mainPath+"java/";
		String packageName = packagePrefix+".dao"+(StringUtil.isEmpty(pgDir)?"":("."+pgDir));
		String mapPath = (mapDir==null?srcPath:(mainPath + mapDir)) + packageName.replaceAll("\\.","/");
		String beanName = packagePrefix + ".domain." + (StringUtil.isEmpty(pgDir)?"":(pgDir+".")) + dbBean;
		createBean(dbTable,dbBean,srcPath,packagePrefix+".domain"+(StringUtil.isEmpty(pgDir)?"":("."+pgDir)));
		createDao(dbBean,srcPath,packageName);
		createSqlMap(dbTable,dbBean,mapPath,beanName,StringUtil.str(packageName,".",dbBean,"Dao"));
	}
	
	/**
	 * 生成Bean和iBatis用的sqlMap文件,用于表名可直接用做对象名的情况<br>
	 * 已包含createBean、createDao、createSqlMap三个动作
	 * @param dbTable
	 * @param includePrefix
	 * @param packagePrefix
	 */
	public void create(String dbTable, boolean includePrefix, String packagePrefix){
		String dbBean = getBeanName(dbTable, includePrefix);
		String pgDir = null;
		if(includePrefix && dbTable.contains("_")) pgDir = dbTable.substring(0, dbTable.indexOf("_"));
		create(dbTable, dbBean, packagePrefix, pgDir, null);
	}
	
	/**
	 * 生成Bean和iBatis用的sqlMap文件,用于表名可直接用做对象名的情况<br>
	 * 已包含createBean、createDao、createSqlMap三个动作
	 * @param dbTable
	 * @param packagePrefix
	 */
	public void create(String dbTable,String packagePrefix){
		create(dbTable,setFirstCharUpcase(dbTable),packagePrefix,null,null);
	}
	
	/**
	 * 创建Bean文件
	 * @param dbTable 数据库表名
	 * @param dbBean 数据库表对应对象名
	 * @param javaPath java源文件目录,如：D:/workspace/iw86/src/main/java
	 * @param packageName 包路径,如：com.iw86.xxx.domain
	 */
	public void createBean(String dbTable,String dbBean,String javaPath, String packageName){
		try {
			JdbcUtil jdbc = new JdbcUtil(this.dataSource);
			jdbc.prepareStatement("show full fields from " + dbTable);
			List<Row> list = jdbc.executeList();
			if (list != null) {
				String beanPath = javaPath;
				beanPath += packageName.replaceAll("\\.","/");
				String bean=beanPath+"/"+dbBean+".java";
				System.out.println(bean+"开始生成。");
				FileUtil.mkDirs(beanPath);
				StringBuilder buf = new StringBuilder();
				buf.append("package ").append(packageName).append(";\n\n");
				buf.append("import java.io.Serializable;\n");
				buf.append("import org.apache.commons.lang.builder.ToStringBuilder;\n");
				buf.append("import org.apache.commons.lang.builder.ToStringStyle;\n\n");
				boolean hasBigDecimal = false;
				StringBuilder buf2 = new StringBuilder();
				buf2.append("/**\n").append(" * @author AutoGenerator\n").append(" * \n").append(" */\n");
				buf2.append("public class ").append(dbBean).append(" implements Serializable {\n\n");
				buf2.append("	private static final long serialVersionUID = 1L;\n\n");
				StringBuilder fun = new StringBuilder();
				for (Row row : list) {
					String field = row.gets("COLUMN_NAME");
					String type = getType((String) row.get("COLUMN_TYPE"));
					if(!hasBigDecimal && type.equals("BigDecimal")) hasBigDecimal = true;
					buf2.append("	/** ").append(row.gets("COLUMN_COMMENT","")).append(" */\n");
					buf2.append("	private ").append(type).append(" ").append(field).append(";\n\n");
					fun.append("	public ").append(type).append(" get");
					fun.append(setFirstCharUpcase(field)).append("() {\n");
					fun.append("		return ").append(field).append(";\n	}\n\n");
					fun.append("	public void set").append(setFirstCharUpcase(field)).append("(");
					fun.append(type).append(" ").append(field).append("){\n");
					fun.append("		this.").append(field).append(" = ").append(field).append(";\n	}\n\n");
				}
				fun.append("	public String toString() {\n");
				fun.append("		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);\n");
				fun.append("	}\n\n");
				if(hasBigDecimal) buf.append("import java.math.BigDecimal;");
				buf.append(buf2).append(fun).append("}");
				FileUtil.writIn(bean, buf.toString(), Constant.UTF_8);
				System.out.println(bean+"生成完毕。");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建Dao类
	 * @param dbBean 数据库表对应对象名
	 * @param javaPath java源文件目录,如：D:/workspace/iw86/src/main/java
	 * @param packageName 包路径,如：com.iw86.xxx.dao
	 */
	public void createDao(String dbBean,String javaPath,String packageName){
		String daoPath = javaPath;
		try {
			daoPath += packageName.replaceAll("\\.", "/");
			String bean=daoPath+"/"+dbBean+"Dao.java";
			System.out.println(bean+"开始生成。");
			FileUtil.mkDirs(daoPath);
			StringBuilder ibuf = new StringBuilder(); //interface代码
			ibuf.append("package ").append(packageName).append(";\n\n");
			ibuf.append("import com.iw86.db.BaseDao;\n");
			ibuf.append("import org.springframework.stereotype.Repository;\n");
			ibuf.append("/**\n").append(" * @author AutoGenerator\n").append(" * \n").append(" */\n").append("@Repository\n");
			ibuf.append("public interface ").append(dbBean).append("Dao extends BaseDao {\n\n");
			ibuf.append("}");
			FileUtil.writIn(bean, ibuf.toString(), Constant.UTF_8);
			System.out.println(bean+"生成完毕。");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 创建iBatis用的sqlMap文件
	 * @param dbTable 表名
	 * @param dbBean 数据库表对应对象名
	 * @param mapPath 生成的文件路径,如：D:/workspace/iw86/src/resources/com/iw86/xxx/dao/
	 * @param beanName bean名,如：com.iw86.xxx.domain.User
	 * @param daoName dao名，如：com.iw86.xxx.dao.UserDao
	 */
	public void createSqlMap(String dbTable,String dbBean,String mapPath, String beanName, String daoName){
		try {
			JdbcUtil jdbc = new JdbcUtil(this.dataSource);
			jdbc.prepareStatement("desc " + dbTable);
			List<Row> list = jdbc.executeList();
			if (list != null) {
				String xml=mapPath+"/"+dbBean+"Dao.xml";
				System.out.println(xml+"开始生成。");
				String keyName=list.get(0).gets("COLUMN_NAME"); //目前取第一个字段作为主键
				StringBuilder buf = new StringBuilder();
				buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
				buf.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n\n");
				buf.append("<mapper namespace=\"").append(daoName).append("\">\n\n");
				buf.append("	<sql id=\"dynamicWhere\">\n");
				buf.append("		<where>\n");
				buf.append("			<if test=\"_parameter.containsKey('").append(keyName).append("s') and ").append(keyName).append("s != null\">\n");
				buf.append("				and ").append(keyName).append(" in (${").append(keyName).append("s})\n");
				buf.append("			</if>\n");
				buf.append("		</where>\n");
				buf.append("	</sql>\n\n");
				buf.append("	<select id=\"select\" resultType=\"").append(beanName).append("\">\n");
				buf.append("		select * from ").append(dbTable).append(" where ").append(keyName).append("=#{").append(keyName).append("}\n");
				buf.append("	</select>\n\n");
				buf.append("	<select id=\"selectRow\" resultType=\"com.iw86.collection.Row\">\n");
				buf.append("		select ${fields} from ").append(dbTable).append(" where ").append(keyName).append("=#{").append(keyName).append("}\n");
				buf.append("	</select>\n\n");
				buf.append("	<select id=\"count\" resultType=\"int\">\n");
				buf.append("		select count(*) from ").append(dbTable).append("\n");
				buf.append("		<include refid=\"dynamicWhere\" />\n");
				buf.append("	</select>\n\n");
				buf.append("	<select id=\"list\" resultType=\"").append(beanName).append("\">\n");
				buf.append("		select * from ").append(dbTable).append("\n");
				buf.append("		<include refid=\"dynamicWhere\" />\n");
				buf.append("		<if test=\"_parameter.containsKey('order') and order!=null\">\n");
				buf.append("			order by ${order}\n");
				buf.append("		</if>\n");
				buf.append("		limit #{start},#{size}\n");
				buf.append("	</select>\n\n");
				buf.append("	<select id=\"listRow\" resultType=\"com.iw86.collection.Row\">\n");
				buf.append("		select ${fields} from ").append(dbTable).append("\n");
				buf.append("		<include refid=\"dynamicWhere\" />\n");
				buf.append("		<if test=\"_parameter.containsKey('order') and order!=null\">\n");
				buf.append("			order by ${order}\n");
				buf.append("		</if>\n");
				buf.append("		limit #{start},#{size}\n");
				buf.append("	</select>\n\n");
				buf.append("	<insert id=\"insert\" parameterType=\"").append(beanName).append("\">\n");
				buf.append("		insert into ").append(dbTable).append(" (");
				StringBuilder insert = new StringBuilder();
				StringBuilder update = new StringBuilder();
				int i=0;
				for (Row row : list) {
					if(i!=0){ //主键不作insert和update
						String field = row.gets("COLUMN_NAME");
						String type = getType((String) row.get("COLUMN_TYPE"));
						if(i!=1){
							buf.append(",");
							insert.append(",");
						}
						buf.append(field);
						insert.append("#{").append(field).append("}");
						if("String".equals(type)){
							update.append("			<if test=\"").append(field).append(" != null\">\n");
						}else{
							update.append("			<if test=\"").append(field).append(" != 0\">\n");
						}
						update.append("				").append(field).append("=").append("#{").append(field).append("},\n");
						update.append("			</if>\n");
					}
					i++;
				}
				buf.append(")\n		values (").append(insert.toString()).append(")\n");
				buf.append("	</insert>\n\n");
				buf.append("	<update id=\"update\" parameterType=\"").append(beanName).append("\">\n");
				buf.append("		update ").append(dbTable).append(" \n");
				buf.append("		<set>\n");
				buf.append(update);
				buf.append("		</set>\n");
				buf.append("		where ").append(keyName).append("=#{").append(keyName).append("}\n");
				buf.append("	</update>\n\n");
				buf.append("	<update id=\"delete\">\n");
				buf.append("		delete from ").append(dbTable).append(" where ").append(keyName).append("=#{").append(keyName).append("}\n");
				buf.append("	</update>\n\n");
				buf.append("</mapper>");
				FileUtil.writIn(xml, buf.toString());
				System.out.println(xml+"生成完毕。");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 判断数据库字段对应的Java类型
	 * @param str
	 * @return
	 */
	private String getType(String str){
		if(StringUtil.isEmpty(str)) return "";
		str=str.toLowerCase();
		for(int i=0,n=intType.length;i<n;i++){
			if(str.startsWith(intType[i])){
				return "int";
			}
		}
		if(str.startsWith("bigint")) return "Long";
		else if(str.startsWith("float") || str.startsWith("decimal")) return "BigDecimal";
		else if(str.startsWith("double")) return "Double";
		else return "String";
	}
	
	/**
	 * 根据表名取对象名
	 * @param table
	 * @param includePrefix 是否包含前缀,例如:tb_xxx 其中tb_为前缀
	 * @return
	 */
	private String getBeanName(String table, boolean includePrefix) {
		StringBuffer sb = new StringBuffer();
		if (table.contains("_")) {
			String[] tables = table.split("_");
			int l = tables.length;
			int s = 0;
			if (includePrefix) {
				s = 1;
			}
			for (int i = s; i < l; i++) {
				String temp = tables[i].trim();
				sb.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1).toLowerCase());
			}
		} else {
			sb.append(table.substring(0, 1).toUpperCase()).append(table.substring(1).toLowerCase());
		}
		return sb.toString();
	}

	
    /**
     * 设置首字母为大写
     * @param s
     * @return
     */
    private static String setFirstCharUpcase(String s){
        if(s == null || s.length() < 1) return s;
        char c[] = s.toCharArray();
        if(c.length > 0 && c[0] >= 'a' && c[0] <= 'z'){
            c[0] = (char)((short)c[0] - 32);
        }
        return String.valueOf(c);
    }
	
}
