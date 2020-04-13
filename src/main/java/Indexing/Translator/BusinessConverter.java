package Indexing.Translator;

import Indexing.Model.Business;
import Indexing.Model.Hours;
import Indexing.Model.Model;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;

import java.util.Optional;

public class BusinessConverter implements Converter {

    public BusinessConverter () {
        textFt.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        textFt.setStored(true);
        textFt.setTokenized(true);
        textFt.setStoreTermVectorOffsets(true);
        textFt.setStoreTermVectorPositions(true);
        textFt.setStoreTermVectors(true);
        stringFt.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        stringFt.setStored(true);
        stringFt.setTokenized(false);
        stringFt.setStoreTermVectorOffsets(true);
        stringFt.setStoreTermVectorPositions(true);
        stringFt.setStoreTermVectors(true);
    }

    public Document Convert(Model b) throws Exception {
        Business biz = (Business) b;
        Document doc = new Document();
        doc.add(new StringField("business_id", biz.businessId, Field.Store.YES));
        doc.add(new Field("name", biz.name, textFt));
        doc.add(new Field("address", biz.address, textFt));
        doc.add(new Field("city", biz.city, stringFt));
        doc.add(new Field("state", biz.state, stringFt));
        doc.add(new Field("postal_code", biz.postalCode, stringFt));
        doc.add(new DoublePoint("latitude", biz.latitude));
        doc.add(new StoredField("latitude", biz.latitude));
        doc.add(new DoublePoint("longitude", biz.longitude));
        doc.add(new StoredField("longitude", biz.longitude));
        doc.add(new DoublePoint("stars", biz.stars));
        doc.add(new StoredField("stars", biz.stars));
        doc.add(new IntPoint("review_count", biz.reviewCount));
        doc.add(new StoredField("review_count", biz.reviewCount));
        doc.add(new Field("is_open", biz.isOpen == 1 ? "True" : "False", stringFt));
        doc.add(new Field("categories", Optional.ofNullable(biz.categories).orElse(""), textFt));
        if(biz.attributes != null) {
            biz.attributes.entrySet().forEach(set -> {
                doc.add(new Field(set.getKey(), set.getValue(), textFt));
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
