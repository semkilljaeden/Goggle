package Model.Business;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday"
})
public class Hours {

    @JsonProperty("Monday")
    public String monday;
    @JsonProperty("Tuesday")
    public String tuesday;
    @JsonProperty("Wednesday")
    public String wednesday;
    @JsonProperty("Thursday")
    public String thursday;
    @JsonProperty("Friday")
    public String friday;
    @JsonProperty("Saturday")
    public String saturday;
    @JsonProperty("Sunday")
    public String sunday;

}
