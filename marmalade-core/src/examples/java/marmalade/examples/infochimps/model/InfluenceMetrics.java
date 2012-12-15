package marmalade.examples.infochimps.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InfluenceMetrics extends Social
{
   @JsonProperty("created_at")
   public Long createdAt;

   public Integer followers;

   public Float influx;

   public Float outflux;

   public Float enthusiasm;

   public Float interesting;

   public Float feedness;

   public Float chattiness;

   public Float sway;

   @JsonProperty("follow_rate")
   public Float follow_rate;

   @JsonProperty("follow_churn")
   public Float followChurn;

   @JsonProperty("at_trstrank")
   public Float atTrstrank;

   @JsonProperty("fo_trstrank")
   public Float foTrstrank;

   @Override
   public String toString()
   {
      return "InfluenceMetrics [createdAt=" + createdAt + ", followers=" + followers + ", influx=" + influx
            + ", outflux=" + outflux + ", enthusiasm=" + enthusiasm + ", interesting=" + interesting + ", feedness="
            + feedness + ", chattiness=" + chattiness + ", sway=" + sway + ", follow_rate=" + follow_rate
            + ", followChurn=" + followChurn + ", atTrstrank=" + atTrstrank + ", foTrstrank=" + foTrstrank + "]";
   }

}
