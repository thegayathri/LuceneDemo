package org.apache.lucene.queryParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTest {
	
	public static void main(String args[]) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:5555/lannister", "root", "root");
        String sqlQuery = "select * from house";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sqlQuery);
        System.out.println("is rs null " + rs.wasNull());
        while(rs.next()) {
        	System.out.println(rs.getString("name"));
        	System.out.println(rs.getString("charac"));
        	System.out.println(rs.getString("age"));
        }
	}

}
