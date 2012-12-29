package marmalade.oauth.spi;

import marmalade.client.Client;
import marmalade.oauth.creds.OAuthCredentials;

import org.apache.http.client.CredentialsProvider;

/**
 *
 */
public interface OAuthCredentialsProvider
{

   void visit(CredentialsProvider provider);

   void visit(Client client);
   
   OAuthCredentials getOAuthCredentials();

   OAuthCredentials getOAuthCredentials(Object context);

   String getKey();
   
   String getSecret();

}
