package Indexing.Translator;

import Indexing.Model.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;

public interface Converter {
    FieldType textFt  = new FieldType();
    FieldType stringFt  = new FieldType();
    Document Convert(Model biz) throws Exception;
    public static <T extends Model> Converter getConverter(Class<T> model) {
        if(model == Review.class)
        {
            return new ReviewConverter();
        }
        else if(model == Business.class)
        {
            return new BusinessConverter();
        }
        else if(model == User.class)
        {
            return new UserConverter();
        }
        else if(model == Tip.class)
        {
            return new TipConverter();
        }
        else {
            return null;
        }
    }
}
