package br.ufrj.cos.lens.odyssey.tools.charon.util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class MysqlConnector {
	private String url = null;
	private String user = null;
	private String passwd = null;
	private Connection session = null;
	
	public MysqlConnector(String databaseURL, String user, String passwd) {
		this.user = user;
		this.passwd = passwd;
		
//		this.url = new String("jdbc:mysql://localhost:3306/")+database;
		this.url = databaseURL;
		
	}
	
	public MysqlConnector() {
		
		Properties props = new Properties();
		try {
			props.load(new FileInputStream("INF/INF.properties"));
		} catch (Exception e) {e.printStackTrace();}
		
		this.user = props.getProperty("db.username");
		this.passwd = props.getProperty("db.password");
		this.url = props.getProperty("db.address");
		
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
}
