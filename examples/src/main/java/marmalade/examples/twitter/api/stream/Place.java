package marmalade.examples.twitter.api.stream;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Place
{
   private Map<String, String> attributes;
   @JsonProperty("bounding_box")
   private BoundingBox boundingBox;
   private String country;
   @JsonProperty("country_code")
   private String countryCode;
   @JsonProperty("full_name")
   private String fullName;
   private String id;
   private String name;
   private String url;
   @JsonProperty("place_type")
   private String placeType;
   private String[] polylines;
   @JsonProperty("contained_within")
   private Place[] containedWithin;
}
