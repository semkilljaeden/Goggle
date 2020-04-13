package Clustering.easyClustering;

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
import java.util.LinkedList;
import java.util.Map;

public class ClusterBuilder extends Retriever {
    private static Logger logger = LogManager.getLogger(ClusterBuilder.class);

    IndexReader reader;
    public ClusterBuilder(IndexType type) throws IOException {
        reader = getReader(type);
        cluster = new HashMap<>();
    }

    HashMap<String, LinkedList<Integer>> cluster;


    public void clustering() throws IOException {
        for (int docId = 0; docId < reader.numDocs(); docId++) {
            Terms vector = reader.getTermVector(docId, "state");
            if(vector == null) {
                if(!cluster.containsKey("NoState")) {
                    cluster.put("NoState", new LinkedList<>());
                }
                cluster.get("NoState").add(docId);
                continue;
            }
            TermsEnum termsEnum = vector.iterator();
            BytesRef text = null;
            while ((text = termsEnum.next()) != null) {
                String term = text.utf8ToString();
                int freq = (int) termsEnum.totalTermFreq();
                if (freq > 0) {
                    if(!cluster.containsKey(term)) {
                        cluster.put(term, new LinkedList<>());
                    }
                    cluster.get(term).add(docId);
                }
            }
        }
    }


    public static void main(String[] args) {
        try {
            ClusterBuilder cb = new ClusterBuilder(IndexType.Business);
            cb.clustering();
            int count = 0;
            int total = 0;
            for(Map.Entry<String, LinkedList<Integer>> s : cb.cluster.entrySet()) {
                count++;
                logger.info("Cluster " + count + " name(top frequent word):" + s.getKey() + " contains " + s.getValue().size() + " docs");
                total += s.getValue().size();
            }
            logger.info("Total docs = " + total);
        } catch (IOException e) {
            logger.error("cannot load index", e);
        }

    }


}
