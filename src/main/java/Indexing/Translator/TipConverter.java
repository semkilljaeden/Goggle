package Indexing.Translator;

import Indexing.Model.Model;
import Indexing.Model.Tip;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;

import java.util.Date;

public class TipConverter implements Converter {

    public TipConverter () {
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

    public Document Convert(Model usr) throws  Exception {
        Document doc = new Document();
        java.lang.reflect.Field[] fields = Tip.class.getDeclaredFields();
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
                        doc.add(new StringField(field.getName(), key, Field.Store.YES));
                        break;
                    case "text":
                        doc.add(new Field(field.getName(), key, textFt));
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
