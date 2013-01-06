package marmalade.examples.twitter.client;

import oauth.signpost.OAuthConsumer;
import marmalade.Marmalade;
import marmalade.client.async.DefaultAsyncClient;
import marmalade.oauth.OAuthClosure;
import marmalade.oauth.creds.ConfigCredentialsProvider;
import marmalade.oauth.creds.ConsumerCredentialsProvider;
import marmalade.oauth.spi.OAuthConfig;
import marmalade.oauth.spi.OAuthCredentialsProvider;

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
   public Marmalade signed(Marmalade m, Object context)
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
