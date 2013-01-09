package glaze.examples.infochimps.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UfoSights
{
   public static class UfoSight
   {
      @JsonProperty("sighted_at")
      public String sighted_at;
      @JsonProperty("reported_at")
      public String reported_at;
      public String location;
      public String shape;
      public String duration;
      public String description;

      @Override
      public String toString()
      {
         return "\nUfoSight [sighted_at=" + sighted_at + ", reported_at=" + reported_at + ", location=" + location
               + ", shape=" + shape + ", duration=" + duration + ", description=" + description + "]\n";
      }
   }

   public Integer total;
   public UfoSight[] results;

   @Override
   public String toString()
   {
      return "UfoSights [total=" + total + ", results=" + Arrays.toString(results) + "]";
   }
}
