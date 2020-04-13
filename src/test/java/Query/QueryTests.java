package Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.junit.Test;


import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class QueryTests {

    Retriever retriever = new Retriever();
    private static Logger logger = LogManager.getLogger(QueryTests.class);


    public List<Function<Similarity, TopDocs>> buildQueries() {
        LinkedList<Function<Similarity, TopDocs>> list = new LinkedList<>();
        list.add((Similarity sim) -> {
            //Shops that opens after 9am and allows dogin Las Vegas
            BooleanQuery.Builder b = new BooleanQuery.Builder();
            b.add(new TermQuery(new Term("city", "Las Vegas")), BooleanClause.Occur.MUST);
            b.add(new TermQuery(new Term("DogsAllowed", "True")), BooleanClause.Occur.MUST);
            b.add( new TermQuery(new Term("DogsAllowed", "True")), BooleanClause.Occur.MUST);
            BooleanQuery.Builder sub = new BooleanQuery.Builder();
            sub.add(IntPoint.newRangeQuery("monday.openingTime", 900, 2359), BooleanClause.Occur.SHOULD);
            sub.add(IntPoint.newRangeQuery("tuesday.openingTime", 900, 2359), BooleanClause.Occur.SHOULD);
            sub.add(IntPoint.newRangeQuery("wednesday.openingTime", 900, 2359), BooleanClause.Occur.SHOULD);
            sub.add(IntPoint.newRangeQuery("thursday.openingTime", 900, 2359), BooleanClause.Occur.SHOULD);
            sub.add(IntPoint.newRangeQuery("friday.openingTime", 900, 2359), BooleanClause.Occur.SHOULD);
            sub.add(IntPoint.newRangeQuery("saturday.openingTime", 900, 2359), BooleanClause.Occur.SHOULD);
            sub.add(IntPoint.newRangeQuery("sunday.openingTime", 900, 2359), BooleanClause.Occur.SHOULD);
            Query subQuery = sub.build();
            BooleanQuery.Builder full = new BooleanQuery.Builder();
            full.add(b.build(), BooleanClause.Occur.MUST);
            full.add(subQuery, BooleanClause.Occur.MUST);
            try {
                return retriever.query(full.build(), sim, IndexType.Business);
            } catch (IOException e) {
                logger.error("io fault", e);
            }
            return null;
        });
        list.add((Similarity sim) -> {
            //categories = hotels，BusinessAcceptsCreditCards=True and (WiFi = u'paid' or 'paid')
            BooleanQuery.Builder b = new BooleanQuery.Builder();
            b.add(new TermQuery(new Term("categories", "hotels")), BooleanClause.Occur.MUST);
            b.add(new TermQuery(new Term("BusinessAcceptsCreditCards", "True")), BooleanClause.Occur.MUST);
            BooleanQuery.Builder sub = new BooleanQuery.Builder();
            sub.add(new TermQuery(new Term("WiFI", "u'paid'")), BooleanClause.Occur.SHOULD);
            sub.add(new TermQuery(new Term("WiFI", "'paid'")), BooleanClause.Occur.SHOULD);
            Query subQuery = sub.build();
            BooleanQuery.Builder full = new BooleanQuery.Builder();
            full.add(b.build(), BooleanClause.Occur.MUST);
            full.add(subQuery, BooleanClause.Occur.MUST);
            try {
                return retriever.query(full.build(), sim, IndexType.Business);
            } catch (IOException e) {
                logger.error("io fault", e);
            }
            return null;
        });
        list.add((Similarity sim) -> {
            //star >=3 and in Ceveland and accept credit card
            BooleanQuery.Builder b = new BooleanQuery.Builder();
            b.add(DoublePoint.newRangeQuery("stars", 3, 10000), BooleanClause.Occur.MUST);
            b.add(new TermQuery(new Term("BusinessAcceptsCreditCards", "True")), BooleanClause.Occur.MUST);
            b.add(new TermQuery(new Term("city", "Cleveland")), BooleanClause.Occur.MUST);
            try {
                return retriever.query(b.build(), sim, IndexType.Business);
            } catch (IOException e) {
                logger.error("io fault", e);
            }
            return null;
        });
        list.add((Similarity sim) -> {
            //star >=3 and in Ceveland and accept credit card
            BooleanQuery.Builder b = new BooleanQuery.Builder();
            b.add(DoublePoint.newRangeQuery("stars", 3, 10000), BooleanClause.Occur.MUST);
            b.add(new TermQuery(new Term("BusinessAcceptsCreditCards", "True")), BooleanClause.Occur.MUST);
            b.add(new TermQuery(new Term("city", "Cleveland")), BooleanClause.Occur.MUST);
            try {
                return retriever.query(b.build(), sim, IndexType.Business);
            } catch (IOException e) {
                logger.error("io fault", e);
            }
            return null;
        });
        list.add((Similarity sim) -> {
            //modecal store in AZ no business parking valet and no business parking garage
            BooleanQuery.Builder b = new BooleanQuery.Builder();
            b.add(new TermQuery(new Term("categories", "medical")), BooleanClause.Occur.MUST);
            b.add(new TermQuery(new Term("BusinessParking.garage", "False")), BooleanClause.Occur.MUST);
            BooleanQuery.Builder sub = new BooleanQuery.Builder();
            sub.add(new TermQuery(new Term("WiFI", "u'paid'")), BooleanClause.Occur.SHOULD);
            sub.add(new TermQuery(new Term("WiFI", "'paid'")), BooleanClause.Occur.SHOULD);
            Query subQuery = sub.build();
            BooleanQuery.Builder full = new BooleanQuery.Builder();
            full.add(b.build(), BooleanClause.Occur.MUST);
            full.add(subQuery, BooleanClause.Occur.MUST);
            try {
                return retriever.query(full.build(), sim, IndexType.Business);
            } catch (IOException e) {
                logger.error("io fault", e);
            }
            return null;
        });
        return list;
    }

    @Test
    public void testBooleanQuery() {
        int count = 1;
        logger.info("Testing Boolean Query");
        for(Function<Similarity, TopDocs> s : buildQueries()) {
            long currentTime = System.nanoTime();
            TopDocs result = s.apply(null);
            logger.info("Query " + count + " Processing Time:" + ((System.nanoTime() - currentTime) / 1000000) + " milliseconds");
            count++;
            Arrays.stream(result.scoreDocs).forEach(v -> {
                try {
                    Document doc = retriever.getSearcher(IndexType.Business).doc(v.doc);
                    logger.info("Score:" + v.score + " " + ((IndexableField)doc.iterator().next()).name() + ": " + ((IndexableField)doc.iterator().next()).stringValue());
                } catch (IOException e) {
                    logger.error("io fault", e);
                }
            });
        }
        logger.info("================================================================================================");
    }

    @Test
    public void testRankingQuery() {
        int count = 1;
        logger.info("Testing Ranking Query (BM25)");
        for(Function<Similarity, TopDocs> s : buildQueries()) {
            long currentTime = System.nanoTime();
            TopDocs result = s.apply(new BM25Similarity());
            logger.info("Query " + count + " Processing Time:" + ((System.nanoTime() - currentTime) / 1000000) + " milliseconds");
            count++;
            Arrays.stream(result.scoreDocs).forEach(v -> {
                try {
                    Document doc = retriever.getSearcher(IndexType.Business).doc(v.doc);
                    logger.info("Score:" + v.score + " " + ((IndexableField)doc.iterator().next()).name() + ": " + ((IndexableField)doc.iterator().next()).stringValue());
                } catch (IOException e) {
                    logger.error("io fault", e);
                }
            });
        }
    }

}