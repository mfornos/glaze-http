package glaze.examples.twitter.api.stream;

import glaze.examples.twitter.api.text.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class Entities
{
   private Hashtag[] hashtags;

   @JsonProperty("user_mentions")
   private Mention[] userMentions;

   private Url[] urls;

   // TODO media

   @Data
   public static class Url
   {
      @JsonProperty("expanded_url")
      private String expandedUrl;
      @JsonProperty("display_url")
      private String displayUrl;
      private String url;
      private Integer[] indices;
   }

   @Data
   public static class Hashtag
   {
      private String text;
      private Integer[] indices;
   }

   @Data
   public static class Mention
   {
      @JsonProperty("screen_name")
      private String screenName;
      private String name;
      private Long id;
      @JsonProperty("id_str")
      private String idStr;
      private Integer[] indices;
   }

   @JsonIgnore
   public List<Entity> asList()
   {
      List<Entity> entities = new ArrayList<Entity>();
      if (urls != null) {
         for (Url u : urls) {
            entities.add(new Entity(u));
         }
      }
      if (hashtags != null) {
         for (Hashtag h : hashtags) {
            entities.add(new Entity(h));
         }
      }
      if (userMentions != null) {
         for (Mention m : userMentions) {
            entities.add(new Entity(m));
         }
      }
      Collections.sort(entities);
      return entities;
   }

}
