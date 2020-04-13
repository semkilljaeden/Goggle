package Indexing.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "business_id",
        "name",
        "address",
        "city",
        "state",
        "postal_code",
        "latitude",
        "longitude",
        "stars",
        "review_count",
        "is_open",
        "attributes",
        "categories",
        "hours"
})
public class Business implements Model {

    @JsonProperty("business_id")
    public String businessId;
    @JsonProperty("name")
    public String name;
    @JsonProperty("address")
    public String address;
    @JsonProperty("city")
    public String city;
    @JsonProperty("state")
    public String state;
    @JsonProperty("postal_code")
    public String postalCode;
    @JsonProperty("latitude")
    public double latitude;
    @JsonProperty("longitude")
    public double longitude;
    @JsonProperty("stars")
    public double stars;
    @JsonProperty("review_count")
    public int reviewCount;
    @JsonProperty("is_open")
    public int isOpen;
    @JsonProperty("attributes")
    @JsonDeserialize(using = AttributeDeserializer.class)
    public Map<String, String> attributes;
    @JsonProperty("categories")
    public String categories;
    @JsonProperty("hours")
    public Hours hours;

}


