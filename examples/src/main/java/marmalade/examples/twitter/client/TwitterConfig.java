package marmalade.examples.twitter.client;

import marmalade.client.config.DefaultPropertyConfig;
import marmalade.oauth.spi.OAuthConfig;

public class TwitterConfig extends DefaultPropertyConfig implements OAuthConfig
{
   
   public TwitterConfig()
   {
      super("twitter.config", "twitter.properties");
   }

   public String getKey()
   {
      return get("key");
   }

   public String getSecret()
   {
      return get("secret");
   }

   public String getTokenKey()
   {
      return get("token.key");
   }

   public String getTokenSecret()
   {
      return get("token.secret");
   }

}
