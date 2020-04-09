import Model.Business.*;
import Translator.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.nio.file.Paths;

public class Indexer<T extends Model> {
    static org.apache.lucene.analysis.Analyzer Analyzer;
    static Directory Dir;
    static IndexWriter Writer;
    Class<T> cls;
    private static Logger logger = LogManager.getLogger(Indexer.class);

    static {
        Analyzer = new AnalyzerWrapper(Analyzer.PER_FIELD_REUSE_STRATEGY) {
            @Override
            protected org.apache.lucene.analysis.Analyzer getWrappedAnalyzer(String fieldName) {
                return fieldName.equals("friends") || fieldName.equals("elite") ? new WhitespaceAnalyzer() : new StandardAnalyzer();
            }
        };
        // 1. create the index
        try {
            Dir = FSDirectory.open(Paths.get(Utils.getProps("indexPath")));
            IndexWriterConfig config = new IndexWriterConfig(Analyzer);
            Writer = new IndexWriter(Dir, config);
        } catch (IOException e) {
            logger.error("IO Error", e);
        }
    }

    public Indexer(Class<T> cls){
        this.cls = cls;

    }

    public void generalIndex(String token) throws IOException {
        JSONObject json = (JSONObject)JSONValue.parse(token);
        Writer.addDocument(GeneralConverter.Convert(json));
    }

    public void IndexModel(String token) throws IOException {
        try {
            T biz = new ObjectMapper().readValue(token, cls);
            Document doc = Converter.getConverter(cls).Convert(biz);
            Writer.addDocument(doc);
        }
        catch (Exception e) {
            logger.error("could not parse json", e);
        }
        //
    }

    public static void CompleteIndexing() throws IOException {
        Writer.flush();
        Writer.close();
    }

    public void Query() {
        int hitsPerPage = 10;
        try {
            IndexReader reader = DirectoryReader.open(Dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Query q = new QueryParser("state", Analyzer).parse("NA");
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

}
