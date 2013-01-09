package glaze.examples.twitter.api.stream;

import glaze.examples.twitter.api.TwitterDateDeserializer;
import glaze.examples.twitter.api.text.Autolink;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Data
public class Tweet
{
   @Data
   public static class Contributor
   {
      private Long id;
      @JsonProperty("id_str")
      private String idStr;
      @JsonProperty("screen_name")
      private String screenName;
   }

   @Data
   public static class Coordinates
   {
      private Float[] coordinates;
      private String type;
   }

   @JsonDeserialize(using = TwitterDateDeserializer.class)
   @JsonProperty("created_at")
   private Date createdAt;

   private Set<Contributor> contributors;

   private Coordinates coordinates;

   private Boolean favorited;

   private Entities entities;

   private Long id;

   @JsonProperty("id_str")
   private String idStr;

   @JsonProperty("in_reply_to_screen_name")
   private String inReplyToScreenName;

   @JsonProperty("in_reply_to_status_id")
   private Long inReplyToStatusId;

   @JsonProperty("in_reply_to_status_id_str")
   private String inReplyToStatusIdStr;

   @JsonProperty("in_reply_to_user_id")
   private Long inReplyToUserId;

   @JsonProperty("in_reply_to_user_id_str")
   private String inReplyToUserIdStr;

   private Place place;

   @JsonProperty("possibly_sensitive")
   private Boolean possiblySensitive;

   private Map<String, Object> scopes;

   @JsonProperty("retweet_count")
   private Integer retweetCount;

   private Boolean retweeted;

   private Boolean truncated;

   @JsonProperty("withheld_copyright")
   private Boolean withheldCopyright;

   @JsonProperty("withheld_in_countries")
   private String[] withheldInCountries;

   @JsonProperty("withheld_scope")
   private String withheldScope;

   private String source;

   private String text;

   private User user;

   @JsonIgnore
   private String html;

   @Getter(AccessLevel.NONE)
   private static Autolink linker = new Autolink();

   @JsonIgnore
   public String getHtml()
   {
      if (html == null) {
         html = linker.autoLinkEntities(text, entities);
      }
      return html;
   }
}
