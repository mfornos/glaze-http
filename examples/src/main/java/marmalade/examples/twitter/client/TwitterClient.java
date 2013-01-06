package marmalade.examples.twitter.client;

import oauth.signpost.OAuthConsumer;
import marmalade.Marmalade;
import marmalade.client.async.AsyncClient;

public interface TwitterClient extends AsyncClient
{
   boolean isPreemptive();

   Marmalade signed(Marmalade m, Object context);

   void setTokens(OAuthConsumer consumer);

   String getKey();

   String getSecret();
}
