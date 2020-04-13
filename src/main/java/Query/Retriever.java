package Query;

import Util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class Retriever {
    private static Logger logger = LogManager.getLogger(Retriever.class);
    Directory directory;
    org.apache.lucene.analysis.Analyzer Analyzer;
    IndexReader businessReader;
    IndexReader tipReader;
    IndexReader reviewReader;
    IndexReader userReader;
    public Retriever() {
        try {
            businessReader = DirectoryReader.open(FSDirectory.open(Paths.get(Utils.getProps("indexPath")+ "Business")));
            tipReader = DirectoryReader.open(FSDirectory.open(Paths.get(Utils.getProps("indexPath") + "Tip")));
            reviewReader = DirectoryReader.open(FSDirectory.open(Paths.get(Utils.getProps("indexPath") + "Review")));
            userReader = DirectoryReader.open(FSDirectory.open(Paths.get(Utils.getProps("indexPath") + "User")));
            Analyzer = new AnalyzerWrapper(Analyzer.PER_FIELD_REUSE_STRATEGY) {
                @Override
                protected org.apache.lucene.analysis.Analyzer getWrappedAnalyzer(String fieldName) {
                    return fieldName.equals("friends") || fieldName.equals("elite") ? new WhitespaceAnalyzer() : new StandardAnalyzer();
                }
            };
        } catch (IOException e) {
            logger.error("cannot load index", e);
        }
    }

    public TopDocs query(Query query, Similarity sim, IndexType type) throws IOException {
        IndexSearcher searcher = getSearcher(type);
        if(sim != null) {
            searcher.setSimilarity(sim);
        }

        TopDocs docs = searcher.search(query, 10);
        return docs;
    }

    public IndexSearcher getSearcher(IndexType type) {
        return new IndexSearcher(getReader(type));
    }

    public IndexReader getReader(IndexType type) {
        switch (type) {
            case Tip:
                return tipReader;
            case User:
                return userReader;
            case Review:
                return reviewReader;
            case Business:
                return businessReader;
            default:
                return null;
        }
    }
}
