package Model.Business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "review_id",
        "user_id",
        "business_id",
        "stars",
        "useful",
        "funny",
        "cool",
        "text",
        "date"
})
public class Review implements Model {

    @JsonProperty("review_id")
    public String reviewId;
    @JsonProperty("user_id")
    public String userId;
    @JsonProperty("business_id")
    public String businessId;
    @JsonProperty("stars")
    public double stars;
    @JsonProperty("useful")
    public int useful;
    @JsonProperty("funny")
    public int funny;
    @JsonProperty("cool")
    public int cool;
    @JsonProperty("text")
    public String text;
    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    public Date date;

}