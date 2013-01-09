package glaze.oauth.creds;

public class DefaultCredentialsProvider extends AbstractCredentialsProvider
{

   private final String key, secret, tokenKey, tokenSecret;

   public DefaultCredentialsProvider(String key, String secret, String tokenKey, String tokenSecret)
   {
      this.key = key;
      this.secret = secret;
      this.tokenKey = tokenKey;
      this.tokenSecret = tokenSecret;
   }

   @Override
   public String getKey()
   {
      return key;
   }

   public OAuthCredentials getOAuthCredentials(Object context)
   {
      return createCredentials(key, secret, tokenKey, tokenSecret);
   }

   @Override
   public String getSecret()
   {
      return secret;
   }

}
