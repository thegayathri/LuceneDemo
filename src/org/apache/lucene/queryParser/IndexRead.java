package org.apache.lucene.queryParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class IndexRead {
    public static void main(String[] arg) throws IOException, ParseException{
       // the text you want to search
        String query = "jamie";
        StandardAnalyzer analyzer = new StandardAnalyzer();
        String index = "index";
        Directory directory = FSDirectory.open(Paths.get(index));
       //here you must specify the column you are searching as you put it in the index file
        Query q = new QueryParser("name", analyzer).parse(query);
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        //display result
        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println("Found a record name:[" + d.get("name") + "] \t charac:[" + d.get("charac")+"] \t age:["+ d.get("age") +"] \t description:["+ d.get("comp") +"]");
        }
    }
}