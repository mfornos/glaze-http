package marmalade.examples.infochimps.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Social
{
   @JsonProperty("user_id")
   public Integer userId;
   
   @JsonProperty("screen_name")
   public String screenName;
}
