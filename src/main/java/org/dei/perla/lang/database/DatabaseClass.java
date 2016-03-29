package org.dei.perla.lang.database;

/*
 * Created by Francesco Filipazzi 28/03/2016
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Random;

import org.dei.perla.core.fpc.Attribute;

import java.sql.Driver;
public class DatabaseClass {
	
	private Connection con;
	private Statement cmd;
	
	public DatabaseClass(){
		
		
	}
	
	/*
	 * Non so che conoscenza hai di mysql da riga di comando, ciò che ti serve:
	 * CREATE DATABASE prove_context; fallo prima di far partire tutto
	 * 
	 * USE prove_context;
	 * DESCRIBE nometabella; ti fa vedere la struttura della tabella
	 * SELECT * FROM nometabella; ti fa vedere tutto ciò che è stato inserito
	 */
	
	public void connect() throws SQLException {
		
				
		String hostAddr= "jdbc:mysql://localhost:3306/prove_context";
		
		if (con!=null){
			con.close();
			this.con = DriverManager.getConnection(hostAddr, "root", "francesco89");
			this.cmd=con.createStatement();
			}
			else{
				this.con = DriverManager.getConnection(hostAddr, "root", "francesco89");
				this.cmd=con.createStatement();
				System.out.println(con.toString());
			}
		
	}
	
	/*
	 * createTable crea la tabella con il nome generato in QueryMenager
	 */
	
	public void createTable(Collection<Attribute> attributes, String tableName) {
		
		String createTableQuery="CREATE TABLE IF NOT EXISTS "+tableName
				+" (identifier INTEGER not NULL  AUTO_INCREMENT, ";
		String type="";
		for (Attribute attribute : attributes) {
			
											
			switch (attribute.getType().getId()) {
			case "integer":
				type="INTEGER";
				break;
			case "float":
				type="FLOAT";
				break;
			case "boolean":
				type="BOOLEAN";
				break;
			case "string":
				type = "VARCHAR(50)";
				break;
			case "timestamp":
				type="TIMESTAMP";
				break;
			}
			
			createTableQuery += attribute.getId() + " " + type + ",";
		}
		createTableQuery +=" PRIMARY KEY ( identifier ))"; 		
		try {
			cmd.executeUpdate(createTableQuery);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveData(String insertingQuery) throws SQLException{
		cmd.executeUpdate(insertingQuery);
	}
	
	
	public  void closeConnection() {
		
	      try {
			cmd.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
}