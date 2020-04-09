package Translator;

import Model.Business.Business;
import Model.Business.Hours;
import Model.Business.Model;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field;

import java.lang.reflect.*;
import java.util.Optional;

public class BusinessConverter implements Converter {

    public Document Convert(Model b) throws Exception {
        Business biz = (Business) b;
        Document doc = new Document();
        doc.add(new StringField("business_id", biz.businessId, Field.Store.YES));
        doc.add(new TextField("name", biz.name, Field.Store.YES));
        doc.add(new TextField("address", biz.address, Field.Store.YES));
        doc.add(new StringField("city", biz.city, Field.Store.YES));
        doc.add(new StringField("state", biz.state, Field.Store.YES));
        doc.add(new StringField("postal_code", biz.postalCode, Field.Store.YES));
        doc.add(new DoublePoint("latitude", biz.latitude));
        doc.add(new StoredField("latitude", biz.latitude));
        doc.add(new DoublePoint("longitude", biz.longitude));
        doc.add(new StoredField("longitude", biz.longitude));
        doc.add(new DoublePoint("stars", biz.stars));
        doc.add(new StoredField("stars", biz.stars));
        doc.add(new IntPoint("review_count", biz.reviewCount));
        doc.add(new StoredField("review_count", biz.reviewCount));
        doc.add(new StringField("is_open", biz.isOpen == 1 ? "True" : "False", Field.Store.YES));
        doc.add(new TextField("categories", Optional.ofNullable(biz.categories).orElse(""), Field.Store.YES));
        if(biz.attributes != null) {
            biz.attributes.entrySet().forEach(set -> {
                doc.add(new StringField(set.getKey(), set.getValue(), Field.Store.YES));
            });
        }

        mapHour(doc, biz.hours);

        return doc;

    }

    public void mapHour(Document doc, Hours hour) throws Exception {
        if(hour == null) {
            return;
        }
        java.lang.reflect.Field[] fields = Hours.class.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            if(field.get(hour) == null) {
                continue;
            }
            String value = field.get(hour).toString();
            doc.add(new IntPoint(field.getName() + ".openingTime",
                    getStartTime(value)));
            doc.add(new StoredField(field.getName() + ".openingTime", getStartTime(value)));
            doc.add(new IntPoint(field.getName() + ".closeTime",
                    getEndTime(value)));
            doc.add(new StoredField(field.getName() + ".closeTime", getEndTime(value)));
        }
    }

    public int getStartTime(String timeString) {
        String time = timeString.split("-")[0];

        int hour = Integer.parseInt(time.split(":")[0].trim());
        int minute = Integer.parseInt(time.split(":")[0].trim());
        return hour * 100 + minute;
    }

    public int getEndTime(String timeString) {
        String time = timeString.split("-")[1];

        int hour = Integer.parseInt(time.split(":")[0].trim());
        int minute = Integer.parseInt(time.split(":")[0].trim());
        return hour * 100 + minute;
    }
}
