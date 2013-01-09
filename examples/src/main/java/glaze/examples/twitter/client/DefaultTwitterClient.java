package glaze.examples.twitter.client;

import oauth.signpost.OAuthConsumer;
import glaze.Glaze;
import glaze.client.async.DefaultAsyncClient;
import glaze.oauth.OAuthClosure;
import glaze.oauth.creds.ConfigCredentialsProvider;
import glaze.oauth.creds.ConsumerCredentialsProvider;
import glaze.oauth.spi.OAuthConfig;
import glaze.oauth.spi.OAuthCredentialsProvider;

public class DefaultTwitterClient extends DefaultAsyncClient implements TwitterClient
{
   private OAuthCredentialsProvider provider;

   public DefaultTwitterClient()
   {
      this(new TwitterConfig());
   }

   public DefaultTwitterClient(OAuthConfig config)
   {
      this(new ConfigCredentialsProvider(config));
   }

   public DefaultTwitterClient(final OAuthConsumer consumer)
   {
      this(new ConsumerCredentialsProvider(consumer));
   }

   public DefaultTwitterClient(OAuthCredentialsProvider provider)
   {
      this.provider = provider;
   }

   @Override
   public boolean isPreemptive()
   {
      return false;
   }

   @Override
   public void setTokens(OAuthConsumer consumer)
   {
      this.provider = new ConsumerCredentialsProvider(consumer);
   }

   @Override
   public Glaze signed(Glaze m, Object context)
   {
      return OAuthClosure.Signed(m, provider, context);
   }

   protected OAuthCredentialsProvider getProvider()
   {
      return provider;
   }

   protected void setProvider(OAuthCredentialsProvider provider)
   {
      this.provider = provider;
   }

   @Override
   public String getKey()
   {
      return provider.getKey();
   }

   @Override
   public String getSecret()
   {
      return provider.getSecret();
   }
}
