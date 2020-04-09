package Model.Business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "user_id",
        "business_id",
        "text",
        "date",
        "compliment_count"
})
public class Tip implements Model {

    @JsonProperty("user_id")
    public String userId;
    @JsonProperty("business_id")
    public String businessId;
    @JsonProperty("text")
    public String text;
    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    public Date date;
    @JsonProperty("compliment_count")
    public int complimentCount;

}