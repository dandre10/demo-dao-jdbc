package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DB {

	
	private static Connection conn = null;
	
	//m�todo para conectar com o banco
	public static Connection getConnection() {
		if(conn == null) {
			try {
				Properties props = loadProperties();
				String url = props.getProperty("dburl");
				conn = DriverManager.getConnection(url,props);
			}
			catch (SQLException erro){
				throw new DbException(erro.getMessage());
			}
		}
		return conn;
	}
	
	
	//m�todo para fechar a conex�o com o banco
	public static void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			}
			catch (SQLException erro){
				throw new DbException(erro.getMessage());
			}
		}
	}
	
	
	
	//m�todo auxiliar para ler o arquivo db.properties que cont�m as conf do banco
	private static Properties loadProperties() {
		try(FileInputStream fs = new FileInputStream("db.properties")){
			Properties props = new Properties();
			props.load(fs);
			return props;
		}
		catch (IOException erro){
			throw new DbException(erro.getMessage());
		}
	}
}
