package Translator;

import org.apache.lucene.document.*;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexOptions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
public class GeneralConverter {

    public static Document Convert(JSONObject object) {
        Document doc = new Document();
        for (String field : (Set<String>) object.keySet()) {

            Add(doc, field, object.get(field));

        }
        return doc;
    }


    public static void AddProperties(Document doc, String prefix, JSONObject obj)

    {

        for (String field : (Set<String>) obj.keySet()) {

            Add(doc, MakePrefix(prefix, field), obj.get(field));

        }

    }


    public static void AddArray(Document doc, String prefix, List<JSONObject> array)

    {

        for (JSONObject obj : array)

        {

            Add(doc, prefix, obj);

        }

    }


    public static String MakePrefix(String str, String add)

    {

        if (str != null && !str.isEmpty())

        {

            return str + "." + add;

        }

        else

        {

            return add;

        }

    }
    public static void Add(Document doc, String prefix, Object token)

    {
        if (token instanceof JSONObject)

        {

            AddProperties(doc, prefix, (JSONObject) token);

        }

        else if (token instanceof List)

        {
            AddArray(doc, prefix, (List<JSONObject>) token);

        }
        else if (token instanceof String) {
            String v = (String)token;
            doc.add(new TextField(prefix, v.toString(), Field.Store.YES));
        }
        else if (token instanceof Integer) {
            Integer v = (Integer)token;
            doc.add(new IntPoint(prefix, v));
            doc.add(new StoredField(prefix, v));
        }
        else if (token instanceof Float) {
            Float v = (Float)token;
            doc.add(new FloatPoint(prefix, v));
            doc.add(new StoredField(prefix, v));
        }
        else if (token instanceof Double) {
            Double v = (Double)token;
            doc.add(new DoublePoint(prefix, v));
            doc.add(new StoredField(prefix, v));
        }
        else {

        }


    }


}
