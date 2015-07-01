package org.apache.lucene.queryParser;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class IndexWrite {
	private static final String INDEX_KEY_NAME = "name";
	private static final String INDEX_KEY_CHARAC = "charac";
	private static final String INDEX_KEY_AGE = "age";
	private static final String DB_KEY_NAME = "name";
	private static final String DB_KEY_CHARAC = "charac";
	private static final String DB_KEY_AGE = "age";

	public static void main(String[] args) throws Exception {
        //create a file which will hold the index files.make sur the path is correct
        String index = "index";
        //establish the mysql connection
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:5555/lannister", "root", "root");
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(FSDirectory.open(Paths.get(index)), config);
        String query = "select * from house";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query);
        //according to the results create the index files
        while (result.next()) {
            Document document = new Document();
        //add the fields to the index as you required
            document.add(new StringField(INDEX_KEY_NAME, result.getString(DB_KEY_NAME), Field.Store.YES));
            document.add(new StringField(INDEX_KEY_CHARAC, result.getString(DB_KEY_CHARAC), Field.Store.YES));
            document.add(new StringField(INDEX_KEY_AGE, result.getString(DB_KEY_AGE), Field.Store.YES));
           //create the index files
            writer.updateDocument(new Term(INDEX_KEY_NAME, result.getString(DB_KEY_NAME)), document);
        }
        writer.close();
    }
	
}