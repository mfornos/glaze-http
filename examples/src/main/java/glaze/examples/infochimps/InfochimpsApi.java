package glaze.examples.infochimps;

import java.net.URI;

import glaze.client.Response;
import glaze.client.UriBuilder;
import glaze.client.handlers.ErrorHandler;

public class InfochimpsApi
{
   /**
    * <p>
    * 200 (Success) The query ran successfully. Note that search queries that
    * return no values will have this response code.
    * <p>
    * 400 (Bad Request) A bad endpoint, or a bad parameter name/value.
    * <p>
    * 401 (ApikeyNotFoundError) Your API key is either incorrect or missing.
    * <p>
    * 403 (UserNeedsLicenseError) Our traffic cop, Buzzkill, says this API
    * requires a license and that the account tied to your API key hasn't agreed
    * to it.
    * <p>
    * 404 (Record Not Found) For queries involving a look-up of a particular
    * value, for instance "&id=32", which is not an ID found in our data store.
    * <p>
    * 500 (Internal Server Error) Something went wrong on our side.
    * 
    */
   private static class InfochimpsErrors implements ErrorHandler
   {

      @Override
      public void onError(Response response)
      {
         int status = response.status();
         switch (status) {
         case 400:
            error("(Bad Request) A bad endpoint, or a bad parameter name/value.");
            break;
         case 401:
            error("(ApikeyNotFoundError) Your API key is either incorrect or missing.");
            break;
         case 403:
            error("(UserNeedsLicenseError) Our traffic cop, Buzzkill, says this API requires a license and that the account tied to your API key hasn't agreed");
            break;
         case 404:
            error("(Record Not Found) For queries involving a look-up of a particular value, for instance \"&id=32\", which is not an ID found in our data store.");
            break;
         default:
            error("(Internal Server Error) Something went wrong on our side.");
            break;
         }

      }

      private void error(String msg)
      {
         System.err.println(msg);
      }

   }

   private static final ErrorHandler EH = new InfochimpsErrors();

   public static InfochimpsApi infochimpsApi(String apikey, String dataPath)
   {
      return new InfochimpsApi(apikey, dataPath);
   }

   public static ErrorHandler infochimpsErrors()
   {
      return EH;
   }

   public static URI uriInfluenceMetrics(String apikey, String screenName)
   {
      return infochimpsApi(apikey, "social/network/tw/influence/metrics").screenName(screenName).build();
   }

   public static URI uriTsrank(String apikey, String screenName)
   {
      return infochimpsApi(apikey, "soc/net/tw/trstrank.json").screenName(screenName).build();
   }

   public static URI uriUfoSight(String apikey, String q, int from, int limit)
   {
      return infochimpsApi(apikey, "science/astronomy/seti/nuforc/ufo_sightings_search").from(from).limit(limit).uriBuilder.addParameter("q", q).build();
   }

   private final UriBuilder uriBuilder;

   private InfochimpsApi(String apikey, String dataPath)
   {
      uriBuilder = UriBuilder.uriBuilderFrom("http://api.infochimps.com/").appendPath(dataPath).addParameter("apikey", apikey);
   }

   public URI build()
   {
      return uriBuilder.build();
   }

   public InfochimpsApi from(int from)
   {
      uriBuilder.addParameter("_from", from);
      return this;
   }

   public InfochimpsApi limit(int limit)
   {
      uriBuilder.addParameter("_limit", limit);
      return this;
   }

   public InfochimpsApi screenName(String screenName)
   {
      uriBuilder.addParameter("screen_name", screenName);
      return this;
   }

}
