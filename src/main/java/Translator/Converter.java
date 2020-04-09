package Translator;

import Model.Business.*;
import org.apache.lucene.document.Document;

public interface Converter {
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
