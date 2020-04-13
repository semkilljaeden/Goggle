package Indexing;

import Indexing.Model.Model;
import Indexing.Translator.Converter;
import Indexing.Translator.GeneralConverter;
import Util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.nio.file.Paths;

public class Indexer<T extends Model> {
    org.apache.lucene.analysis.Analyzer Analyzer;
    Directory Dir;
    IndexWriter Writer;
    Class<T> cls;
    private static Logger logger = LogManager.getLogger(Indexer.class);



    public Indexer(Class<T> cls){
        this.cls = cls;
        Analyzer = new AnalyzerWrapper(Analyzer.PER_FIELD_REUSE_STRATEGY) {
            @Override
            protected org.apache.lucene.analysis.Analyzer getWrappedAnalyzer(String fieldName) {
                return fieldName.equals("friends") || fieldName.equals("elite") ? new WhitespaceAnalyzer() : new StandardAnalyzer();
            }
        };
        // 1. create the index
        try {
            Dir = FSDirectory.open(Paths.get(Utils.getProps("indexPath") + cls.getSimpleName()));
            IndexWriterConfig config = new IndexWriterConfig(Analyzer);
            Writer = new IndexWriter(Dir, config);
        } catch (IOException e) {
            logger.error("IO Error", e);
        }
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

    public void CompleteIndexing() throws IOException {
        Writer.flush();
        Writer.close();
    }


}
