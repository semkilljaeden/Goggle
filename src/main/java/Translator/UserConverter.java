package Translator;

import Model.Business.Business;
import Model.Business.Hours;
import Model.Business.Model;
import Model.Business.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.lucene.document.*;

import java.util.Optional;

public class UserConverter implements Converter {

    public Document Convert(Model usr) throws  Exception {
        Document doc = new Document();
        java.lang.reflect.Field[] fields = User.class.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            if(field.get(usr) == null) {
                continue;
            }
            Object value = field.get(usr);
            if(value instanceof String) {
                String key = value.toString();
                switch (field.getName()) {
                    case "userId":
                        doc.add(new StringField(field.getName(), key, Field.Store.YES));
                        break;
                    case "name":
                    case "yelpingSince":
                    case "elite":
                    case "friends":
                        doc.add(new TextField(field.getName(), key.replace(",", " "), Field.Store.YES));
                        break;
                    default:
                        break;
                }
            }
            else if(value instanceof Double) {
                doc.add(new DoublePoint(field.getName(), (Double)value));
                doc.add(new StoredField(field.getName(), (Double)value));
            }
            else if(value instanceof Integer) {
                doc.add(new IntPoint(field.getName(), (Integer)value));
                doc.add(new StoredField(field.getName(), (Integer)value));
            }
            else {
                throw new Exception("invalid DataType");
            }
        }

        return doc;

    }
}
