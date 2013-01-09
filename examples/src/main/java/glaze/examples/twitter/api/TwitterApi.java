package glaze.examples.twitter.api;

import static glaze.Glaze.Get;
import static glaze.client.UriBuilder.uriBuilderFrom;

import glaze.examples.twitter.api.stream.Tweet;
import glaze.examples.twitter.client.TwitterClient;
import glaze.examples.twitter.client.TwitterFlow;

import java.util.concurrent.Future;

import glaze.Glaze;
import glaze.client.UriBuilder;
import glaze.spi.Registry;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthException;

import com.google.common.base.Preconditions;

public class TwitterApi
{
   private static final String BASE_URI = "https://api.twitter.com/1.1";

   private final TwitterClient client;
   private TwitterFlow flow;

   public TwitterApi()
   {
      this(Registry.lookup(TwitterClient.class));
   }

   public TwitterApi(TwitterClient client)
   {
      Preconditions.checkNotNull(client);

      this.client = client;
   }

   public void cancelFlow()
   {
      this.flow = null;
   }

   public Tweet[] homeTimeline()
   {
      try {
         return promiseHomeTimeline().get();
      } catch (Exception e) {
         throw new TwitterApiException(e);
      }
   }

   public Future<Tweet[]> promiseHomeTimeline()
   {
      return defaults(Get(svcPath("/statuses/home_timeline.json").build())).mapAsync(client, Tweet[].class);
   }

   public void shutdown()
   {
      cancelFlow();
      client.shutdown();
   }

   public String startFlow() throws OAuthException
   {
      return startFlow(OAuth.OUT_OF_BAND);
   }

   public String startFlow(String callback) throws OAuthException
   {
      try {
         this.flow = new TwitterFlow(client.getKey(), client.getSecret());
         return this.flow.requestAuthorization(callback);
      } catch (OAuthException e) {
         this.flow = null;
         throw e;
      }
   }

   public OAuthConsumer verify(String verifier) throws OAuthException
   {
      synchronized (flow) {
         Preconditions.checkNotNull(flow);

         try {
            OAuthConsumer consumer = flow.confirmAuthorization(verifier);
            client.setTokens(consumer);
            return consumer;
         } finally {
            flow = null;
         }
      }
   }

   protected Glaze defaults(Glaze m)
   {
      // XXX context
      return client.isPreemptive() ? m : client.signed(m, null);
   }

   protected UriBuilder svcPath(Object... paths)
   {
      return uriBuilderFrom(BASE_URI).appendPaths(paths);
   }

}
