package Query;

import Util.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class QueryTests {

    Retriever retriever = new Retriever();
    private static Logger logger = LogManager.getLogger(QueryTests.class);


    public List<Function<Similarity, Tuple<IndexType, TopDocs>>> buildQueries() {
        LinkedList<Function<Similarity, Tuple<IndexType, TopDocs>>> list = new LinkedList<>();

        /***
         *  Advance Query for geological query
         */
        list.add((Similarity sim) -> {
            //Find all business that are nearby a gym called Xtreme Couture
            double epislon = 0.005;
            try {
                Query q = new QueryParser("name", new StandardAnalyzer()).parse("name:\"Xtreme Couture MMA\"");
                TopDocs docs = retriever.query(q, sim, IndexType.Business);
                Document doc = retriever.getSearcher(IndexType.Business).doc(docs.scoreDocs[0].doc);
                double latitude = doc.getField("latitude").numericValue().doubleValue();
                double longitude = doc.getField("longitude").numericValue().doubleValue();
                Query q2 = DoublePoint.newRangeQuery("latitude", latitude - epislon, latitude + epislon);
                Query q3 = DoublePoint.newRangeQuery("longitude", longitude - epislon, longitude + epislon);
                BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
                finalQuery.add(q2, BooleanClause.Occur.MUST);
                finalQuery.add(q3, BooleanClause.Occur.MUST);
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(finalQuery.build(), sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });

        list.add((Similarity sim) -> {
            //Find all restaurants that are nearby a gym called Xtreme Couture
            double epislon = 0.005;
            try {
                Query q = new QueryParser("name", new StandardAnalyzer()).parse("name:\"Xtreme Couture MMA\"");
                TopDocs docs = retriever.query(q, sim, IndexType.Business);
                Document doc = retriever.getSearcher(IndexType.Business).doc(docs.scoreDocs[0].doc);
                double latitude = doc.getField("latitude").numericValue().doubleValue();
                double longitude = doc.getField("longitude").numericValue().doubleValue();
                Query q2 = DoublePoint.newRangeQuery("latitude", latitude - epislon, latitude + epislon);
                Query q3 = DoublePoint.newRangeQuery("longitude", longitude - epislon, longitude + epislon);
                Query q4 = new QueryParser("", new StandardAnalyzer()).parse("categories: restaurants");
                BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
                finalQuery.add(q2, BooleanClause.Occur.MUST);
                finalQuery.add(q3, BooleanClause.Occur.MUST);
                finalQuery.add(q4, BooleanClause.Occur.MUST);
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(finalQuery.build(), sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });





        /***
         *  Normal Query for geological query
         */

        list.add((Similarity sim) -> {
            //yelpingSince: 2010
            try {
                Query q = new QueryParser("name", retriever.Analyzer).parse("yelpingSince: 2010");
                return new Tuple<IndexType, TopDocs>(IndexType.User,retriever.query(q, sim, IndexType.User));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                Query q = new QueryParser("name", retriever.Analyzer).parse("categories: salons AND Music =dj");
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(q, sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                //"text:amazing AND stars: [4 TO 5] AND useful>= 100"
                Query q = new QueryParser("name", new WhitespaceAnalyzer()).parse("text:amazing");
                Query rangeQuery = DoublePoint.newRangeQuery("stars",  4, 5);
                Query rangeQuery2 = DoublePoint.newRangeQuery("useful",  100, 100000);
                BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
                finalQuery.add(q, BooleanClause.Occur.MUST);
                finalQuery.add(rangeQuery, BooleanClause.Occur.MUST);
                finalQuery.add(rangeQuery2, BooleanClause.Occur.MUST);
                return new Tuple<IndexType, TopDocs>(IndexType.Review,retriever.query(q, sim, IndexType.Review));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                Query q = new QueryParser("", new WhitespaceAnalyzer()).parse("state: AZ AND categories: automotive AND BusinessAcceptsCreditCards: true");
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(q, sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                Query q = new QueryParser("name", new WhitespaceAnalyzer()).parse("categories: restaurants AND (NoiseLevel: u'quiet OR NoiseLevel: quiet)");
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(q, sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                Query q = new QueryParser("name", new WhitespaceAnalyzer()).parse("state: NV AND categories: restaurants AND RestaurantsDelivery: true");
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(q, sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                Query q = new QueryParser("name", new WhitespaceAnalyzer()).parse("state: AZ AND categories: doctors AND ByAppointmentOnly:false");
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(q, sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                Query q = new QueryParser("name", new WhitespaceAnalyzer()).parse("state: NY AND categories: restaurants AND GoodForKids:true");
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(q, sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });

        list.add((Similarity sim) -> {
            try {
                Query q = new QueryParser("name", new WhitespaceAnalyzer()).parse("categories: hotels AND BusinessAcceptsCreditCards: true AND (WiFi: u'free OR WiFi: free)");
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(q, sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                Query q = new QueryParser("name", new WhitespaceAnalyzer()).parse("GoodForKids:true AND BikeParking: true AND Alcohol: u'none");
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(q, sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });

        list.add((Similarity sim) -> {
            try {
                Query q = new QueryParser("name", new WhitespaceAnalyzer()).parse("postal_code:89109 AND categories: restaurants");
                Query rangeQuery = DoublePoint.newRangeQuery("stars",  4, 5);
                BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
                finalQuery.add(q, BooleanClause.Occur.MUST);
                finalQuery.add(rangeQuery, BooleanClause.Occur.MUST);
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(finalQuery.build(), sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });

        list.add((Similarity sim) -> {
            try {
                Query q = new QueryParser("name", new WhitespaceAnalyzer()).parse("city: Phoenix AND RestaurantsTakeOut:true AND categories:mexican");
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(q, sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                Query q = new QueryParser("name", new WhitespaceAnalyzer()).parse("categories: restaurants AND (RestaurantsAttire: u'casual OR RestaurantsAttire: casual)");
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(q, sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                Query q = new QueryParser("name", new WhitespaceAnalyzer()).parse("city: Charlotte AND DriveThru: true AND categories: restaurants");
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(q, sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                //state: NV AND BusinessAcceptsCreditCards = true AND categories: hotels AND stars: [3 TO 5]
                Query q = new QueryParser("", new WhitespaceAnalyzer()).parse("state: NV AND BusinessAcceptsCreditCards:true AND categories:hotels");
                Query rangeQuery = DoublePoint.newRangeQuery("stars",  3, 5);
                BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
                finalQuery.add(q, BooleanClause.Occur.MUST);
                finalQuery.add(rangeQuery, BooleanClause.Occur.MUST);
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(finalQuery.build(), sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                //DogsAllowed:true AND city:Toronto AND monday.openingTime:0
                Query q = new QueryParser("", new WhitespaceAnalyzer()).parse("DogsAllowed:true AND city:Toronto");
                Query rangeQuery = IntPoint.newRangeQuery("monday.openingTime",  0, 0);
                BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
                finalQuery.add(q, BooleanClause.Occur.MUST);
                finalQuery.add(rangeQuery, BooleanClause.Occur.MUST);
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(finalQuery.build(), sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                //categories:medical AND state: AZ AND BusinessAcceptsCreditCards:true AND review_count: [20 TO 500]
                Query q = new QueryParser("", new WhitespaceAnalyzer()).parse("categories:medical AND state: AZ AND BusinessAcceptsCreditCards:true");
                Query rangeQuery = IntPoint.newRangeQuery("review_count",  20, 500);
                BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
                finalQuery.add(q, BooleanClause.Occur.MUST);
                finalQuery.add(rangeQuery, BooleanClause.Occur.MUST);
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(finalQuery.build(), sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                Query q = new QueryParser("name", new WhitespaceAnalyzer()).parse("RestaurantsCounterService:true AND RestaurantsTakeOut: true AND Caters:false AND categories:restaurants");
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(q, sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                //HasTV:true AND RestaurantsGoodForGroups: true AND Alcohol: u'full_bar AND friday.closeTime: 2323 AND review_count: [50 TO 200]
                Query q = new QueryParser("name", new WhitespaceAnalyzer()).parse("HasTV:true AND RestaurantsGoodForGroups: true AND Alcohol: u'full_bar");
                Query rangeQuery = IntPoint.newRangeQuery("friday.closeTime",  2323, 2323);
                Query rangeQuery2 = IntPoint.newRangeQuery("review_count",  50, 200);
                BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
                finalQuery.add(q, BooleanClause.Occur.MUST);
                finalQuery.add(rangeQuery, BooleanClause.Occur.MUST);
                finalQuery.add(rangeQuery2, BooleanClause.Occur.MUST);
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(finalQuery.build(), sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        list.add((Similarity sim) -> {
            try {
                Query q = new QueryParser("name", new WhitespaceAnalyzer()).parse("BusinessAcceptsBitcoin:true AND ByAppointmentOnly: true AND is_open:True");
                return new Tuple<IndexType, TopDocs>(IndexType.Business,retriever.query(q, sim, IndexType.Business));
            } catch (ParseException | IOException e) {
                Assert.fail();
            }
            return null;
        });
        return list;
    }

    @Test
    public void testBooleanQuery() {
        int count = 1;
        logger.info("Testing Boolean Query");
        for(Function<Similarity, Tuple<IndexType, TopDocs>> s : buildQueries()) {
            long currentTime = System.nanoTime();
            Tuple<IndexType, TopDocs> result = s.apply(new BooleanSimilarity());
            logger.info("Query " + count + " Processing Time:" + ((System.nanoTime() - currentTime) / 1000000) + " milliseconds");
            count++;
            Arrays.stream(result.y.scoreDocs).forEach(v -> {
                try {
                    Document doc = retriever.getSearcher(result.x).doc(v.doc);
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
        logger.info("Testing Ranking Query");
        for(Function<Similarity, Tuple<IndexType, TopDocs>> s : buildQueries()) {
            long currentTime = System.nanoTime();
            Tuple<IndexType, TopDocs> result = s.apply(null);
            logger.info("Query " + count + " Processing Time:" + ((System.nanoTime() - currentTime) / 1000000) + " milliseconds");
            count++;
            Arrays.stream(result.y.scoreDocs).forEach(v -> {
                try {
                    Document doc = retriever.getSearcher(result.x).doc(v.doc);
                    logger.info("Score:" + v.score + " " + ((IndexableField)doc.iterator().next()).name() + ": " + ((IndexableField)doc.iterator().next()).stringValue());
                } catch (IOException e) {
                    logger.error("io fault", e);
                }
            });
        }
    }

}