package marmalade.oauth.spi;

import marmalade.client.Client;
import marmalade.oauth.OAuthCredentials;
import marmalade.oauth.OAuthSchemeFactory;
import marmalade.spi.ServiceContrib;

import org.apache.http.auth.AuthScope;
import org.apache.http.client.CredentialsProvider;

public abstract class DefaultCredentialsProvider implements OAuthCredentialsProvider, ServiceContrib
{

   @Override
   public Class<?> serviceClass()
   {
      return OAuthCredentialsProvider.class;
   }

   @Override
   public Object serviceImpl()
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

   protected AuthScope getAuthScope()
   {
      return new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, OAuthSchemeFactory.SCHEME_NAME);
   }

   abstract protected OAuthCredentials getOAuthCredentials();

}
