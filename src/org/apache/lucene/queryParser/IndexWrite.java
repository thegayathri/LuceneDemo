package org.apache.lucene.queryParser;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IndexWrite {
    private static final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:5555/lannister";
    private static final String DB_USER_NAME = "root";
    private static final String DB_PASSWORD = "kalilinux";
    private static List<String> mColumnNames;
    private static String mTableName;
    private static String mIndexPath;

	public static void main(String[] args) throws Exception {
        getArgs(args);
        ResultSet resultSet = getDBResults(mTableName, null);
        indexResults(resultSet);
    }

    private static void getArgs(String[] args) {
        mTableName = "house";
        mIndexPath = "index";
        for(int i=0;i<args.length;i++) {
            if("-table".equals(args[i])) {
                mTableName = args[i+1];
            } else if("-index".equals(args[i])) {
                mIndexPath = args[i+1];
            }
        }
    }

    /**
     * Gives the column names from a result set
     * @param resultSet
     * @return list of column names present in the result set
     */
    private static List<String> getColumnNames(ResultSet resultSet) {
        List<String> columnNames = new ArrayList<String>();
        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            for(int i=0;i<resultSetMetaData.getColumnCount();i++) {
                columnNames.add(resultSetMetaData.getColumnName(i+1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Queries the given table and returns the result. If no query is given returns the entire table
     * @param query Query that has to be executed on the given table
     * @return ResultSet corresponding to the query
     */
    private static ResultSet getDBResults(String tableName, String query) {
        if(query==null) {
            query = "SELECT * FROM " + tableName;
        }
        try {
            Class.forName(DB_CLASS_NAME).newInstance();
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER_NAME, DB_PASSWORD);
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create index files according to the results set from the database
     * @param resultSet Results from the database query
     */
    private static void indexResults(ResultSet resultSet) {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(FSDirectory.open(Paths.get(mIndexPath)), config);
            while (resultSet.next()) {
                Document document = new Document();
                //add the fields to the index as you required
                document.add(new LongField("id", resultSet.getInt("id"), Field.Store.NO));
                document.add(new StringField("name", resultSet.getString("name"), Field.Store.YES));
                document.add(new StringField("title", resultSet.getString("title"), Field.Store.YES));
                document.add(new StringField("status", resultSet.getString("status"), Field.Store.YES));
                document.add(new LongField("age", resultSet.getLong("age"), Field.Store.YES));
                //create the index files
                writer.updateDocument(new Term("name", resultSet.getString("name")), document);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}