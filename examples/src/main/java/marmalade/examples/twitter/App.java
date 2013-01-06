package marmalade.examples.twitter;

import java.io.IOException;

import marmalade.examples.twitter.api.TwitterApi;
import marmalade.examples.twitter.api.stream.Tweet;
import marmalade.examples.twitter.client.PreemptiveTwitterClient;
import marmalade.examples.twitter.client.TwitterConfig;
import marmalade.examples.twitter.client.TwitterFlow;
import marmalade.oauth.util.ConsoleFlow;
import oauth.signpost.exception.OAuthException;

public class App
{

   public static void main(String[] args) throws IOException, OAuthException
   {
      App app = new App();
      app.homeTimeline();

      System.exit(0);
   }

   private final TwitterApi api;

   public App()
   {
      api = new TwitterApi(new PreemptiveTwitterClient());
      setShutdownHook();
   }

   /**
    * Start an authentication console flow.
    * 
    * @throws OAuthException
    * @throws IOException
    */
   public void flow() throws OAuthException, IOException
   {
      TwitterFlow flow = new TwitterFlow(new TwitterConfig());
      ConsoleFlow.execute(flow);
   }

   /**
    * Get your home tweets.
    */
   public void homeTimeline()
   {
      Tweet[] tweets = api.homeTimeline();

      for (Tweet tweet : tweets) {
         System.out.println(tweet);
      }
   }

   private void setShutdownHook()
   {
      Runtime.getRuntime().addShutdownHook(new Thread()
      {
         @Override
         public void run()
         {
            api.shutdown();
         }
      });
   }
}
