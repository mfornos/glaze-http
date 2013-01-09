package glaze.examples.twitter.client;

import oauth.signpost.OAuthConsumer;
import glaze.Glaze;
import glaze.client.async.AsyncClient;

public interface TwitterClient extends AsyncClient
{
   boolean isPreemptive();

   Glaze signed(Glaze m, Object context);

   void setTokens(OAuthConsumer consumer);

   String getKey();

   String getSecret();
}
