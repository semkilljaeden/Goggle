package Indexing.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "user_id",
        "name",
        "review_count",
        "yelping_since",
        "useful",
        "funny",
        "cool",
        "elite",
        "friends",
        "fans",
        "average_stars",
        "compliment_hot",
        "compliment_more",
        "compliment_profile",
        "compliment_cute",
        "compliment_list",
        "compliment_note",
        "compliment_plain",
        "compliment_cool",
        "compliment_funny",
        "compliment_writer",
        "compliment_photos"
})
public class User implements Model {

    @JsonProperty("user_id")
    public String userId;
    @JsonProperty("name")
    public String name;
    @JsonProperty("review_count")
    public int reviewCount;
    @JsonProperty("yelping_since")
    public String yelpingSince;
    @JsonProperty("useful")
    public int useful;
    @JsonProperty("funny")
    public int funny;
    @JsonProperty("cool")
    public int cool;
    @JsonProperty("elite")
    public String elite;
    @JsonProperty("friends")
    public String friends;
    @JsonProperty("fans")
    public int fans;
    @JsonProperty("average_stars")
    public double averageStars;
    @JsonProperty("compliment_hot")
    public int complimentHot;
    @JsonProperty("compliment_more")
    public int complimentMore;
    @JsonProperty("compliment_profile")
    public int complimentProfile;
    @JsonProperty("compliment_cute")
    public int complimentCute;
    @JsonProperty("compliment_list")
    public int complimentList;
    @JsonProperty("compliment_note")
    public int complimentNote;
    @JsonProperty("compliment_plain")
    public int complimentPlain;
    @JsonProperty("compliment_cool")
    public int complimentCool;
    @JsonProperty("compliment_funny")
    public int complimentFunny;
    @JsonProperty("compliment_writer")
    public int complimentWriter;
    @JsonProperty("compliment_photos")
    public int complimentPhotos;

}