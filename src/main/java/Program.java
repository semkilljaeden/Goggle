import Model.Business.Business;
import Model.Business.Review;
import Model.Business.Tip;
import Model.Business.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;

public class Program {
    private static Logger logger = LogManager.getLogger(Program.class);
    public static void main(String[] args) {

        try {
            Parse(new Indexer(Business.class), "yelp_academic_dataset_business.json");
            Parse(new Indexer(Review.class),"yelp_academic_dataset_review.json");
            Parse(new Indexer(Tip.class),"yelp_academic_dataset_tip.json");
            Parse(new Indexer(User.class),"yelp_academic_dataset_user.json");
            Indexer.CompleteIndexing();
        } catch (Exception e) {
            logger.error("IO Error", e);
        }
    }

    public static void Parse(Indexer indexer, String fileName) {
        logger.info("Start to parse " + fileName);
        int count = 0;
        try (BufferedReader br = new BufferedReader((new InputStreamReader(new FileInputStream(Utils.getProps("dataPath") + fileName),
                "UTF-8")))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(count % 100000 == 0) {
                    logger.info(count + " row parsed");
                }
                count++;
                indexer.IndexModel(line);
            }
            logger.info(count +  " " + fileName + " total rows parsed");
        } catch (FileNotFoundException e) {
            logger.error("Cannot find data file", e);
        } catch (IOException e) {
            logger.error("IO Error", e);
        }
    }
}