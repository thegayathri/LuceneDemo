package org.apache.lucene.queryParser;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
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

import java.io.IOException;
import java.nio.file.Paths;

public class IndexRead {
    private static final String USAGE = "Usage:\tjava org.apache.lucene.demo.IndexRead [-index dir] [-field f] [-query string] [-paging hitsPerPage]";
    private static String mIndexPath;
    private static int mHitsPerPage;
    private static String mQuery;
    private static IndexSearcher mIndexSearcher;
    private static String mField;
    public static void main(String[] args) throws IOException, ParseException{
        initArgs(args);
        initSearcher();
        ScoreDoc[] searchResults = searchQuery(mIndexSearcher, mField, mQuery, mHitsPerPage);
        displayResults(mIndexSearcher, searchResults);
    }

    private static void initArgs(String args[]) {

        if (args.length > 0
                && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
            System.out.println(USAGE);
            System.exit(0);
        }

        mIndexPath = "index"; //TODO: make required index path as default
        mHitsPerPage = 10;
        mQuery = "Cersei"; //TODO: remove this and make a prompt when wrong query is given
        mField = "name"; //TODO: remove this and make required database field as default
        for(int i=0;i<args.length;i++) {
            if("-query".equals(args[i])) {
                mQuery = args[i+1];
            } else if("-index".equals(args[i])) {
                mIndexPath = args[i+1];
            } else if("-field".equals(args[i])) {
                mField = args[i+1];
            } else if ("-hits".equals(args[i])) {
                mHitsPerPage = Integer.parseInt(args[i+1]);
                if(mHitsPerPage<=0) {
                    System.err.println("There must be at least 1 hit per page");
                    System.exit(1);
                }
            }
        }
    }

    private static void initSearcher() {
        try {
            Directory directory = FSDirectory.open(Paths.get(mIndexPath));
            IndexReader reader = DirectoryReader.open(directory);
            mIndexSearcher = new IndexSearcher(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ScoreDoc[] searchQuery(IndexSearcher indexSearcher, String field, String needle, int hitsPerPage) {
        KeywordAnalyzer keywordAnalyzer = new KeywordAnalyzer();
        try {
            QueryParser queryParser = new QueryParser(field, keywordAnalyzer);
            queryParser.setLowercaseExpandedTerms(false);
            Query query = queryParser.parse(needle);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
            indexSearcher.search(query, collector);
            return collector.topDocs().scoreDocs;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Displays details of the search results using given index searcher
     * @param indexSearcher IndexSearcher to get indexed documents
     * @param searchResults Search results for the given query
     * @throws IOException
     */
    public static void displayResults(IndexSearcher indexSearcher, ScoreDoc[] searchResults) throws IOException {
        System.out.println("Found " + searchResults.length + " hits.");
        for(int i=0;i<searchResults.length;++i) {
            int docId = searchResults[i].doc;
            Document document = indexSearcher.doc(docId);
            System.out.println("Name : " + document.get("name"));
            System.out.println("Title : " + document.get("title"));
            System.out.println("Age : " + document.get("age"));
            System.out.println("Status : " + document.get("status"));
        }
    }
}