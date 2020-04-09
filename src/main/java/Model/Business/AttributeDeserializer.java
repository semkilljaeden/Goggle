package Model.Business;
import java.util.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AttributeDeserializer extends JsonDeserializer<Map<String, String>> {
    @Override
    public Map<String, String> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        Iterable<Map.Entry<String, JsonNode>> iterable = () -> node.fields();
        Map<String, String> result =  StreamSupport.stream(iterable.spliterator(), false).map(ev -> {
            if(ev.getKey().compareTo("BusinessParking") == 0 && ev.getValue().textValue().contains(":")) {
                String value = ev.getValue().textValue();
                return Arrays.stream(value
                        .replace("{", "")
                        .replace("}", "")
                        .split(",")).map(s -> {
                            String[] tuple = s.replace("'", "").split(":");
                            return new AbstractMap.SimpleEntry<String, String>("BusinessParking." + tuple[0], tuple[1]);
                }).collect(Collectors.toList());
            }
            else {
                ArrayList<AbstractMap.SimpleEntry<String, String>> list = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
                list.add(new AbstractMap.SimpleEntry<String, String>(ev.getKey(), ev.getValue().textValue()));
                return list;
            }
        }).flatMap(ev -> {
            return ev.stream();
        }).collect(Collectors.toMap(ev -> {
            return ev.getKey();
        }, ev -> {
            return ev.getValue();
        }));
        return result;
    }
}
