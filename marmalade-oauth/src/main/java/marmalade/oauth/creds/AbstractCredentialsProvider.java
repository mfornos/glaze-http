package marmalade.oauth.creds;

import marmalade.client.Client;
import marmalade.oauth.OAuthSchemeFactory;
import marmalade.oauth.spi.OAuthCredentialsProvider;
import marmalade.spi.ServiceProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.auth.AuthScope;
import org.apache.http.client.CredentialsProvider;

public abstract class AbstractCredentialsProvider implements OAuthCredentialsProvider,
      ServiceProvider<OAuthCredentialsProvider>
{
   @Override
   public Class<OAuthCredentialsProvider> serviceClass()
   {
      return OAuthCredentialsProvider.class;
   }

   @Override
   public OAuthCredentialsProvider serviceImpl()
   {
      return this;
   }

   @Override
   public void visit(Client client)
   {
      client.auth(getAuthScope(), getOAuthCredentials());
   }

   @Override
   public void visit(CredentialsProvider provider)
   {
      AuthScope scope = getAuthScope();
      OAuthCredentials creds = getOAuthCredentials();
      provider.setCredentials(scope, creds);
   }

   protected OAuthCredentials createCredentials(String key, String secret, String tokenKey, String tokenSecret)
   {
      CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(key, secret);
      consumer.setTokenWithSecret(tokenKey, tokenSecret);
      return new OAuthCredentials(consumer);
   }

   protected AuthScope getAuthScope()
   {
      return new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, OAuthSchemeFactory.SCHEME_NAME);
   }

   @Override
   public OAuthCredentials getOAuthCredentials()
   {
      return getOAuthCredentials(null);
   }

}
