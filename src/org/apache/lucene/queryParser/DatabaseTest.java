package org.apache.lucene.queryParser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTest {
	
	public static void main(String args[]) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/houses", "root", "kalilinux");
        String sqlQuery = "select * from lannister";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sqlQuery);
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        List<String> columnNames = new ArrayList<String>();
        System.out.println("First columns is " + resultSetMetaData.getColumnName(5));
        for(int i=0;i<resultSetMetaData.getColumnCount();i++) {
            columnNames.add(resultSetMetaData.getColumnName(i+1));
            System.out.println(resultSetMetaData.getColumnName(i+1));
        }
        while(rs.next()) {

        	System.out.println(rs.getString("name"));
        	System.out.println(rs.getString("charac"));
        	System.out.println(rs.getString("age"));
        }
	}

}
