package Clustering.overlapCoefficient;

import Query.IndexType;
import Query.Retriever;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClusterBuilder extends Retriever {
    private static Logger logger = LogManager.getLogger(ClusterBuilder.class);
    public static String[] businessDocumentFieldNames = new String[] {
            "city",
            "state",
            "categories"
    };
    public HashMap<String, Integer> cityTermList;
    public HashMap<String, Integer> stateTermList;
    public HashMap<String, Integer> categoriesTermList;
    public HashMap<String, HashMap<String, Integer>> termList;
    public DocVector[] docVector;
    IndexReader reader;
    String[] currentFields;
    public ClusterBuilder(IndexType type, String[] fields) throws IOException {
        reader = getReader(type);
        currentFields = fields;
        docVector = new DocVector[reader.numDocs()];
        cityTermList = new HashMap<String, Integer>();
        stateTermList = new HashMap<String, Integer>();
        categoriesTermList = new HashMap<String, Integer>();
        termList = new HashMap<>();
        termList.put("city", cityTermList);
        termList.put("state", cityTermList);
        termList.put("categories", cityTermList);
        initTermLIst();
    }
    public void initTermLIst() throws IOException {
        int pos = 0;
        for (int docId = 0; docId < reader.numDocs(); docId++) {
            for(String field : currentFields) {
                Terms vector = reader.getTermVector(docId, field);
                if(vector == null) {
                    continue;
                }
                TermsEnum termsEnum = vector.iterator();
                BytesRef text = null;
                while ((text = termsEnum.next()) != null) {
                    String term = text.utf8ToString();
                    termList.get(field).put(term, 0);
                }
            }
            if(docId % 10000 == 0)
                logger.info("processed " + docId + " docs");
        }
        for(Map.Entry<String,HashMap<String, Integer>> map : termList.entrySet()) {
            pos = 0;
            for(Map.Entry<String,Integer> s : map.getValue().entrySet())
            {
                s.setValue(pos++);
            }
        }
    }

    public HashMap<String, HashMap<String, Integer>> cloneTermList() {
        HashMap<String, Integer> cityTermList = new HashMap<String, Integer>(this.cityTermList);
        HashMap<String, Integer> stateTermList = new HashMap<String, Integer>(this.stateTermList);
        HashMap<String, Integer> categoriesTermList = new HashMap<String, Integer>(this.categoriesTermList);
        HashMap<String, HashMap<String, Integer>> termList = new HashMap<>();
        termList.put("city", cityTermList);
        termList.put("state", cityTermList);
        termList.put("categories", cityTermList);
        return termList;
    }

    public DocVector[] GetDocumentVectors() throws IOException {
        for (int docId = 0; docId < reader.numDocs(); docId++) {
            for(String field : currentFields) {
                Terms vector = reader.getTermVector(docId, field);
                if(vector == null) {
                    continue;
                }
                TermsEnum termsEnum = vector.iterator();
                BytesRef text = null;
                docVector[docId] = new DocVector(cloneTermList());
                while ((text = termsEnum.next()) != null) {
                    String term = text.utf8ToString();
                    int freq = (int) termsEnum.totalTermFreq();
                    if (freq > 0) {
                        docVector[docId].setEntry(field, term);
                    }
                }
            }
            if(docId % 10000 == 0)
                logger.info("processed " + docId + " docs");
        }
        return docVector;
    }


    public static void main(String[] args) {
        try {
            ClusterBuilder cb = new ClusterBuilder(IndexType.Business, ClusterBuilder.businessDocumentFieldNames);
            DocVector[] docVector = cb.GetDocumentVectors();
            //new KMean(6, Arrays.stream(docVector).map(s -> s.vector).toArray(RealVector[]::new)).run();
        } catch (IOException e) {
            logger.error("cannot load index", e);
        }

    }


}
