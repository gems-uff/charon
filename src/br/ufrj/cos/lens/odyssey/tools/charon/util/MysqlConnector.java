package br.ufrj.cos.lens.odyssey.tools.charon.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MysqlConnector {
	private String url = null;
	private String user = null;
	private String passwd = null;
	private Connection session = null;
	
	public MysqlConnector(String database, String user, String passwd) {
		this.user = user;
		this.passwd = passwd;
		
		this.url = database;
		
	}
	
	public synchronized void connect(String param) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			session = DriverManager.getConnection(url, user, passwd);
		} catch (Exception e) {
			System.out.println("Erro de conexão com o banco");
		}
	}
	
	public synchronized void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			session = DriverManager.getConnection(url, user, passwd);
		} catch (Exception e) {
			System.out.println("Erro de conexão com o banco");
		}
	}
	
	public boolean isConnected() {
		boolean status=false;
		
		if(session == null) {
			return false;
		}
		
		try {
			status = !(session.isClosed());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return status;
	}
	
	public synchronized void disconnect() {
		try {
	        if(session != null) {
	          session.close();
	        }
	      } catch(SQLException e) {
	    	  e.printStackTrace();
	      }
	}
	
	public ResultSet query(String sql) {
		ResultSet set = null;
		try {
			Statement stm = session.createStatement();
			set = stm.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return set;
	}
	
	public void exec(String sql) {
		
		try {
			Statement stm = session.createStatement();
			stm.execute(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void a(ArrayList<Object> b){
		int c=1;
	}
//	public String createInsertQuery(String tableName, String[] params, String[] values){
	public String createInsertQuery(String tableName, String paramsa, String valuesa){
		String[] params=null;
		String[] values=null;
		String query = "INSERT INTO "+tableName+" ";
		
		String paramList = "";
		String valueList = "";
		
		if(params.length>0){
			paramList = "("+params[0];
			valueList = " VALUES (\""+values[0]+"\"";
		}
		
		for (int i=1; i<params.length; i++) {
			paramList += ", \"" + params[i]+"\"";
			valueList += ", \"" + values[i]+"\"";
		}
		
		if(params.length>0){
			paramList = ")";
			valueList = ")";
		}
		
		return query + paramList + valueList;
	}
}
