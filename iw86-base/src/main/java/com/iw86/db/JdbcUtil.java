/**
 * 
 */
package com.iw86.db;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.iw86.collection.Row;

/**
 * 纯jdbc操作类
 * @author tanghuang
 */
@SuppressWarnings("rawtypes")
public class JdbcUtil implements Closeable{
	
	private Connection conn = null;
	
    private PreparedStatement prepStmt = null;
    
    /**
     * 构造函数方式1
     * @param con
     */
    public JdbcUtil(Connection con){
    	this.conn = con;
    }
    
    public JdbcUtil(DataSource dataSource){
    	try {
			this.conn = dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 构造函数方式2
     * @param driver
     * @param url
     * @param user
     * @param password
     */
    public JdbcUtil(String driver, String url, String user, String password) {
		try {
			if(conn==null){
				Class.forName(driver);
				conn =DriverManager.getConnection(url,user,password);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
    
    /**
     * @param sql
     * @throws SQLException
     */
    public void prepareStatement(String sql) throws SQLException{
    	prepStmt = conn.prepareStatement(sql);
	}
	
	/**
	 * @param index
	 * @param value
	 * @throws SQLException
	 */
	public void setString(int index, String value) throws SQLException{
		prepStmt.setString(index, value);
	}
	
	public void setInt(int index, int value) throws SQLException{
		prepStmt.setInt(index, value);
	}
	
    /**
     * @param index
     * @param value
     * @throws SQLException
     */
    public void setObject(int index, Object value) throws SQLException{
	    if(value == null) value = "";
	    prepStmt.setObject(index, value);
	}

	/**
	 * @return
	 * @throws SQLException
	 */
	public ResultSet executeRs() throws SQLException{
	    return prepStmt.executeQuery();
	}
	
    /**
     * @return
     * @throws SQLException
     */
    public Row executeRow() throws SQLException{
    	return resultset2Row(executeRs());
    }
    
    /**
     * @return
     * @throws SQLException
     */
    public List<Row> executeList() throws SQLException{
    	return resultset2List(executeRs());
    }
    
    /**
     * @return
     * @throws SQLException
     */
    public int executeUpdate() throws SQLException{
	    if(prepStmt != null){
	        return prepStmt.executeUpdate();
	    } else{
	        return -1;
	    }
	}
	
    /**
     * 执行插入操作，返回插入的主键值(int)
     * @return
     * @throws SQLException
     */
    public int executeInsert() throws SQLException{
	    if(executeUpdate() > 0){
	        return getInsertId();
	    } else{
	        return -1;
	    }
	}
    
	@SuppressWarnings("unchecked")
	private Row resultset2Row(ResultSet rs){
	    Row row = null;
	    try{
	        ResultSetMetaData meta = rs.getMetaData();
	        int columnCount = meta.getColumnCount();
	        if(rs.next()){
	        	row = new Row();
	            for(int i = 1; i <= columnCount; i++){
	                Object oTemp = rs.getObject(i);
	                if(oTemp == null) oTemp = "";
	                row.put(meta.getColumnName(i), oTemp);
	            }
	        }
	    }catch(SQLException e){
	        e.printStackTrace();
	    }finally{
        	closeRs(rs);
        }
	    return row;
	}
	
	@SuppressWarnings("unchecked")
	private List<Row> resultset2List(ResultSet rs){
		List<Row> rowlist = new ArrayList<Row>();
	    try{
	        ResultSetMetaData meta = rs.getMetaData();
	        int columnCount = meta.getColumnCount();
	        while(rs.next()){
	        	Row row = new Row();
	            for(int i = 1; i <= columnCount; i++){
	                Object oTemp = rs.getObject(i);
	                if(oTemp == null) oTemp = "";
	                row.put(meta.getColumnName(i), oTemp);
	            }
	            rowlist.add(row);
	        }
	    }catch(SQLException e){
	        e.printStackTrace();
	    }finally{
        	closeRs(rs);
        }
	    return rowlist;
	}

    private int getInsertId() throws SQLException{
        int id = -1;
        ResultSet rs = null;
        try {
            rs = prepStmt.getGeneratedKeys();
            if(rs.next()){
                id = rs.getInt(1);
            }
        } catch(SQLException e) {
        	e.printStackTrace();
        }finally{
        	closeRs(rs);
        }
        return id;
    }
	
    /**
     * 关闭ResultSet
     * @param rs
     */
    public void closeRs(ResultSet rs){
    	if(rs!=null){
    		try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			rs=null;
    	}
    }
    
    /**
     * 关闭Statement
     */
    public void closeStmt(){
        if(prepStmt != null){
            try{
                prepStmt.close();
            }catch(SQLException e){
               e.printStackTrace();
            }
            prepStmt = null;
        }
    }
    
    /**
     * 关闭整个连接，包括Statement
     */
    public void close(){
        try{
            closeStmt();
            if(conn != null){
                conn.close();
                conn = null;
            }
        }catch(SQLException e){
        	e.printStackTrace();
        }
    }
    
}
