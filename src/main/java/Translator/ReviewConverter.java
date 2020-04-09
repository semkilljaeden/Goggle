package Translator;

import Model.Business.Model;
import Model.Business.Review;
import Model.Business.Tip;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.lucene.document.*;

import java.util.Date;

public class ReviewConverter implements Converter {

    public Document Convert(Model usr) throws  Exception {
        Document doc = new Document();
        java.lang.reflect.Field[] fields = Review.class.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            if(field.get(usr) == null) {
                continue;
            }
            Object value = field.get(usr);
            if(value instanceof String) {
                String key = value.toString();
                switch (field.getName()) {
                    case "userId":
                    case "businessId":
                    case "reviewId":
                        doc.add(new StringField(field.getName(), key, Field.Store.YES));
                        break;
                    case "text":
                        doc.add(new TextField(field.getName(), key, Field.Store.YES));
                        break;
                    default:
                        break;
                }
            }
            else if(value instanceof Date) {
                doc.add(new LongPoint(field.getName(), ((Date) value).getTime()));
                doc.add(new StoredField(field.getName(), ((Date) value).getTime()));
            }
            else if(value instanceof Integer) {
                doc.add(new IntPoint(field.getName(), (Integer)value));
                doc.add(new StoredField(field.getName(), (Integer)value));
            }
            else if(value instanceof Double) {
                doc.add(new DoublePoint(field.getName(), (Double)value));
                doc.add(new StoredField(field.getName(), (Double)value));
            }
            else {
                throw new Exception("invalid DataType");
            }
        }
        return doc;

    }
}
