package marmalade.oauth.creds;

import marmalade.oauth.spi.OAuthConfig;

public class ConfigCredentialsProvider extends DefaultCredentialsProvider
{

   public ConfigCredentialsProvider(OAuthConfig config)
   {
      super(config.getKey(), config.getSecret(), config.getTokenKey(), config.getTokenSecret());
   }

}
