package glaze.examples.twitter.client;

import glaze.client.config.DefaultPropertyConfig;
import glaze.oauth.spi.OAuthConfig;

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
