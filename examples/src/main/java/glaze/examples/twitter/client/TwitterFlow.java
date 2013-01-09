package glaze.examples.twitter.client;

import glaze.oauth.OAuthFlow;
import glaze.oauth.spi.OAuthConfig;

public class TwitterFlow extends OAuthFlow
{

   public TwitterFlow(OAuthConfig config)
   {
      this(config.getKey(), config.getSecret());
   }

   public TwitterFlow(String key, String secret)
   {
      super(key, secret);
   }

   @Override
   public String name()
   {
      return "Twitter";
   }

   @Override
   protected String accessTokenUrl()
   {
      return "https://api.twitter.com/oauth/access_token";
   }

   @Override
   protected String authorizeWebsiteUrl()
   {
      return "https://api.twitter.com/oauth/authorize";
   }

   @Override
   protected String requestTokenUrl()
   {
      return "https://api.twitter.com/oauth/request_token";
   }

}
