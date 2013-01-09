package glaze.oauth.spi;

import glaze.client.Client;
import glaze.oauth.creds.OAuthCredentials;

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
